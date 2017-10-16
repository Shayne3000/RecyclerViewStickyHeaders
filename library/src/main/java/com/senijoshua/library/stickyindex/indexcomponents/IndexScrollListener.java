package com.senijoshua.library.stickyindex.indexcomponents;

import android.support.v7.widget.RecyclerView;

import com.senijoshua.library.stickyindex.indexcomponents.interfaces.Publisher;
import com.senijoshua.library.stickyindex.indexcomponents.interfaces.Subscriber;

import java.util.ArrayList;
import java.util.List;


public class IndexScrollListener extends RecyclerView.OnScrollListener implements Publisher{
    private List<Subscriber> subscribers;

    public IndexScrollListener() {
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        notifySubscribers(rv, dx, dy);
    }


    @Override
    public void register(Subscriber newObserver) {
        subscribers.add(newObserver);
    }

    @Override
    public void unregister(Subscriber existentObserver) {
        subscribers.remove(existentObserver);
    }

    @Override
    public void notifySubscribers (RecyclerView rv, int dx, int dy) {
        for (Subscriber subscriber :  subscribers) {
            subscriber.update(rv, dx, dy);
        }
    }

    public void setOnScrollListener(RecyclerView rv) {
        rv.addOnScrollListener(this);
    }
}
