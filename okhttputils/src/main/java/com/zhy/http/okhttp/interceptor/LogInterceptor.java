package com.zhy.http.okhttp.interceptor;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by Administrator on 2017/1/4.
 *
 * 日志存在问题，感觉导致多次网络请求
 */

public class LogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();

        Logger.wtf("method:\n  %s\nurl:\n  %s\nheader:\n  %s\npareams:\n  %s", request.method(), request.url(),
                request.headers(), request.body());

        Logger.wtf("url:%s \nReceived response for %s", request.url(), (t2 - t1) / 1e6d);

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                Logger.wtf("Couldn't decode the response body; charset is likely malformed.");
                return response;
            }
        }

        Logger.json(buffer.clone().readString(charset));

        return response;
    }

}
