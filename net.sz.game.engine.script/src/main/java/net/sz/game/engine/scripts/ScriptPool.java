package net.sz.game.engine.scripts;

import io.netty.util.internal.StringUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import net.sz.game.engine.szlog.SzLogger;

/**
 * 脚本加载器
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class ScriptPool {

    private static SzLogger log = SzLogger.getLogger();

    public ScriptPool() {
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception {
//        String property = System.getProperty("user.dir");
//        ScriptPool.getInstance().loadJava("F:\\javatest\\tttt\\", property + "\\target\\classes\\out\\");
//        Iterator<IBaseScript> scripts = ScriptPool.getInstance().getEvts(IBaseScript.class);
//        while (scripts.hasNext()) {
//            IBaseScript next = scripts.next();
//        }
    }

    //源文件夹
    private String sourceDir;
    //输出文件夹
    private String outDir;
    //附加的jar包地址
    private String jarsDir = "F:\\javatest\\mavenproject1\\target\\";

    ConcurrentHashMap<String, ConcurrentHashMap<String, IBaseScript>> scriptInstances = new ConcurrentHashMap<>(0);
    ConcurrentHashMap<String, ConcurrentHashMap<String, IBaseScript>> tmpScriptInstances = new ConcurrentHashMap<>(0);

    /**
     * 获取当前根目录
     *
     * @return
     */
    public String getUserDir() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        return p.replace(".", File.separator);
    }

    public void setSource(String source) throws Exception {
        this.setSource(source, source + File.separatorChar + "out");
    }

    public void setSource(String source, String out) throws Exception {
        if (StringUtil.isNullOrEmpty(source)) {
            log.error("指定 输入 输出 目录为空");
            throw new Exception("目录为空");
        }
        this.sourceDir = source;
        this.outDir = out;
    }

    /**
     *
     * @param name
     * @return
     */
    public ArrayList<IBaseScript> getEvts(String name) {
        ConcurrentHashMap<String, IBaseScript> scripts = ScriptPool.this.scriptInstances.get(name);
        if (scripts != null) {
            return new ArrayList<>(scripts.values());
        }
        return new ArrayList<>();
    }

    /**
     *
     * @param <T>
     * @param t
     * @return
     */
    public <T> ArrayList<T> getEvts(Class<T> t) {
        return (ArrayList<T>) getEvts(t.getName());
    }

    /**
     * 获取对应的脚本实例
     *
     * @param <T>
     * @param t
     * @return
     */
    public <T> Iterator<T> getIterator(Class<T> t) {
        return new MyIterator<>(t.getName());
    }

    public void deleteDirectory() {
        MyFileMonitor.deleteDirectory(this.outDir);
    }

    //<editor-fold desc="public final void Compile()">
    /**
     * 编译 java 源文件
     *
     * @return
     */
    ArrayList<String> compile() {
        deleteDirectory();
        return this.compile("");
    }
    //</editor-fold>

    //<editor-fold desc="public final void Compile(String... fileNames)">
    /**
     * 编译文件
     *
     * @param fileNames 文件列表
     * @return
     */
    ArrayList<String> compile(String... fileNames) {
        ArrayList<String> strs = new ArrayList<>();
        if (null != fileNames) {
            List<File> sourceFileList = new ArrayList<>(0);
            for (String fileName : fileNames) {
                //得到filePath目录下的所有java源文件
                File sourceFile = new File(this.sourceDir + fileName);
                getFiles(sourceFile, sourceFileList, ".java");
            }
            DiagnosticCollector<JavaFileObject> oDiagnosticCollector = new DiagnosticCollector<>();
            // 获取编译器实例
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // 获取标准文件管理器实例
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(oDiagnosticCollector, Locale.CHINESE, Charset.forName("utf-8"));
            try {
                // 没有java文件，直接返回
                if (sourceFileList.isEmpty()) {
                    log.error(this.sourceDir + "目录下查找不到任何java文件");
//                    strs.add(this.sourceDir + "目录下查找不到任何java文件");
                    return strs;
                }
//                for (File file : sourceFileList) {
//                    String replaceAll = file.getPath().substring(sourceDir.length() - 1);
//                    log.info("找到脚本文件：" + replaceAll);
//                }
                if (log.isInfoEnabled()) {
                    log.info("找到脚本并且需要编译的文件共：" + sourceFileList.size());
                }
                //创建输出目录，如果不存在的话
                new java.io.File(this.outDir).mkdirs();
                // 获取要编译的编译单元
                Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
                /**
                 * 编译选项，在编译java文件时，编译程序会自动的去寻找java文件引用的其他的java源文件或者class。
                 * -sourcepath选项就是定义java源文件的查找目录， -classpath选项就是定义class文件的查找目录。
                 */
                ArrayList<String> options = new ArrayList<>(0);
                options.add("-g");
                options.add("-source");
                options.add("1.8");
//                    options.add("-Xlint");
//                    options.add("unchecked");
                options.add("-encoding");
                options.add("UTF-8");
                options.add("-sourcepath");
                options.add(this.sourceDir); //指定文件目录
                options.add("-d");
                options.add(this.outDir); //指定输出目录

                ArrayList<File> jarsList = new ArrayList<>();
                getFiles(this.jarsDir, jarsList, ".jar");
                String jarString = "";
                for (File jar : jarsList) {
                    jarString += jar.getPath() + ";";
                }
                if (!StringUtil.isNullOrEmpty(jarString)) {
                    options.add("-classpath");
                    options.add(jarString);//指定附加的jar包
                }

                JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, oDiagnosticCollector, options, null, compilationUnits);
                // 运行编译任务
                Boolean call = compilationTask.call();
                if (!call) {
                    for (Diagnostic oDiagnostic : oDiagnosticCollector.getDiagnostics()) {
                        strs.add(((JavaFileObject) (oDiagnostic.getSource())).getName() + " line:" + oDiagnostic.getLineNumber());
                        log.error("加载脚本错误：" + ((JavaFileObject) (oDiagnostic.getSource())).getName() + " line:" + oDiagnostic.getLineNumber());
                    }
                }
            } catch (Throwable ex) {
                strs.add(this.sourceDir + "错误：" + ex);
                log.error("加载脚本错误：", ex);
            } finally {
                try {
                    fileManager.close();
                } catch (Throwable ex) {
                }
            }
        } else {
            log.error(this.sourceDir + "目录下查找不到任何java文件");
            strs.add(this.sourceDir + "目录下查找不到任何java文件");
        }
        return strs;
    }
    //</editor-fold>

    //<editor-fold desc="查找该目录下的所有的 java 文件 public void getFiles(File sourceFile, List<File> sourceFileList, String endName)">
    /**
     *
     * @param source
     * @param sourceFileList
     * @param endName
     */
    private void getFiles(String source, List<File> sourceFileList, final String endName) {
        File sFile = new File(source);
        getFiles(sFile, sourceFileList, endName);
    }

    /**
     * 查找该目录下的所有的 java 文件
     *
     * @param sourceFile ,单文件或者目录
     * @param sourceFileList 返回目录所包含的所有文件包括子目录
     * @param endName
     */
    private void getFiles(File sourceFile, List<File> sourceFileList, final String endName) {
        if (sourceFile.exists() && sourceFileList != null) {// 文件或者目录必须存在
            if (sourceFile.isDirectory()) {// 若file对象为目录
                // 得到该目录下以.java结尾的文件或者目录
                File[] childrenFiles = sourceFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            return true;
                        } else {
                            String name = pathname.getName();
                            return name.endsWith(endName);
                        }
                    }
                });
                // 递归调用
                for (File childFile : childrenFiles) {
                    getFiles(childFile, sourceFileList, endName);
                }
            } else {// 若file对象为文件
                String replaceAll = sourceFile.getPath().substring(sourceDir.length() - 1);
                log.error("找到脚本文件：" + replaceAll);
                sourceFileList.add(sourceFile);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="加载脚本 public void loadJava()">
    /**
     * 加载脚本文件，并且会删除之前的所有脚本
     *
     * @return
     */
    public ArrayList<String> loadJava() {
        synchronized (this) {
            ArrayList<String> compile = this.compile();
            if (compile == null || compile.isEmpty()) {
                List<File> sourceFileList = new ArrayList<>(0);
                //得到filePath目录下的所有java源文件
                getFiles(this.outDir, sourceFileList, ".class");
                String[] fileNames = new String[sourceFileList.size()];
                for (int i = 0; i < sourceFileList.size(); i++) {
                    fileNames[i] = sourceFileList.get(i).getPath();
                }
                tmpScriptInstances = new ConcurrentHashMap<>();
                loadClass(compile, fileNames);
                if (tmpScriptInstances.size() > 0) {
                    scriptInstances.clear();
                    scriptInstances = tmpScriptInstances;
                }
            }
            return compile;
        }
    }

    /**
     * 加载脚本文件，更新替换脚本
     *
     * @param source 加载的文件或者目录
     * @return
     */
    public ArrayList<String> loadJava(String... source) {
        synchronized (this) {
            ArrayList<String> compile;
            if (source == null || source.length <= 0) {
                compile = this.compile();
            } else {
                compile = this.compile(source);
            }

            if (compile == null || compile.isEmpty()) {
                List<File> sourceFileList = new ArrayList<>(0);
                //得到filePath目录下的所有java源文件
                for (String string : source) {
                    getFiles(this.outDir + string, sourceFileList, ".class");
                }
                String[] fileNames = new String[sourceFileList.size()];
                for (int i = 0; i < sourceFileList.size(); i++) {
                    fileNames[i] = sourceFileList.get(i).getPath();
                }
                tmpScriptInstances = new ConcurrentHashMap<>();
                loadClass(compile, fileNames);
                if (tmpScriptInstances.size() > 0) {
                    for (Map.Entry<String, ConcurrentHashMap<String, IBaseScript>> entry : tmpScriptInstances.entrySet()) {
                        String key = entry.getKey();
                        ConcurrentHashMap<String, IBaseScript> value = entry.getValue();
                        scriptInstances.put(key, value);
                    }
                }
            }
            return compile;
        }
    }

    /**
     * 加载脚本文件
     *
     * @param names
     */
    void loadClass(ArrayList<String> compile, String... names) {
        try {
            ScriptClassLoader loader = new ScriptClassLoader(compile);
            for (String name : names) {
                String tmpName = name.replace(outDir, "").replace(".class", "").replace(File.separatorChar, '.');
                loader.loadClass(tmpName);
            }
        } catch (Throwable e) {
            compile.add("  " + e.toString());
        }
    }
    //</editor-fold>

    //<editor-fold desc="自定义迭代器 public class MyIterator<T> implements Iterator<T>">
    /**
     *
     * @param <T>
     */
    public class MyIterator<T> implements Iterator<T> {

        Iterator<IBaseScript> iterator = null;

        public MyIterator(String key) {
            ConcurrentHashMap<String, IBaseScript> scripts = ScriptPool.this.scriptInstances.get(key);
            if (scripts != null) {
                iterator = scripts.values().iterator();
            }
        }

        @Override
        public T next() {
            //忽略是否存在键的问题
            if (iterator == null) {
                return null;
            }
            return (T) iterator.next();
        }

        @Override
        public boolean hasNext() {
            //忽略是否存在键的问题
            if (iterator == null) {
                return false;
            }
            return iterator.hasNext();
        }

    }
    //</editor-fold>

    //<editor-fold desc="自定义文件加载器 class ScriptClassLoader extends ClassLoader">
    class ScriptClassLoader extends ClassLoader {

        ArrayList<String> msgList;

        public ScriptClassLoader(ArrayList<String> msgList) {
            this.msgList = msgList;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            Class<?> defineClass = null;
            defineClass = super.loadClass(name);
            return defineClass;
        }

        @Override
        protected Class<?> findClass(String name) {
            byte[] classData = getClassData(name);
            Class<?> defineClass = null;
            if (classData != null) {
                try {
                    defineClass = defineClass(name, classData, 0, classData.length);
                    if (defineClass.isInterface() || Modifier.isAbstract(defineClass.getModifiers())
                            || Modifier.isStatic(defineClass.getModifiers())
                            || Modifier.isTransient(defineClass.getModifiers())) {
                        return defineClass;
                    }
                    Object newInstance = null;
                    if (IInitBaseScript.class.isAssignableFrom(defineClass) || IBaseScript.class.isAssignableFrom(defineClass)) {
                        //读取加载的类的接口情况，是否实现了最基本的借口，如果不是，表示加载的本身自主类
                        newInstance = defineClass.newInstance();
                        if (newInstance != null) {
                            String nameString = defineClass.getName() + ", ";
                            Class<?>[] interfaces = defineClass.getInterfaces();
                            for (Class<?> aInterface : interfaces) {
                                //判断实例是否继承 IBaseScript
                                if (IBaseScript.class.isAssignableFrom(aInterface)) {
                                    nameString += aInterface.getName() + ", ";
                                    if (!tmpScriptInstances.containsKey(aInterface.getName())) {
                                        tmpScriptInstances.put(aInterface.getName(), new ConcurrentHashMap<>());
                                    }
                                    tmpScriptInstances.get(aInterface.getName()).put(defineClass.getName(), (IBaseScript) newInstance);
                                }
                            }
                            log.error("成功加载脚本：" + nameString);
                            if (newInstance instanceof IInitBaseScript) {
                                ((IInitBaseScript) newInstance)._init();
                            }
                        }
                    }
                } catch (Throwable ex) {
                    this.msgList.add(name + "  " + ex.toString());
                    if (log.isInfoEnabled()) {
                        log.info("加载脚本发生错误", ex);
                    }
                }
            }
            return defineClass;
        }

        private byte[] getClassData(String className) {
            String path = classNameToPath(className);
            try {
                File file = new File(path);
                if (file.exists()) {
                    InputStream ins = new FileInputStream(path);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bufferSize = 4096;
                    byte[] buffer = new byte[bufferSize];
                    int bytesNumRead = 0;
                    while ((bytesNumRead = ins.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesNumRead);
                    }
                    return baos.toByteArray();
                } else {
                    this.msgList.add(" 自定义脚本文件不存在： " + path);
                    log.error("自定义脚本文件不存在：" + path);
                }
            } catch (Throwable ex) {
                log.error(className, ex);
                this.msgList.add(className + "  " + ex.toString());
            }
            return null;
        }

        private String classNameToPath(String className) {
            //return className;
            return new File(outDir + File.separatorChar + className.replace('.', File.separatorChar) + ".class").getPath();
        }
    }
    //</editor-fold>
}
