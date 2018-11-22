package com.kery.mobilemanager.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kery.mobilemanager.R;


/**
 * Created by huanglongfei on 16/7/9.
 */
public class MyTitleBar extends RelativeLayout {
    private final float DEFAULT_TEXT_SZIE = 16;
    //与父布局margin
    private final int DEFAULT_PARENT_MARGIN = 30;
    //控件之间margin
    private final int DEFAULT_BROTHER_MARGIN = 40;
    private final int DEFAULT_TEXT_COLOR;
    private boolean mShowBack = false;
    private boolean mShowImgOne = false;
    private boolean mShowImgTwo = false;
    private boolean mShowTextOne = false;
    private boolean mShowTextTwo = false;
    private boolean mShowTitle = false;
    private float mTextszie = DEFAULT_TEXT_SZIE;
    private int mTextsColor;
    private float mTitleszie = DEFAULT_TEXT_SZIE;
    private int mTitleColor;
    private String mTextOne = "";
    private String mTextTwo = "";
    private String mTextTitle = "";

    private float mPaddingLeft = DEFAULT_PARENT_MARGIN;
    private float mPaddingRight = DEFAULT_PARENT_MARGIN;
    private float mInnerpadding = DEFAULT_BROTHER_MARGIN;

    private Drawable mImgBack;
    private Drawable mImgOne;
    private Drawable mImgTwo;

    //元素
    private TextView tvTitle;
    private TextView tvOne;
    private TextView tvTwo;

    public TextView getTvTwo() {
        return tvTwo;
    }

    public ImageView getIvBack() {
        return ivBack;
    }

    private ImageView ivBack;
    private ImageView ivOne;
    private ImageView ivTwo;

//    private int tvTitleId = 0x100;
//    private int tvOneId = 0x101;
//    private int tvTwoId = 0x102;
//    private int ivBackId = 0x103;
//    private int ivOneId = 0x104;
//    private int ivTwoId = 0x105;
//    private int vEmptyId = 0x106;

    //Listener
    private IvBackClickListener mIvBackClickListener;
    private IvTwoClickListener mIvTwoClickListener;
    private IvOneClickListener mIvOneClickListener;
    private TvTwoClickListener mTvTwoClickListener;
    private TvOneClickListener mTvOneClickListener;


    public MyTitleBar(Context context) {
        this(context, null);
    }

    public MyTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DEFAULT_TEXT_COLOR = getResources().getColor(R.color.c_FFFFFF);
        mTextsColor = DEFAULT_TEXT_COLOR;
        mTitleColor = DEFAULT_TEXT_COLOR;
        LayoutInflater.from(context).inflate(R.layout.older_title_bar, this, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics
                    ()));
//        int paddingTop = getResources().getDimensionPixelOffset(R.dimen.titlebar_paddingtop);
//        setPadding(0, paddingTop, 0, 0);
//        setBackgroundColor(Color.parseColor("#22000000"));

        ivBack = (ImageView) findViewById(R.id.ivBackId);
        ivOne = (ImageView) findViewById(R.id.ivOneId);
        ivTwo = (ImageView) findViewById(R.id.ivTwoId);
        tvTitle = (TextView) findViewById(R.id.tvTitleId);
        tvOne = (TextView) findViewById(R.id.tvOneId);
        tvTwo = (TextView) findViewById(R.id.tvTwoId);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.my_title_bar);
        final int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.my_title_bar_show_back:
                    mShowBack = ta.getBoolean(attr, false);
                    break;
                case R.styleable.my_title_bar_show_img_one:
                    mShowImgOne = ta.getBoolean(attr, false);

                    break;
                case R.styleable.my_title_bar_show_img_two:
                    mShowImgTwo = ta.getBoolean(attr, false);

                    break;
                case R.styleable.my_title_bar_show_text_one:
                    mShowTextOne = ta.getBoolean(attr, false);

                    break;
                case R.styleable.my_title_bar_show_text_Two:
                    mShowTextTwo = ta.getBoolean(attr, false);

                    break;
                case R.styleable.my_title_bar_text_size:
                    mTextszie = (int) ta.getDimension(attr, DEFAULT_TEXT_SZIE);

                    break;
                case R.styleable.my_title_bar_text_color:
                    mTextsColor = ta.getColor(attr, DEFAULT_TEXT_COLOR);

                    break;
                case R.styleable.my_title_bar_text_one:
                    mTextOne = ta.getString(attr);

                    break;
                case R.styleable.my_title_bar_text_two:
                    mTextTwo = ta.getString(attr);
                    break;
                case R.styleable.my_title_bar_img_back:
                    mImgBack = ta.getDrawable(attr);

                    break;
                case R.styleable.my_title_bar_img_one:
                    mImgOne = ta.getDrawable(attr);

                    break;
                case R.styleable.my_title_bar_img_two:
                    mImgTwo = ta.getDrawable(attr);

                    break;
                case R.styleable.my_title_bar_show_title:
                    mShowTitle = ta.getBoolean(attr, false);

                    break;
                case R.styleable.my_title_bar_title_size:
                    mTitleszie = ta.getDimension(attr, DEFAULT_TEXT_SZIE);

                    break;
                case R.styleable.my_title_bar_title_color:
                    mTitleColor = ta.getColor(attr, DEFAULT_TEXT_COLOR);

                    break;
                case R.styleable.my_title_bar_text_title:
                    mTextTitle = ta.getString(attr);

                    break;
                case R.styleable.my_title_bar_bar_paddingLeft:
                    mPaddingLeft = ta.getDimension(attr, DEFAULT_PARENT_MARGIN);

                    break;
                case R.styleable.my_title_bar_bar_paddingRight:
                    mPaddingRight = ta.getDimension(attr, DEFAULT_PARENT_MARGIN);

                    break;
                case R.styleable.my_title_bar_inner_padding:
                    mInnerpadding = ta.getDimension(attr, DEFAULT_BROTHER_MARGIN);

                    break;
            }

        }
        ta.recycle();
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr) {
//        if (mImgBack != null)
//            ivBack.setImageDrawable(mImgBack);

//        ivBack.setBackground(getResources().getDrawable(R.drawable.circle,null));
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvBackClickListener != null) {
                    mIvBackClickListener.ivBackClick(v);
                } else {
                    //默认点击事件
                    ((Activity) context).finish();
                }
            }
        });

        //title
//        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleszie);
        tvTitle.setText(mTextTitle);
//        tvTitle.setTextColor(mTitleColor);  不用设置textcolor
        if (mImgTwo != null)
            ivTwo.setImageDrawable(mImgTwo);
        ivTwo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvTwoClickListener != null)
                    mIvTwoClickListener.ivTwoClick(v);
            }
        });
        if (mImgOne != null)
            ivOne.setImageDrawable(mImgOne);

        ivOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIvOneClickListener != null)
                    mIvOneClickListener.ivOneClick(v);
            }
        });
        tvTwo.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextszie);
        tvTwo.setText(mTextTwo);
//        tvTwo.setTextColor(mTextsColor);

        tvTwo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvTwoClickListener != null)
                    mTvTwoClickListener.tvTwoClick(v);
            }
        });


        //tvOne
        tvOne.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextszie);
        tvOne.setText(mTextOne);
//        tvOne.setTextColor(mTextsColor);

        tvOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTvOneClickListener != null)
                    mTvOneClickListener.tvOneClick(v);
            }
        });
        show(mShowBack, ivBack);
        show(mShowTitle, tvTitle);
        show(mShowImgTwo, ivTwo);
        show(mShowImgOne, ivOne);
        show(mShowTextTwo, tvTwo);
        show(mShowTextOne, tvOne);
    }

    private void show(boolean show, View view) {
        view.setVisibility(show ? VISIBLE : GONE);
    }

    public String getTvTwoText() {
        return mTextTwo;
    }

    public void setTvTwoText(String textTwo) {
        this.mTextTwo = textTwo;
        tvTwo.setText(mTextTwo);
        tvTwo.setVisibility(View.VISIBLE);
    }

    public void setTvTwoVisible(int visible) {
        tvTwo.setVisibility(visible);
    }

    public void setTvTwoVisible() {
        if (tvTwo.getVisibility() == GONE) {
            tvTwo.setVisibility(VISIBLE);
        }
    }

    public void setTvTwoInvisible() {
        if (tvTwo.getVisibility() == VISIBLE) {
            tvTwo.setVisibility(GONE);
        }
    }


    public void setTvTitleVisible(int visible) {
        tvTitle.setVisibility(visible);
    }


    public void setTvTitleText(String titleText) {
        this.mTextTitle = titleText;
        tvTitle.setText(mTextTitle);
        tvTitle.setVisibility(VISIBLE);
    }

    public void setIvBackVisible(int visible) {
        this.ivBack.setVisibility(visible);
    }

    public void setIvBackClickListener(IvBackClickListener listener) {
        this.mIvBackClickListener = listener;
    }

    public void setIvBackDrawable(Drawable drawable) {
        this.ivBack.setImageDrawable(drawable);
        ivBack.setVisibility(VISIBLE);
    }

    public void setIvTwoVisible(int visible) {
        this.ivTwo.setVisibility(visible);
    }

    public void setIvTwoClickListener(IvTwoClickListener listener) {
        this.mIvTwoClickListener = listener;
    }

    public void setIvTwoDrawable(Drawable drawable) {
        this.ivTwo.setImageDrawable(drawable);
        ivTwo.setVisibility(VISIBLE);
    }

    public void setIvOneClickListener(IvOneClickListener listener) {
        this.mIvOneClickListener = listener;
    }

    public void setIvOneDrawable(Drawable drawable) {
        this.ivOne.setImageDrawable(drawable);
        ivOne.setVisibility(VISIBLE);
    }

    public void setTvTwoClickListener(TvTwoClickListener listener) {
        this.mTvTwoClickListener = listener;
    }

    public void setTvOneClickListener(TvOneClickListener listener) {
        this.mTvOneClickListener = listener;
    }

    public interface IvBackClickListener {
        void ivBackClick(View v);
    }

    public interface IvTwoClickListener {
        void ivTwoClick(View v);
    }

    public interface IvOneClickListener {
        void ivOneClick(View v);
    }

    public interface TvTwoClickListener {
        void tvTwoClick(View v);
    }

    public interface TvOneClickListener {
        void tvOneClick(View v);
    }

}
