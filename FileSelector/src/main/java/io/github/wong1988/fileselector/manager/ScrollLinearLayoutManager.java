package io.github.wong1988.fileselector.manager;


import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ScrollLinearLayoutManager extends LinearLayoutManager {

    private boolean mCanScroll = true;

    public ScrollLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        if (!mCanScroll)
            return false;
        else
            return super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally() {
        if (!mCanScroll)
            return false;
        else
            return super.canScrollHorizontally();
    }

    public void setCanScroll(boolean b) {
        mCanScroll = b;
    }
}
