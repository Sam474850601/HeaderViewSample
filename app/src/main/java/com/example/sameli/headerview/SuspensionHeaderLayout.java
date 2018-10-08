package com.example.sameli.headerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by same.li on 2018/10/8.
 */

public class SuspensionHeaderLayout extends ViewGroup {
    private Scroller mScroller;
    public SuspensionHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View topChildView = getChildAt(0);

        measureChild(topChildView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED ));
        View headerView = getChildAt(1);
        measureChild(headerView, MeasureSpec.makeMeasureSpec(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED ));
        View contentView = getChildAt(2);
        measureChild(contentView,
                MeasureSpec.makeMeasureSpec(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), MeasureSpec.EXACTLY ),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight()-getPaddingTop()-getPaddingBottom()
                        -headerView.getMeasuredHeight(), MeasureSpec.EXACTLY ));

    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View topChildView = getChildAt(0);
        View headerView = getChildAt(1);
        View contentView = getChildAt(2);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        topChildView.layout(paddingLeft, paddingTop  ,
                paddingLeft+topChildView.getMeasuredWidth(),paddingTop+ topChildView.getMeasuredHeight() );
        headerView.layout(paddingLeft,
                paddingTop+ topChildView.getMeasuredHeight(),
                paddingLeft+headerView.getMeasuredWidth(),
                paddingTop+ topChildView.getMeasuredHeight()+ headerView.getMeasuredHeight());
        contentView.layout(
                   paddingLeft,
                paddingTop+ topChildView.getMeasuredHeight()+ headerView.getMeasuredHeight(),
                getMeasuredWidth()-getPaddingRight(),
                paddingTop+ topChildView.getMeasuredHeight()+ headerView.getMeasuredHeight()+contentView.getMeasuredHeight());
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private float moveY;

    private float scrollMove = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                moveY = event.getRawY();
                Log.e("SuspensionHeaderLayout", "ACTION_DOWN");
                return  true;
            }
            case MotionEvent.ACTION_MOVE:{

                float currentY = event.getRawY();
                if(Math.abs(getScrollY())>= getChildAt(0).getMeasuredHeight()){
                    return false;
                }
                int value = getScrollY()+(int) (moveY - currentY);
                if(value<0)
                    return false;
                Log.e("SuspensionHeaderLayout", "ACTION_MOVE:"+value);
                scrollTo(getScrollX(), value);
                moveY =currentY;
            }break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{
                //mScroller.startScroll(0, getScrollY(), 0, getScrollY()+(int)( moveY -event.getRawY() ));
                invalidate();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return Math.abs(getScrollY())< getChildAt(0).getMeasuredHeight();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
