package io.github.wong1988.fileselector;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.List;

import io.github.wong1988.fileselector.adapter.PreviewAdapter;
import io.github.wong1988.fileselector.entity.ImgInfo;
import io.github.wong1988.fileselector.listener.PreviewListener;
import io.github.wong1988.fileselector.manager.ScrollLinearLayoutManager;

/**
 * 支持删除时，顶部阴影不会去除，并且按钮持续显示，并且不支持下拉关闭，点击图片，显示、隐藏顶部按钮
 */
public class PreviewImgView extends FrameLayout {

    private final TextView mTv;
    private final LinearLayout mLl;
    private final RecyclerView mRv;
    private final ImageView mBack;
    private final ImageView mDelete;
    private final PreviewAdapter mAdapter;
    private int mCurrentItem = -1;
    private PreviewListener mPreviewListener;
    private final ObjectAnimator mAnimator;
    private final List<ImgInfo> mDeleteList = new ArrayList<>();

    public PreviewImgView(@NonNull Context context) {
        this(context, null);
    }

    public PreviewImgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewImgView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.github_a_pv, this, true);
        mTv = findViewById(R.id.github_a_pv_tv);
        mLl = findViewById(R.id.github_a_pv_ll);
        mRv = findViewById(R.id.github_a_pv_rv);
        mBack = findViewById(R.id.github_a_pv_back);
        mDelete = findViewById(R.id.github_a_pv_delete);

        ScrollLinearLayoutManager layout = new ScrollLinearLayoutManager(context);
        layout.setOrientation(RecyclerView.HORIZONTAL);
        mRv.setLayoutManager(layout);

        mAdapter = new PreviewAdapter(layout);
        mRv.setAdapter(mAdapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mRv);

        mRv.addOnScrollListener(new RecyclerViewPageChangeListenerHelper(pagerSnapHelper, new OnPageChangeListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mPreviewListener != null)
                    mPreviewListener.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mPreviewListener != null)
                    mPreviewListener.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCurrentItem != position && mCurrentItem != -1) {
                    mAdapter.notifyItemChanged(mCurrentItem);
                    mCurrentItem = position;
                    setTv(mCurrentItem);
                }
                if (mPreviewListener != null)
                    mPreviewListener.onPageSelected(position);

            }
        }));

        mAdapter.setListener(new PreviewAdapter.Listener() {
            @Override
            public void onAlphaChanged(float alpha) {
                if (mPreviewListener != null)
                    mPreviewListener.onAlphaChanged(alpha);
            }

            @Override
            public void onTranslationYChanged(float translationY) {
                if (mPreviewListener != null)
                    mPreviewListener.onTranslationYChanged(translationY);

                mLl.setVisibility(translationY == 0 && mAnimator.isRunning() ? VISIBLE : GONE);
            }

            @Override
            public void onCloseListener() {
                if (mPreviewListener != null)
                    mPreviewListener.onClosed(mDeleteList, mAdapter.getAttachData());
            }

            @Override
            public void onClick(int position, ImgInfo imgInfo) {
                if (mShowDelete) {
                    mLl.setVisibility(mLl.getVisibility() == VISIBLE ? GONE : VISIBLE);
                } else {
                    if (mPreviewListener != null)
                        mPreviewListener.onClosed(mDeleteList, mAdapter.getAttachData());
                }
            }
        });

        mAnimator = ObjectAnimator.ofFloat(mTv, "alpha", 1f, 0f);
        mAnimator.setDuration(3000);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLl.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mShowDelete)
                    mLl.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewListener != null)
                    mPreviewListener.onClosed(mDeleteList, mAdapter.getAttachData());
            }
        });

        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentItem < 0 || mAdapter.getAttachData().size() <= mCurrentItem)
                    return;

                if (mAdapter.getAttachData().get(mCurrentItem).isForbidDelete())
                    // 当前图片不让删除
                    return;

                mDeleteList.add(mAdapter.getAttachData().get(mCurrentItem));
                mAdapter.getAttachData().remove(mCurrentItem);
                mAdapter.notifyItemRemoved(mCurrentItem);

                if (mCurrentItem == mAdapter.getItemCount()) {
                    mCurrentItem = mCurrentItem - 1;
                }

                if (mCurrentItem == -1) {

                    if (mPreviewListener != null)
                        mPreviewListener.onClosed(mDeleteList, mAdapter.getAttachData());

                } else {
                    setTv(mCurrentItem);

                    if (mPreviewListener != null)
                        mPreviewListener.onPageSelected(mCurrentItem);
                }
            }
        });
    }


    private boolean mShowDelete = false;
    private boolean mShowBack = false;

    /**
     * @param supportBack   是否支持返回
     * @param supportDelete 是否支持删除，true时不支持下拉关闭
     */
    public void setButton(boolean supportBack, boolean supportDelete) {
        mShowDelete = supportDelete;
        mShowBack = supportBack;
        mBack.setVisibility(mShowBack ? VISIBLE : INVISIBLE);
        mDelete.setVisibility(mShowDelete ? VISIBLE : INVISIBLE);

        mAdapter.setVerticalMove(mVerticalClose && !mShowDelete);
    }

    private boolean mVerticalClose = true;

    /**
     * 垂直滑动关闭页面，注：如果显示删除按钮设置为true也不会生效
     */
    public void setVerticalMoveClose(boolean b) {
        mVerticalClose = b;
        mAdapter.setVerticalMove(mVerticalClose && !mShowDelete);
    }

    /**
     * 添加数据源
     */
    public void addData(List<ImgInfo> data) {
        boolean rvEmpty = mAdapter.getItemCount() == 0;
        mAdapter.addData(data);
        if (rvEmpty && data != null && data.size() > 0) {
            mCurrentItem = 0;
            setTv(mCurrentItem);
        }
    }

    /**
     * 设置展示当前显示的位置
     */
    public void setCurrentItem(int position) {
        mRv.scrollToPosition(position);

        if (position >= 0 && position < mAdapter.getItemCount()) {
            mCurrentItem = position;
            setTv(mCurrentItem);
        }
    }

    /**
     * 获取当前显示的item索引
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * 设置监听
     */
    public void setPreviewListener(PreviewListener previewListener) {
        this.mPreviewListener = previewListener;
    }

    public void onBackPress() {
        if (mPreviewListener != null)
            mPreviewListener.onClosed(mDeleteList, mAdapter.getAttachData());
    }

    @SuppressLint("SetTextI18n")
    private void setTv(int position) {
        mLl.setVisibility(VISIBLE);
        mTv.setText(position + 1 + " / " + mAdapter.getItemCount());
        mAnimator.cancel();
        mAnimator.start();
    }

    public static class RecyclerViewPageChangeListenerHelper extends RecyclerView.OnScrollListener {

        private final SnapHelper mSnapHelper;
        private final OnPageChangeListener onPageChangeListener;
        private int oldPosition = -1;// 防止同一Position多次触发

        public RecyclerViewPageChangeListenerHelper(SnapHelper snapHelper, OnPageChangeListener onPageChangeListener) {
            this.mSnapHelper = snapHelper;
            this.onPageChangeListener = onPageChangeListener;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (onPageChangeListener != null) {
                onPageChangeListener.onScrolled(recyclerView, dx, dy);
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int position = 0;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 获取当前选中的itemView
            View view = mSnapHelper.findSnapView(layoutManager);
            if (view != null) {
                // 获取itemView的position
                position = layoutManager.getPosition(view);
            } else
                position = -1;
            if (onPageChangeListener != null) {
                onPageChangeListener.onScrollStateChanged(recyclerView, newState);
                if (oldPosition != position) {
                    oldPosition = position;
                    onPageChangeListener.onPageSelected(position);
                }
            }
        }
    }

    private interface OnPageChangeListener {
        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onPageSelected(int position);
    }
}
