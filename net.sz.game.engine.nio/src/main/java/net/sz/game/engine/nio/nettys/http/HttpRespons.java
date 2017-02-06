package net.sz.game.engine.nio.nettys.http;

import java.util.ArrayList;

/**
 * 封装的HTTP响应对象
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpRespons {

    /**
     * HTTP请求URL的地址
     */
    String urlString;
    /**
     * HTTP默认响应的端口
     */
    int defaultPort;
    /**
     * Gets the file name of this URL 获取URL的名字
     */
    String file;
    /**
     * URL主机名称
     */
    String host;
    /**
     * URL的地址部分
     */
    String path;
    /**
     * URL地址响应端口
     */
    int port;
    /**
     * URL的协议
     */
    String protocol;
    /**
     * URL的查询部分
     */
    String query;
    /**
     * URL的anchor (also known as the "reference") 部分
     */
    String ref;
    /**
     * URL的用户信息部分
     */
    String userInfo;
    /**
     * URL连接的编码方式
     */
    String contentEncoding;
    /**
     * URL的响应内容
     */
    String content;
    /**
     * URL的响应类型
     */
    String contentType;
    /**
     * HTTP请求的返回状态码，正常为200
     */
    int code;
    /**
     * HTTP请求的返回消息提示
     */
    String message;
    /**
     * HTTP请求的类型
     */
    String method;
    /**
     * HTTP连接的超时时间
     */
    int connectTimeout;
    /**
     * HTTP连接的读取超时时间
     */
    int readTimeout;

    ArrayList<String> contentCollection;

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<String> getContentCollection() {
        return contentCollection;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getMethod() {
        return method;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getUrlString() {
        return urlString;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public String getFile() {
        return file;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getQuery() {
        return query;
    }

    public String getRef() {
        return ref;
    }

    public String getUserInfo() {
        return userInfo;
    }
}
