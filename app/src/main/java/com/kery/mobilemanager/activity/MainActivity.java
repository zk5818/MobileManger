package com.kery.mobilemanager.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kery.mobilemanager.R;
import com.kery.mobilemanager.adapter.EasyAdapter;
import com.kery.mobilemanager.base.HanBaseActivity;
import com.kery.mobilemanager.model.Card;
import com.kery.mobilemanager.utils.JsonUtils;
import com.kery.mobilemanager.utils.LogUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends HanBaseActivity {

    @Override
    public void initTitleBar() {

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
    public void response_success(int message_type, String s) {
        LogUtil.log(message_type, s);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    SharedPreferences sp;
    Vibrator vibrator;
    int padding;
    LinearLayoutManager manager;
    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
    ItemTouchHelper helper = new ItemTouchHelper(callback) {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            outRect.set(padding, position == 0 ? padding : padding / 2, padding, padding / 2);
        }
    };
    ImageView drawer;

    @Override
    public void initView() {
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics());
        list = (RecyclerView) findViewById(R.id.list);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);
        helper.attachToRecyclerView(list);
        drawerLayout = (DrawerLayout) root;
        drawerLayout.addDrawerListener(drawerListener);
        drawerLayout.setScrimColor(Color.parseColor("#50000000"));
        drawer = (ImageView) findViewById(R.id.drawer);
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(Gravity.START))
                    drawerLayout.openDrawer(Gravity.START);
            }
        });



    }

    DrawerLayout drawerLayout;
    DrawerLayout.SimpleDrawerListener drawerListener = new DrawerLayout.SimpleDrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
            View view = drawerLayout.getChildAt(0);
            ViewCompat.setTranslationX(view, slideOffset * drawerView.getWidth());
        }
    };

    @Override
    public void onViewVisable() {
        super.onViewVisable();
        sp = getSharedPreferences(SPNAME, MODE_PRIVATE);
    }

    @Override
    public ViewGroup getOverLayView() {
        return (FrameLayout) findViewById(R.id.temp);
    }

    @Override
    public void initListener() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START, false);
    }

    RecyclerView list;
    HashMap<String, Integer> imgmap = new HashMap<>();
    EasyAdapter<Card> cardMuliteAdapter;
    String[] types = {"stock", "reports", "temp"};
    int[] imgs = {R.drawable.home_img_tudo,
            R.drawable.home_img_report, R.drawable.home_img_analysis,
    };

    @Override
    public void initData() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        for (int i = 0; i < types.length; i++)
            imgmap.put(types[i], imgs[i]);
        cardMuliteAdapter = new EasyAdapter<Card>(0, new BitmapDrawable(getResources(), BitmapFactory.decodeResource
                (getResources(), getDataEmptyDrawable()))) {
            @Override
            public void bindBean(ViewHodler vh, Card card) {
                Integer imgid = imgmap.get(card.type);
                vh.setText(R.id.content1, card.title)
                        .setText(R.id.content2, card.content1)
                        .setText(R.id.content3, card.content2)
                        .setImage(R.id.cardimg, imgid == null ? 0 : imgid);
                ImageView icon = (ImageView) vh.itemView.findViewById(R.id.icon_right);
                icon.setVisibility(card.is_new == 1 ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onItemClick(View v, Card card) {
                Intent intent = new Intent();
                Class k = null;
                switch (String.valueOf(card.type)) {
                    case "stock":
                        Snackbar.make(v, "功能待开发，请等待", Snackbar.LENGTH_LONG).show();
//                        k = StockManagerActivity.class;
                        break;
                    case "reports":
                        k = SaleActivity.class;
                        intent.putExtra(KEY_TITLE, "销售状况")
                                .putExtra(KEY_RIGHT_ICON, R.drawable.titlebar_adding);
                        break;
                    case "temp":
                        Snackbar.make(v, "功能待开发，请等待", Snackbar.LENGTH_LONG).show();
                        break;
                }
                if (k == null)
                    return;
                intent.setClass(v.getContext(), k);
                startActivity(intent);
            }

            @Override
            public int getItemViewLayoutId(Card card) {
                return R.layout.item_main_app3;
            }
        };
        list.setAdapter(cardMuliteAdapter);
        List<Card> cardList = JsonUtils.parseList(Card.class, mData, "data", "pages");
        //权限控制，没有权限就不显示出来
        sortCard(cardList);
    }

    private void sortCard(List<Card> cardList) {
        cardMuliteAdapter.addData(cardList);
    }

    @Override
    protected void onDestroy() {
        vibrator.cancel();
        super.onDestroy();
    }

    @Override
    public int getDataEmptyDrawable() {
        return R.drawable.icon_no_message;
    }

    String mData = "{\"status\":true,\"msg\":\"\",\"data\":{\"pages\":[{\"sort\":1,\"reddot\":0,\"title\":\"库存管理\",\"type\":\"stock\",\"content1\":\"库存000\",\"is_new\":0,\"content2\":\"库存999\"},{\"sort\":2,\"reddot\":0,\"title\":\"经营状况\",\"type\":\"reports\",\"content1\":\"今日收益8888\",\"is_new\":0,\"content2\":\"当月月收益88888\"},{\"sort\":3,\"reddot\":0,\"title\":\"客源管理\",\"type\":\"temp\",\"content1\":\"待分配客户0\",\"is_new\":0,\"content2\":\"进行中客户0\"}]}}";
}