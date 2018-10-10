package com.example.sameli.headerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by same.li on 2018/10/8.
 * 悬浮标题栏滑动组件。类似谷歌的联动组件
 * 这是个线性的布局，并且要求子View只能是3个组件。
 */

public class SuspensionLinearLayout extends ViewGroup {
    private Scroller mScroller;
    private VelocityTracker velocityTracker;
    int mTouchSlop;
    int mMinimumVelocity;
    int mMaximumVelocity;

    private int extraHeight;

    /**
     * 设置滑动距离， 滑动距离等于  第一个view高度减去 height的值
     * @param height
     */
    public void setExternalHeight(int height) {
        this.extraHeight = height;
    }


    public SuspensionLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(3!=getChildCount()){
            throw new IllegalArgumentException("子view必须是3个。");
        }
        View topHeaderChildView = getChildAt(0);
        headerViewHeight = topHeaderChildView.getMeasuredHeight();
        measureChild(topHeaderChildView, MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        View suspensionView = getChildAt(1);
        measureChild(suspensionView, MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        View contentView = getChildAt(2);
        measureChild(contentView,
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                        - suspensionView.getMeasuredHeight()-(extraHeight>0?headerViewHeight - extraHeight:0), MeasureSpec.EXACTLY));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View topChildView = getChildAt(0);
        View headerView = getChildAt(1);
        View contentView = getChildAt(2);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        topChildView.layout(paddingLeft, paddingTop,
                paddingLeft + topChildView.getMeasuredWidth(), paddingTop + topChildView.getMeasuredHeight());
        headerView.layout(paddingLeft,
                paddingTop + topChildView.getMeasuredHeight(),
                paddingLeft + headerView.getMeasuredWidth(),
                paddingTop + topChildView.getMeasuredHeight() + headerView.getMeasuredHeight());
        contentView.layout(
                paddingLeft,
                paddingTop + topChildView.getMeasuredHeight() + headerView.getMeasuredHeight(),
                getMeasuredWidth() - getPaddingRight(),
                paddingTop + topChildView.getMeasuredHeight() + headerView.getMeasuredHeight() + contentView.getMeasuredHeight()+(extraHeight>0? headerViewHeight-extraHeight*2:0));
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private float moveY;
    private float downY;

    private float scrollMove = 0;

    int mPointerId;

    float yVelocity;
    int headerViewHeight;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (null == velocityTracker) {
            velocityTracker = VelocityTracker.obtain();
        }

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                super.dispatchTouchEvent(event);
                downY = moveY = event.getY();
                mPointerId = event.getPointerId(0);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                velocityTracker.addMovement(event);
                float currentY = event.getY();
                int value = getScrollY() + (int) (moveY - currentY);
                if (value < 0) {
                    value = 0;
                }
                if(extraHeight>0){
                    if (value > headerViewHeight - extraHeight) {
                        value = headerViewHeight - extraHeight;
                    }
                }else {
                    if (value > headerViewHeight) {
                        value = headerViewHeight;
                    }
                }

                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                moveY = currentY;
                yVelocity = velocityTracker.getYVelocity(mPointerId);
                scrollTo(getScrollX(), value);

                if(null != onSuspensionListener){
                    float  currentPercent = getScrollY()/ (float)(extraHeight>0?headerViewHeight - extraHeight:headerViewHeight);
                    if(currentPersent != currentPercent){
                        onSuspensionListener.onScroll(  currentPersent =currentPercent );
                    }
                }
                return super.dispatchTouchEvent(event);
            }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {

                //  mScroller.startScroll(0, 0,  0, getChildAt(0).getMeasuredHeight());
                boolean isSwipeDown =  event.getY() -downY >0;
                if(Math.abs(yVelocity) >= mMaximumVelocity){
                    scrollTo(0, isSwipeDown?0:(extraHeight>0?headerViewHeight - extraHeight:headerViewHeight));
                }
                else if (Math.abs(yVelocity) >= mMinimumVelocity) {
                    if(Math.abs(event.getY() -downY) > mTouchSlop ){

                        if(isSwipeDown){

                            if(0 !=  getScrollY()){
                                if(extraHeight>0){
                                    if(getScrollY()<  headerViewHeight - extraHeight){
                                        mScroller.fling(0, getScrollY(), 0, (int) -(yVelocity*2), 0, 0 , 0, headerViewHeight - extraHeight);
                                        invalidate();
                                    }
                                }else {
                                    if(getScrollY()<  headerViewHeight){
                                        mScroller.fling(0, getScrollY(), 0, (int) -(yVelocity*2), 0, 0 , 0,headerViewHeight);
                                        invalidate();
                                    }
                                }
                            }
                        }else {

                            if(extraHeight>0){
                                if(headerViewHeight - extraHeight !=  getScrollY()){
                                    mScroller.fling(0,  getScrollY(), 0, (int) -(yVelocity*2), 0, 0 , 0,headerViewHeight - extraHeight  );
                                    invalidate();
                                }
                            }else {
                                if(getScrollY() !=   headerViewHeight){
                                    mScroller.fling(0,  getScrollY(), 0, (int) -(yVelocity*2), 0, 0 , 0,headerViewHeight  );
                                    invalidate();
                                }
                            }
                        }
                    }
                }
                velocityTracker.clear();
                velocityTracker.recycle();
                velocityTracker = null;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private float currentPersent;

    private  OnSuspensionListener onSuspensionListener;

    public void setOnSuspensionListener(OnSuspensionListener onSuspensionListener) {
        this.onSuspensionListener = onSuspensionListener;
    }

    //滚动回调
    public interface OnSuspensionListener{
        void  onScroll(float persent);
    }

    /**
     * 显示全部视图
     */
    public void  show(){
        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
        }
        scrollTo(0, 0);
    }

    /**
     * 隐藏顶部文件
     */
    public void  hide(){
        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
        }
        scrollTo(0, extraHeight>0?headerViewHeight - extraHeight:headerViewHeight);
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
            if(null != onSuspensionListener){
                float currentPercent =  mScroller.getCurrY() / (float)(extraHeight>0?headerViewHeight - extraHeight:headerViewHeight);
                if(currentPersent != currentPercent){
                    onSuspensionListener.onScroll(  currentPersent =currentPercent );
                }
            }
        }
    }
}
