package net.sz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AppendContentToFile {

    public static void main(String[] args) throws Exception {
        AppendContentToFile a = new AppendContentToFile();
        int forcount = 10000;
        while (true) {

            long bigen = 0, end = 0;

            System.in.read();
            bigen = System.currentTimeMillis();
            for (int i = 0; i < forcount; i++) {
                a.method1("d:\\dd.txt", "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\n");
            }
            end = System.currentTimeMillis();

            System.out.println("耗时：" + (end - bigen));

            System.in.read();

            bigen = System.currentTimeMillis();
            for (int i = 0; i < forcount; i++) {
                a.writer("d:\\dd.txt", "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222\n");
            }
            end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - bigen));

            System.in.read();

            bigen = System.currentTimeMillis();
            for (int i = 0; i < forcount; i++) {
                a.method3("d:\\dd.txt", "3333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333\n");
            }
            end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - bigen));

        }
    }

    FileWriter fw = null;
    PrintWriter pw;

    public void method1(String file, String conent) {
        if (fw == null) {
            try {
                /*如果文件存在，则追加内容；如果文件不存在，则创建文件*/
                File f = new File(file);
                fw = new FileWriter(f, true);
                pw = new PrintWriter(fw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pw.print(conent);
        pw.flush();
//        try {
//            fw.flush();
//            pw.close();
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    BufferedWriter out = null;
    public void writer(String filePath, String conent) {
        try {
            if (out == null) {
                File file = new File(filePath);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            }
            out.write(conent);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    
    RandomAccessFile randomFile = null;

    public void method3(String fileName, String content) {
        try {
            if (randomFile == null) {
                /*打开一个随机访问文件流，按读写方式*/
                randomFile = new RandomAccessFile(fileName, "rw");
            }
            /*文件长度，字节数*/
            long fileLength = randomFile.length();

            /*将写文件指针移到文件尾。*/
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
//            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
