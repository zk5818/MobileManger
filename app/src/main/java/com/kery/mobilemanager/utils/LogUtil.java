package com.kery.mobilemanager.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/6.
 * <p>
 * hanze author;
 * <p>
 * 此类是一个负责Log的工具类
 */

public class LogUtil {

    public static boolean LOGGABLE = true;
    public static final String TAG = "shuidi";

    public static <T> void printArray(T[] a) {
        if (!LOGGABLE)
            return;
        StackTraceElement[] arr = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        if (arr != null && arr.length >= 4)
            sb.append(arr[3].getFileName()).append(" <--> ").append(arr[3].getMethodName());
        sb.append("   -->   ").append(Arrays.toString(a));
        Log.e(TAG, sb.toString());
    }

    public static void log(Object... objects) {
        if (!LOGGABLE)
            return;
        StackTraceElement[] arr = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        if (arr != null && arr.length >= 4)
            sb.append(arr[3].getFileName()).append(" <--> ").append(arr[3].getMethodName());
        if (objects != null && objects.length > 0)
            for (Object o : objects) {
                sb.append("  <->  ");
                if (o == null) {
                    sb.append("null");
                } else {
                    if (o.getClass().isArray()) {
                        Class k = o.getClass().getComponentType();
                        String a = "";
                        if (k.equals(byte.class)) {
                            byte[] brr = (byte[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(short.class)) {
                            short[] brr = (short[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(int.class)) {
                            int[] brr = (int[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(long.class)) {
                            long[] brr = (long[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(float.class)) {
                            float[] brr = (float[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(double.class)) {
                            double[] brr = (double[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(boolean.class)) {
                            boolean[] brr = (boolean[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(char.class)) {
                            char[] brr = (char[]) o;
                            a = Arrays.toString(brr);
                        } else if (k.equals(String.class)) {
                            String[] brr = (String[]) o;
                            a = Arrays.toString(brr);
                        } else {
                            Object[] brr = (Object[]) o;
                            a = Arrays.toString(brr);
                        }
                        sb.append(a);
                    } else sb.append(String.valueOf(o));
                }
            }

        Log.e(TAG, sb.toString());
    }

    public static void printIntent(Intent intent) {
        if (intent == null) LogUtil.log("intent Null");
        if (!LOGGABLE || intent == null)
            return;
        if (intent.getData() != null)
            Log.e(TAG, intent.getDataString());
        if (intent.getExtras() == null)
            return;
        Bundle bundle = intent.getExtras();
        if (bundle.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet())
            sb.append(key).append(" ---  ").append(bundle.get(key)).append(",");
        sb.deleteCharAt(sb.length() - 1);
        Log.e(TAG, sb.toString());
    }
}
