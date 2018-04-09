package com.banjvduo.apitest.common;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Http 请求工具类
 * <p>
 * User: stagry@gmail.com
 * Date: 18/4/8
 * Time: 16:51
 * Created by IntelliJ IDEA.
 */
public class HttpUtil {

    private static OkHttpClient client = null;

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 请求是否自动携带 Cookie
     */
    private static final boolean AUTO_COOKIE = true;

    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 3;

    /**
     * 读超时时间
     */
    private static final int READ_TIMEOUT = 10;

    /**
     * 写超时时间
     */
    private static final int WRITE_TIMEOUT = 10;

    /**
     * json 数据格式定义
     */
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //设置超时时间
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        if (AUTO_COOKIE) {
            //设置请求自动携带 Cookie
            builder.cookieJar(new CookieJar() {

                private Map<String, List<Cookie>> cookieMap = new ConcurrentHashMap<>();

                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                    cookieMap.put(httpUrl.host(), list);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    if (httpUrl.url().getPath().contains("login")) {
                        return Collections.emptyList();
                    }
                    List<Cookie> list = cookieMap.get(httpUrl.host());
                    return list != null ? list : Collections.EMPTY_LIST;
                }
            });
        }

        client = builder.build();
    }

    /**
     * Get 请求,获得 Http 状态码
     *
     * @param url 请求地址
     * @return 状态码
     * @throws IOException
     */
    public static int getForCode(String url) throws IOException {
        return getForCode(url, null, null);
    }

    /**
     * Get 请求传递请求参数,获得 Http 状态码
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return 状态码
     * @throws IOException
     */
    public static int getForCode(String url, Map<String, String> param) throws IOException {
        return getForCode(url, param, null);
    }

    /**
     * Get 请求传递请求参数,配置请求头,获得 Http 状态码
     *
     * @param url    请求地址
     * @param param  请求参数
     * @param header 请求头
     * @return 状态码
     * @throws IOException
     */
    public static int getForCode(String url, Map<String, String> param, Map<String, String> header) throws IOException {
        Request.Builder builder = new Request.Builder().url(urlEncoded(url, param));
        Request request = addHeaders(builder, header).build();
        Response response = client.newCall(request).execute();
        return response.code();
    }

    /**
     * Post 请求,获得 Http 状态码
     *
     * @param url 请求地址
     * @return 状态码
     * @throws IOException
     */
    public static int postForCode(String url) throws IOException {
        return postFormDataForCode(url, null, null);
    }

    /**
     * Post 请求以 Form-Data 形式传递请求参数,获得 Http 状态码
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return 状态码
     * @throws IOException
     */
    public static int postFormDataForCode(String url, Map<String, String> param) throws IOException {
        return postFormDataForCode(url, param, null);
    }

    /**
     * Post 请求以 Form-Data 形式传递请求参数,配置请求头,获得 Http 状态码
     *
     * @param url    请求地址
     * @param param  请求参数
     * @param header 请求头
     * @return 状态码
     * @throws IOException
     */
    public static int postFormDataForCode(String url, Map<String, String> param, Map<String, String> header) throws IOException {
        RequestBody requestBody = requestBody(param);
        return postForCode(url, requestBody, header);
    }

    /**
     * Post 请求以 application/json 形式传递请求参数,配置请求头,获得 Http 状态码
     *
     * @param url     请求地址
     * @param jsonStr 请求内容(Json 字符串)
     * @return 状态码
     * @throws IOException
     */
    public static int postJsonForCode(String url, String jsonStr) throws IOException {
        return postJsonForCode(url, jsonStr, null);
    }

    /**
     * Post 请求以 application/json 形式传递请求参数,配置请求头,获得 Http 状态码
     *
     * @param url     请求地址
     * @param jsonStr 请求内容(Json 字符串)
     * @param header  请求头
     * @return 状态码
     * @throws IOException
     */
    public static int postJsonForCode(String url, String jsonStr, Map<String, String> header) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonStr);
        Response response = post(url, requestBody, header);
        return response.code();
    }

    /**
     * Post 请求以 application/json 形式传递请求参数,配置请求头,获得返回内容
     *
     * @param url     请求地址
     * @param jsonStr 请求内容(Json 字符串)
     * @param header  请求头
     * @return 返回内容
     * @throws IOException
     */
    public static String postJson(String url, String jsonStr, Map<String, String> header) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonStr);
        Response response = post(url, requestBody, header);
        return response.body().string();
    }

    /**
     * Post 请求传递请求参数,配置请求头,获得 Http 状态码
     *
     * @param url         请求地址
     * @param requestBody {@link RequestBody}
     * @param header      请求头
     * @return 状态码
     * @throws IOException
     */
    private static int postForCode(String url, RequestBody requestBody, Map<String, String> header) throws IOException {
        Response response = post(url, requestBody, header);
        return response.code();
    }

    /**
     * Post 请求传递请求参数,配置请求头,获得 {@link Response}
     *
     * @param url         请求地址
     * @param requestBody {@link RequestBody}
     * @param header      请求头
     * @return {@link Response}
     * @throws IOException
     */
    private static Response post(String url, RequestBody requestBody, Map<String, String> header) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        Request request = addHeaders(builder, header).build();
        Response response = client.newCall(request).execute();
        logger.info(response.body().string());
        return response;
    }

    /**
     * 将参数转换为 Form-Data 形式
     *
     * @param param 请求参数
     * @return RequestBody {@link RequestBody}
     */
    private static RequestBody requestBody(Map<String, String> param) {
        FormBody.Builder builder = new FormBody.Builder();
        if (param != null && param.size() != 0) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                builder.addEncoded(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 将请求参数转义后拼接到 url 后面
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return 拼接后的 url
     */
    private static String urlEncoded(String url, Map<String, String> param) {
        if (param == null || param.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        try {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                sb.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 将请求头配置到 builder 中
     *
     * @param builder {@link Request.Builder}
     * @param header  请求头
     * @return {@link Request.Builder}
     */
    private static Request.Builder addHeaders(Request.Builder builder, Map<String, String> header) {
        if (header != null && header.size() != 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder;
    }
}
