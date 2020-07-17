package cc.solart.nestedceiling.widget;

public interface OnChildAttachStateListener {

    /**
     * 子布局吸附到顶部时回调
     */
    void onChildAttachedToTop();

    /**
     * 子布局从顶部脱离时回调
     */
    void onChildDetachedFromTop();
}
