package com.kery.mobilemanager.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kery.mobilemanager.utils.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Hanz on 2017/12/4.
 */

public abstract class EasyAdapter<T> extends RecyclerView.Adapter<EasyAdapter.ViewHodler> {

    public HashSet<T> checkedIds = new HashSet<>();//这个set是给需要多选或者单选的列表用的
    List<T> data = new ArrayList<>();
    RecyclerView attchView;
    Bitmap old;//最原始的位图
    private LayerDrawable placeHolder;
    private BitmapDrawable oldDrawable;
    RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (attchView == null || oldDrawable == null)
                return;
            placeHolder.getDrawable(1).setAlpha(isEmpty() ? 255 : 0);
            attchView.invalidateDrawable(placeHolder);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            LogUtil.log(positionStart, itemCount);
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (oldDrawable == null)
                return;
            if (old != null)//已经对图片处理过了
                return;
//            Rect layer_rect = new Rect(0, 0, attchView.getWidth(), attchView.getHeight());
//            LogUtil.log(layer_rect);
            Bitmap bitmap = oldDrawable.getBitmap();
            old = bitmap;
            if (bitmap == null || bitmap.isRecycled()) return;
            float dn = attchView.getResources().getDisplayMetrics().density;
//            LogUtil.log(dn);
            Matrix matrix = new Matrix();
            RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
//            LogUtil.log(rectF.toShortString());
            matrix.setRectToRect(rectF, new RectF(0, 0, 220 * dn, 220 * dn), Matrix.ScaleToFit.CENTER);
            //新的位图
            Bitmap copy = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            LogUtil.log(copy.getWidth(), copy.getHeight());
            oldDrawable = new BitmapDrawable(attchView.getResources(), copy);
            oldDrawable.setGravity(Gravity.CENTER);
            placeHolder.getDrawable(1).setAlpha(isEmpty() ? 255 : 0);
            attchView.invalidateDrawable(placeHolder);
        }
    };
    View.OnAttachStateChangeListener stateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            LogUtil.log("View  离开window ");
            if (oldDrawable != null) {
                Bitmap copy = oldDrawable.getBitmap();
                if (copy != null && !copy.isRecycled())
                    copy.recycle();
            }
            if (old != null && !old.isRecycled())
                old.recycle();
            v.removeCallbacks(runnable);
            v.removeOnAttachStateChangeListener(this);
        }
    };

    public EasyAdapter(@ColorInt int color, @Nullable BitmapDrawable db) {
        registerAdapterDataObserver(observer);
        ColorDrawable colorDrawable = new ColorDrawable(color == 0 ? Color.TRANSPARENT : color);
        if (db == null) {
            placeHolder = new LayerDrawable(new Drawable[]{colorDrawable});
        } else {
            if (db.getBitmap() == null || db.getBitmap().isRecycled())
                throw new IllegalStateException("位图不能为null");
            db.mutate();
            Drawable[] arr = new Drawable[2];
            arr[0] = colorDrawable;
            oldDrawable = db;
            db.setGravity(Gravity.CENTER);
            arr[1] = oldDrawable;
            placeHolder = new LayerDrawable(arr);
        }
    }

    public EasyAdapter(int color, @Nullable BitmapDrawable db, List<T> tList) {
        this(color, db);
        addData(tList);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void addData(List<T> tList) {
        data.clear();
        checkedIds.clear();
        if (tList != null && !tList.isEmpty())
            data.addAll(tList);
        notifyDataSetChanged();
    }

    public void addMore(List<T> list) {
        if (list == null || list.isEmpty())
            return;
        data.addAll(list);
        notifyItemRangeInserted(data.size() - list.size(), list.size());
        if (attchView == null)
            return;
        attchView.scrollToPosition(data.size() - list.size());
    }

    public List<T> getData() {
        return data;
    }

    public void onItemClick(View view, T t) {

    }

    @Override
    public void onBindViewHolder(ViewHodler viewHodler, int i) {
        viewHodler.itemView.setOnClickListener(viewHodler);
        bindBean(viewHodler, data.get(i));
    }

    public abstract void bindBean(ViewHodler vh, T t);

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        attchView = recyclerView;
        attchView.removeOnAttachStateChangeListener(stateChangeListener);
        attchView.addOnAttachStateChangeListener(stateChangeListener);
        attchView.setBackground(placeHolder);
        attchView.post(runnable);
        if (attchView.getLayoutManager() == null)
            LogUtil.log("你忘记设置 LayoutManager了!!!!");
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        //这里也要把这些移除掉，
        attchView.removeCallbacks(runnable);
        attchView = null;
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = View.inflate(viewGroup.getContext(), i, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-1, -2);
        view.setLayoutParams(lp);
        return new ViewHodler(view);
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewLayoutId(data.get(position));
    }

    @LayoutRes
    public abstract int getItemViewLayoutId(T t);

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHodler extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHodler(View itemView) {
            super(itemView);
        }


        public ViewHodler setChecked(@IdRes int checkId, boolean checked) {
            CheckBox cb = (CheckBox) itemView.findViewById(checkId);
            cb.setChecked(checked);
            return this;
        }

        public ViewHodler setClickEvent(int id) {
            itemView.findViewById(id).setOnClickListener(this);
            return this;
        }

        public ViewHodler addText(int id, CharSequence text) {
            TextView textView = (TextView) itemView.findViewById(id);
            StringBuilder stringBuilder = new StringBuilder(textView.getContentDescription())
                    .append(text);
            textView.setText(stringBuilder);
            return this;
        }


        public ViewHodler setText(int id, CharSequence text) {
            TextView textView = (TextView) itemView.findViewById(id);
            textView.setText(text);
            return this;
        }

        public ViewHodler setHint(@IdRes int id, CharSequence hint) {
            TextView textView = (TextView) itemView.findViewById(id);
            textView.setHint(hint);
            return this;
        }

        public ViewHodler setText_and_color(int id, CharSequence text, @ColorInt int textcolor) {
            TextView textView = (TextView) itemView.findViewById(id);
            textView.setText(text);
            textView.setTextColor(textcolor);
            return this;
        }

        public ViewHodler setVisibility(@IdRes int viewId, @Flags int flag) {
            itemView.findViewById(viewId).setVisibility(flag);
            return this;
        }

        public ViewHodler setSlected(@IdRes int viewId, boolean select) {
            itemView.findViewById(viewId).setSelected(select);
            return this;
        }

        public ViewHodler setEnable(@IdRes int viewId, boolean enable) {
            itemView.findViewById(viewId).setEnabled(enable);
            return this;
        }

        public ViewHodler setTextDrawable(@IdRes int id, @DrawableRes int drawableId, Rect rect, int gravity) {
            Drawable drawable = null;
            if (drawableId != 0) {
                drawable = ContextCompat.getDrawable(itemView.getContext(), drawableId);
                drawable.mutate();
                drawable.setBounds(rect);
            }
            TextView textView = (TextView) itemView.findViewById(id);
            switch (gravity) {
                case Gravity.LEFT:
                    textView.setCompoundDrawables(drawable, null, null, null);
                    break;
                case Gravity.TOP:
                    textView.setCompoundDrawables(null, drawable, null, null);
                    break;
                case Gravity.RIGHT:
                    textView.setCompoundDrawables(null, null, drawable, null);
                    break;
                case Gravity.BOTTOM:
                    textView.setCompoundDrawables(null, null, null, drawable);
                    break;
            }
            return this;
        }

        public ViewHodler setImage(int id, Object object) {
            ImageView imageView = (ImageView) itemView.findViewById(id);
            Glide.with(imageView.getContext()).load(object).into(imageView);
            return this;
        }

        public ViewHodler setImage(@IdRes int id, Object object, @DrawableRes int pre, @DrawableRes int error) {
            ImageView imageView = (ImageView) itemView.findViewById(id);
            RequestOptions options = new RequestOptions()
                    .placeholder(pre)
                    .centerCrop()
                    .error(error);
            Glide.with(imageView.getContext()).load(object).apply(options).into(imageView);
            return this;
        }

        @Override
        public void onClick(View v) {
            RecyclerView recyclerView = (RecyclerView) itemView.getParent();
            if (recyclerView.getAdapter() != null && recyclerView.getAdapter() instanceof EasyAdapter) {
                EasyAdapter adapter = (EasyAdapter) recyclerView.getAdapter();
                if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                    //造成这种可能的原因是可能adapterzhengzai 刷新，
                    LogUtil.log("getAdapterPosition", "No position ");
                    return;
                }
                adapter.onItemClick(v, adapter.getData().get(getAdapterPosition()));
            } else {
                LogUtil.log("adapter不是这个SimpleAdapter 的实现类");
            }
        }


        @Retention(RetentionPolicy.SOURCE)
        @IntDef(value = {View.VISIBLE, View.INVISIBLE, View.GONE})
        public @interface Flags {
        }
    }
}
