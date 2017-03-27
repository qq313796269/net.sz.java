package net.sz.game.engine.nio.nettys.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.HashMap;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class NioHttpRequest {

    private static SzLogger log = SzLogger.getLogger();

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
    //对象
    private ChannelHandlerContext session;
    //完整的请求
    private FullHttpRequest request;
    //post或者get完整参数
    private HashMap<String, String> params;

    private String url;
    private String ip;
    //直接post或者content参数
    private String httpBody;
    protected boolean isRespons = false;
    StringBuilder builder = new StringBuilder();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void addContent(String msg) {
        builder.append(msg);
    }

    public void addContentLine(String msg) {
        builder.append(msg).append("\r\n");
    }

    /**
     * HttpContentType.All 回复 http 请求
     */
    public void respons() {
        this.respons(HttpContentType.Html);
    }

    /**
     * 回复http请求
     *
     * @param contentType
     */
    public void respons(HttpContentType contentType) {
        try {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(builder.toString().getBytes("utf-8")));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
            response.headers().set(HttpHeaders.Names.CONTENT_ENCODING, "utf-8");
            response.headers().set(HttpHeaders.Names.ACCEPT_CHARSET, "utf-8");
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            session.writeAndFlush(response);
            session.close();
            isRespons = true;
        } catch (Throwable ex) {
            log.error("HttpRequestMessage.respons 失败", ex);
        }
    }

    /**
     * 将会返回404错误
     */
    public void close() {
        close(null);
    }

    /**
     * 返回关闭情况
     */
    public void close(String msg) {
        NettyHttpServer.close(session, msg);
    }

    public ChannelHandlerContext getSession() {
        return session;
    }

    public void setSession(ChannelHandlerContext session) {
        this.session = session;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public String getParam(String key) {
        return getParams().get(key);
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

    public String getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(String httpBody) {
        this.httpBody = httpBody;
    }

}
