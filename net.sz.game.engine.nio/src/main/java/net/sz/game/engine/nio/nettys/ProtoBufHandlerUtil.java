package net.sz.game.engine.nio.nettys;

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

import net.sz.game.engine.szlog.SzLogger;

/**
 * 插件，生产handler程序
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ProtoBufHandlerUtil {

    private static SzLogger log = SzLogger.getLogger();
    private static final String[] DEFAULT_INCLUDES = new String[]{"java"};
    private String savePath;
    private CreateType[] CreateTypes = null;
    private static final String packagePatten = "package ";
    private static final String messagePatten;

    static {
        CreateType[] values = CreateType.values();
        String str = "";
        for (int i = 0; i < values.length; i++) {
            str += values[i].getMsg();
            if (i < values.length - 1) {
                str += "|";
            }
        }
        messagePatten = "(.*)public static final class [" + str + "](.*)Message extends(.*)";
    }

    public enum CreateType {
        /**
         * 登录服务器 11, "GL"
         */
        LSReq(11, "GL"),
        /**
         * 登录服务器 12, "LG"
         */
        LSRes(12, "LG"),
        /**
         * 游戏服务器 21, "Req"
         */
        GSReq(21, "Req"),
        /**
         * 游戏服务器 22, "Res"
         */
        GSRes(22, "Res"),
        /**
         * 数据中心 31, "GD"
         */
        DSReq(31, "GD"),
        /**
         * 数据中心 32, "DG"
         */
        DSRes(32, "DG"),
        /**
         * 数据中心 31, "DL"
         */
        DLReq(33, "DL"),
        /**
         * 数据中心 32, "LD"
         */
        DLRes(34, "LD"),
        /**
         * 充值服务器发送消息 41, "GB"
         */
        BGReq(41, "GB"),
        /**
         * 充值服务器返回消息 42, "BG"
         */
        BGRes(42, "BG"),;
        private final int index;
        private final String msg;

        CreateType(int index, String msg) {
            this.index = index;
            this.msg = msg;
        }

        public int getIndex() {
            return index;
        }

        public String getMsg() {
            return msg;
        }

    }

    @Deprecated
    public static void createHandler(String pathString, CreateType... create) {
        try {
            String userPath = System.getProperty("user.dir");
            File directory = new File(userPath + pathString);//设定为当前文件夹
            String protoPath = directory.getCanonicalPath();//获取标准的路径
            log.error(protoPath);
            ProtoBufHandlerUtil handler = new ProtoBufHandlerUtil(
                    protoPath + File.separator + "src" + File.separator + "main" + File.separator + "java",
                    userPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator,
                    "com" + File.separator + "game" + File.separator + "proto" + File.separator + "handler", create);
            handler.execute();
        } catch (Throwable e) {
            log.error("", e);
        }
        System.exit(0);
    }

    /**
     *
     * @param pathString
     * @param create
     */
    public static void createScriptHandler(String pathString, CreateType... create) {
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
                    p + File.separator + "proto" + File.separator + "handler", create);
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

    ProtoBufHandlerUtil(String readPath, String basedirPath, String savePath) {
        this.savePath = savePath;
        this.basedir = new File(readPath);
        this.outFile = new File(basedirPath + savePath);
    }

    ProtoBufHandlerUtil(String readPath, String basedirPath, String savePath, CreateType... create) {
        this.savePath = savePath;
        this.basedir = new File(readPath);
        this.outFile = new File(basedirPath + savePath);
        this.CreateTypes = create;
    }

    void execute() {
        if (includes == null) {
            includes = DEFAULT_INCLUDES;
        }

        List<File> rfFiles = new ArrayList<>();
        getRfFiles(rfFiles, basedir);
        for (File file : rfFiles) {
            try {
                if (log.isInfoEnabled()) {
                    log.info(getFilecounts(file).toString());
                }
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
                    if (CreateTypes != null) {
                        for (CreateType CreateType1 : CreateTypes) {
                            if (className.startsWith(CreateType1.getMsg())) {
                                classNames.add(className);
                            }
                        }
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
                        // String packageName ;//"com.game.loginsr.proto"
                        // String protoName ;//"LoginMessage"
                        String reqClassName = className.replace("Message", "");//"ReqTokenLoginMessage"
                        String resClassName = null;//"ResTokenLoginMessage"
                        if (className.startsWith("Req")) {
                            resClassName = className;
                            resClassName = resClassName.replaceFirst("Req", "Res");
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
        code.append("import net.sz.game.engine.nio.nettys.tcp.NettyTcpHandler;").append("\n");
        code.append("import net.sz.game.engine.scripts.IInitBaseScript;").append("\n");
        code.append("import ").append(importProto).append(";").append("\n");
        code.append("").append("\n");
        code.append("import net.sz.game.engine.szlog.SzLogger;").append("\n");
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
        code.append("    private static SzLogger log = SzLogger.getLogger();\n");
        code.append("    ").append("\n");
        code.append("    @Override").append("\n");
        code.append("    public void _init() {").append("\n");
        code.append("    net.sz.game.engine.nio.nettys.NettyPool.getInstance().register(").append("\n");
        code.append("           ").append(importProto).append(".xx,//消息消息id").append("\n");
        code.append("           xx,//线程id").append("\n");
        code.append("           ").append("this, //消息执行的handler").append("\n");
        code.append("           ").append(importProto).append(".").append(reqClassName).append("Message.newBuilder(),//消息体").append("\n");
        code.append("           ").append("0 // mapThreadQueue 协议请求地图服务器中的具体线程,默认情况下,每个地图服务器都有切只有一个Main线程.").append("\n");
        code.append("           ").append("//一般情况下玩家在地图的请求,都是Main线程处理的,然而某些地图,可能会使用多个线程来处理大模块的功能.").append("\n");
        code.append("           ").append(");").append("\n");
        code.append("    }").append("\n");
        code.append("    public ").append(reqClassName).append("Handler() {").append("\n");
        code.append("    ").append("\n");
        code.append("    }").append("\n");
        code.append("    ").append("\n");
        code.append("    @Override").append("\n");
        code.append("    public void run() {").append("\n");
        code.append("        // TODO 处理").append(reqMessageName).append("消息").append("\n");
        code.append("        ").append(reqMessageName).append("Message reqMessage = (").append(reqMessageName).append("Message) getMessage();").append("\n");
        if (resClassName != null) {
            code.append("        //").append(resMessageName).append(".Builder builder4Res = ").append(resMessageName).append(".newBuilder();").append("\n");
        }
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
