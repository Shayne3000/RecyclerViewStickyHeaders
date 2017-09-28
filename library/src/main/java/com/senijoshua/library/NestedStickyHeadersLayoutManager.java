package com.senijoshua.library;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This provides the same functionality as the LinearLayoutManager usually paired the RecyclerView. However, this
 * provides the additional feature of positioning the section parent/child headers in a sticky manner.
 */

public class NestedStickyHeadersLayoutManager extends RecyclerView.LayoutManager{

    private NestedSectionStickyHeaderAdapter adapter;

    // holds all the visible section child headers
    private HashSet<View> ChildHeaderViews = new HashSet<>();

    // holds all the visible section parent headers
    private HashSet<View> ParentHeaderViews = new HashSet<>();

    // holds the ChildHeaderPosition for each child header
    private HashMap<Integer, ChildHeaderPosition> childHeaderPositionsBySection = new HashMap<>();

    // holds the ParentHeaderPosition for each parent header
    private HashMap<Integer, ParentHeaderPosition> parentHeaderPositionsBySection = new HashMap<>();

    private ChildHeaderPositionChangedCallback childHeaderPositionChangedCallback;

    private ParentHeaderPositionChangedCallback parentHeaderPositionChangedCallback;

    // adapter position of first (lowest-y-value) visible item.
    private int firstViewAdapterPosition;

    // top of first (lowest-y-value) visible item.
    private int firstViewTop;

    // adapter position (iff >= 0) of the item selected in scrollToPosition
    private int scrollTargetAdapterPosition = -1;

    private SavedState pendingSavedState;

    public enum ParentHeaderPosition {
        NONE,
        NATURAL,
        STICKY,
        TRAILING
    }

    public enum ChildHeaderPosition {
        NONE,
        NATURAL,
        STICKY,
        TRAILING
    }

    /**
     * Callback interface for monitoring when parent header positions change between members of the ParentHeaderPosition enum values.
     * This can be useful if client code wants to change appearance for headers in ParentHeaderPosition.STICKY vs normal positioning.
     */
    public interface ParentHeaderPositionChangedCallback {
        /**
         * Called when a section's parent header positioning changes. The position can be ParentHeaderPosition.NONE, ParentHeaderPosition.NATURAL, ParentHeaderPosition.STICKY or ParentHeaderPosition.TRAILING
         *
         * @param sectionIndex the sections [0...n)
         * @param header       the header view
         * @param oldPosition  the previous positioning of the header (NONE, NATURAL, STICKY or TRAILING)
         * @param newPosition  the new positioning of the header (NATURAL, STICKY or TRAILING)
         */
        void onParentHeaderPositionChanged(int sectionIndex, View header, ParentHeaderPosition oldPosition, ParentHeaderPosition newPosition);
    }

    /**
     * Callback interface for monitoring when child header positions change between members of the ChildHeaderPosition enum values.
     * This can be useful if client code wants to change appearance for headers in ChildHeaderPosition.STICKY vs normal positioning.
     */
    public interface ChildHeaderPositionChangedCallback {
        /**
         * Called when a section's child header positioning changes. The position can either be ChildHeaderPosition.NONE, ChildHeaderPosition.NATURAL, ChildHeaderPosition.STICKY or ChildHeaderPosition.TRAILING
         *
         * @param sectionIndex the sections [0...n)
         * @param header       the header view
         * @param oldPosition  the previous positioning of the header (NONE, NATURAL, STICKY or TRAILING)
         * @param newPosition  the new positioning of the header (NATURAL, STICKY or TRAILING)
         */
        void onChildHeaderPositionChanged(int sectionIndex, View header, ChildHeaderPosition oldPosition, ChildHeaderPosition newPosition);
    }

    public NestedStickyHeadersLayoutManager() {
    }

    public ChildHeaderPositionChangedCallback getChildHeaderPositionChangedCallback() {
        return childHeaderPositionChangedCallback;
    }

    public ParentHeaderPositionChangedCallback getParentHeaderPositionChangedCallback() {
        return parentHeaderPositionChangedCallback;
    }

    /**
     * Assign a callback object to be notified when a child header view position changes between states of the ChildHeaderPosition enum
     * @param childHeaderPositionChangedCallback the callback
     */
    public void setChildHeaderPositionChangedCallback(ChildHeaderPositionChangedCallback childHeaderPositionChangedCallback) {
        this.childHeaderPositionChangedCallback = childHeaderPositionChangedCallback;
    }

    /**
     * Assign a callback object to be notified when a parent header view position changes between states of the ParentHeaderPosition enum
     * @param parentHeaderPositionChangedCallback the callback
     */
    public void setParentHeaderPositionChangedCallback(ParentHeaderPositionChangedCallback parentHeaderPositionChangedCallback) {
        this.parentHeaderPositionChangedCallback = parentHeaderPositionChangedCallback;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);

        try {
            adapter = (NestedSectionStickyHeaderAdapter) newAdapter;
        } catch (ClassCastException e) {
            throw new ClassCastException("NestedStickyHeadersLayoutManager must be used with a RecyclerView that employs the NestedSectionStickyHeaderAdapter");
        }

        removeAllViews();

        ChildHeaderViews.clear();
        childHeaderPositionsBySection.clear();
        ParentHeaderViews.clear();
        parentHeaderPositionsBySection.clear();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);

        try {
            adapter = (NestedSectionStickyHeaderAdapter) view.getAdapter();
        } catch (ClassCastException e) {
            throw new ClassCastException("NestedStickyHeadersLayoutManager must be used with a RecyclerView that employs the NestedSectionStickyHeaderAdapter");
        }
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);

        // Update positions in case we need to save post-detach
        updateFirstAdapterPosition();
    }

    private static class SavedState implements Parcelable {
        int firstViewAdapterPosition = RecyclerView.NO_POSITION;
        int firstViewTop = 0;

        SavedState() {
        }

        SavedState(Parcel in) {
            firstViewAdapterPosition = in.readInt();
            firstViewTop = in.readInt();
        }

        public SavedState(SavedState other) {
            firstViewAdapterPosition = other.firstViewAdapterPosition;
            firstViewTop = other.firstViewTop;
        }

        boolean isValid() {
            return firstViewAdapterPosition >= 0;
        }

        void invalidate() {
            firstViewAdapterPosition = RecyclerView.NO_POSITION;
        }

        @Override
        public String toString() {
            return "<" + this.getClass().getCanonicalName() + " firstViewAdapterPosition: " + firstViewAdapterPosition + " firstViewTop: " + firstViewTop + ">";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(firstViewAdapterPosition);
            dest.writeInt(firstViewTop);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }
}
