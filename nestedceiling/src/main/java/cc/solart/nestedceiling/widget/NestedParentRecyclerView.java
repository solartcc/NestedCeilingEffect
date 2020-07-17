package cc.solart.nestedceiling.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.solart.nestedceiling.utils.FindTarget;
import cc.solart.nestedceiling.utils.FlingHelper;

public class NestedParentRecyclerView extends RecyclerView implements NestedScrollingParent3, NestedScrollingParent2 {
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private NestedScrollingParentHelper mParentHelper;
    private FlingHelper mFlingHelper;
    private ViewGroup mContentView;
    private int mTotalDy = 0;
    // 记录y轴加速度
    private int mVelocityY = 0;
    private Float mLastY = 0f;
    private int mNestedYOffsets = 0;
    private boolean mIsStartChildFling = false;
    private boolean mIsChildAttachedToTop = false;
    private boolean mIsChildDetachedFromTop = true;
    private final ArrayList<OnChildAttachStateListener> mOnChildAttachStateListeners =
            new ArrayList<>();

    public NestedParentRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NestedParentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedParentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        mParentHelper = new NestedScrollingParentHelper(this);
        mFlingHelper = new FlingHelper(getContext());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layoutManager) {
        super.setLayoutManager(layoutManager);
        mLayoutManager = layoutManager;
    }

    public void addOnChildAttachStateListener(OnChildAttachStateListener listener) {
        mOnChildAttachStateListeners.add(listener);
    }

    /**
     * 判断是否是嵌套滑动布局的位置，可覆写该方法定义位置条件
     * 默认最后一个 item 的位置
     *
     * @param child
     * @return
     */
    protected boolean isTargetPosition(View child) {
        if (mLayoutManager != null && mAdapter != null) {
            int position = mLayoutManager.getPosition(child);
            return position + 1 == mAdapter.getItemCount();
        }
        return false;
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        if (isTargetPosition(child)) {
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            lp.height = getMeasuredHeight();
            child.setLayoutParams(lp);
            mContentView = (ViewGroup) child;
        }
    }

    @Override
    public void onChildDetachedFromWindow(@NonNull View child) {
        if (isTargetPosition(child)) {
            mContentView = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean isTouchInChildArea = (mContentView != null) && (e.getY() >= mContentView.getTop());
        // 此控件滑动到底部或者触摸区域在子嵌套布局不拦截事件
        if (isScrollEnd() || isTouchInChildArea) {
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mLastY = e.getY();
            mNestedYOffsets = 0;
            mVelocityY = 0;
            stopScroll();
        }
        RecyclerView child = FindTarget.findChildScrollTarget(mContentView);
        boolean handle = false;
        if (child != null) {
            // 如果此控件已经滑动到底部，需要让子嵌套布局滑动剩余的距离
            // 或者子嵌套布局向下还未到顶部，也需要让子嵌套布局先滑动一段距离
            if (isScrollEnd() || (handle = !isChildScrollTop(child))) {
                int deltaY = (int) (mLastY - e.getY());
                child.scrollBy(0, deltaY);
                if (handle) {
                    // 子嵌套布局向下滑动时，要记录y轴的偏移量
                    mNestedYOffsets += deltaY;
                }
            }
        }
        mLastY = e.getY();
        // 更新触摸事件的偏移位置，以保证视图平滑的连贯性
        e.offsetLocation(0, mNestedYOffsets);
        return handle || super.onTouchEvent(e);
    }

    private boolean isScrollEnd() {
        return !canScrollVertically(1);
    }

    private boolean isChildScrollTop(RecyclerView child) {
        return !child.canScrollVertically(-1);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (mIsStartChildFling) {
            mTotalDy = 0;
            mIsStartChildFling = false;
        }
        mTotalDy += dy;
        boolean attached = dy > 0 && isScrollEnd();
        if (attached && mIsChildDetachedFromTop) {
            mIsChildAttachedToTop = true;
            mIsChildDetachedFromTop = false;
            final int listenerCount = mOnChildAttachStateListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                OnChildAttachStateListener listener = mOnChildAttachStateListeners.get(i);
                listener.onChildAttachedToTop();
            }
        }

        boolean detached = dy < 0 && !isScrollEnd();
        if (detached && mIsChildAttachedToTop) {
            RecyclerView child = FindTarget.findChildScrollTarget(mContentView);
            if (child == null || isChildScrollTop(child)) {
                mIsChildDetachedFromTop = true;
                mIsChildAttachedToTop = false;
                final int listenerCount = mOnChildAttachStateListeners.size();
                for (int i = 0; i < listenerCount; i++) {
                    OnChildAttachStateListener listener = mOnChildAttachStateListeners.get(i);
                    listener.onChildDetachedFromTop();
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            dispatchChildFling();
        }
    }

    private void dispatchChildFling() {
        if (mVelocityY != 0) {
            double splineFlingDistance = mFlingHelper.getSplineFlingDistance(mVelocityY);
            if (splineFlingDistance > mTotalDy) {
                childFling(mFlingHelper.getVelocityByDistance(splineFlingDistance - mTotalDy));
            }
        }
        mTotalDy = 0;
        mVelocityY = 0;
    }

    private void childFling(int velocityY) {
        RecyclerView child = FindTarget.findChildScrollTarget(mContentView);
        if (child != null) {
            child.fling(0, velocityY);
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY = mFlingHelper.getFlingVelocity(velocityY);
        boolean fling = super.fling(velocityX, velocityY);
        if (!fling || velocityY <= 0) {
            mVelocityY = 0;
        } else {
            mIsStartChildFling = true;
            mVelocityY = velocityY;
        }
        return fling;
    }

    // NestedScrollingParent3

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        onNestedScrollInternal(dyUnconsumed, type, consumed);
    }

    private void onNestedScrollInternal(int dyUnconsumed, int type, @Nullable int[] consumed) {
        final int oldScrollY = computeVerticalScrollOffset();
        scrollBy(0, dyUnconsumed);
        final int myConsumed = computeVerticalScrollOffset() - oldScrollY;

        if (consumed != null) {
            consumed[1] += myConsumed;
        }
        final int myUnconsumed = dyUnconsumed - myConsumed;

        dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null, type, consumed);
    }

    // NestedScrollingParent2

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mParentHelper.onStopNestedScroll(target, type);
        stopNestedScroll(type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScrollInternal(dyUnconsumed, type, null);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean isParentScroll = dispatchNestedPreScroll(dx, dy, consumed, null, type);
        // 在父嵌套布局没有滑动时，处理此控件是否需要滑动
        if (!isParentScroll) {
            // 向上滑动且此控件没有滑动到底部时，需要让此控件继续滑动以保证滑动连贯一致性
            boolean needKeepScroll = dy > 0 && !isScrollEnd();
            if (needKeepScroll) {
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        }
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(
            @NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(
            @NonNull View child, @NonNull View target, int nestedScrollAxes) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        onNestedScrollInternal(dyUnconsumed, ViewCompat.TYPE_TOUCH, null);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onNestedFling(
            @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        if (!consumed) {
            dispatchNestedFling(0, velocityY, true);
            fling(0, (int) velocityY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

}
