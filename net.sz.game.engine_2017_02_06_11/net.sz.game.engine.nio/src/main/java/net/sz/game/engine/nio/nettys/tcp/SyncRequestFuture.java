package net.sz.game.engine.nio.nettys.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncRequestFuture {
    
    private Long id;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private SyncRequestResponse response;

    private static final Map<Long, SyncRequestFuture> FUTURES = new ConcurrentHashMap();

    public SyncRequestFuture(Long id) {
        register(id);
        this.id = id;
    }
    private void register(Long id){
         FUTURES.put(id, this);
    }
   
    public boolean isDone() {
        return response != null;
    }

    public SyncRequestResponse get(int timeout) throws TimeoutException {

        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone()
                            || System.currentTimeMillis() - start >= timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) { 
                //确保超时异常都能删除
                FUTURES.remove(id);
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                //确保超时异常都能删除
                FUTURES.remove(id);
                throw new TimeoutException("timeout");
            }
        }
        return response;
    }

    public void doReceived(SyncRequestResponse response) {
        lock.lock();
        try {
            this.response = response;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }

    }

    public static void received(SyncRequestResponse response) {
        try {
            SyncRequestFuture future = FUTURES.remove(response.getSyncId());
            if (future != null) {
                future.doReceived(response);
            } else {
                System.out.println("some error!");
            }
        } finally {
            // CHANNELS.remove(response.getId());
        }
    }
}
