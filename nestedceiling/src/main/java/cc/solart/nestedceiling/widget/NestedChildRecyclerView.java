package cc.solart.nestedceiling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import cc.solart.nestedceiling.utils.FindTarget;
import cc.solart.nestedceiling.utils.FlingHelper;


public class NestedChildRecyclerView extends RecyclerView {
    private FlingHelper mFlingHelper;
    private boolean mIsParentStartFling = false;
    private int mTotalDy = 0;
    private int mVelocity = 0;

    public NestedChildRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
        setNestedScrollingEnabled(true);
    }

    private void setup() {
        mFlingHelper = new FlingHelper(getContext());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            dispatchParentFling();
        }
    }

    private void dispatchParentFling() {
        if (isScrollTop() && mVelocity != 0) {
            //此控件已经滑动到顶部，且竖直方向加速度不为0，如果有多余的距离则交由父嵌套布局继续fling
            double flingDistance = mFlingHelper.getSplineFlingDistance(mVelocity);
            if (flingDistance > (Math.abs(mTotalDy))) {
                RecyclerView parent = FindTarget.findParentScrollTarget(this);
                if (parent != null) {
                    parent.fling(0, -mFlingHelper.getVelocityByDistance(flingDistance + mTotalDy));
                }
            }
            mTotalDy = 0;
            mVelocity = 0;
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (mIsParentStartFling) {
            mTotalDy = 0;
            mIsParentStartFling = false;
        }
        mTotalDy += dy;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (!isAttachedToWindow()) return false;
        velocityY = mFlingHelper.getFlingVelocity(velocityY);
        boolean fling = super.fling(velocityX, velocityY);
        if (!fling || velocityY >= 0) {
            mVelocity = 0;
        } else {
            mIsParentStartFling = true;
            mVelocity = velocityY;
        }
        return fling;
    }

    private boolean isScrollTop() {
        return !canScrollVertically(-1);
    }
}
