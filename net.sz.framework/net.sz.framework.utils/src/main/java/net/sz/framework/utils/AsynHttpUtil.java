package net.sz.framework.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AsynHttpUtil {

    private static final SzLogger log = SzLogger.getLogger();

    /**
     *
     * @param url
     * @param params
     * @return 这是异步的post方法 记住 最后要把Unirest.shutdown(); 队列 关闭掉
     */
    public static org.json.JSONObject post(String url, Map<String, Object> params) {
        HttpRequestWithBody request = Unirest.post(url).header("accept", "application/json");
        if (params != null) {
            request.fields(params);
        }

        //这是异步回调函数 执行惊醒处理
        Future<HttpResponse<String>> future = request.asStringAsync(new Callback<String>() {

            @Override
            public void failed(UnirestException e) {
                log.info("The request has failed");
            }

            @Override
            public void completed(HttpResponse<String> response) {
                int code = response.getStatus();
                // Map<String, String> headers = response.getHeaders();
                String body = response.getBody();
                //  InputStream rawBody = response.getRawBody();.
                log.info("body：" + body);
            }

            @Override
            public void cancelled() {
                log.info("The request has been cancelled");
            }

        });

        return null;

    }

    /**
     *
     * @param url
     * @param params
     * @return 这是异步的post方法 记住 最后要把Unirest.shutdown(); 队列 关闭掉
     */
    public static org.json.JSONObject get(String url, Map<String, Object> params) {

        GetRequest request = null;
        if (params == null) {
            request = Unirest.get(url);
        }
        request = Unirest.get(url + "?" + stringify(params));

        Future<HttpResponse<JsonNode>> future = request.asJsonAsync();
        HttpResponse<JsonNode> resp = null;
        try {
            resp = future.get();
            org.json.JSONObject ojson = resp.getBody().getObject();

            return ojson;
        } catch (Exception e) {

        }

        return null;

    }

    public static String stringify(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public static void main(String[] args) throws Exception {
        int thread_num = 10;

        int client_num = 10;
        final String tokenUrl = "https://openapi.baidu.com/social/oauth/2.0/token";

        ExecutorService exec = Executors.newCachedThreadPool();
        // 50个线程可以同时访问
        final Semaphore semp = new Semaphore(100);
        // 模拟460个客户端访问
        long ftime = System.currentTimeMillis();
        for (int index = 0; index < client_num; index++) {
            Runnable run = new Runnable() {
                public void run() {
                    try {
                        org.json.JSONObject json = AsynHttpUtil.post(tokenUrl, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            exec.execute(run);
        }
        // 退出线程池
        long etime = System.currentTimeMillis();
        log.info("AsynhttpUtil同步方式执行" + thread_num + "个并发访问 " + client_num + "个客服端所花费的时间 " + (etime - ftime) + " ms");
        Unirest.shutdown();
        exec.shutdown();
    }

}
