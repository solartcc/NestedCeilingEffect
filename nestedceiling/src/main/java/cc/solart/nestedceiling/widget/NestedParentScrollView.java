package cc.solart.nestedceiling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.solart.nestedceiling.utils.FindTarget;
import cc.solart.nestedceiling.utils.FlingHelper;

public class NestedParentScrollView extends NestedScrollView {

    private static final int ONLY_CHILD = 1;
    private static final int LIMIT_GRANDSON = 2;
    private ViewGroup mChildView;
    private View mTopView;
    private ViewGroup mContentView;
    // 记录y轴滑动的距离
    private int mTotalDy = 0;
    // 记录y轴加速度
    private int mVelocityY = 0;
    private boolean mIsStartFling = false;
    private FlingHelper mFlingHelper;
    private boolean mIsChildAttachedToTop = false;
    private boolean mIsChildDetachedFromTop = true;
    private final ArrayList<OnChildAttachStateListener> mOnChildAttachStateListeners =
            new ArrayList<>();

    public NestedParentScrollView(@NonNull Context context) {
        this(context, null);
    }

    public NestedParentScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedParentScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        mFlingHelper = new FlingHelper(getContext());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void addOnChildAttachStateListener(OnChildAttachStateListener listener) {
        mOnChildAttachStateListeners.add(listener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != ONLY_CHILD) {
            throw new IllegalStateException("NestedParentScrollView must host only one direct child");
        }
        mChildView = (ViewGroup) getChildAt(0);
        if (mChildView.getChildCount() != LIMIT_GRANDSON) {
            throw new IllegalStateException("NestedParentScrollView must host only two indirect grandson");
        }

        mTopView = mChildView.getChildAt(0);
        mContentView = (ViewGroup) mChildView.getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams lp = mContentView.getLayoutParams();
        lp.height = getMeasuredHeight();
        mContentView.setLayoutParams(lp);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean isParentScroll = dispatchNestedPreScroll(dx, dy, consumed, null, type);
        if(!isParentScroll) {
            // 向上滑动且当前滑动距离小于顶部视图的高度时，需要此控件滑动响应的距离以保证滑动连贯性
            boolean needKeepScroll = dy > 0 && getScrollY() < mTopView.getMeasuredHeight();
            if (needKeepScroll) {
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public void fling(int velocityY) {
        velocityY = mFlingHelper.getFlingVelocity(velocityY);
        super.fling(velocityY);
        if (velocityY <= 0) {
            velocityY = 0;
        } else {
            mIsStartFling = true;
            mVelocityY = velocityY;
        }
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
        if(mIsStartFling){
            mTotalDy = 0;
            mIsStartFling = false;
        }
        boolean isScrollEnd;
        if(isScrollEnd = scrollY == mChildView.getMeasuredHeight() - getMeasuredHeight()) {
            dispatchChildFling();
        }
        int dy = scrollY - oldScrollY;
        mTotalDy += dy;

        boolean attached = dy > 0 && isScrollEnd;
        if(attached && mIsChildDetachedFromTop) {
            mIsChildAttachedToTop = true;
            mIsChildDetachedFromTop = false;
            final int listenerCount = mOnChildAttachStateListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                OnChildAttachStateListener listener = mOnChildAttachStateListeners.get(i);
                listener.onChildAttachedToTop();
            }
        }

        boolean detached = dy < 0 && !isScrollEnd;
        if(mIsChildAttachedToTop && detached){
            mIsChildDetachedFromTop = true;
            mIsChildAttachedToTop = false;
            final int listenerCount = mOnChildAttachStateListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                OnChildAttachStateListener listener = mOnChildAttachStateListeners.get(i);
                listener.onChildDetachedFromTop();
            }
        }
    }

    private void dispatchChildFling(){
        if(mVelocityY != 0) {
            double splineFlingDistance = mFlingHelper.getSplineFlingDistance(mVelocityY);
            if(splineFlingDistance > mTotalDy){
                childFling(mFlingHelper.getVelocityByDistance(splineFlingDistance - mTotalDy));
            }
        }
        mTotalDy = 0;
        mVelocityY = 0;
    }

    private void childFling(int velocityY){
        RecyclerView target = FindTarget.findChildScrollTarget(mContentView);
        if(target != null) {
            target.fling(0, velocityY);
        }
    }

}
