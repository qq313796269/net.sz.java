package net.sz.game.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 用来主动发起HTTP请求的类
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class HttpUtil {

    /**
     * 如果请求抛错，或者未找到，都是这个字符串
     */
    public static final String Http404 = "404 NOT FOUND";

    public enum HTTPMethod {

        POST("POST"),
        GET("GET");
        String value;

        HTTPMethod(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    public static void main(String[] args) throws IOException {
        for (int j = 0; j < 4; j++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        long currentTimeMillis = System.currentTimeMillis();
                        String urlGet = urlGet("http://127.0.0.1:1000/login", "platform=100&username=ROBOT111&userpwd=1");
                        long currentTimeMillis1 = System.currentTimeMillis();
                        System.out.println((currentTimeMillis1 - currentTimeMillis) + " " + urlGet);
                    }
                }
            }).start();
        }
    }

    /**
     *
     * @param urlString
     * @param parms post 参数键值对
     * @return
     */
    public static String urlPost(String urlString, Map<String, Object> parms) {
        return urlPost(urlString, parms, 100);
    }

    /**
     *
     * @param urlString
     * @param parms post 参数键值对
     * @param timeout
     * @return
     */
    public static String urlPost(String urlString, Map<String, Object> parms, int timeout) {
        return urlPost(urlString, parms, null, timeout);
    }

    /**
     *
     * @param urlString
     * @param parms post 参数键值对
     * @param properties head 参数键值对
     * @param timeout
     * @return
     */
    public static String urlPost(String urlString, Map<String, Object> parms, Map<String, Object> properties, int timeout) {
        StringBuilder builder = new StringBuilder();
        if (parms != null) {
            int i = 0;
            for (Map.Entry<String, Object> entry : parms.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                builder.append(key).append("=").append(value);
                if (i < parms.size() - 1) {
                    builder.append("&");
                }
                i++;
            }
        }
        return urlPost(urlString, builder.toString(), properties, timeout);
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static String urlPost(String urlString) {
        return urlPost(urlString, "", null, 100);
    }

    /**
     *
     * @param urlString
     * @param timeout
     * @return
     */
    public static String urlPost(String urlString, int timeout) {
        return urlPost(urlString, "", null, timeout);
    }

    /**
     *
     * @param urlString
     * @param msg 键=值,键=值
     * @return
     */
    public static String urlPost(String urlString, String msg) {
        return urlPost(urlString, msg, 0);
    }

    /**
     *
     * @param urlString
     * @param msg 键=值,键=值
     * @param timeout
     * @return
     */
    public static String urlPost(String urlString, String msg, int timeout) {
        return urlPost(urlString, msg, null, timeout);
    }

    /**
     *
     * @param urlString
     * @param msg 键=值,键=值
     * @param properties
     * @param timeout
     * @return
     */
    public static String urlPost(String urlString, String msg, Map<String, Object> properties, int timeout) {
        return sendUrl(urlString, msg, HTTPMethod.POST, properties, timeout);
    }

    /**
     *
     * @param urlString
     * @param parms
     * @param timeout
     * @return
     */
    public static String urlGet(String urlString, Map<String, Object> parms, int timeout) {
        return urlGet(urlString, parms, null, timeout);
    }

    /**
     *
     * @param urlString
     * @param parms
     * @param properties
     * @param timeout
     * @return
     */
    public static String urlGet(String urlString, Map<String, Object> parms, Map<String, Object> properties, int timeout) {
        StringBuilder builder = new StringBuilder();
        if (parms != null) {
            int i = 0;
            for (Map.Entry<String, Object> entry : parms.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                builder.append(key).append("=").append(value);
                if (i < parms.size() - 1) {
                    builder.append("&");
                }
                i++;
            }
        }
        return urlGet(urlString, builder.toString(), properties, timeout);
    }

    /**
     *
     * @param urlString
     * @return
     */
    public static String urlGet(String urlString) {
        return urlGet(urlString, "", null, 100);
    }

    public static String urlGet(String urlString, String msg) {
        return urlGet(urlString, msg, null, 100);
    }

    public static String urlGet(String urlString, String msg, int timeout) {
        return urlGet(urlString, msg, null, timeout);
    }

    /**
     *
     * @param urlString
     * @param msg
     * @param properties
     * @param timeout
     * @return
     */
    public static String urlGet(String urlString, String msg, Map<String, Object> properties, int timeout) {
        return sendUrl(urlString, msg, HTTPMethod.GET, properties, timeout);
    }

    static String sendUrl(String urlString, String msg, HTTPMethod method, Map<String, Object> properties, int timeout) {
        try {
            HttpURLConnection urlConnection = null;

            URL url = new URL(urlString);
            //请求协议(此处是http)生成的URLConnection类，用于打开URL连接
            urlConnection = (HttpURLConnection) url.openConnection();

            if (properties != null) {
                for (String key : properties.keySet()) {
                    //设置请求属性
                    urlConnection.addRequestProperty(key, properties.get(key).toString());
                }
            }
            //// 设定请求的方法为"GET"，默认是GET
            urlConnection.setRequestMethod(method.getValue());
            //http正文内，因此需要设为true, 默认情况下是false;
            urlConnection.setDoOutput(true);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            urlConnection.setDoInput(true);
            // Post 请求不能使用缓存
            urlConnection.setUseCaches(false);
            /* 先用 400 毫秒 */
            urlConnection.setConnectTimeout(400);
            urlConnection.setReadTimeout(timeout);
            //此处getOutputStream会隐含的进行connect
            try (OutputStream outputStream = urlConnection.getOutputStream()) {
                outputStream.write(msg.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();
            }
            return makeContent(urlString, urlConnection);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("发送http请求", ex);
        }
    }

    /**
     * 得到响应对象
     *
     * @param urlConnection
     * @return 响应对象
     * @throws IOException
     */
    static String makeContent(String urlString, HttpURLConnection urlConnection) {
        String stringRet = Http404;
        try {
            String ecod = urlConnection.getContentEncoding();
            if (ecod == null) {
                ecod = "utf-8";
            }
            if (urlConnection.getResponseCode() == 200) {
                int len = urlConnection.getContentLength();
                if (len > 0) {
                    try (InputStream in = urlConnection.getInputStream()) {
                        byte[] buf = new byte[len];
                        String line = null;
                        if (in.read(buf) == len) {
                            line = new String(buf, ecod);
                        }
                        stringRet = line;
                    }
                }
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("发送http请求", ex);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return stringRet;
    }

}
