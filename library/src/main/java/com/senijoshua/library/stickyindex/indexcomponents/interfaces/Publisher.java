package com.senijoshua.library.stickyindex.indexcomponents.interfaces;


import android.support.v7.widget.RecyclerView;

public interface Publisher {
    //Register a subscriber to receive scroll updates
    void register(Subscriber newObserver);
    //Unregister a subscriber to no longer receive scroll updates
    void unregister(Subscriber existentObserver);
    void notifySubscribers(RecyclerView rv, int dx, int dy);
}
