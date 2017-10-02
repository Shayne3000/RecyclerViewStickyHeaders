package com.senijoshua.library;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This provides the same functionality as the LinearLayoutManager usually paired the RecyclerView. However, this
 * provides the additional feature of positioning the section parent/child headers in a sticky manner.
 */

public class NestedStickyHeadersLayoutManager extends RecyclerView.LayoutManager {

    private static final String CLASS_NAME = NestedStickyHeadersLayoutManager.class.getSimpleName();

    private NestedSectionAdapter adapter;

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
     *
     * @param childHeaderPositionChangedCallback the callback
     */
    public void setChildHeaderPositionChangedCallback(ChildHeaderPositionChangedCallback childHeaderPositionChangedCallback) {
        this.childHeaderPositionChangedCallback = childHeaderPositionChangedCallback;
    }

    /**
     * Assign a callback object to be notified when a parent header view position changes between states of the ParentHeaderPosition enum
     *
     * @param parentHeaderPositionChangedCallback the callback
     */
    public void setParentHeaderPositionChangedCallback(ParentHeaderPositionChangedCallback parentHeaderPositionChangedCallback) {
        this.parentHeaderPositionChangedCallback = parentHeaderPositionChangedCallback;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        super.onAdapterChanged(oldAdapter, newAdapter);

        try {
            adapter = (NestedSectionAdapter) newAdapter;
        } catch (ClassCastException e) {
            throw new ClassCastException("NestedStickyHeadersLayoutManager must be used with a RecyclerView that employs the NestedSectionAdapter");
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
            adapter = (NestedSectionAdapter) view.getAdapter();
        } catch (ClassCastException e) {
            throw new ClassCastException("NestedStickyHeadersLayoutManager must be used with a RecyclerView that employs the NestedSectionAdapter");
        }
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);

        // Update positions in case we need to save post-detach
        updateFirstAdapterPosition();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (pendingSavedState != null) {
            return pendingSavedState;
        }

        // Check if we're detached; if not, update
        if (adapter != null) {
            updateFirstAdapterPosition();
        }
        SavedState state = new SavedState();
        state.firstViewAdapterPosition = firstViewAdapterPosition;
        state.firstViewTop = firstViewTop;

        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null) {
            return;
        }

        if (state instanceof SavedState) {
            pendingSavedState = (SavedState) state;
            requestLayout();
        } else {
            Log.e(CLASS_NAME, "onRestoreInstanceState: invalid saved state class, expected: " + SavedState.class.getCanonicalName() + " but got: " + state.getClass().getCanonicalName());
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (adapter == null) {
            return;
        }

        if (scrollTargetAdapterPosition >= 0) {
            firstViewAdapterPosition = scrollTargetAdapterPosition;
            firstViewTop = 0;
            scrollTargetAdapterPosition = RecyclerView.NO_POSITION;
        } else if (pendingSavedState != null && pendingSavedState.isValid()) {
            firstViewAdapterPosition = pendingSavedState.firstViewAdapterPosition;
            firstViewTop = pendingSavedState.firstViewTop;
            pendingSavedState = null; // we're done with saved state now
        } else {
            updateFirstAdapterPosition();
        }

        int top = firstViewTop;

        // RESET
        ChildHeaderViews.clear();
        childHeaderPositionsBySection.clear();
        ParentHeaderViews.clear();
        parentHeaderPositionsBySection.clear();
        detachAndScrapAttachedViews(recycler);

        int height;
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int parentBottom = getHeight() - getPaddingBottom();
        //sum total of the height of each of the views in the adapter
        int totalRenderedHeight = 0;

        // If we emptied the view with a notify, we may overshoot and fail to draw
        if (firstViewAdapterPosition > state.getItemCount()) {
            firstViewAdapterPosition = 0;
        }

        // walk through adapter starting at firstViewAdapterPosition stacking each vended item
        for (int adapterPosition = firstViewAdapterPosition; adapterPosition < state.getItemCount(); adapterPosition++) {

            View v = recycler.getViewForPosition(adapterPosition);
            addView(v);
            measureChildWithMargins(v, 0, 0);

            int itemViewType = getViewBaseType(v);
            if (itemViewType == NestedSectionAdapter.TYPE_PARENT_HEADER) {
                ParentHeaderViews.add(v);

                // use the parent header's height
                height = getDecoratedMeasuredHeight(v);
                layoutDecorated(v, left, top, right, top + height);

                adapterPosition++;

            } else if (itemViewType == NestedSectionAdapter.TYPE_CHILD_HEADER) {

                ChildHeaderViews.add(v);

                // use the child header's height
                height = getDecoratedMeasuredHeight(v);

                layoutDecorated(v, left, top, right, top + height);

                adapterPosition++;
            } else {
                height = getDecoratedMeasuredHeight(v);
                layoutDecorated(v, left, top, right, top + height);
            }

            top += height;
            totalRenderedHeight += height;

            // if the item we just laid out falls off the bottom of the view, we're done
            if (v.getBottom() >= parentBottom) {
                break;
            }
        }

        // determine if scrolling is necessary to fill viewport
        int innerHeight = getHeight() - (getPaddingTop() + getPaddingBottom());
        if (totalRenderedHeight < innerHeight) {
            // note: we're passing null for RecyclerView.State - this is "safe"
            // only because we don't use it for scrolling negative dy
            scrollVerticallyBy(totalRenderedHeight - innerHeight, recycler, null);
        } else {
            // no scroll correction necessary, so position headers
            updateHeaderPositions(recycler);
        }
    }

    /**
     * Get the parent header item for a section. If it's not in the view hierarchy, then create it
     *
     * @param recycler     the recycler
     * @param sectionIndex the concerned section
     * @return the parent header or null if the adapter denotes that this section has no parent header
     */
    private View createSectionParentHeaderIfNeeded(RecyclerView.Recycler recycler, int sectionIndex) {
        if (adapter.doesSectionHaveParentHeader(sectionIndex)) {
            //confirm the presence of a parent header for this section
            for (int i = 0, n = getChildCount(); i < n; i++) {
                View view = getChildAt(i);
                if (getViewBaseType(view) == NestedSectionAdapter.TYPE_PARENT_HEADER && getViewSectionIndex(view) == sectionIndex) {
                    return view;
                }
            }

            // parent header absent, create one
            int parentHeaderAdapterPosition = adapter.getAdapterPositionForSectionParentHeader(sectionIndex);
            View parentHeaderView = recycler.getViewForPosition(parentHeaderAdapterPosition);
            ParentHeaderViews.add(parentHeaderView);
            addView(parentHeaderView);
            measureChildWithMargins(parentHeaderView, 0, 0);

            return parentHeaderView;
        }

        return null;
    }

    /**
     * Get the child header item for a section. If it's not in the view hierarchy, then create it
     *
     * @param recycler     the recycler
     * @param sectionIndex the concerned section
     * @return the child header or null if the adapter denotes that this section has no child header
     */
    private View createSectionChildHeaderIfNeeded(RecyclerView.Recycler recycler, int sectionIndex) {
        if (adapter.doesSectionHaveChildHeader(sectionIndex)) {
            //confirm the presence of a child header for this section
            for (int i = 0, n = getChildCount(); i < n; i++) {
                View view = getChildAt(i);
                if (getViewBaseType(view) == NestedSectionAdapter.TYPE_CHILD_HEADER && getViewSectionIndex(view) == sectionIndex) {
                    return view;
                }
            }

            // child header absent, create one
            int childHeaderAdapterPosition = adapter.getAdapterPositionForSectionChildHeader(sectionIndex);
            View childHeaderView = recycler.getViewForPosition(childHeaderAdapterPosition);
            ChildHeaderViews.add(childHeaderView);
            addView(childHeaderView);
            measureChildWithMargins(childHeaderView, 0, 0);

            return childHeaderView;
        }

        return null;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }

        int scrolled = 0;
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();


        if (dy < 0) {

            // list content moving downwards, so we're panning to top of list

            View topView = getTopmostChildView();
            while (scrolled > dy) {

                // get the topmost view
                int hangingTop = Math.max(-getDecoratedTop(topView), 0);
                int scrollBy = Math.min(scrolled - dy, hangingTop); // scrollBy is positive, causing content to move downwards

                scrolled -= scrollBy;
                offsetChildrenVertical(scrollBy);

                // render next view above topView

                if (firstViewAdapterPosition > 0 && scrolled > dy) {
                    firstViewAdapterPosition--;

                    // we're skipping the headers. they should already be rendered
                    int itemViewType = adapter.getItemViewBaseType(firstViewAdapterPosition);
                    boolean isChildHeader = itemViewType == NestedSectionAdapter.TYPE_CHILD_HEADER;

                    // skip the header, move to next item above
                    if (isChildHeader) {
                        firstViewAdapterPosition--;
                        if (firstViewAdapterPosition < 0) {
                            break;
                        }

                        itemViewType = adapter.getItemViewBaseType(firstViewAdapterPosition);
                        isChildHeader = itemViewType == NestedSectionAdapter.TYPE_CHILD_HEADER;

                        // If it's still a child header, we don't need to do anything right now
                        if (isChildHeader) {
                            break;
                        }
                    }

                    boolean isParentHeader = itemViewType == NestedSectionAdapter.TYPE_PARENT_HEADER;

                    if (isParentHeader) {
                        firstViewAdapterPosition--;
                        if (firstViewAdapterPosition < 0) {
                            break;
                        }

                        itemViewType = adapter.getItemViewBaseType(firstViewAdapterPosition);
                        isParentHeader = itemViewType == NestedSectionAdapter.TYPE_PARENT_HEADER;

                        // If it's still a parent header, we don't need to do anything right now
                        if (isParentHeader) {
                            break;
                        }
                    }

                    View v = recycler.getViewForPosition(firstViewAdapterPosition);
                    addView(v, 0);

                    int bottom = getDecoratedTop(topView);
                    int top;
                    measureChildWithMargins(v, 0, 0);
                    top = bottom - getDecoratedMeasuredHeight(v);
                    layoutDecorated(v, left, top, right, bottom);
                    topView = v;

                } else {
                    break;
                }

            }

        } else {

            // list content moving up, we're headed to bottom of list....the sticky behaviour would be more visible here

            int parentHeight = getHeight();
            View bottomView = getBottommostChildView();

            while (scrolled < dy) {
                int hangingBottom = Math.max(getDecoratedBottom(bottomView) - parentHeight, 0);
                int scrollBy = -Math.min(dy - scrolled, hangingBottom); // scrollBy is negative, causing content to move upwards
                scrolled -= scrollBy;
                offsetChildrenVertical(scrollBy);

                int adapterPosition = getViewAdapterPosition(bottomView);
                int nextAdapterPosition = adapterPosition + 1;

                if (scrolled < dy && nextAdapterPosition < state.getItemCount()) {

                    int top = getDecoratedBottom(bottomView);

                    int itemViewType = adapter.getItemViewBaseType(nextAdapterPosition);
                    if (itemViewType == NestedSectionAdapter.TYPE_PARENT_HEADER) {

                        // get the header and measure it so we can followup immediately by vending the ghost header
                        View headerView = createSectionParentHeaderIfNeeded(recycler, adapter.getSectionForAdapterPosition(nextAdapterPosition));
                        int height = getDecoratedMeasuredHeight(headerView);
                        layoutDecorated(headerView, left, 0, right, height);

                        // but we need to vend the followup ghost header too
                        nextAdapterPosition++;
                        View ghostHeader = recycler.getViewForPosition(nextAdapterPosition);
                        addView(ghostHeader);
                        layoutDecorated(ghostHeader, left, top, right, top + height);
                        bottomView = ghostHeader;

                    } else if (itemViewType == SectioningAdapter.TYPE_GHOST_HEADER) {

                        // get the header and measure it so we can followup immediately by vending the ghost header
                        View headerView = createSectionHeaderIfNeeded(recycler, adapter.getSectionForAdapterPosition(nextAdapterPosition));
                        int height = getDecoratedMeasuredHeight(headerView);
                        layoutDecorated(headerView, left, 0, right, height);

                        // but we need to vend the followup ghost header too
                        View ghostHeader = recycler.getViewForPosition(nextAdapterPosition);
                        addView(ghostHeader);
                        layoutDecorated(ghostHeader, left, top, right, top + height);
                        bottomView = ghostHeader;

                    } else {

                        View v = recycler.getViewForPosition(nextAdapterPosition);
                        addView(v);

                        measureChildWithMargins(v, 0, 0);
                        int height = getDecoratedMeasuredHeight(v);
                        layoutDecorated(v, left, top, right, top + height);
                        bottomView = v;
                    }

                } else {
                    break;
                }
            }
        }

        View topmostView = getTopmostChildView();
        if (topmostView != null) {
            firstViewTop = getDecoratedTop(topmostView);
        }

        updateHeaderPositions(recycler);
        recycleViewsOutOfBounds(recycler);
        return scrolled;
    }

    /**
     * Updates firstViewAdapterPosition to the adapter position of the highest item in the list
     *
     * @return the y value of the topmost view in the layout, or paddingTop if empty
     */
    private int updateFirstAdapterPosition() {

        // empty
        if (getChildCount() == 0) {
            firstViewAdapterPosition = 0;
            firstViewTop = getPaddingTop();
            return firstViewTop;
        }

        View topmostView = getTopmostChildView();
        if (topmostView != null) {
            firstViewAdapterPosition = getViewAdapterPosition(topmostView);
            firstViewTop = Math.min(topmostView.getTop(), getPaddingTop());
            return firstViewTop;
        }

        // as far as I can tell, if notifyDataSetChanged is called, onLayoutChildren
        // will be called, but all views will be marked as having NO_POSITION for
        // adapterPosition, which means the above approach of finding the topmostChildView
        // doesn't work. So, basically, leave firstViewAdapterPosition and firstViewTop
        // alone.
        return firstViewTop;
    }

    private int getViewBaseType(View view) {
        int adapterPosition = getViewAdapterPosition(view);
        return adapter.getItemViewBaseType(adapterPosition);
    }

    private int getViewSectionIndex(View view) {
        int adapterPosition = getViewAdapterPosition(view);
        return adapter.getSectionForAdapterPosition(adapterPosition);
    }

    int getViewAdapterPosition(View view) {
        return getViewViewHolder(view).getAdapterPosition();
    }

    private NestedSectionAdapter.ViewHolder getViewViewHolder(View view) {
        return (NestedSectionAdapter.ViewHolder) view.getTag(R.id.nested_section_view_holder_tag);
    }

    private View getTopmostChildView() {
        if (getChildCount() == 0) {
            return null;
        }

        View topmostView = null;
        int top = Integer.MAX_VALUE;

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View v = getChildAt(i);

            // ignore views which are being deleted
            if (getViewAdapterPosition(v) == RecyclerView.NO_POSITION) {
                continue;
            }

            // ignore headers
            if (getViewBaseType(v) == NestedSectionAdapter.TYPE_CHILD_HEADER) {
                continue;
            }

            if (getViewBaseType(v) == NestedSectionAdapter.TYPE_PARENT_HEADER) {
                continue;
            }

            int t = getDecoratedTop(v);
            if (t < top) {
                top = t;
                topmostView = v;
            }
        }

        return topmostView;
    }

    View getBottommostChildView() {
        if (getChildCount() == 0) {
            return null;
        }

        View bottommostView = null;
        int bottom = Integer.MIN_VALUE;

        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View v = getChildAt(i);

            // ignore views which are being deleted
            if (getViewAdapterPosition(v) == RecyclerView.NO_POSITION) {
                continue;
            }

            // ignore headers
            if (getViewBaseType(v) == NestedSectionAdapter.TYPE_CHILD_HEADER) {
                continue;
            }

            if (getViewBaseType(v) == NestedSectionAdapter.TYPE_PARENT_HEADER) {
                continue;
            }

            int b = getDecoratedBottom(v);
            if (b > bottom) {
                bottom = b;
                bottommostView = v;
            }
        }

        return bottommostView;
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
