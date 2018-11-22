package com.kery.mobilemanager.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字符串工具类
 * @author dingyue
 * <p>创建日期：2016-09-06</p>
 * */

public class StringUtil {

    /**
     * 实体对象转成Gson字符串
     * @param obj
     * @return
     * */
    public static String toGson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * Gson字符串转成实体对象
     * @param gsonStr
     * @param clazz
     * @return
     * */
    public static Object fromGson(String gsonStr, Class<?> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(gsonStr, clazz);
    }

    /**
     * Gson字符串转成实体对象
     * @param gsonStr
     * @param type
     * @return
     * */
    public static <T> T fromGson(String gsonStr, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(gsonStr, type);
    }

    /**
     * 字节数组转字符串
     * @param bytes
     * @return
     * */
    public static String byteArrayToString(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<bytes.length; i++) {
            buf.append(bytes[i]);
            buf.append(",");
        }
        return buf.toString();
    }

    public static List<String> stringToList(String string) {
        List<String> list = new ArrayList<>();
        String[] strings = string.split("┦");
        for (int i=0; i<strings.length; i++) {
            String str1 = strings[i];
            String[] strs1 = str1.split("◎");
            for (int j=0; j<strs1.length; j++) {
                String str2 = strs1[j];
                String[] strs2 = str2.split("⊙");
                for (int k=0; k<strs2.length; k++) {
                    list.add(strs2[k]);
                }
            }
        }
        return list;
    }

    /**
     * 字符串转list
     * @param str
     * @return
     * */
    public static List<Map<String, String>> strToList(String str) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = null;
        String[] strs1 = str.split("┦");
        for (int i=0; i<strs1.length; i++) {
            String[] strs2 = strs1[i].split("⊙");
            map = new HashMap<>();
            for (int j=0; j<strs2.length; j++) {
                String key = "";
                String value = "";
                key = strs2[j].split("◎")[0];
                if (strs2[j].split("◎").length == 2) {
                    value = strs2[j].split("◎")[1];
                } else {
                    value = "";
                }
                map.put(key, value);
            }
            list.add(map);
        }
        return list;
    }
    public static Map<String, String> strToNameMap(String str) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = null;
        String[] strs1 = str.split("┦");
        map = new HashMap<>();
        for (int i=0; i<strs1.length; i++) {
            map.put(strs1[i].split("⊙")[0].split("◎")[1],strs1[i].split("⊙")[1].split("◎")[1]);
            //list.add(map);
        }
        return map;
    }
    /**
     * 字符串转list
     * @param str
     * @param regex
     * @return
     * */
    public static List<String> strToList(String str, String regex) {
        List<String> list = new ArrayList<>();
        String[] strs = str.split(regex);
        for (int i=0; i<strs.length; i++) {
            list.add(strs[i]);
        }
        return list;
    }

//    /**
//     * 获取成品单据号
//     * @return
//     */
//    public static String getNewGoodNo(String maxNo,String type){
//        if (type.equals("CPRK"))
//
//            return  "CPRK1" + DateUtil.getCurrentDate("yyyyMMddHHmmss");
//
//        else if(type.equals("CPCK"))
//
//            return  "CPCK1" + DateUtil.getCurrentDate("yyyyMMddHHmmss");
//
//        else
//
//            return  "CPJG1" + DateUtil.getCurrentDate("yyyyMMddHHmmss");
//    }

    /**
     * 把字符串数组转字符串
     * @param stringArray
     * @return
     */
    public static String stringArrayToString(String[] stringArray) {
        StringBuffer buf = new StringBuffer();
        String string = "";
        if (stringArray != null && stringArray.length > 0) {
            for (String s : stringArray) {
                buf.append(s+",");
            }
            string = buf.toString();
        }
        return string.substring(0, string.length());
    }
}
