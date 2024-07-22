package com.hjq.demo.ui.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.hjq.base.BaseAdapter;
import com.hjq.demo.R;
import com.hjq.demo.app.AppAdapter;
import com.hjq.demo.http.glide.GlideApp;
import com.hjq.demo.http.model.DeviceType;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2021/02/28
 *    desc   : 设备型号 适配器
 */
public final class DeviceTypeAdapter extends AppAdapter<DeviceType> implements BaseAdapter.OnItemClickListener {

    public static final int TAB_MODE_DESIGN = 1;
    public static final int TAB_MODE_SLIDING = 2;

    /** 当前选中条目位置 */
    private int mSelectedPosition = -1;

    /** 导航栏监听对象 */
    @Nullable
    private OnDevTypeListener mListener;

    /** DevType 样式 */
    private final int mDevTypeMode;

    /** DevType 宽度是否固定 */
    private final boolean mFixed;

    public DeviceTypeAdapter(Context context) {
        this(context, TAB_MODE_DESIGN, true);
    }

    public DeviceTypeAdapter(Context context, int tabMode, boolean fixed) {
        super(context);
        mDevTypeMode = tabMode;
        mFixed = fixed;
        setOnItemClickListener(this);
        registerAdapterDataObserver(new DevTypeAdapterDataObserver());
    }

    @Override
    public int getItemViewType(int position) {
        return mDevTypeMode;
    }

    @NonNull
    @Override
    public BaseAdapter<?>.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TAB_MODE_DESIGN:
                return new DesignViewHolder();
            case TAB_MODE_SLIDING:
                return new SlidingViewHolder();
            default:
                throw new IllegalArgumentException("are you ok?");
        }
    }

    @Override
    protected RecyclerView.LayoutManager generateDefaultLayoutManager(Context context) {
        if (mFixed) {
            return new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false);
        } else {
            return new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // 禁用 RecyclerView 条目动画
        recyclerView.setItemAnimator(null);
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int position) {
        if (mSelectedPosition == position) {
            return;
        }
        notifyItemChanged(mSelectedPosition);
        mSelectedPosition = position;
        notifyItemChanged(position);
    }

    /**
     * 设置导航栏监听
     */
    public void setOnDevTypeListener(@Nullable OnDevTypeListener listener) {
        mListener = listener;
    }

    /**
     * {@link OnItemClickListener}
     */

    @Override
    public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
        if (mSelectedPosition == position) {
            return;
        }

        if (mListener == null) {
            mSelectedPosition = position;
            notifyDataSetChanged();
            return;
        }

        if (mListener.onDevTypeSelected(recyclerView, position)) {
            mSelectedPosition = position;
            notifyDataSetChanged();
        }
    }

    private final class DesignViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTitleView;
        private final ImageView mImgeView;

        private DesignViewHolder() {
            super(R.layout.rc_devtype_item_design);
            mTitleView = findViewById(R.id.tv_devtype_title);
            mImgeView = findViewById(R.id.iv_devtype_avatar);
            if (!mFixed) {
                return;
            }
            View itemView = getItemView();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(layoutParams);
        }

        @Override
        public void onBindView(int position) {
            mTitleView.setText(getItem(position).getTitle());
            mTitleView.setSelected(mSelectedPosition == position);
            if(getItem(position).getThumbpic()!=null && !getItem(position).getThumbpic().isEmpty()){
                // 显示圆角的 ImageView
                GlideApp.with(getContext())
                        .load(getItem(position).getThumbpic())
                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_6))))
                        .into(mImgeView);
            }else{
                GlideApp.with(getContext())
                        .load(R.drawable.device)
                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_6))))
                        .into(mImgeView);
            }

        }
    }

    private final class SlidingViewHolder extends AppAdapter<?>.ViewHolder
            implements ValueAnimator.AnimatorUpdateListener {

        private final int mDefaultTextSize;
        private final int mSelectedTextSize;

        private final TextView mTitleView;
        private final ImageView mImgeView;

        private SlidingViewHolder() {
            super(R.layout.tab_item_sliding);
            mTitleView = findViewById(R.id.tv_devtype_title);
            mImgeView = findViewById(R.id.iv_devtype_avatar);

            mDefaultTextSize = (int) getResources().getDimension(R.dimen.sp_14);
            mSelectedTextSize = (int) getResources().getDimension(R.dimen.sp_15);

            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDefaultTextSize);


            if (!mFixed) {
                return;
            }
            View itemView = getItemView();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            itemView.setLayoutParams(layoutParams);
        }

        @Override
        public void onBindView(int position) {
            mTitleView.setText(getItem(position).getTitle());
            mTitleView.setSelected(mSelectedPosition == position);
            if(getItem(position).getThumbpic()!=null && !getItem(position).getThumbpic().isEmpty()){
                // 显示圆角的 ImageView
                GlideApp.with(getContext())
                        .load(getItem(position).getThumbpic())
                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_6))))
                        .into(mImgeView);
            }else{
                GlideApp.with(getContext())
                        .load(R.drawable.device)
                        .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_6))))
                        .into(mImgeView);
            }



            int textSize = (int) mTitleView.getTextSize();
            if (mSelectedPosition == position) {
                if (textSize != mSelectedTextSize) {
                    startAnimator(mDefaultTextSize, mSelectedTextSize);
                }
                return;
            }

            if (textSize != mDefaultTextSize) {
                startAnimator(mSelectedTextSize, mDefaultTextSize);
            }
        }

        private void startAnimator(int start, int end) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
            valueAnimator.addUpdateListener(this);
            valueAnimator.setDuration(100);
            valueAnimator.start();
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) animation.getAnimatedValue());
        }
    }

    /**
     * 数据改变监听器
     */
    private final class DevTypeAdapterDataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            refreshLayoutManager();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {}

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {}

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            refreshLayoutManager();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            refreshLayoutManager();
            if (getSelectedPosition() > positionStart - itemCount) {
                setSelectedPosition(positionStart - itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {}

        private void refreshLayoutManager() {
            if (!mFixed) {
                return;
            }
            RecyclerView recyclerView = getRecyclerView();
            if (recyclerView == null) {
                return;
            }
            recyclerView.setLayoutManager(generateDefaultLayoutManager(getContext()));
        }
    }

    /**
     * DevType 监听器
     */
    public interface OnDevTypeListener {

        /**
         * DevType 被选中了
         */
        boolean onDevTypeSelected(RecyclerView recyclerView, int position);
    }
}