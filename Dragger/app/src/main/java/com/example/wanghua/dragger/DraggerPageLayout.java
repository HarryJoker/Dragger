package com.example.wanghua.dragger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DraggerPageLayout extends FrameLayout {

    private ViewDragHelper dragHelper;

    private int curPage = 0;

    private ViewLayout defDragViewLayout = new DraggerPageLayout.ViewLayout(0,0,0,0);

    private Map<Integer, ViewLayout> curViewLayouts;

    private Map<Integer,Map<Integer,ViewLayout>>  cacheLayouts = new HashMap<>();

    private List<View> cacheViews = new ArrayList<>();

    public DraggerPageLayout(@NonNull Context context) {
        super(context);
    }

    public DraggerPageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //第二个参数就是滑动灵敏度的意思 可以随意设置
        dragHelper = ViewDragHelper.create(this, 1.0f, new DraggerCallBack());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final TextView tv_sign = findViewById(R.id.tv_sign);
        tv_sign.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
               defDragViewLayout = new ViewLayout(tv_sign.getLeft(), findViewById(R.id.layout_sign).getTop(), tv_sign.getWidth(), tv_sign.getHeight());
                Logger.d("defDragViewLayout : " + defDragViewLayout);

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理事件
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Logger.d("DragGridLayout onLayout " + left + " " + top + " " + right + " " + bottom);

        layoutDragViews();
    }

    public void nextPage() {
        selectCurrentPage(curPage + 1);
    }

    public void lastPage() {
        selectCurrentPage(curPage - 1);
    }

    public void selectCurrentPage(int page) {
        changePage(page);
    }

    private void changePage(int page) {
        //上一下一页之前更新当前的dragView layouts缓存
        if (curViewLayouts != null && curViewLayouts.size() > 0) {
            cacheLayouts.put(curPage, curViewLayouts);
        }

        Logger.d(cacheLayouts);

        this.curPage = page;
        //有缓存layout，刷新所有dragView位置
        if (cacheLayouts.containsKey(page)) {
            curViewLayouts = cacheLayouts.get(page);
            layoutDragViews();
        } else {
            //没有缓存新页面，清空当前缓存dragView layout 池
            if (curViewLayouts != null) curViewLayouts = null;
            //所有dragView 归位
            resetDragViews();
        }

    }

    //没有缓存layout的dragView全部归位
    private void resetDragViews() {
        for (View dragVeiw : cacheViews) {
            dragVeiw.layout(defDragViewLayout.getLeft(), defDragViewLayout.getTop(), defDragViewLayout.getRight(), defDragViewLayout.getBottom());
        }
    }

    private void layoutDragViews() {
        for (int index = 0; index < cacheViews.size(); index++) {
            View dragView = cacheViews.get(index);
            //根据缓存layout刷新dragView
            if (curViewLayouts.containsKey(index)) {
                ViewLayout drayViewLayout = curViewLayouts.get(index);

                Logger.d("dragView  exist ViewLayout:  " + index + "  " + drayViewLayout);

                dragView.layout(drayViewLayout.getLeft(), drayViewLayout.getTop(), drayViewLayout.getRight(), drayViewLayout.getBottom());
            } else {
                //用不到的缓存view归位
                Logger.d("dragView  not exist ViewLayout:  " + index);
                dragView.layout(defDragViewLayout.getLeft(), defDragViewLayout.getTop(), defDragViewLayout.getRight(), defDragViewLayout.getBottom());
            }
        }
    }

    public void addDragView(View view) {
        this.addView(view);
    }


    class DraggerCallBack extends ViewDragHelper.Callback {

        //这个地方实际上函数返回值为true就代表可以滑动 为false 则不能滑动
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child instanceof LinearLayout) return false;
            return true;
        }

        //这个地方实际上left就代表 你将要移动到的位置的坐标。返回值就是最终确定的移动的位置。
        // 我们要让view滑动的范围在我们的layout之内
        //实际上就是判断如果这个坐标在layout之内 那我们就返回这个坐标值。
        //如果这个坐标在layout的边界处 那我们就只能返回边界的坐标给他。不能让他超出这个范围
        //除此之外就是如果你的layout设置了padding的话，也可以让子view的活动范围在padding之内的.
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //取得左边界的坐标
            final int leftBound = getPaddingLeft();
            //取得右边界的坐标
            final int rightBound = getWidth() - child.getWidth() - leftBound;
            //这个地方的含义就是 如果left的值 在leftbound和rightBound之间 那么就返回left
            //如果left的值 比 leftbound还要小 那么就说明 超过了左边界 那我们只能返回给他左边界的值
            //如果left的值 比rightbound还要大 那么就说明 超过了右边界，那我们只能返回给他右边界的值
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        //纵向的注释就不写了 自己体会
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - child.getHeight() - topBound;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        //需要子view的点击事件时添加，并且在xml文件中设置click属性为true
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        //需要子view的点击事件时添加，并且在xml文件中设置click属性为true
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            Logger.d("onViewReleased " + " xvel:" + xvel + " yvel:" + yvel);
            //缓存dragview 的layout
            cacheDragView(releasedChild);
        }

        private void cacheDragView(View dragView) {
            //缓存dragView的layout
            int postion;
            if (cacheViews.contains(dragView)) {
                postion = cacheViews.indexOf(dragView);
            } else {
                cacheViews.add(dragView);
                postion = cacheViews.indexOf(dragView);
            }
            if (curViewLayouts == null) curViewLayouts = new HashMap<>();
            curViewLayouts.put(postion, new ViewLayout(dragView.getLeft(), dragView.getTop(), dragView.getMeasuredWidth(), dragView.getMeasuredHeight()));
            Logger.d(" viewlayouts: " + curViewLayouts);
        }
    }

    class ViewLayout {
        private int left;
        private int top;
        private int width;
        private int height;
        private float pointX;
        private float pointY;

        public ViewLayout(int l, int t, int w, int h) {
            this.left = l;
            this.top = t;
            this.width = w;
            this.height = h;
        }


        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return left + width;
        }

        public int getBottom() {
            return top + height;
        }

        @Override
        public String toString() {
            return "left:" + left + "top:" + top + "right:" + getRight() + " bottom:" + getBottom();
        }
    }
}
