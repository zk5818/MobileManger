package com.kery.mobilemanager.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kery.mobilemanager.R;
import com.kery.mobilemanager.adapter.RecyclerViewAdapter;
import com.kery.mobilemanager.adapter.RecyclerViewHolder;
import com.kery.mobilemanager.base.HanBaseActivity;
import com.kery.mobilemanager.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class StockManagerActivity extends HanBaseActivity {
    @Override
    public void initTitleBar() {
        getIntent().putExtra(KEY_TITLE, "库存管理");
        getIntent().putExtra(KEY_RIGHT_ICON, R.drawable.titlebar_adding);
        super.initTitleBar();
    }

    @Override
    public void message(Message message) {

    }

    @Override
    public void dofirstRequest() {

    }

    @Override
    public void response_error(int message_type, String error_text) {

    }

    @Override
    public void response_success(int message_type,String s) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_stock_manager;
    }

    RecyclerView rvItems;
    SwipeRefreshLayout refreshLayout;

    @Override
    public void initView() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);
    }

    @Override
    public ViewGroup getOverLayView() {
        return rvItems;
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //进行网络请求
                LogUtil.log("开始刷新请求");
            }
        });
    }

    RecyclerViewAdapter mAdapter;

    @Override
    public void initData() {
        List<User> users = new ArrayList<>();
        users.add(new User("1", "王志军"));
        users.add(new User("2", "王或军"));
        users.add(new User("3", "了志军"));
        users.add(new User("4", "例就掉"));
        rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvItems.addItemDecoration(decoration);
        rvItems.setAdapter(mAdapter = new RecyclerViewAdapter<User>(this, R.layout.item_search_house, users) {
            @Override
            public void convert(RecyclerViewHolder holder, final User mItem, int position) {
                holder.setText(R.id.house_info, mItem.name);
            }
        });
    }

    @Override
    public void onRightImageClick(ImageView imageView) {
        super.onRightImageClick(imageView);
        startActivity(new Intent(imageView.getContext(), AddDeviceActivity.class)
                .putExtra(KEY_TITLE, "添加产品")
                .putExtra(KEY_RIGHT_TEXT, "保存"));
    }

    RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
        private float mDividerHeight;
        private Paint mPaint = new Paint();
        private Paint mPaint1 = new Paint();

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = 60;
                mDividerHeight = 60;
            }
            outRect.left = 200;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int childCount = parent.getChildCount();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.RED);
            mPaint1.setAntiAlias(true);
            mPaint1.setColor(Color.WHITE);
            for (int i = 0; i < childCount; i++) {
                View view = parent.getChildAt(i);
                int index = parent.getChildAdapterPosition(view);
                //第一个ItemView不需要绘制
                if (index == 0) {
                    continue;
                }
                float dividerTop = view.getTop() - mDividerHeight;
                float dividerLeft = parent.getPaddingLeft();
                float dividerBottom = view.getTop() - mDividerHeight + 1;
                float dividerRight = parent.getWidth() - parent.getPaddingRight();
                c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, mPaint);

            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round, options);
            mPaint.setColor(Color.GRAY);
            for (int j = 0; j < childCount; j++) {
                View view = parent.getChildAt(j);
                int index = parent.getChildAdapterPosition(view);
                if (index == 0) {
                    c.drawLine(100, view.getTop() + view.getHeight() / 2, 100, view.getTop() + view.getHeight(), mPaint);
                    c.drawCircle(100, view.getTop() + view.getHeight() / 2, 20, mPaint);
                    c.drawCircle(100, view.getTop() + view.getHeight() / 2, 19, mPaint1);
                } else if (index == childCount - 1) {
                    c.drawLine(100, view.getTop() - mDividerHeight, 100, view.getTop() + view.getHeight() / 2, mPaint);
                    c.drawCircle(100, view.getTop() + view.getHeight() / 2, 20, mPaint);
                } else {
                    c.drawLine(100, view.getTop() - mDividerHeight, 100, view.getTop() + view.getHeight(), mPaint);
                    c.drawCircle(100, (view.getBottom() + view.getTop() - mDividerHeight) / 2, 20, mPaint);
                    Rect rect = new Rect();
                    rect.set(70, (int) ((view.getBottom() + view.getTop() - mDividerHeight) / 2 - 30), 130, (int) (view.getBottom() + view.getTop() - mDividerHeight) / 2 + 30);
                    c.drawBitmap(bitmap, null, rect, null);
                }

            }
        }
    };

    private class User {
        private String id;
        private String name;

        private User(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
