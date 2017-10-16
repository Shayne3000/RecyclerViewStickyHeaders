package com.senijoshua.library.stickyindex.indexcomponents.interfaces;

import android.support.v7.widget.RecyclerView;

public interface Subscriber {
    //similar to the onScroll method
    void update(RecyclerView rv, float dx, float dy);
}
