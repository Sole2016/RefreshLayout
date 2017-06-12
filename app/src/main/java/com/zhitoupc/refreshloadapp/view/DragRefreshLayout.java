package com.zhitoupc.refreshloadapp.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhitoupc.refreshloadapp.R;

public class DragRefreshLayout extends LinearLayout {
    private static final String TAG = "DragRefreshLayout";
    private int lastY;
    private boolean isDispatch = false;
    private int touchSlop;
    private View mTarget;
    private ImageView headImage;
    private int currentState = OnRefreshStatusChangeListener.NORMAL;
    private int headerHeight = 150;
    private int currentHeaderHeight = 0;
    private int lastDelY = 0;//保存上次移动的差值，用于判断方向是否发生了变化
    private float PI = 180;
    private OnRefreshStatusChangeListener refreshStatusChangeListener;

    public DragRefreshLayout(Context context) {
        this(context, null);
    }

    public DragRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setRefreshStatusChangeListener(OnRefreshStatusChangeListener listener) {
        refreshStatusChangeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headImage = new ImageView(getContext());
        headImage.setImageResource(R.mipmap.refresh);
        headImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(headImage,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        headImage.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(currentHeaderHeight, MeasureSpec.EXACTLY));
//        Log.e(TAG, "onMeasure: "+currentHeaderHeight );

        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - currentHeaderHeight - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        headImage.layout(getMeasuredWidth()/2 - headImage.getMeasuredWidth()/2,t+getPaddingTop(),
                headImage.getMeasuredWidth() + getMeasuredWidth()/2,headImage.getMeasuredHeight());
        mTarget.layout(l, t + headImage.getMeasuredHeight(), r, b);
    }

    public void ensureTarget(){
        for (int i = 0; i < getChildCount(); i++) {
            if (!getChildAt(i).equals(headImage)) {
                mTarget = getChildAt(i);
            }
        }
    }

    private void onStatusChange(){
        if (refreshStatusChangeListener != null) {
            refreshStatusChangeListener.onStatusChange(headImage,currentState);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDispatch = false;
                lastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int delY = (int) (ev.getY() - lastY);
                if (Math.abs(delY) > touchSlop) {
                    if (delY > 0) {
                        if (isDispatch || !canChildScrollUp()) {
                            isDispatch = true;
                        } else {
                            isDispatch = false;
                        }
                        return isDispatch;
                    }
                }else{
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                isDispatch = false;
                break;
            default:
                break;
        }
        return isDispatch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int curY = (int) event.getY();
                int delY = curY - lastY;
                if(lastDelY > delY && delY > 0){
                    currentHeaderHeight = Math.max(0,Math.min((currentHeaderHeight - delY/100),headerHeight));
                }else{
                    currentHeaderHeight = Math.max(0,Math.min((currentHeaderHeight + delY/100),headerHeight));
                }
                headImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,currentHeaderHeight));
                float percent = (float)currentHeaderHeight/(float)headerHeight;
                if (refreshStatusChangeListener != null) {
                    refreshStatusChangeListener.changeAnim(headImage,percent);
                }
                invalidate();
                lastDelY = delY;
                break;
            case MotionEvent.ACTION_UP:
                if(currentHeaderHeight > (headerHeight/2)){
                    currentState = OnRefreshStatusChangeListener.REFRESHING;
                    upHeightToMax();
                }else{
                    currentState = OnRefreshStatusChangeListener.NORMAL;
                    lowHeightToMin();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setRefreshing(boolean flag){
        if(flag){
            currentState = OnRefreshStatusChangeListener.REFRESHING;
            upHeightToMax();
        }else{
            currentState = OnRefreshStatusChangeListener.NORMAL;
            lowHeightToMin();
        }
        onStatusChange();
    }

    public void lowHeightToMin(){
        ValueAnimator animator = ValueAnimator.ofInt(currentHeaderHeight,0);
        animator.setDuration(350);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentHeaderHeight = (int) animation.getAnimatedValue();
                headImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,currentHeaderHeight));
            }
        });
        animator.start();
    }

    public void upHeightToMax(){
        ValueAnimator animator = ValueAnimator.ofInt(currentHeaderHeight,headerHeight);
        animator.setDuration(350);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentHeaderHeight = (int) animation.getAnimatedValue();
                headImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,currentHeaderHeight));
                if(currentHeaderHeight == headerHeight){
                    onStatusChange();
                }
            }
        });
        animator.start();
    }

    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /**
     * 刷新状态监听
     */
    public abstract static class OnRefreshStatusChangeListener {
        public static final int NORMAL = 0X00;//默认状态
        public static final int TOUCH = 0X01;//触摸
        public static final int MOVE = 0X02;//滑动
        public static final int REFRESHING = 0X03;//刷新
        public static final int RELEASE = 0X03;//释放

        protected abstract void onStatusChange(View view, int status);

        public void changeAnim(View view,float pro){

        }
    }

}
