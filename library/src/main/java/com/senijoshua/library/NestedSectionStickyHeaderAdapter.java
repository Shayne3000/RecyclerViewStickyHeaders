package com.senijoshua.library;


import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class NestedSectionStickyHeaderAdapter extends RecyclerView.Adapter<NestedSectionStickyHeaderAdapter.ViewHolder> {
    private ArrayList<Section> sections;
    private int[] sectionIndicesByAdapterPosition;
    private int totalNumberOfItems;
    private Handler mainThreadHandler;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private int section;
        private int numberOfItemsInSection;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public int getItemViewBaseType() {
            return NestedSectionStickyHeaderAdapter.unmaskBaseViewType(getItemViewType());
        }

        public int getItemViewUserType() {
            return NestedSectionStickyHeaderAdapter.unmaskUserViewType(getItemViewType());
        }

        /*
          Corresponds to the header for just the items in this section
         */
        public boolean isChildHeader() {
            return false;
        }

        /*
         Corresponds to the header that stays above the childheader
         */
        public boolean isParentHeader() {
            return false;
        }

        public int getSection() {
            return section;
        }

        public void setSection(int section) {
            this.section = section;
        }

        public int getNumberOfItemsInSection() {
            return numberOfItemsInSection;
        }

        public void setNumberOfItemsInSection(int numberOfItemsInSection) {
            this.numberOfItemsInSection = numberOfItemsInSection;
        }
    }

    public static class ItemViewHolder extends ViewHolder {
        int positionInSection;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public int getPositionInSection() {
            return positionInSection;
        }

        public void setPositionInSection(int positionInSection) {
            this.positionInSection = positionInSection;
        }
    }

    public static class ChildHeaderViewHolder extends ViewHolder {
        public ChildHeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public boolean isChildHeader() {
            return super.isChildHeader();
        }
    }

    public static class ParentHeaderViewHolder extends ViewHolder {
        public ParentHeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public boolean isParentHeader() {
            return super.isParentHeader();
        }
    }

    /**
     * @return Number of sections
     */
    public int getNumberOfSections() {
        return 0;
    }

    /**
     * @param sectionIndex index of the concerned section
     * @return the number of items in the specified section
     */
    public int getNumberOfItemsInSection(int sectionIndex) {
        return 0;
    }

    /**
     * @param sectionIndex index of the concerned section
     * @return true if this section has a child header
     */
    public boolean doesSectionHaveChildHeader(int sectionIndex) {
        return false;
    }

    /**
     * @param sectionIndex index of the concerned section
     * @return true if this section has a parent header
     */
    public boolean doesSectionHaveParentHeader(int sectionIndex) {
        return false;
    }

    /**
     * Override this function to set the view type for the section child header
     * For scenarios with multiple types of child headers, override this to return an integer in range [0,255] specifying a custom type for this header.
     * The value you return here will be passed to onCreateChildHeaderViewHolder and onBindChildHeaderViewHolder as the user-defined ViewType
     *
     * @param sectionIndex the child header's section
     * @return the custom type for this header in range [0,255]
     */
    public int getSectionChildHeaderViewType(int sectionIndex) {
        return 0;
    }

    /**
     * Override this function to set the view type for the section parent header
     * For scenarios with multiple types of parent headers, override this to return an integer in range [0,255] specifying a custom type for this header.
     * The value you return here will be passed to onCreateParentHeaderViewHolder and onBindParentHeaderViewHolder as the user-defined ViewType
     *
     * @param sectionIndex the parent header's section
     * @return the custom type for this header in range [0,255]
     */
    public int getSectionParentHeaderViewType(int sectionIndex) {
        return 0;
    }

    /**
     * Override this function to set the view type for the section item
     * For scenarios with multiple types of items, override this to return an integer in range [0,255] specifying a custom type for the item at this position
     * The value you return here will be passed to onCreateItemViewHolder and onBindItemViewHolder as the user-defined ViewType
     *
     * @param sectionIndex the items's section
     * @param itemIndex    the position of the item in the section
     * @return the custom type for this item in range [0,255]
     */
    public int getSectionItemViewType(int sectionIndex, int itemIndex) {
        return 0;
    }

    /**
     * This is used to create a ViewHolder for an item view in a section.
     *
     * @param parent       The ViewGroup into which the new View will be added after it's been bound to an adapter position.
     * @param itemViewType If getSectionItemViewType is overridden to vend custom types, this will be the specified type
     * @return A new ItemViewHolder holding an item view
     */
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemViewType) {
        return null;
    }

    /**
     * Binds item data to the ViewHolder at a specified position
     *
     * @param holder       the view holder to update
     * @param sectionIndex index of the section that holds the item
     * @param itemIndex    index of the concerned item in the section
     * @param itemViewType the type of the view which could be a user-provided custom type through overriding getItemViewType
     */
    public void onBindItemViewHolder(ItemViewHolder holder, int sectionIndex, int itemIndex, int itemViewType) {

    }

    /**
     * This creates a ViewHolder for the Section child header view
     *
     * @param parent              The ViewGroup that the new view would be added to after it's been bound to an adapter position.
     * @param childHeaderViewType if getSectionChildHeaderViewType is overriden to return custom types, this will be the specified type
     * @return a new SectionChildHeaderViewHolder containing a child header view
     */
    public ChildHeaderViewHolder onCreateChildHeaderViewHolder(ViewGroup parent, int childHeaderViewType) {
        return null;
    }

    /**
     * This binds child header data for a given section
     *
     * @param holder              the holder that would be updated
     * @param sectionIndex        index of the section that contains the child header to update
     * @param childHeaderViewType the type of the view that could be a user-supplied custom type through overriding getSectionChildHeaderViewType
     */
    public void onBindChildHeaderViewHolder(ChildHeaderViewHolder holder, int sectionIndex, int childHeaderViewType) {

    }

    /**
     * This creates a ViewHolder for the Section parent header view
     *
     * @param parent               The ViewGroup that the new view would be added to after it's been bound to an adapter position.
     * @param parentHeaderViewType if getSectionParentHeaderViewType is overriden to return custom types, this will be the specified type
     * @return a new SectionParentHeaderViewHolder containing a parent header view
     */
    public ParentHeaderViewHolder onCreateParentHeaderViewHolder(ViewGroup parent, int parentHeaderViewType) {
        return null;
    }

    /**
     * This binds parent header data for a given section
     *
     * @param holder               the holder that would be updated
     * @param sectionIndex         index of the section that contains the parent header to update
     * @param parentHeaderViewType the type of the view that could be a user-supplied custom type through overriding getSectionParentHeaderViewType
     */
    public void onBindParentHeaderViewHolder(ParentHeaderViewHolder holder, int sectionIndex, int parentHeaderViewType) {

    }

    private static class Section {
        int adapterPosition;    // adapterPosition of first item (the header) of this section. It's either a parent or a child;
        int numberOfItems;      // number of items (not including parent and child headers)
        int length;             // total number of items in sections including parent and child headers
        boolean hasChildHeader; // if true, section has a child header
        boolean hasParentHeader;// if true, section has a parent header
    }

    private void buildSectionIndex() {
        sections = new ArrayList<>();

        int i = 0;
        for (int sectionIndex = 0, sectionCount = getNumberOfSections(); sectionIndex < sectionCount; sectionIndex++) {
            Section section = new Section();
            section.adapterPosition = i;
            section.hasChildHeader = doesSectionHaveChildHeader(sectionIndex);
            section.hasParentHeader = doesSectionHaveParentHeader(sectionIndex);
            section.length = section.numberOfItems = getNumberOfItemsInSection(sectionIndex);

            if (section.hasChildHeader) {
                section.length += 1; // room for child header
            }
            if (section.hasParentHeader) {
                section.length++;
            }

            this.sections.add(section);

            i += section.length;
        }

        totalNumberOfItems = i;

        i = 0;
        sectionIndicesByAdapterPosition = new int[totalNumberOfItems];
        for (int sectionIndex = 0, sectionCount = getNumberOfSections(); sectionIndex < sectionCount; sectionIndex++) {
            Section section = sections.get(sectionIndex);
            for (int p = 0; p < section.length; p++) {
                sectionIndicesByAdapterPosition[i + p] = sectionIndex;
            }

            i += section.length;
        }
    }

    /**
     * Given a position in the adapter, determine which section contains that item
     *
     * @param adapterPosition the position in the adapter which ranges from 0 to getItemCount() - 1
     * @return the index of the section containing that item
     */
    public int getSectionForAdapterPosition(int adapterPosition) {
        if (sections == null) {
            buildSectionIndex();
        }

        if (getItemCount() == 0) {
            return -1;
        }

        if (adapterPosition < 0 || adapterPosition >= getItemCount()) {
            throw new IndexOutOfBoundsException("The position " + adapterPosition + " is not within the range of items in the adapter");
        }

        return sectionIndicesByAdapterPosition[adapterPosition];
    }

    

}

