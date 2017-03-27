package net.sz.game.engine.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.sz.game.engine.szlog.SzLogger;

public class PackageUtil {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {
        //String packageName = "net.sz.engine.nio.netty";
        String packageName = "net.sz.game.engine-1.0-SNAPSHOT.jar";
        // List<String> classNames = getClassName(packageName);
        ArrayList<Class<?>> classNames = getClazzs(packageName);
        if (classNames != null) {
            for (Class<?> className : classNames) {
                log.error(className.getName());
            }
        }
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名
     * @return 类的完整名称
     */
    public static ArrayList<Class<?>> getClazzs(String packageName) {
        return getClazzs(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName 包名
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    public static ArrayList<Class<?>> getClazzs(String packageName, boolean childPackage) {
        ArrayList<Class<?>> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath;
        if (packageName.endsWith(".jar") || packageName.endsWith(".war")) {
            packagePath = packageName;
        } else {
            packagePath = packageName.replace(".", "/");
        }
        try {
            Enumeration<URL> resources = loader.getResources(packagePath);
            if (resources != null) {
                URL url = null;
                while (resources.hasMoreElements()) {
                    url = resources.nextElement();
                    if (url != null) {

                        String type = url.getProtocol();
//                        log.error(type + " _ " + url.toString());
                        if (type.equals("file")) {
                            fileNames = getClassByFile(url.getPath(), childPackage);
                        } else if (type.equals("jar")) {
                            fileNames = getClassByJar(url.getPath(), childPackage);
                        }
                    } else {
                        fileNames = getClassByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
                    }
                }
            }
        } catch (Throwable e) {
            log.error("", e);
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath 文件路径
     * @param className 类名集合
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static ArrayList<Class<?>> getClassByFile(String filePath, boolean childPackage) throws ClassNotFoundException {
        ArrayList<Class<?>> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassByFile(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    Class<?> clazz = Class.forName(childFilePath);
//                    Class<?> clazz = myClassLoader.getClazz(childFilePath, childFile);
                    if (clazz != null) {
                        myClassName.add(clazz);
                    }
                }
            }
        }

        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath jar文件路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static ArrayList<Class<?>> getClassByJar(String jarPath, boolean childPackage) {
        ArrayList<Class<?>> myClassName = new ArrayList<>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
//                            log.error(entryName);
//                            Class<?> clazz = myClassLoader.getClazz(entryName, jarEntry.getExtra());
                            Class<?> clazz = Class.forName(entryName);
                            if (clazz != null) {
                                myClassName.add(clazz);
                            } else {
                                log.error("加载失败：" + entryName);
                            }
                        }
                    } else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
//                            Class<?> clazz = myClassLoader.getClazz(entryName, jarEntry.getExtra());
                            Class<?> clazz = Class.forName(entryName);
                            if (clazz != null) {
                                myClassName.add(clazz);
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            log.error("", e);
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls URL集合
     * @param packagePath 包路径
     * @param childPackage 是否遍历子包
     * @return 类的完整名称
     */
    private static ArrayList<Class<?>> getClassByJars(URL[] urls, String packagePath, boolean childPackage) {
        ArrayList<Class<?>> myClassName = new ArrayList<>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }
}
