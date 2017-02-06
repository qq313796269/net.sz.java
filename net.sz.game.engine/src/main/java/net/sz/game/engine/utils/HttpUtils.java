package net.sz.game.engine.utils;

//package com.game.engine.utils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
//import java.net.HttpURLConnection;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.X509Certificate;
//import java.util.Base64;
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.X509TrustManager;
//
///**
// *
// */
//public class HttpUtils {
//
//    public static ByteArrayOutputStream URLPost(String strUrl, String content) throws IOException {
//        return URLPost(strUrl, null, content.getBytes("UTF-8"));
//    }
//
//    public static ByteArrayOutputStream URLPost(String strUrl, byte[] content) throws IOException {
//        return URLPost(strUrl, null, content);
//    }
//
//    /**
//     * POST METHOD
//     *
//     * @param strUrl String
//     * @param contentType
//     * @param content Map
//     * @throws IOException
//     * @return String
//     */
//    public static ByteArrayOutputStream URLPost(String strUrl, String contentType, byte[] content) throws IOException {
//
//        URL url = new URL(strUrl);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        try {
//            // verifierHostname();
//
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setAllowUserInteraction(false);
//            con.setUseCaches(false);
//            con.setRequestMethod("POST");
//            // contentType = "application/x-www-form-urlencoded;charset=UTF-8"
//            // contentType = "text/html; charset=UTF-8"
//            if (contentType != null) {
//                con.setRequestProperty("Content-Type", contentType);
//            } else {
//                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//            }
////            else {
////                con.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
////            }
//            con.setRequestProperty("Content-Length", String.valueOf(content.length));
//            con.setConnectTimeout(30000);// jdk 1.5换成这个,连接超时
//            con.setReadTimeout(30000);// jdk 1.5换成这个,读操作超时
//
//            try (OutputStream outputStream = con.getOutputStream()) {
//                outputStream.write(content);
//                outputStream.flush();
//            }
//
//            int responseCode = con.getResponseCode();
//
//            if (HttpURLConnection.HTTP_OK == responseCode) {
//                byte[] buffer = new byte[512];
//                int len = -1;
//                try (InputStream is = con.getInputStream()) {
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    while ((len = is.read(buffer)) != -1) {
//                        bos.write(buffer, 0, len);
//                    }
//                    return bos;
//                }
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (null != con) {
//                con.disconnect();
//            }
//        }
//        return null;
//    }
//
//    public static ByteArrayOutputStream URLGet(String strUrl) throws IOException {
//        return URLGet(strUrl, 30000);
//    }
//
//    /**
//     * GET METHOD
//     *
//     * @param strUrl String
//     * @param timeout
//     * @throws IOException
//     * @return List
//     */
//    public static ByteArrayOutputStream URLGet(String strUrl, int timeout) throws IOException {
//        URL url = new URL(strUrl);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        InputStream is = null;
//        ByteArrayOutputStream bos = null;
//        try {
//            con.setUseCaches(false);
//            con.setConnectTimeout(timeout);// jdk 1.5换成这个,连接超时
//            con.setReadTimeout(timeout);// jdk 1.5换成这个,读操作超时
//            HttpURLConnection.setFollowRedirects(true);
//            int responseCode = con.getResponseCode();
//            if (HttpURLConnection.HTTP_OK == responseCode) {
//                // String headerField = con.getHeaderField("Content-Length");
//                // System.out.println("Content-Length = " + headerField);
//                byte[] buffer = new byte[512];
//                int len = -1;
//                is = con.getInputStream();
//                bos = new ByteArrayOutputStream();
//                while ((len = is.read(buffer)) != -1) {
//                    bos.write(buffer, 0, len);
//                }
//                return bos;
//
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            con.disconnect();
//            is.close();
//        }
//        return bos;
//    }
//
//    /**
//     * 充值相关的HTTP请求使用
//     * GET METHOD
//     *
//     * @param strUrl String
//     * @throws IOException
//     * @return List
//     */
//    public static ByteArrayOutputStream URLGetByRecharge(String strUrl) throws IOException {
//        URL url = new URL(strUrl);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        InputStream is = null;
//        ByteArrayOutputStream bos = null;
//        try {
//            con.setUseCaches(false);
//            con.setConnectTimeout(7000);// jdk 1.5换成这个,连接超时
//            con.setReadTimeout(7000);// jdk 1.5换成这个,读操作超时
//            HttpURLConnection.setFollowRedirects(true);
//            int responseCode = con.getResponseCode();
//            if (HttpURLConnection.HTTP_OK == responseCode) {
//                // String headerField = con.getHeaderField("Content-Length");
//                // System.out.println("Content-Length = " + headerField);
//                byte[] buffer = new byte[512];
//                int len = -1;
//                is = con.getInputStream();
//                bos = new ByteArrayOutputStream();
//                while ((len = is.read(buffer)) != -1) {
//                    bos.write(buffer, 0, len);
//                }
//                return bos;
//
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            con.disconnect();
//            is.close();
//        }
//        return bos;
//    }
//
//    private static void verifierHostname() throws NoSuchAlgorithmException, KeyManagementException {
//        SSLContext sslContext = null;
//        sslContext = SSLContext.getInstance("TLS");
//        X509TrustManager xtm = new X509TrustManager() {
//            public void checkClientTrusted(X509Certificate[] chain,
//                    String authType) {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain,
//                    String authType) {
//            }
//
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        };
//        X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
//        sslContext.init(null, xtmArray, new java.security.SecureRandom());
//        if (sslContext != null) {
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
//                    .getSocketFactory());
//        }
//        HostnameVerifier hnv = new HostnameVerifier() {
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        };
//        HttpsURLConnection.setDefaultHostnameVerifier(hnv);
//    }
//    
//
//    public static void main(String[] args) throws IOException {
//        String req = "http://120.132.90.138:8083/gamehttp?msgid=1&cmd=" + Base64.getEncoder().encodeToString("checkglobthreads".getBytes("UTF-8")) + "&playerid=10001001&token=01b90579-e9dc-4bca-900a-8234fd9d62fd";
//        ByteArrayOutputStream URLGet = HttpUtils.URLGet(req);
//        String res = new String(URLGet.toByteArray(), "UTF-8");
//        System.out.println(res);
//    }
//}
