package net.sz.game.engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * 用来主动发起HTTP请求的类
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class HttpUtil {

    private static Logger log = Logger.getLogger(HttpUtil.class);
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
                        log.debug((currentTimeMillis1 - currentTimeMillis) + " " + urlGet);
                    }
                }
            }).start();
        }
    }

    public static String urlPost(String urlString, Map<String, String> parms) {
        return urlPost(urlString, parms, 100);
    }

    public static String urlPost(String urlString, Map<String, String> parms, int timeout) {
        return urlPost(urlString, parms, null, timeout);
    }

    public static String urlPost(String urlString, Map<String, String> parms, Map<String, String> properties, int timeout) {
        StringBuilder builder = new StringBuilder();
        if (parms != null) {
            int i = 0;
            for (Map.Entry<String, String> entry : parms.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.append(key).append("=").append(value);
                if (i < parms.size() - 1) {
                    builder.append("&");
                }
                i++;
            }
        }
        return urlPost(urlString, builder.toString(), properties, timeout);
    }

    public static String urlPost(String urlString) {
        return urlPost(urlString, "", null, 100);
    }

    public static String urlPost(String urlString, int timeout) {
        return urlPost(urlString, "", null, timeout);
    }

    public static String urlPost(String urlString, String msg) {
        return urlPost(urlString, msg, 0);
    }

    public static String urlPost(String urlString, String msg, int timeout) {
        return urlPost(urlString, msg, null, timeout);
    }

    /**
     *
     * @param urlString
     * @param msg
     * @param properties
     * @return
     */
    public static String urlPost(String urlString, String msg, Map<String, String> properties, int timeout) {
        return sendUrl(urlString, msg, HTTPMethod.POST, properties, timeout);
    }

    public static String urlGet(String urlString, Map<String, String> parms, int timeout) {
        return urlGet(urlString, parms, null, timeout);
    }

    public static String urlGet(String urlString, Map<String, String> parms, Map<String, String> properties, int timeout) {
        StringBuilder builder = new StringBuilder();
        if (parms != null) {
            int i = 0;
            for (Map.Entry<String, String> entry : parms.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.append(key).append("=").append(value);
                if (i < parms.size() - 1) {
                    builder.append("&");
                }
                i++;
            }
        }
        return urlGet(urlString, builder.toString(), properties, timeout);
    }

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
     * @return
     */
    public static String urlGet(String urlString, String msg, Map<String, String> properties, int timeout) {
        return sendUrl(urlString, msg, HTTPMethod.GET, properties, timeout);
    }

    static String sendUrl(String urlString, String msg, HTTPMethod method, Map<String, String> properties, int timeout) {
        try {
            HttpURLConnection urlConnection = null;

            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            if (properties != null) {
                for (String key : properties.keySet()) {
                    urlConnection.addRequestProperty(key, properties.get(key));
                }
            }

            urlConnection.setRequestMethod(method.getValue());
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            /* 先用 400 毫秒 */
            urlConnection.setConnectTimeout(400);
            urlConnection.setReadTimeout(timeout);
            try (OutputStream outputStream = urlConnection.getOutputStream()) {
                outputStream.write(msg.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();
            }
            return makeContent(urlString, urlConnection);
        } catch (Exception ex) {
            //log.debug("发送http请求" + urlString, ex);
            log.debug("发送http请求" + urlString + " 失败！！！" + ex.toString());
        }
        return null;
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
        } catch (Exception e) {
            log.error("回调错误" + urlConnection.getURL(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return stringRet;
    }

}
