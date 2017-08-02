package net.sz.framework.sznio.http;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.NioSession;
import net.sz.framework.utils.GlobalUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NioHttpRequest extends NioSession implements java.lang.Comparable {

    private static final SzLogger log = SzLogger.getLogger();

    @Override
    public int compareTo(Object o) {
        if (o instanceof NioHttpRequest) {
            if (((NioHttpRequest) o).id == this.id) {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NioHttpRequest) {
            if (((NioHttpRequest) o).id == this.id) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 输出内容格式
     */
    public enum HttpContentType {

        All("*/*; charset=UTF-8"),
        Text("text/plain; charset=UTF-8"),
        Json("applicaton/x-json; charset=UTF-8"),
        Html("text/html; charset=UTF-8"),
        Xml("text/xml; charset=UTF-8"),
        Javascript("application/javascript; charset=UTF-8");
        String value;

        HttpContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }
    //post或者get完整参数
    private HashMap<String, String> heads = new HashMap<>();
    //post或者get完整参数
    private HashMap<String, String> params = new HashMap<>();
    //直接post或者content参数
    private String url;
    private String method;
    private String httpcontent = null;
    private StringBuilder contentBuilder = new StringBuilder();

    public NioHttpRequest(SocketChannel channel) {
        this.id = GlobalUtil.getUUIDToLong();
        this.channel = channel;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, String> getHeads() {
        if (heads == null) {
            heads = new HashMap<>();
        }
        return heads;
    }

    public void setHeads(HashMap<String, String> heads) {
        this.heads = heads;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpcontent() {
        return httpcontent;
    }

    public void setHttpcontent(String httpcontent) {
        this.httpcontent = httpcontent;
    }

    /**
     * 获取参数
     *
     * @param key
     * @return
     */
    public String getParam(String key) {
        return params.get(key);
    }

    /**
     *
     * @param content
     */
    public void addContent(Object content) {
        contentBuilder.append(content);
    }

    /**
     *
     * @param content
     */
    public void addContentLn(Object content) {
        contentBuilder.append(content).append("\r\n");
    }

    public void clear() {
        contentBuilder.delete(0, contentBuilder.length());
    }

    public void respons(HttpContentType contentType) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("HTTP/1.0 200 OK").append("\n");
            builder.append("Content-Type: ").append(contentType.getValue()).append("\n");
            byte[] bytes = contentBuilder.toString().getBytes("utf-8");
            builder.append("Content-Length: ").append(bytes.length).append("\n");
            builder.append("Connection: close").append("\n");
            builder.append("").append("\n");
            builder.append(bytes);
            ByteBuffer wrap = ByteBuffer.wrap(builder.toString().getBytes("utf-8"));
            this.send(wrap);
//            log.debug(this.id + " ：发送消息长度 " + wrap.array().length + " ：执行耗时：" + (TimeUtil.currentTimeMillis() - createTime));
        } catch (Exception ex) {
            log.error("HttpRequestMessage.respons失败", ex);
        }
        this.close();
    }

    /**
     * 404
     */
    public void responsFailure() {
        clear();
        addContent("404 not find bind url: ");
        addContent(ip);
        addContent(":");
        addContent(port);
        addContent("/");
        addContent(url);
        respons(NioHttpRequest.HttpContentType.Text);
    }

    @Override
    public void close() {
        try {
            super.close();
            this.heads = null;
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return "NioHttpRequest{" + "id=" + id + ", available=" + available + ", ip=" + ip + ", port=" + port + "url=" + url + ", method=" + method + ", httpcontent=" + httpcontent + '}';
    }

}
