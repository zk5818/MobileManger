package com.kery.mobilemanager.base;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kery.mobilemanager.R;
import com.kery.mobilemanager.utils.AppUtil;
import com.kery.mobilemanager.utils.LogUtil;
import com.kery.mobilemanager.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Hanze on 2017/11/17.
 */

public abstract class HanBaseActivity extends AppCompatActivity {

    //专门用来保存分散式对象至sp文件的(有些对象不分集中式分散式，也放到这里)
    private static final String ObjectSpFileName_FENSAN = "objectSp_fs";
    //专门用来保存集中式式对象至sp文件的
    private static final String ObjectSpFileName_JIZHONG = "objectSp_jz";

    /**
     * @param object 将此对象保存到xml文件里
     */
    public void saveObject(Object object) {
        saveObject(object, false);
    }

    public void saveObject(Object object, boolean jz) {
        if (object == null)
            return;
        SharedPreferences sp = getSharedPreferences(jz ? ObjectSpFileName_JIZHONG : ObjectSpFileName_FENSAN,
                MODE_PRIVATE);
        sp.edit().putString(object.getClass().getSimpleName(), gson.toJson(object)).apply();
    }

    public <T> T restoreObject(@NonNull Class<T> k) {
        return restoreObject(k, false);
    }

    public <T> T restoreObject(@NonNull Class<T> k, boolean jz) {
        SharedPreferences sp = getSharedPreferences(jz ? ObjectSpFileName_JIZHONG : ObjectSpFileName_FENSAN,
                MODE_PRIVATE);
        String json = sp.getString(k.getSimpleName(), "");
        return fromGson(json, k, new String[]{});
    }

    public <T> void saveList(@NonNull Class<T> k, @NonNull List<T> list, boolean jz) {
        SharedPreferences sp = getSharedPreferences(jz ? ObjectSpFileName_JIZHONG : ObjectSpFileName_FENSAN,
                MODE_PRIVATE);
        sp.edit().putString(k.getSimpleName(), gson.toJson(list)).apply();
    }

    public <T> List<T> restoreList(Class<T> k, boolean jz) {
        SharedPreferences sp = getSharedPreferences(jz ? ObjectSpFileName_JIZHONG : ObjectSpFileName_FENSAN,
                MODE_PRIVATE);
        String json = sp.getString(k.getSimpleName(), "");
        return fromGson(json, k, (String) null);
    }

    public void show(String text) {
        if (TextUtils.isEmpty(text) || !canshow)
            return;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    boolean canshow;

    @Override
    protected void onResume() {
        super.onResume();
        canshow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        canshow = false;
    }

    public static final String KEY_OBJ = "obj";//传第一个序列化对象

    public static final String KEY_OBJ_2 = "obj2";//第二个序列化对象

    public static final class ImageUrl {
        @Override
        public String toString() {
            return "ImageUrl{" +
                    "status='" + status + '\'' +
                    ", url='" + url + '\'' +
                    ", id=" + id +
                    ", msg='" + msg + '\'' +
                    '}';
        }

        public String status;
        public String url;
        public int id;
        public String msg;
    }


    /**
     * 没有数据，空数据占位图
     */
    public void showDataEmpty() {
        if (getDataEmptyDrawable() == 0)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            getOverLayView().getOverlay().clear();
        Glide.with(this).load(getDataEmptyDrawable()).into(placeHodlerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            getOverLayView().getOverlay().add(tempView);
    }

    //app3.0新创建的sp文件名
    public static final String SPNAME = "app3";
    /**
     * 根视图
     */
    public ViewGroup root;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDead())
                return;
            switch (msg.what) {
                case msg_type_network_error://不是response返回的
                    responseFail(msg.arg1, String.valueOf(msg.obj));
                    break;
                case msg_type_network_success://网络请求成功,200-300
                    responseOk(msg.arg1, (byte[]) msg.obj);
                    break;
                case msg_type_network_no_success://网络请求错误，有response但是响应码大于等于300
                    response_no_success(msg.arg1, msg.arg2, new String((byte[]) msg.obj));
                    break;
                default:
                    message(msg);
                    break;
            }
        }
    };

    //网络错误
    static final int msg_type_network_error = -10001;
    //网络请求成功-200
    static final int msg_type_network_success = -10002;
    //网络请求响应错误300-600
    static final int msg_type_network_no_success = -10003;

    /**
     * @return 判断activity是否即将销毁
     */
    public boolean isDead() {
        boolean b = isFinishing();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            b = b || isDestroyed();
        return b;
    }


    PopupWindow window;

    /**
     * popupwindow  的contentview
     */
    private View view;


    /**
     * titlebar  右边的textview
     */
    public TextView rightText;
    public ImageView rightImg, searchImg;

    public void setRightText(CharSequence text) {
        rightText.setText(text);
    }

    //占位图的viewgRoup
    public View tempView;

    //    {"errors":{"detail":"认证令牌无效。"}}

    /**
     * @return 返回错误信息
     */
    public String getErrorMessage(String content) {
        String a = getGsonValue(content, "errors");
        String b = getGsonValue(a, "detail");
        return b;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && window != null && window.isShowing()) {
            window.dismiss();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    /**
     * 为了防止空指针异常，比较字符串
     */
    public boolean stringEquals(String a, String b) {
        if (TextUtils.isEmpty(a))
            return false;
        return a.equals(b);
    }


    /**
     * 处理消息事件
     *
     * @param message {@link Message}
     */
    public abstract void message(Message message);

    /**
     * gson 转化成对象
     */
    public <T> T fromGson(byte[] bytes, Class<T> klass) {
        try {
            T t = gson.fromJson(new String(bytes), klass);
            LogUtil.log(t);
            return t;
        } catch (Exception e) {
            LogUtil.log(new String(bytes), "gson转换错误\n", e.getMessage());
            return null;
        }
    }


    /**
     * @param s    字符串json
     * @param k    类型
     * @param keys key数组,最多三个
     */
    public <T> T fromGson(String s, Class<T> k, String... keys) {
        try {
            JSONObject object = new JSONObject(s);
            if (keys == null || keys.length == 0) {
                return getT(s, k);
            } else {
                String key1 = keys[0];
                if (!object.has(key1)) {
                    LogUtil.log("gson转换失败不含有这个key", key1);
                    return null;
                }
                String value1 = object.getString(key1);
                if (keys.length == 1)
                    return getT(value1, k);
                if (keys.length >= 2) {
                    String key2 = keys[1];
                    JSONObject object2 = new JSONObject(value1);
                    if (!object2.has(key2)) {
                        LogUtil.log("gson转换失败不含有这个key", key2);
                        return null;
                    }
                    String value2 = object2.getString(key2);
                    if (keys.length == 2)
                        return getT(value2, k);
                    if (keys.length == 3) {
                        String key3 = keys[2];
                        JSONObject object3 = new JSONObject(value2);
                        if (!object3.has(key3)) {
                            LogUtil.log("gson转换失败不含有这个key", key3);
                            return null;
                        }
                        String value3 = object3.getString(key3);
                        return getT(value3, k);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log("Gson转换失败，不是正常json格式", e.getMessage());
            return null;
        }
        return null;
    }


    public String getGsonValue(String s, String key) {
        if (TextUtils.isEmpty(s))
            return "";
        try {
            JSONObject o = new JSONObject(s);
            if (!o.has(key))
                return "";
            return String.valueOf(o.get(key));
        } catch (Exception e) {
            LogUtil.log("gson有bug ", key, e.getMessage());
        }
        return "";
    }

    /***
     *
     * @return 返回集合
     * */
    public <T> List<T> fromGson(String s, Class<T> k, @Nullable String key) {
        if (TextUtils.isEmpty(s))
            return null;
        List<T> list = new ArrayList<>();
        try {
            if (TextUtils.isEmpty(key)) {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    T t = getT(array.getString(i), k);
                    if (t != null)
                        list.add(t);
                }
                return list;
            }
            JSONObject object = new JSONObject(s);
            if (!object.has(key)) {
                LogUtil.log("gson转换失败不含有这个key", key);
                return null;
            }
            JSONArray array = object.getJSONArray(key);
            if (array != null && array.length() != 0) {
                for (int i = 0; i < array.length(); i++) {
                    T t = getT(array.getString(i), k);
                    if (t != null)
                        list.add(t);
                }
                return list;
            }
            return null;
        } catch (Exception e) {
            LogUtil.log("gson转换错误", e.getMessage());
            return null;
        }
    }

    @Nullable
    private <T> T getT(String s, Class<T> k) {
        try {
            T t = gson.fromJson(s, k);
            return t;
        } catch (Exception e) {
            LogUtil.log("Gson转换失败，不是正常json格式", e.getMessage());
        }
        return null;
    }


    View.OnClickListener titleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    finishActivity(v);
                    break;
                case R.id.right_click:
                    onRightClick(rightText);
                    break;
                case R.id.search_pic:
                    onSearchImageClick(searchImg);
                    break;
                case R.id.right_pic:
                    onRightImageClick(rightImg);
                    break;

            }
        }
    };


    /**
     * 标题栏左边点击事件
     */
    public void finishActivity(View view) {
        finish();
    }

    /**
     * 标题栏右边textview事件
     */
    public void onRightClick(TextView view) {

    }

    /**
     * 标题栏右边imageview点击事件
     */
    public void onRightImageClick(ImageView imageView) {

    }

    public void onSearchImageClick(ImageView imageView) {

    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, @StyleRes int resid, boolean first) {
        resid = R.style.MyTheme1;
        super.onApplyThemeResource(theme, resid, first);
        overridePendingTransition(R.anim.right_in, R.anim.anim_stay);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_stay, R.anim.right_out);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clearAllRequest();
        dofirstRequest();
    }


    public Gson gson;

    //占位图
    public ImageView placeHodlerView;

    public SharedPreferences sharedPreferences;


    private Set<Integer> hashSet = Collections.synchronizedSet(new HashSet<Integer>());

    /**
     * 让实现类去填充消息类型，如果填充进去，网络请求的时候会判断是否含有这个消息类型
     * 如果有，就使用分散式前缀，因为很多网络请求都是使用一个前缀
     */
    public void inflateSet(Set<Integer> set) {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() == 0)
            throw new IllegalArgumentException("布局文件没有set");
        //https://www.jianshu.com/p/31396863d1aa GsonBuilder 详解
        gson = new Gson();
        root = (ViewGroup) View.inflate(this, getLayoutId(), null);
        setContentView(root);
        sharedPreferences = getSharedPreferences(SPUtil.FILE_NAME, MODE_PRIVATE);
        initTitleBar();
        initView();
        inflateSet(hashSet);
        if (getOverLayView() == null)
            throw new IllegalArgumentException("必须返回一个viewgroup");
        initListener();
        initData();
        //占位图
        tempView = getLayoutInflater().inflate(R.layout.place_holder, null);
        tempView.addOnAttachStateChangeListener(l);
        placeHodlerView = (ImageView) tempView.findViewById(R.id.place_holder);
    }

    View.OnAttachStateChangeListener l = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            ViewGroup g = (ViewGroup) v.getParent();
            tempView.layout(0, 0, g.getWidth(), g.getHeight());
            int w = placeHodlerView.getLayoutParams().width;
            int h = placeHodlerView.getLayoutParams().height;
            placeHodlerView.layout((g.getWidth() - w) / 2, (g.getHeight() - h) / 2, (g.getWidth() + w) / 2, (g
                    .getHeight() + h) / 2);
        }

        @Override
        public void onViewDetachedFromWindow(View v) {

        }
    };

    /**
     * 标记是否已经可见了，控件初始化完成
     */
    boolean attachwindow;

    /**
     * 网络请求次数
     */
    int requestCount;


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onViewVisable();
            dofirstRequest();
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            attachwindow = true;
            if (window != null)
                return;
            view = View.inflate(this, R.layout.window_loading, null);
            int width = getResources().getDimensionPixelSize(R.dimen.loading_dilog_width);
            window = new PopupWindow(view, width, width);
            window.setOutsideTouchable(true);
            getOverLayView().post(runnable);
        }
    }

    /**
     * 当视图可见的时候
     */
    public void onViewVisable() {

    }

    /**
     * 第一次网络请求只能在这里进行
     */
    public abstract void dofirstRequest();


    public ViewGroup titleBar;

    /**
     * 初始化titlebar title,图标 点击事件,如果不想要titlebar 就重写
     */
    public void initTitleBar() {
        titleBar = (ViewGroup) getLayoutInflater().inflate(R.layout.title_bar, root, false);
        root.addView(titleBar, 0);
        findViewById(R.id.back).setOnClickListener(titleClickListener);
//        findViewById(R.id.right_container).setOnClickListener(titleClickListener);
        findViewById(R.id.right_pic).setOnClickListener(titleClickListener);
        findViewById(R.id.search_pic).setOnClickListener(titleClickListener);
        TextView t = (TextView) findViewById(R.id.title);
        String title = getIntent().getStringExtra(KEY_TITLE);
        t.setText(title);
        rightText = (TextView) findViewById(R.id.right_click);
        rightText.setOnClickListener(titleClickListener);
        rightImg = (ImageView) findViewById(R.id.right_pic);
        searchImg = (ImageView) findViewById(R.id.search_pic);
        rightText.setText(getIntent().getStringExtra(KEY_RIGHT_TEXT));
        int rightIcon = getIntent().getIntExtra(KEY_RIGHT_ICON, 0);
        if (rightIcon != 0)
            rightImg.setImageResource(rightIcon);
        int searchIcon = getIntent().getIntExtra(KEY_SEARCH_ICON, 0);
        if (searchIcon != 0)
            searchImg.setImageResource(searchIcon);

    }

    public static final String KEY_TITLE = "title";
    public static final String KEY_RIGHT_ICON = "right_icon";
    public static final String KEY_SEARCH_ICON = "search_icon";
    public static final String KEY_RIGHT_TEXT = "right_text";


    void responseFail(int message_type, String error_text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getOverLayView().getOverlay().clear();
            getOverLayView().getOverlay().add(tempView);
            placeHodlerView.setImageResource(R.drawable.icon_network_error);
        }
        show("网络异常，请检查网络连接是否良好");
        window.dismiss();
        response_error(message_type, error_text);
    }


    void responseOk(int message_type, byte[] bytes) {
        window.dismiss();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            getOverLayView().getOverlay().clear();
        response_success(message_type, new String(bytes).replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "").
                replace("<string xmlns=\"http://tempuri.org/\">", "").replace("</string>", "").replace("\n","").replace("\r",""));
    }

    /**
     * @return 返回一个加载失败的图片显示出来
     */
    public int getDataEmptyDrawable() {
        return 0;
    }


    /**
     * get请求失败
     *
     * @param message_type 消息类型
     */
    public abstract void response_error(int message_type, String error_text);

    /**
     * @param message_type 消息类型
     * @param status_code  响应码300-600
     * @param error_text   错误信息
     */
    public void response_no_success(int message_type, int status_code, String error_text) {
        LogUtil.log("!响应异常!", message_type, status_code, error_text);
        window.dismiss();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            getOverLayView().getOverlay().clear();
    }

    /**
     * get请求成功
     *
     * @param message_type 消息类型
     * @param s            返回的byte数组
     */
    public abstract void response_success(int message_type, String s);

    //{"status": true, "msg": "", "data": null},智能门锁操作是否成功
    public boolean status(String content) {
        return getGsonValue(content, "status").equals("true");
    }

    String getBaseUrl(int msgtype) {
        return "http://218.94.84.210:8080/FinanceSystemService/Service1.asmx/";
    }

    /**
     * PUT网络请求
     *
     * @param message_type 消息类型,
     * @param headers      请求头
     * @param params       请求体
     * @param file         片段
     * @param showProgress 是否实现popupwindow
     */
    public void put(final int message_type, @NonNull LinkedHashMap<String, String> headers, @NonNull
            LinkedHashMap<String, String> params, String file, boolean showProgress) {
        if (!attachwindow)
            return;
        if (window != null && !window.isShowing() && showProgress)
            window.showAtLocation(root, Gravity.CENTER, 0, 0);
        requestCount++;
        try {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers == null)
                headers = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.removeHeader("User-Agent").addHeader("User-Agent", String.valueOf(System
                    .getProperty("http.agent")));

            StringBuilder sb = new StringBuilder();
            sb.append(getBaseUrl(message_type)).append(file);
            FormBody.Builder builder1 = new FormBody.Builder();
            if (params != null) {
                params.put("mobile_version", AppUtil.getVersionName(this));
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder1.add(entry.getKey(), entry.getValue());
                }
            }
            String url = sb.toString();
            Call call = client.newCall(builder.url(url).put(builder1.build()).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type), e.getMessage());
                        return;
                    }
                    Message.obtain(handler, msg_type_network_error, message_type, 0, e.getMessage()).sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        response.body().close();
                        return;
                    }
                    Message.obtain(handler,
                            response.isSuccessful() ? msg_type_network_success : msg_type_network_no_success,
                            message_type, response.code(), response.body().bytes()).sendToTarget();
                }
            });
            callList.add(call);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
    }


    //新增加的防止失败重连的httpclient
    static OkHttpClient client;

    synchronized static OkHttpClient getInstance() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
        }
        return client;
    }


    public void get(final int message_type, @NonNull LinkedHashMap<String, String> headers, @NonNull
            LinkedHashMap<String, String> params, String file, boolean showProgress) {
        if (!attachwindow)
            return;
        if (window != null && !window.isShowing() && showProgress)
            window.showAtLocation(root, Gravity.CENTER, 0, 0);
        requestCount++;
        try {
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers == null)
                headers = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.removeHeader("User-Agent").addHeader("User-Agent", String.valueOf(System
                    .getProperty("http.agent")));

            StringBuilder sb = new StringBuilder();

            sb.append(getBaseUrl(message_type)).append(file).append("?");
            if (params == null)
                params = new LinkedHashMap<>();
            sb.append("mobile_version").append("=").append(AppUtil.getVersionName(this)).append("&");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            if (!params.containsKey("terminal"))
                sb.append("terminal").append("=").append("Android").append("&");
            sb.deleteCharAt(sb.length() - 1);

            String url = sb.toString();
            Call call = client.newCall(builder.url(url).get().build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        return;
                    }
                    Message.obtain(handler, msg_type_network_error, message_type, 0, e.getMessage()).sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        response.body().close();
                        return;
                    }
                    LogUtil.log(response.request().headers().toString(), response.request().url());
                    Message.obtain(handler,
                            response.isSuccessful() ? msg_type_network_success : msg_type_network_no_success,
                            message_type, response.code(), response.body().bytes()).sendToTarget();
                }
            });
            callList.add(call);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
    }

    /**
     * PATCH请求
     *
     * @param message_type 消息类型,
     * @param headers      请求头
     * @param params       请求体
     * @param file         片段
     * @param showProgress 是否实现popupwindow
     */

    public void patch(final int message_type, LinkedHashMap<String, String> headers,
                      LinkedHashMap<String, String> params, String file, boolean showProgress) {
        if (!attachwindow)
            return;
        if (window != null && !window.isShowing() && showProgress)
            window.showAtLocation(root, Gravity.CENTER, 0, 0);
        requestCount++;
        try {
            OkHttpClient client = OkHttpUtils.getInstance().setCertificates(getAssets().open("MissouriRiverCer.cer"))
                    .getOkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers == null)
                headers = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.removeHeader("User-Agent").addHeader("User-Agent", String.valueOf(System
                    .getProperty("http.agent")));
            StringBuilder sb = new StringBuilder();

            sb.append(getBaseUrl(message_type)).append(file);
            FormBody.Builder fb = new FormBody.Builder();
            if (params != null && !params.isEmpty())
                for (LinkedHashMap.Entry<String, String> entry : params.entrySet())
                    fb.add(entry.getKey(), entry.getValue());

            String url = sb.toString();
            Call call = client.newCall(builder.url(url).patch(fb.build()).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        return;
                    }
                    Message.obtain(handler, msg_type_network_error, message_type, 0, e.getMessage()).sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        response.body().close();
                        return;
                    }
                    LogUtil.log(response.request().headers().toString(), response.request().url());
                    Message.obtain(handler,
                            response.isSuccessful() ? msg_type_network_success : msg_type_network_no_success,
                            message_type, response.code(), response.body().bytes()).sendToTarget();
                }
            });
            callList.add(call);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
    }

    /**
     * POST请求
     *
     * @param message_type 消息类型,
     * @param headers      请求头
     * @param params       请求体
     * @param file         片段
     * @param showProgress 是否实现popupwindow
     */
    public void post(final int message_type, LinkedHashMap<String, String> headers,
                     LinkedHashMap<String, String> params, String file, boolean showProgress) {
        if (!attachwindow)
            return;
        if (window != null && !window.isShowing() && showProgress)
            window.showAtLocation(root, Gravity.CENTER, 0, 0);
        requestCount++;
        try {
            OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers == null)
                headers = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.removeHeader("User-Agent").addHeader("User-Agent", String.valueOf(System
                    .getProperty("http.agent")));
            StringBuilder sb = new StringBuilder();

            sb.append(getBaseUrl(message_type)).append(file);

            FormBody.Builder fb = new FormBody.Builder();
            if (params != null && !params.isEmpty())
                for (LinkedHashMap.Entry<String, String> entry : params.entrySet())
                    fb.add(entry.getKey(), entry.getValue());

            String url = sb.toString();
            Call call = client.newCall(builder.url(url).post(fb.build()).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        return;
                    }
                    Message.obtain(handler, msg_type_network_error, message_type, 0, e.getMessage()).sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        response.body().close();
                        return;
                    }
                    LogUtil.log(response.request().headers().toString(), response.request().url());
                    Message.obtain(handler,
                            response.isSuccessful() ? msg_type_network_success : msg_type_network_no_success,
                            message_type, response.code(), response.body().bytes()).sendToTarget();
                }
            });
            callList.add(call);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
    }

    /**
     * POST请求
     *
     * @param message_type 消息类型,
     * @param headers      请求头
     * @param params       请求体
     * @param file         片段
     * @param showProgress 是否实现popupwindow
     */
    public void delete(final int message_type, LinkedHashMap<String, String> headers,
                       LinkedHashMap<String, String> params, String file, boolean showProgress) {
        if (!attachwindow)
            return;
        if (window != null && !window.isShowing() && showProgress)
            window.showAtLocation(root, Gravity.CENTER, 0, 0);
        requestCount++;
        try {
            OkHttpClient client = OkHttpUtils.getInstance().setCertificates(getAssets().open("MissouriRiverCer.cer"))
                    .getOkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers == null)
                headers = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.removeHeader("User-Agent").addHeader("User-Agent", String.valueOf(System
                    .getProperty("http.agent")));
            StringBuilder sb = new StringBuilder();

            sb.append(getBaseUrl(message_type)).append(file).append("?");
            FormBody.Builder fb = new FormBody.Builder();
            if (params != null && !params.isEmpty()) {
                params.put("mobile_version", AppUtil.getVersionName(this));
                for (LinkedHashMap.Entry<String, String> entry : params.entrySet()) {
                    fb.add(entry.getKey(), entry.getValue());
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }

            String url = sb.toString();
            Call call = client.newCall(builder.url(url).delete(fb.build()).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        return;
                    }
                    Message.obtain(handler, msg_type_network_error, message_type, 0, e.getMessage()).sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        response.body().close();
                        return;
                    }
                    LogUtil.log(response.request().headers().toString(), response.request().url());
                    Message.obtain(handler,
                            response.isSuccessful() ? msg_type_network_success : msg_type_network_no_success,
                            message_type, response.code(), response.body().bytes()).sendToTarget();
                }

            });
            callList.add(call);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
    }


    /**
     * POST请求
     *
     * @param message_type 消息类型,
     * @param headers      请求头
     * @param params       请求体
     * @param file         片段
     * @param showProgress 是否实现popupwindow
     */
    public void postFile(final int message_type, LinkedHashMap<String, String> headers,
                         LinkedHashMap<String, String> params, String file, boolean showProgress,
                         PostFormBuilder.FileInput input) {
        if (!attachwindow)
            return;
        if (window != null && !window.isShowing() && showProgress)
            window.showAtLocation(root, Gravity.CENTER, 0, 0);
        requestCount++;
        try {
            OkHttpClient client = OkHttpUtils.getInstance().setCertificates(getAssets().open("MissouriRiverCer.cer"))
                    .getOkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers == null)
                headers = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
            builder.removeHeader("User-Agent").addHeader("User-Agent", String.valueOf(System
                    .getProperty("http.agent")));
            StringBuilder sb = new StringBuilder();
            sb.append(getBaseUrl(message_type)).append(file);
            MultipartBody.Builder b = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            if (params == null)
                params = new LinkedHashMap<>();
            params.put("mobile_version", AppUtil.getVersionName(this));
            for (LinkedHashMap.Entry<String, String> entry : params.entrySet())
                b.addFormDataPart(entry.getKey(), entry.getValue());
            RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(input
                    .filename)), input.file);
            b.addFormDataPart(input.key, input.filename, fileBody);
            String url = sb.toString();
            Call call = client.newCall(builder.url(url).post(b.build()).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        return;
                    }
                    Message.obtain(handler, msg_type_network_error, message_type, 0, e.getMessage()).sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (isDead() || call.isCanceled()) {
                        LogUtil.log(String.format("消息类型%s已被取消", message_type));
                        response.body().close();
                        return;
                    }
                    LogUtil.log(response.request().headers().toString(), response.request().url());
                    Message.obtain(handler,
                            response.isSuccessful() ? msg_type_network_success : msg_type_network_no_success,
                            message_type, response.code(), response.body().bytes()).sendToTarget();
                }
            });
            callList.add(call);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 大量设置点击事件，懒得一个个写了
     *
     * @param listener 点击事件
     * @param views    View
     */
    public void setClickListener(View.OnClickListener listener, View... views) {
        if (views == null || views.length == 0)
            return;
        for (View v : views)
            v.setOnClickListener(listener);
    }

    /**
     * {@link AppCompatActivity {@link #setContentView(int)}}
     *
     * @return 返回布局id
     */
    @LayoutRes
    public abstract int getLayoutId();

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * @return 返回一个viewgroup  主要作用是用来显示网络请求失败的界面
     */
    public abstract ViewGroup getOverLayView();

    /**
     * 设置监听事件
     */
    public abstract void initListener();

    /**
     * 初始化数据 ，或者发送网络请求
     */
    public abstract void initData();


    List<Call> callList = new ArrayList<>();

    /**
     * 清除所有网络请求
     */
    public void clearAllRequest() {
        for (Call call : callList)
            if (!call.isExecuted() || !call.isCanceled())
                call.cancel();
        callList.clear();
    }

    @Override
    protected void onDestroy() {
        clearAllRequest();
        tempView.removeOnAttachStateChangeListener(l);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            getOverLayView().getOverlay().clear();
        getOverLayView().removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
        handler = null;
    }

    public static int[] arr = {
            R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.h5,
            R.id.h6, R.id.h7};

    public static void setRed(View view, int... textids) {
        if (textids == null || textids.length == 0)
            textids = arr;
        for (int id : textids) {
            if (view.findViewById(id) == null)
                continue;
            TextView t = (TextView) view.findViewById(id);
            if (TextUtils.isEmpty(t.getText()) || t.getText().charAt(t.getText().length() - 1) != '*')
                continue;
            SpannableString spannableString = new SpannableString(t.getText());
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            spannableString.setSpan(colorSpan, spannableString.length() - 1, spannableString.length(), Spanned
                    .SPAN_INCLUSIVE_EXCLUSIVE);
            t.setText(spannableString);
        }
    }
}
