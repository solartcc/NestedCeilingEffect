package cc.solart.nestedceiling.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import cc.solart.nestedceiling.widget.NestedChildRecyclerView;
import cc.solart.nestedceiling.widget.NestedParentRecyclerView;

public final class FindTarget {

    @Nullable
    public static RecyclerView findChildScrollTarget(ViewGroup contentView) {
        if(contentView == null) return null;
        for (int i = 0; i < contentView.getChildCount(); i++) {
            View view = contentView.getChildAt(i);
            if(view instanceof RecyclerView && view.getClass() == NestedChildRecyclerView.class){
                return (RecyclerView) view;
            } else if(view instanceof ViewGroup){
                RecyclerView target = findChildScrollTarget((ViewGroup) view);
                if(target != null){
                    return target;
                }
            }
        }
        return null;
    }

    @Nullable
    public static RecyclerView findParentScrollTarget(View view){
        ViewParent parent = view.getParent();
        while ((parent != null && parent.getClass() != NestedParentRecyclerView.class)){
            parent = parent.getParent();
        }

        if(parent != null){
            return (RecyclerView) parent;
        }
        return null;
    }
}
