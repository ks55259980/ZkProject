package com.wemarklinks.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huhaoyu
 * Created On 2017/2/8 下午9:30.
 */

public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUrl.class);

    public interface Schema {
        String HTTP = "http";
        String HTTPS = "https";
    }

    private static OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getClient() {
        return client;
    }

    public static String get(String scheme, String host, Integer port, String[] segments,
            Map<String, Object> queryParams) {
        HttpUrl url = createHttpUrl(scheme, host, port, segments, queryParams);
        Request request = new Request.Builder().url(url).get().build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("HttpUtil make GET request error: " + url.toString(), e);
            return null;
        }
    }

    public static String get(String url) {
        Request request = new Request.Builder().url(url).get().build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("HttpUtil make GET request error: " + url.toString(), e);
            return null;
        }
    }

    public static String post(String scheme, String host, Integer port, String[] segments,
            Map<String, Object> queryParams, Map<String, Object> data) {
        HttpUrl url = createHttpUrl(scheme, host, port, segments, queryParams);
        FormBody.Builder builder = new FormBody.Builder();
        if (data != null) {
            for (Entry<String, Object> e : data.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                if (key == null || value == null) {
                    continue;
                }
                builder.add(key, String.valueOf(value));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("HTTP make POST request error: " + url.toString(), e);
            return null;
        }
    }

    public static String postJson(String url, String jsonStr) {
        try {
            RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), jsonStr);
            Request request = new Request.Builder().url(url).post(jsonBody).build();
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("HTTP post json error: " + url.toString(), e);
            return null;
        }
    }

    public static String put(String scheme, String host, Integer port, String[] segments,
            Map<String, Object> queryParams, Map<String, Object> data) {
        HttpUrl url = createHttpUrl(scheme, host, port, segments, queryParams);
        FormBody.Builder builder = new FormBody.Builder();
        if (data != null) {
            for (Entry<String, Object> e : data.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                if (key == null || value == null) {
                    continue;
                }
                builder.add(key, String.valueOf(value));
            }
        }
        Request request = new Request.Builder().url(url).put(builder.build()).build();
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("HTTP make POST request error: " + url.toString(), e);
            return null;
        }
    }

    public static HttpUrl createHttpUrl(String scheme, String host, Integer port, String[] segments,
            Map<String, Object> queryParams) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(scheme).host(host);
        if (port != null) {
            builder.port(port);
        }
        if (segments != null) {
            for (String segment : segments) {
                builder.addPathSegment(segment);
            }
        }
        if (queryParams != null) {
            List<String> list = new ArrayList<>(queryParams.keySet());
            Collections.sort(list);
            // 大多要求参数列表字典序
            for (String key : list) {
                // 传参前对需要编码的参数进行编码，如redirect_url
                builder.addEncodedQueryParameter(key, String.valueOf(queryParams.get(key)));
            }
        }
        return builder.build();
    }

    public static String convertMapToUrlEncodedQueryString(Map<String, Object> map) {
        String rawQueryString = convertMapToRawQueryString(map);
        logger.info("raw query String: {}", rawQueryString);
        try {
            String encodedQuery = UriUtils.encodeQuery(rawQueryString, CharEncoding.UTF_8);
            logger.info("url encoded query String: {}", encodedQuery);
            return encodedQuery;
        } catch (UnsupportedEncodingException e) {
            logger.error("cannot encode query strings");
        }
        return null;
    }

    public static String convertMapToRawQueryString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = new ArrayList<>(map.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", key, map.get(key).toString()));
        }
        return sb.toString();
    }

}
