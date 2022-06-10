package io.github.wong1988.fileselector;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;


public class FingerQQGroup extends FrameLayout {

    private PhotoView photoView;

    private float mDownY;
    private float mTranslationY;
    private float mLastTranslationY;
    private static int MAX_TRANSLATE_Y = 500;
    private final static int MAX_EXIT_Y = 300;
    private final static long DURATION = 150;
    private boolean isAnimate = false;
    private int mTouchSlop;
    private onAlphaChangedListener mOnAlphaChangedListener;


    public FingerQQGroup(Context context) {
        this(context, null);
    }

    public FingerQQGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FingerQQGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mTouchSlop = ViewConfiguration.getTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View childAt = getChildAt(0);

        if (childAt instanceof PhotoView) {
            photoView = (PhotoView) childAt;
        } else {
            throw new RuntimeException("容器内第一个View必须是PhotoView");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean isIntercept = false;
        int action = ev.getAction() & ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
            case MotionEvent.ACTION_MOVE:
                if (null != photoView) {
                    isIntercept = photoView.getScale() == 1 && ev.getPointerCount() == 1 && Math.abs(ev.getRawY() - mDownY) > 2 * mTouchSlop;
                }
                break;
        }
        return isIntercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getRawY();
            case MotionEvent.ACTION_MOVE:
                if (null != photoView) {
                    onOneFingerPanActionMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }
        return true;
    }

    private boolean mVerticalMove = true;

    /**
     * 设置是否可以响应垂直方向滑动
     */
    public void canVerticalMove(boolean verticalMove) {
        mVerticalMove = verticalMove;
    }

    private void onOneFingerPanActionMove(MotionEvent event) {

        if (!mVerticalMove)
            return;

        float moveY = event.getRawY();
        mTranslationY = moveY - mDownY + mLastTranslationY;
        float percent = Math.abs(mTranslationY / (MAX_TRANSLATE_Y + photoView.getHeight()));
        float mAlpha = (1 - percent);
        if (mAlpha > 1) {
            mAlpha = 1;
        } else if (mAlpha < 0) {
            mAlpha = 0;
        }
        setAlpha((int) (mAlpha * 255));
        // 触发回调 根据距离处理其他控件的透明度 显示或者隐藏角标，文字信息等
        if (null != mOnAlphaChangedListener) {
            mOnAlphaChangedListener.onTranslationYChanged(mTranslationY);
        }
        setScrollY(this, -(int) mTranslationY);
    }

    private void onActionUp() {
        if (Math.abs(mTranslationY) > MAX_EXIT_Y) {
            exitWithTranslation(mTranslationY);
        } else {
            resetCallBackAnimation();
        }
    }

    public void exitWithTranslation(float currentY) {
        if (currentY > 0) {
            ValueAnimator animDown = ValueAnimator.ofFloat(mTranslationY, getHeight());
            animDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = (float) animation.getAnimatedValue();
                    setScrollY(FingerQQGroup.this, -(int) fraction);
                }
            });
            animDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    reset();
                    if (null != mOnAlphaChangedListener) {
                        mOnAlphaChangedListener.onCloseListener();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animDown.setDuration(DURATION);
            animDown.setInterpolator(new LinearInterpolator());
            animDown.start();
        } else {
            ValueAnimator animUp = ValueAnimator.ofFloat(mTranslationY, -getHeight());
            animUp.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = (float) animation.getAnimatedValue();
                    setScrollY(FingerQQGroup.this, -(int) fraction);
                }
            });
            animUp.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    reset();
                    if (null != mOnAlphaChangedListener) {
                        mOnAlphaChangedListener.onCloseListener();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animUp.setDuration(DURATION);
            animUp.setInterpolator(new LinearInterpolator());
            animUp.start();
        }
    }

    private void resetCallBackAnimation() {
        ValueAnimator animatorY = ValueAnimator.ofFloat(mTranslationY, 0);
        animatorY.setDuration(DURATION);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (isAnimate) {
                    mTranslationY = (float) valueAnimator.getAnimatedValue();
                    mLastTranslationY = mTranslationY;
                    setScrollY(FingerQQGroup.this, -(int) mTranslationY);
                }
            }
        });
        animatorY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimate) {
                    mTranslationY = 0;
                    setAlpha(255);
                    invalidate();
                    reset();
                }
                isAnimate = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorY.start();
    }


    public interface onAlphaChangedListener {
        void onAlphaChanged(float alpha);

        void onTranslationYChanged(float translationY);

        void onCloseListener();
    }

    // 暴露的回调方法（可根据位移距离或者alpha来改变主UI控件的透明度等
    public void setOnAlphaChangeListener(onAlphaChangedListener alphaChangeListener) {
        mOnAlphaChangedListener = alphaChangeListener;
    }

    private void reset() {
        if (null != mOnAlphaChangedListener) {
            mOnAlphaChangedListener.onTranslationYChanged(mTranslationY);
            mOnAlphaChangedListener.onAlphaChanged(1);
        }
    }


    private void setAlpha(int alpha) {

        ViewGroup parent = (ViewGroup) getParent();

        if (null != parent && parent.getBackground() != null) {
            parent.getBackground().mutate().setAlpha(alpha);
        } else {
            throw new RuntimeException("当前容器的父容器需要设置背景");
        }
    }

    private void setScrollY(ViewGroup viewGroup, int value) {
        viewGroup.scrollTo(viewGroup.getScrollX(), value);
    }
}
