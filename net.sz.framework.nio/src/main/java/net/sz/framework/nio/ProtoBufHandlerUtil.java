package net.sz.framework.nio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.sz.framework.szlog.SzLogger;

/**
 * 插件，生产handler程序
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ProtoBufHandlerUtil {

    private static final SzLogger log = SzLogger.getLogger();
    private static final String[] DEFAULT_INCLUDES = new String[]{"java"};
    private static final String packagePatten = "package ";
    private String savePath;
    private String messagePatten;
    private String messageYz;

//    static {
//        CreateType[] values = CreateType.values();
//        String str = "";
//        for (int i = 0; i < values.length; i++) {
//            str += values[i].getMsg();
//            if (i < values.length - 1) {
//                str += "|";
//            }
//        }
//        messagePatten = "(.*)public static final class [" + str + "](.*)Message extends(.*)";
//    }
    /**
     *
     * @param pathString
     * @param regex GL|SL|LG
     * @deprecated
     */
    @Deprecated
    public static void createHandler(String pathString, String regex) {
        try {
            String userPath = System.getProperty("user.dir");
            File directory = new File(userPath + pathString);//设定为当前文件夹
            String protoPath = directory.getCanonicalPath();//获取标准的路径
            log.error(protoPath);
            ProtoBufHandlerUtil handler = new ProtoBufHandlerUtil(
                    protoPath + File.separator + "src" + File.separator + "main" + File.separator + "java",
                    userPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator,
                    "com" + File.separator + "game" + File.separator + "proto" + File.separator + "handler", regex);
            handler.execute();
        } catch (Throwable e) {
            log.error("", e);
        }
        System.exit(0);
    }

    /**
     *
     * @param pathString
     * @param regex GL|SL|LG
     */
    public static void createScriptHandler(String pathString, String regex) {
        try {
            String userPath = System.getProperty("user.dir");
            String replace = userPath.replace("-scripts", "").replace(File.separator, "=");
            String[] split = replace.split("=");
            String p = split[split.length - 1];
            p = p.replace(".", File.separator);

            File directory = new File(userPath + pathString);//设定为当前文件夹
            String protoPath = directory.getCanonicalPath();//获取标准的路径
            log.error(protoPath);
            ProtoBufHandlerUtil handler = new ProtoBufHandlerUtil(
                    protoPath + File.separator + "src" + File.separator + "main" + File.separator + "java",
                    userPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator,
                    p + File.separator + "proto" + File.separator + "handler", regex);
            handler.execute();
        } catch (Exception e) {
            log.error("", e);
        }
        System.exit(0);
    }

    /**
     * 项目根目录
     *
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    private File outFile;
    /**
     * 额外参数，由于没有配置expression，所以只能过通过pom.xml plugin->configuration配置获得
     *
     * @parameter
     */
    private String[] includes;

    ProtoBufHandlerUtil(String readPath, String basedirPath, String savePath, String regex) {
        this.savePath = savePath;
        this.basedir = new File(readPath);
        this.outFile = new File(basedirPath + savePath);
        /*匹配需要验证的消息字符*/
        this.messagePatten = "(.*)public static final class (" + regex + ").*Message extends(.*)";
        /*匹配消息验证开头字符*/
        this.messageYz = "^(" + regex + ").*";
    }

    void execute() {
        if (includes == null) {
            includes = DEFAULT_INCLUDES;
        }

        List<File> rfFiles = new ArrayList<>();
        getRfFiles(rfFiles, basedir);
        for (File file : rfFiles) {
            try {
                String toString = getFilecounts(file).toString();
                log.info(toString);
            } catch (FileNotFoundException ex) {
                log.error("找不到文件1", ex);
            } catch (IOException ex) {
                log.error("找不到文件2", ex);
            }
        }
    }

    private void getRfFiles(List<File> files, File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                getRfFiles(files, f);
            }
        } else {
            for (String include : includes) {
                if (file.getName().endsWith(include)) {
                    files.add(file);
                    break;
                }
            }

        }
    }

    private FileCountsInfo getFilecounts(File file) throws FileNotFoundException, IOException {
        String packageName = "";
        HashSet<String> classNames = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        int count = 0;
        try {
            while (br.ready()) {
                String readLine = br.readLine();
                if (packageName.equals("") && readLine.startsWith(packagePatten)) {
                    packageName = readLine.replace(packagePatten, "").replace(";", "");// + ".handler";
                    if (log.isInfoEnabled()) {
                        log.info("packageName:: " + packageName);
                    }
                } else if (readLine.matches(messagePatten)) {
                    int indexOf0 = readLine.indexOf("class ") + 6;
                    int indexOf1 = readLine.indexOf(" extends");
                    String className = readLine.substring(indexOf0, indexOf1);
                    if (className.matches(messageYz)) {
                        classNames.add(className);
                    }
                }
                count++;
            }
        } catch (IOException e) {
            log.error("getFilecounts", e);
        } finally {
            br.close();
        }

        if (!classNames.isEmpty()) {
            String protoName = file.getName().replace(".java", "");
            String module = protoName;
            module = module.replace("Message", "");
            module = module.toLowerCase();

            for (String className : classNames) {
                String fileName = className.replace("Message", "Handler.java"); // UserVersionMessageHandler.java
                // String filePath = sourceDirectory + "\\" + packageName.replace(".", "\\") + "\\handler\\" + module+ "\\"+ fileName; // E:\game\game\game-plugin\src\main\java\com\game\proto\handler\UserVersionMessageHandler.java
                String filePath = outFile + "\\" + module + "\\" + fileName;
                if (log.isInfoEnabled()) {
                    log.info("fileName " + fileName);
                    log.info("filePath " + filePath);
                }
                File newFile = new File(filePath);
                if (!newFile.exists()) {
                    createFile(newFile);
                    if (log.isInfoEnabled()) {
                        log.info("创建成功");
                    }

                    {
                        // 写入类容
                        String reqClassName = className.replace("Message", "");
                        String resClassName = null;
                        if (className.startsWith("Req")) {
                            resClassName = reqClassName.replaceFirst("Req", "Res");
                        }
                        if (log.isInfoEnabled()) {
                            log.info("================");
                            log.info(newFile.toString());
                            log.info(packageName); // com.game.loginsr.proto
                            log.info(protoName); // LoginMessage
                            log.info(reqClassName); // ReqCreateSelectUserCmd
                            log.info(resClassName); // ResCreateSelectUserCmd
                            log.info("================");
                        }
                        genCodeTemplate(newFile, packageName, protoName, reqClassName, resClassName, module);
                    }
                } else if (log.isInfoEnabled()) {
                    log.info("已经存在");
                }
            }
        }

        return new FileCountsInfo(file, count);
    }

    static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    static boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            makeDir(file.getParentFile());
        }
        return file.createNewFile();
    }

    /**
     *
     * @param packageName "com.game.loginsr.proto"
     * @param protoName "LoginMessage"
     * @param reqClassName "ReqTokenLogin"
     * @param resClassName "ResTokenLogin"
     * @param module "login"
     */
    private void genCodeTemplate(File file, String packageName, String protoName, String reqClassName, String resClassName, String module) {
        String importProto = packageName + "." + protoName;
        String reqMessageName = protoName + "." + reqClassName;
        String resMessageName = protoName + "." + resClassName;

        StringBuilder code = new StringBuilder();
        String packagename = savePath.replaceAll("[\\\\|/]", ".") + "." + module;
        code.append("package ").append(packagename).append(";").append("\n");
        code.append("\n");
        code.append("import net.sz.framework.nio.tcp.NettyCoder;").append("\n");
        code.append("import net.sz.framework.nio.tcp.NettyTcpHandler;").append("\n");
        code.append("import net.sz.framework.scripts.IInitBaseScript;").append("\n");
        code.append("import net.sz.framework.szlog.SzLogger;").append("\n");
        code.append("import ").append(importProto).append(";").append("\n");
        code.append("").append("\n");
        code.append("").append("\n");
        code.append("/**").append("\n");
        code.append(" *").append("\n");
        code.append(" *<br>").append("\n");
        code.append(" * author 失足程序员<br>").append("\n");
        code.append(" * mail 492794628@qq.com<br>").append("\n");
        code.append(" * phone 13882122019<br>").append("\n");
        code.append(" */").append("\n");
        code.append("public final class ").append(reqClassName).append("Handler extends NettyTcpHandler implements IInitBaseScript {").append("\n");
        code.append("    \n");
        code.append("    private static final SzLogger log = SzLogger.getLogger();\n");
        code.append("    private static final long serialVersionUID = 1L;\n");
        code.append("    ").append("\n");
         code.append("    public ").append(reqClassName).append("Handler() {").append("\n");
        code.append("    ").append("\n");
        code.append("    }").append("\n");
        code.append("    ").append("\n");
        code.append("    @Override").append("\n");
        code.append("    public void _init() {").append("\n");
        code.append("        //把消息自动注册到消息中心").append("\n");
        code.append("        NettyCoder.register(").append("\n");
        code.append("                NettyCoder.getMessageId(").append(reqClassName).append("Message.newBuilder()),/*消息消息id*/").append("\n");
        code.append("                xx,/*线程id*/").append("\n");
        code.append("                ").append("this, /*消息执行的handler*/").append("\n");
        code.append("                ").append(reqClassName).append("Message.newBuilder(),/*消息体*/").append("\n");
        code.append("                ").append("null /*消息队列*/").append("\n");
        code.append("        ").append(");").append("\n");
        code.append("    }").append("\n");
        code.append("    ").append("\n");
        code.append("    @Override").append("\n");
        code.append("    public void run() {").append("\n");
        code.append("        // TODO 处理").append(reqMessageName).append("消息").append("\n");
        code.append("        ").append(reqMessageName).append("Message reqMessage = (").append(reqMessageName).append("Message) getMessage();").append("\n");
        if (resClassName != null) {
            code.append("        //").append(resMessageName).append(".Builder builder4Res = ").append(resMessageName).append(".newBuilder();").append("\n");
        }
        code.append("\n");
        code.append("    }").append("\n");
        code.append("}").append("\n");

        if (file.canWrite()) {
            try {
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
                osw.write(code.toString());
                osw.flush();
            } catch (IOException ex) {
                log.error("写入代码模版到" + file.getName() + "失败!", ex);

            }
        }
    }
}

class FileCountsInfo {

    private File file;
    private int count;

    public FileCountsInfo(File file, int count) {
        this.file = file;
        this.count = count;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return file.toString() + " count: " + count;
    }
}
