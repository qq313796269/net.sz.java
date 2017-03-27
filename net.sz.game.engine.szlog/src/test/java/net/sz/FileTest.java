package net.sz;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class FileTest {

    public FileTest() {
    }

    static void write(String msg) {
        try {
            /*将字节码写入目标文件-->相当于烟已经进入到嘴里*/
            byte[] bytes = msg.getBytes("utf-8");
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            buf.put(bytes);
            buf.flip();
            outFc.write(buf);
        } catch (Exception e) {
        }
    }

    static FileOutputStream outf;
    static FileChannel outFc;

    public static void main(String[] args) throws Exception {

        File outFile = new File("/home/szlog/sz_bak.log");
        if (!outFile.exists()) {
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();
        }
        outf = new FileOutputStream(outFile, true);
        /*准备文件读取的管道*/
        outFc = outf.getChannel();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.err.println("钩子函数");
                    outFc.close();
                    outf.flush();
                    outf.close();
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
        }));
        System.out.println("准备就绪");

        while (true) {
            long bigen = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                write("sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss\n");
                outf.flush();
            }
            System.out.println(System.currentTimeMillis() - bigen);
//            Thread.sleep(1);
            System.in.read();
        }
    }

    static void t1() {
        FileOutputStream out = null;
        FileOutputStream outSTr = null;
        BufferedOutputStream Buff = null;
        FileWriter fw = null;
        int count = 1000000;//写文件行数
        try {
            File file;

            {
                file = new File("d:/test/add1.txt");
                outSTr = new FileOutputStream(file);
                Buff = new BufferedOutputStream(outSTr);
                long begin0 = System.currentTimeMillis();
                for (int i = 0; i < count; i++) {
                    Buff.write("测试java 文件操作\r\n".getBytes());
                }
                Buff.flush();
                Buff.close();
                long end0 = System.currentTimeMillis();
                System.out.println("BufferedOutputStream执行耗时:" + (end0 - begin0) + " 豪秒");
            }
            {
                file = new File("d:/test/add2.txt");
                fw = new FileWriter(file);
                long begin3 = System.currentTimeMillis();
                for (int i = 0; i < count; i++) {
                    fw.write("测试java 文件操作\r\n");
                }
                fw.close();
                long end3 = System.currentTimeMillis();
                System.out.println("FileWriter执行耗时:" + (end3 - begin3) + " 豪秒");
            }
            {
                file = new File("d:/test/add0.txt");
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                out = new FileOutputStream(file);
                long begin = System.currentTimeMillis();
                for (int i = 0; i < count; i++) {
                    out.write("测试java 文件操作\r\n".getBytes());
                }
                out.close();
                long end = System.currentTimeMillis();
                System.out.println("FileOutputStream执行耗时:" + (end - begin) + " 豪秒");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
                Buff.close();
                outSTr.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
