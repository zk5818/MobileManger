package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.request.PatchFormRequest;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jason.huang on 2016/7/13 0013.
 */
public class PatchFormBuilder extends OkHttpRequestBuilder {

    private List<FileInput> files = new ArrayList<>();

    @Override
    public RequestCall build() {
        return new PatchFormRequest(url, tag, params, headers, files).build();
    }

    public PatchFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }
    }

    //
    @Override
    public PatchFormBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PatchFormBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PatchFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PatchFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public PatchFormBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PatchFormBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }
}
