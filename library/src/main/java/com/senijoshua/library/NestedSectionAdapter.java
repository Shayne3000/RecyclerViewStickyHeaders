package com.senijoshua.library;


import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class NestedSectionAdapter extends RecyclerView.Adapter<NestedSectionAdapter.ViewHolder> {
    public static final int NO_POSITION = -1;
    public static final int TYPE_PARENT_HEADER = 0;
    public static final int TYPE_CHILD_HEADER = 1;
    public static final int TYPE_ITEM = 2;

    private ArrayList<Section> sections;
    private int[] sectionIndicesByAdapterPosition;
    private int totalNumberOfItems;
    private Handler mainThreadHandler;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int baseViewType = unmaskBaseViewType(viewType);
        int userViewType = unmaskUserViewType(viewType);

        switch (baseViewType) {
            case TYPE_ITEM:
                return onCreateItemViewHolder(parent, userViewType);
            case TYPE_CHILD_HEADER:
                return onCreateChildHeaderViewHolder(parent, userViewType);
            case TYPE_PARENT_HEADER:
                return onCreateParentHeaderViewHolder(parent, userViewType);
        }

        throw new IndexOutOfBoundsException("The viewType: " + viewType + " is unrecognized and it does not correspond to either TYPE_ITEM, TYPE_CHILD_HEADER or TYPE_PARENT");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int section = getSectionForAdapterPosition(position);

        // bind the sections to this view holder
        holder.setSection(section);
        holder.setNumberOfItemsInSection(getNumberOfItemsInSection(section));

        // tag the viewHolder's item so as to make it possible to track in the layout manager
        tagViewHolderItemView(holder, section, position);

        int baseType = unmaskBaseViewType(holder.getItemViewType());
        int userType = unmaskUserViewType(holder.getItemViewType());
        switch (baseType) {
            case TYPE_PARENT_HEADER:
                onBindParentHeaderViewHolder((ParentHeaderViewHolder) holder, section, userType);
                break;

            case TYPE_CHILD_HEADER:
                onBindChildHeaderViewHolder((ChildHeaderViewHolder) holder, section, userType);
                break;

            case TYPE_ITEM:
                ItemViewHolder itemHolder = (ItemViewHolder) holder;
                int positionInSection = getPositionOfItemInSection(section, position);
                itemHolder.setPositionInSection(positionInSection);
                onBindItemViewHolder(itemHolder, section, positionInSection, userType);
                break;

            default:
                throw new IllegalArgumentException("The viewType: " + baseType + " is unrecognized and it does not correspond to either TYPE_ITEM, TYPE_CHILD_HEADER or TYPE_PARENT");
        }
    }

    /**
     * Tag the item View of each of the view holders with information needed for the layout to handle its sticky positioning.
     * @param holder the holder containing the itemView to tag
     * @param section the section that this itemView relates to
     * @param adapterPosition the adapter position of the view holder
     */
    private void tagViewHolderItemView(ViewHolder holder, int section, int adapterPosition) {
        View view = holder.itemView;
        view.setTag(R.id.nested_section_view_holder_tag, holder);
    }

    @Override
    public int getItemCount() {
        if (sections == null) {
            buildSectionIndex();
        }
        return totalNumberOfItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (sections == null) {
            buildSectionIndex();
        }

        if (position < 0) {
            throw new IndexOutOfBoundsException(" The adapterPosition (" + position + ") cannot be less than 0");
        } else if (position >= getItemCount()) {
            throw new IndexOutOfBoundsException(" The adapterPosition (" + position + ")  cannot be greater than getItemCount() (" + getItemCount() + ")");
        }

        int sectionIndex = getSectionForAdapterPosition(position);
        Section section = this.sections.get(sectionIndex);
        int localPosition = position - section.adapterPosition;

        int baseType = getItemViewBaseType(section, localPosition);
        int userType = 0;

        switch (baseType) {
            case TYPE_PARENT_HEADER:
                userType = getSectionParentHeaderViewType(sectionIndex);
                if (userType < 0 || userType > 0xFF) {
                    throw new IllegalArgumentException("Custom parent header view type (" + userType + ") must be in range [0,255]");
                }
                break;
            case TYPE_CHILD_HEADER:
                userType = getSectionChildHeaderViewType(sectionIndex);
                if (userType < 0 || userType > 0xFF) {
                    throw new IllegalArgumentException("Custom child header view type (" + userType + ") must be in range [0,255]");
                }
                break;
            case TYPE_ITEM:
                // adjust local position to accommodate the parent and child header
                if (section.hasParentHeader) {
                    localPosition -= 1;
                }

                if (section.hasChildHeader) {
                    localPosition -= 1;
                }
                userType = getSectionItemViewType(sectionIndex, localPosition);
                if (userType < 0 || userType > 0xFF) {
                    throw new IllegalArgumentException("Custom item view type (" + userType + ") must be in range [0,255]");
                }
                break;
        }

        // base is bottom 8 bits, user type next 8 bits
        return ((userType & 0xFF) << 8) | (baseType & 0xFF);
    }

    private int getItemViewBaseType(Section section, int localPosition) {
        if (section.hasParentHeader && section.hasChildHeader) {
            if (localPosition == 0) {
                return TYPE_PARENT_HEADER;
            } else if (localPosition == 1) {
                return TYPE_CHILD_HEADER;
            } else {
                return TYPE_ITEM;
            }
        } else if (section.hasParentHeader) {
            if (localPosition == 0) {
                return TYPE_PARENT_HEADER;
            } else {
                return TYPE_ITEM;
            }
        } else if (section.hasChildHeader) {
            if (localPosition == 0) {
                return TYPE_CHILD_HEADER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    /**
     * @param adapterPosition the adapterPosition of the item in question
     * @return the custom user type of the item at the adapterPosition
     */
    public int getItemViewBaseType(int adapterPosition) {
        return unmaskUserViewType(getItemViewType(adapterPosition));
    }

    public static int unmaskBaseViewType(int itemViewTypeMask) {
        return itemViewTypeMask & 0xFF; // base view type (PARENT_HEADER/CHILD_HEADER/ITEM) is lower 8 bits
    }

    public static int unmaskUserViewType(int itemViewTypeMask) {
        return (itemViewTypeMask >> 8) & 0xFF; // use type is in 0x0000FF00 segment
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private int section;
        private int numberOfItemsInSection;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public int getItemViewBaseType() {
            return NestedSectionAdapter.unmaskBaseViewType(getItemViewType());
        }

        public int getItemViewUserType() {
            return NestedSectionAdapter.unmaskUserViewType(getItemViewType());
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

    private void assessSectionState(int sectionIndex) {
        if (sections == null) {
            buildSectionIndex();
        }

        if (sectionIndex < 0) {
            throw new IndexOutOfBoundsException("The given groupIndex " + sectionIndex + " is less than 0");
        }

        if (sectionIndex >= sections.size()) {
            throw new IndexOutOfBoundsException("The given groupIndex " + sectionIndex + " >= the group size (" + sections.size() + ")");
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

    /**
     * Given a sectionIndex and an adapter position, get the local position of an item in a section
     * where the first item has position 0
     *
     * @param sectionIndex    the index of the containing section
     * @param adapterPosition the given adapter position
     * @return the position of an item in that section denoted by the sectionIndex
     * <p/>
     * Note, if the adapterPosition corresponds to a sectionIndex parent or child header, this will return -1
     */
    public int getPositionOfItemInSection(int sectionIndex, int adapterPosition) {
        assessSectionState(sectionIndex);

        Section section = this.sections.get(sectionIndex);
        int itemPositionInSection = adapterPosition - section.adapterPosition;
        if (itemPositionInSection > section.length) {
            throw new IndexOutOfBoundsException("The adapterPosition: " + adapterPosition + " is beyond section with Index: " + sectionIndex + " with length: " + section.length);
        }

        if (section.hasChildHeader) {
            // adjust for header and ghostHeader
            itemPositionInSection -= 1;
        }

        if (section.hasParentHeader) {
            // adjust for header
            itemPositionInSection -= 1;
        }

        return itemPositionInSection;
    }

    /**
     * Given a sectionIndex index, and an offset into the section where 0 is the parent header, 1 is the child Header,
     * 2 is the first item in the sectionIndex, return the corresponding "global" adapter position
     *
     * @param sectionIndex      index of the concerned section
     * @param offsetIntoSection offset into section
     * @return the "global" adapter adapterPosition
     */
    private int getAdapterPosition(int sectionIndex, int offsetIntoSection) {
        assessSectionState(sectionIndex);

        Section section = this.sections.get(sectionIndex);
        int adapterPosition = section.adapterPosition;
        return offsetIntoSection + adapterPosition;
    }

    /**
     * Return the adapter position that corresponds to the parent header of the provided section
     *
     * @param sectionIndex the index of the concerned section
     * @return adapter position of that section's parent header, or NO_POSITION if section has no parent header
     */
    public int getAdapterPositionForSectionParentHeader(int sectionIndex) {
        if (doesSectionHaveParentHeader(sectionIndex)) {
            return getAdapterPosition(sectionIndex, 0);
        } else {
            return NO_POSITION;
        }
    }

    /**
     * Return the adapter position that corresponds to the child header of the provided section
     *
     * @param sectionIndex the index of the concerned section
     * @return adapter position of that section's child header, or NO_POSITION if section has no child header
     */
    public int getAdapterPositionForSectionChildHeader(int sectionIndex) {
        if (doesSectionHaveChildHeader(sectionIndex)) {
            if (doesSectionHaveParentHeader(sectionIndex)) {
                return getAdapterPosition(sectionIndex, 1);
            } else {
                return getAdapterPosition(sectionIndex, 0);
            }
        } else {
            return NO_POSITION;
        }
    }

    /**
     * Return the adapter position corresponding to a specific item in the section
     *
     * @param sectionIndex      the index of the concerned section
     * @param offsetIntoSection the offset of the item in the section where 0 would be the first item in the section
     * @return adapter position of the item in the section
     */

    public int getAdapterPositionForSectionItem(int sectionIndex, int offsetIntoSection) {
        if (doesSectionHaveParentHeader(sectionIndex)) {
            if (doesSectionHaveChildHeader(sectionIndex)) {
                return getAdapterPosition(sectionIndex, offsetIntoSection) + 2; // parent header is at position 0, childHeader at position 1
            } else {
                return getAdapterPosition(sectionIndex, offsetIntoSection) + 1; // parent header is at position 0
            }
        } else {
            if (doesSectionHaveChildHeader(sectionIndex)) {
                return getAdapterPosition(sectionIndex, offsetIntoSection) + 1; // childHeader at position 0
            } else {
                return getAdapterPosition(sectionIndex, offsetIntoSection); //no headers
            }
        }
    }

    /**
     * Notify that all data in the list is invalid and the whole list needs to be reloaded.
     * This should be called instead of the RecyclerView.Adapter.notifyDataSetChanged equivalent.
     */
    public void notifyAllSectionsDataSetChanged() {
        buildSectionIndex();
        notifyDataSetChanged();
    }

    /**
     * Notify that all the items in a given section are invalid and the section should be reloaded.
     *
     * @param sectionIndex index of the section to reload
     */
    public void notifySectionDataSetChanged(int sectionIndex) {
        if (sections == null) {
            buildSectionIndex();
            notifyAllSectionsDataSetChanged();
        } else {
            buildSectionIndex();
            Section section = this.sections.get(sectionIndex);
            notifyItemRangeChanged(section.adapterPosition, section.length);
        }
    }

    /**
     * Notify that a given item in a section has been invalidated and should be reloaded
     * This should be called instead of the RecyclerView.Adapter.notifyItemChanged equivalent
     *
     * @param sectionIndex the index of the section containing the item
     * @param itemIndex    the index of the item in the section (where 0 is the first item in the section)
     */
    public void notifySectionItemChanged(int sectionIndex, int itemIndex) {
        if (sections == null) {
            buildSectionIndex();
            notifyAllSectionsDataSetChanged();
        } else {
            buildSectionIndex();
            Section section = this.sections.get(sectionIndex);
            if (itemIndex >= section.numberOfItems) {
                throw new IndexOutOfBoundsException("The item Index " + itemIndex + " exceeds the number of items: " + section.numberOfItems + " in section: " + sectionIndex);
            }
            if (section.hasChildHeader) {
                itemIndex += 1;
            }

            if (section.hasParentHeader) {
                itemIndex += 1;
            }
            notifyItemChanged(section.adapterPosition + itemIndex);
        }
    }

    /**
     * Notify that an item has been added to a section
     * This should be called instead of the RecyclerView.Adapter.notifyItemInserted equivalent
     *
     * @param sectionIndex index of the section in question
     * @param itemIndex    index of the item where 0 is the index of the first item in the section
     */
    public void notifySectionItemInserted(int sectionIndex, int itemIndex) {
        if (sections == null) {
            buildSectionIndex();
            notifyAllSectionsDataSetChanged();
        } else {
            buildSectionIndex();
            Section section = this.sections.get(sectionIndex);

            int offset = getSectionOffset(section, itemIndex);
            notifyItemInserted(section.adapterPosition + offset);
        }
    }

    /**
     * Notify that an item has been removed from a section
     * This should be called instead of the RecyclerView.Adapter.notifyItemRemoved
     *
     * @param sectionIndex index of the section in question
     * @param itemIndex    index of the item in the section where 0 is the index of the first item in the section
     */
    public void notifySectionItemRemoved(int sectionIndex, int itemIndex) {
        if (sections == null) {
            buildSectionIndex();
            notifyAllSectionsDataSetChanged();
        } else {
            buildSectionIndex();
            Section section = this.sections.get(sectionIndex);

            int offset = getSectionOffset(section, itemIndex);
            notifyItemRemoved(section.adapterPosition + offset);
        }
    }


    private int getSectionOffset(Section section, int offset) {
        if (section.hasChildHeader) {
            offset += 1;
        }

        if (section.hasParentHeader) {
            offset += 1;
        }
        return offset;
    }

    /**
     * Notify that a new section has been added
     *
     * @param sectionIndex position of the new section where the first section position starts from 0
     */
    public void notifySectionInserted(int sectionIndex) {
        if (sections == null) {
            buildSectionIndex();
            notifyAllSectionsDataSetChanged();
        } else {
            buildSectionIndex();
            Section section = this.sections.get(sectionIndex);
            notifyItemRangeInserted(section.adapterPosition, section.length);
        }
    }

    /**
     * Notify that a section has been removed
     *
     * @param sectionIndex position of the removed section
     */
    public void notifySectionRemoved(int sectionIndex) {
        if (sections == null) {
            buildSectionIndex();
            notifyAllSectionsDataSetChanged();
        } else {
            Section section = this.sections.get(sectionIndex);
            buildSectionIndex();
            notifyItemRangeRemoved(section.adapterPosition, section.length);
        }
    }

    /**
     * Post an action to be run later.
     * RecyclerView doesn't like being mutated during a scroll. We can't detect when a
     * scroll is actually happening, unfortunately, so the best we can do is post actions
     * from notify* methods to be run at a later date.
     *
     * @param action action to run
     */
    private void post(Runnable action) {
        if (mainThreadHandler == null) {
            mainThreadHandler = new Handler(Looper.getMainLooper());
        }

        mainThreadHandler.post(action);
    }
}

