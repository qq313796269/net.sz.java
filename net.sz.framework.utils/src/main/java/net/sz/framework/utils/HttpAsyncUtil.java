package net.sz.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.sz.framework.szlog.SzLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * 异步http请求
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class HttpAsyncUtil {

    private static final SzLogger log = SzLogger.getLogger();

    static CloseableHttpAsyncClient client = null;

    static final AsyncCallBack DEFAULT_CALL_BACK = new AsyncCallBack() {
    };

    /**
     * @param args
     */
    public static void main(String[] args) {
        HttpAsyncUtil.start();
        for (int i = 0; i < 1; i++) {
            HttpAsyncUtil.urlGetAsync("http://www.kuaidi100.com/?from=openv");
        }
    }

    static public void start() {
        if (client == null) {
            synchronized (DEFAULT_CALL_BACK) {
                if (client == null) {
                    HttpAsyncClientBuilder create = HttpAsyncClientBuilder.create();
                    create.setMaxConnTotal(1000);
                    create.setMaxConnPerRoute(1000);
                    client = create.build();
                    client.start();
                }
            }
        }
    }

    /**
     * 设置信任自定义的证书
     *
     * @param keyStorePath 密钥库路径
     * @param keyStorepass 密钥库密码
     * @return
     */
    static public SSLContext custom(String keyStorePath, String keyStorepass) {
        SSLContext sc = null;

        KeyStore trustStore = null;
        try (FileInputStream instream = new FileInputStream(new File(keyStorePath))) {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException e) {
            log.error("", e);
        }
        return sc;
    }

    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    static public SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 设置代理
     *
     * @param hostOrIP
     * @param port
     * @return
     */
    static public HttpAsyncClientBuilder proxy(String hostOrIP, int port) {
        // 依次是代理地址，代理端口号，协议类型
        HttpHost proxy = new HttpHost(hostOrIP, port, "http");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        return HttpAsyncClients.custom().setRoutePlanner(routePlanner);
    }

    /**
     * 发送请求
     *
     * @param url
     * @
     */
    static public void urlGetAsync(String url) {
        urlGetAsync(url, "utf-8", DEFAULT_CALL_BACK);
    }

    /**
     * 发送请求
     *
     * @param url
     * @param asyncCallBack
     * @
     */
    static public void urlGetAsync(String url, final AsyncCallBack asyncCallBack) {
        urlGetAsync(url, "utf-8", asyncCallBack);
    }

    /**
     *
     * @param url
     * @param encoding
     * @param asyncCallBack
     * @
     */
    static public void urlGetAsync(String url, final String encoding, final AsyncCallBack asyncCallBack) {
        HttpGet httpGet = new HttpGet(url);
        sendAsync(httpGet, encoding, asyncCallBack);
    }

    /**
     * 默认编码格式utf-8
     *
     * @param url
     * @param map
     * @
     */
    static public void urlPostAsync(String url, Map<String, String> map) {
        urlPostAsync(url, map, "utf-8", DEFAULT_CALL_BACK);
    }

    static public void urlPostAsync(String url, String paramString) {
        urlPostAsync(url, paramString, "utf-8", DEFAULT_CALL_BACK);
    }

    /**
     * 默认编码格式utf-8
     *
     * @param url
     * @param map
     * @param asyncCallBack
     * @
     */
    static public void urlPostAsync(String url, Map<String, String> map, final AsyncCallBack asyncCallBack) {
        urlPostAsync(url, map, "utf-8", asyncCallBack);
    }

    static public void urlPostAsync(String url, String paramString, final AsyncCallBack asyncCallBack) {
        urlPostAsync(url, paramString, "utf-8", asyncCallBack);
    }

    /**
     * 默认编码格式utf-8
     *
     * @param url
     * @param map
     * @param encoding
     * @
     */
    static public void urlPostAsync(String url, Map<String, String> map, final String encoding) {
        urlPostAsync(url, map, "utf-8", DEFAULT_CALL_BACK);
    }

    static public void urlPostAsync(String url, String paramString, final String encoding) {
        urlPostAsync(url, paramString, "utf-8", DEFAULT_CALL_BACK);
    }

    /**
     * 模拟请求
     *
     * @param url 资源地址
     * @param map 参数列表
     * @param encoding 编码
     * @param asyncCallBack 结果处理类
     */
    static public void urlPostAsync(String url, Map<String, String> map, final String encoding, final AsyncCallBack asyncCallBack) {

        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        //装填参数
        List<NameValuePair> nvps = new ArrayList<>();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
        } catch (Exception e) {
            if (asyncCallBack != null) {
                asyncCallBack.failed(e);
            } else {
                DEFAULT_CALL_BACK.failed(e);
            }
        }
        sendAsync(httpPost, encoding, asyncCallBack);
    }

    static public void urlPostAsync(String url, String paramString, final String encoding, final AsyncCallBack asyncCallBack) {

        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        //设置参数到请求对象中
        httpPost.setEntity(new StringEntity(paramString, encoding));
        sendAsync(httpPost, encoding, asyncCallBack);
    }

    static void sendAsync(HttpRequestBase request, final String encoding, final AsyncCallBack asyncCallBack) {

        start();

        if (log.isDebugEnabled()) {
            log.debug("请求地址：" + request.getURI().toString());
        }

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

//        //绕过证书验证，处理https请求
//        SSLContext sslcontext = createIgnoreVerifySSL();
//
//        // 设置协议http和https对应的处理socket链接工厂的对象
//        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
//                .register("http", NoopIOSessionStrategy.INSTANCE)
//                .register("https", new SSLIOSessionStrategy(sslcontext))
//                .build();
//        //配置io线程
//        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors()).build();
//        //设置连接池大小
//        ConnectingIOReactor ioReactor;
//        ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
//        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor, null, sessionStrategyRegistry, null);
//
//        //创建自定义的httpclient对象
//        final CloseableHttpAsyncClient client = proxy("127.0.0.1", 8087).setConnectionManager(connManager).build();
//      CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();

        /*执行请求操作，并拿到结果（异步）*/
        client.execute(request, new FutureCallback<HttpResponse>() {

            @Override
            public void failed(Exception ex) {
                if (asyncCallBack != null) {
                    asyncCallBack.failed(ex);
                } else {
                    DEFAULT_CALL_BACK.failed(ex);
                }
            }

            @Override
            public void completed(HttpResponse resp) {
                String body = "";
                try {
                    HttpEntity entity = resp.getEntity();
                    if (entity != null) {
                        entity.getContentLength();
                        try (InputStream instream = entity.getContent()) {
                            final StringBuilder sb = new StringBuilder();
                            final char[] tmp = new char[1024];
                            try (Reader reader = new InputStreamReader(instream, encoding)) {
                                int l;
                                while ((l = reader.read(tmp)) != -1) {
                                    sb.append(tmp, 0, l);
                                }
                                body = sb.toString();
                            }
                        } finally {
                            EntityUtils.consume(entity);
                        }
                    }
                } catch (ParseException | IOException e) {
                    log.error("completed not callback", e);
                }
                if (asyncCallBack != null) {
                    asyncCallBack.completed(body);
                } else {
                    DEFAULT_CALL_BACK.completed(body);
                }

            }

            @Override
            public void cancelled() {
                if (asyncCallBack != null) {
                    asyncCallBack.cancelled();
                } else {
                    DEFAULT_CALL_BACK.cancelled();
                }
            }
        });
    }

    /**
     * 关闭client对象
     *
     * @param client
     */
    static public void close(CloseableHttpAsyncClient client) {
        try {
            client.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 回掉函数
     */
    public interface AsyncCallBack {

        /**
         * 调用失败，执行该方法
         *
         * @param e
         * @return
         */
        default Object failed(Exception e) {
            log.error("AsyncCallBack default failed" + Thread.currentThread().getName() + "--失败了--" + e.getClass().getName() + "--" + e.getMessage());
            return null;
        }

        /**
         * 处理正常时，执行该方法
         *
         * @param respBody
         * @return
         */
        default Object completed(String respBody) {
            if (log.isDebugEnabled()) {
                log.debug("AsyncCallBack default completed" + Thread.currentThread().getName() + "--获取内容：" + respBody);
            }
            return null;
        }

        /**
         * 处理取消时，执行该方法
         *
         * @return
         */
        default Object cancelled() {
            if (log.isDebugEnabled()) {
                log.debug("AsyncCallBack default cancelled" + Thread.currentThread().getName() + "--取消了");
            }
            return null;
        }
    }

}
