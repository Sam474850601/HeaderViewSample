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
 */

public class SuspensionLinearLayout extends ViewGroup {
    private Scroller mScroller;
    private VelocityTracker velocityTracker;
    int mTouchSlop;
    int mMinimumVelocity;
    int mMaximumVelocity;

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
        View topChildView = getChildAt(0);

        measureChild(topChildView, MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        View headerView = getChildAt(1);
        measureChild(headerView, MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        View contentView = getChildAt(2);
        measureChild(contentView,
                MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                        - headerView.getMeasuredHeight(), MeasureSpec.EXACTLY));

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
                paddingTop + topChildView.getMeasuredHeight() + headerView.getMeasuredHeight() + contentView.getMeasuredHeight());
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private float moveY;
    private float downY;

    private float scrollMove = 0;

    int mPointerId;

    float yVelocity;

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
                int measuredHeight = getChildAt(0).getMeasuredHeight();
                int value = getScrollY() + (int) (moveY - currentY);
                if (value < 0) {
                    value = 0;
                }
                if (value > measuredHeight) {
                    value = measuredHeight;
                }
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                moveY = currentY;
                yVelocity = velocityTracker.getYVelocity(mPointerId);
                scrollTo(getScrollX(), value);
                return super.dispatchTouchEvent(event);
            }
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                Log.e("SuspensionHeaderLayout", "dispatchTouchEvent ACTION_UP");
                //  mScroller.startScroll(0, 0,  0, getChildAt(0).getMeasuredHeight());
                if ((Math.abs(yVelocity) >= mMinimumVelocity)) {
                    if(Math.abs(event.getY() -downY) > mTouchSlop ){
                        boolean isSwipeDown =  event.getY() -downY >0;
                        Log.e("SuspensionHeaderLayout", "isSwipeDown "+isSwipeDown);
                        if(isSwipeDown){

                            if(0 !=  getScrollY()){
                                if(getScrollY()<  getChildAt(0).getMeasuredHeight()){
                                    mScroller.fling(0, getScrollY(), 0, (int) -yVelocity, 0, 0 , 0, getChildAt(0).getMeasuredHeight());
                                    invalidate();
                                }
                            }

                        }else {
                            int measuredHeight = getChildAt(0).getMeasuredHeight();
                            if( getChildAt(0).getMeasuredHeight() !=  getScrollY()){

                                mScroller.fling(0,  getScrollY(), 0, (int) -yVelocity, 0, 0 , 0,measuredHeight  );
                                invalidate();
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



    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
