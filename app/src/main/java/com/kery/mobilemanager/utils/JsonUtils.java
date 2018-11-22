package com.kery.mobilemanager.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/12.
 */

public class JsonUtils {
    private static Gson gson = new Gson();

    @Nullable
    public static <T> T fromJson(Class<T> k, String json, String... keys) {
        if (TextUtils.isEmpty(json))
            return null;
        if (keys == null || keys.length == 0)
            return a(k, json);
        return a(k, getJsonValue(json, keys));
    }


    public static String toJson(Object o) {
        if (o == null)
            return null;
        return gson.toJson(o);
    }

    public static <T> List<T> parseList(Class<T> k, String json, String... keys) {
        List<T> list = new ArrayList<>();
        if (TextUtils.isEmpty(json))
            return list;
        if (keys != null && keys.length != 0)
            json = getJsonValue(json, keys);
        List<T> temp = fromJsonArray(json, k);
        list.addAll(temp);
        return list;
    }


    private static <T> List<T> fromJsonArray(String json, Class<T> k) {
        List<T> list = new ArrayList<>();
        if (TextUtils.isEmpty(json))
            return list;
        try {
            JSONArray array = new JSONArray(json);
            if (array.length() == 0)
                return list;
            for (int i = 0; i < array.length(); i++) {
                T t = a(k, String.valueOf(array.get(i)));
                if (t != null)
                    list.add(t);
            }
        } catch (Exception e) {
            LogUtil.log(e.getMessage());
        }
        return list;
    }

    private static <T> T a(Class<T> k, String json) {
        try {
            return gson.fromJson(json, k);
        } catch (Exception e) {
            LogUtil.log(e.getMessage(), json);
        }
        return null;
    }

    public static String getJsonValue(String json, String... keys) {
        if (TextUtils.isEmpty(json))
            return "";
        if (keys == null || keys.length == 0)
            return json;
        String temp = json;
        //取string到倒数第二个key
        for (String key : keys) {
            temp = b(temp, key);
            if (TextUtils.isEmpty(temp) || temp.equalsIgnoreCase("null"))
                return "";
        }
        return temp;
    }


    public static String getDefaultValue(String json, CharSequence defaultValue, String... keys) {
        if (TextUtils.isEmpty(json))
            return String.valueOf(defaultValue);
        if (keys == null || keys.length == 0)
            return json;
        String temp = json;
        //取string到倒数第二个key
        for (String key : keys) {
            temp = b(temp, key);
            if (TextUtils.isEmpty(temp) || temp.equalsIgnoreCase("null"))
                return String.valueOf(defaultValue);
        }
        return temp;
    }

    private static String b(String json, String key) {
        try {
            JSONObject object = new JSONObject(json);
            return object.optString(key);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(json, key, e.getMessage());
        }
        return "";
    }
}
