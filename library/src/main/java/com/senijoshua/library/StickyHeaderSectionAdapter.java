package com.senijoshua.library;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class StickyHeaderSectionAdapter extends RecyclerView.Adapter<StickyHeaderSectionAdapter.GroupViewHolder> {

    private ArrayList<Group> groups;
    private ArrayList<Section> sections;
    private HashMap<Integer, Boolean> hiddenGroups = new HashMap<>();
    private HashMap<Integer, Boolean> hiddenSections = new HashMap<>();
    private HashMap<Integer, GroupSelectionState> selectionStateByGroup = new HashMap<>();
    private HashMap<Integer, SectionSelectionState> selectionStateBySection = new HashMap<>();
    private int[] groupIndicesByAdapterPosition;
    private int totalNumberOfItems;
    private Handler mainThreadHandler;


    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        private int group;
        private int groupSectionsCount;

        public GroupViewHolder(View itemView) {
            super(itemView);
        }

        public boolean isGroupHeader() {
            return false;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public int getGroupSectionsCount() {
            return groupSectionsCount;
        }

        public void setGroupSectionsCount(int groupSectionsCount) {
            this.groupSectionsCount = groupSectionsCount;
        }

        public int getItemViewBaseType() {
            return SectioningAdapter.unmaskBaseViewType(getItemViewType());
        }

        public int getItemViewUserType() {
            return SectioningAdapter.unmaskUserViewType(getItemViewType());
        }
    }

    public static class SectionViewHolder extends GroupViewHolder {
        private int section;
        private int sectionPositionInGroup;
        private int sectionItemsCount;

        public SectionViewHolder(View itemView) {
            super(itemView);
        }

        public boolean isSectionHeader() {
            return false;
        }

        public int getSection() {
            return section;
        }

        private void setSection(int section) {
            this.section = section;
        }

        public int getSectionItemsCount() {
            return sectionItemsCount;
        }

        public void setSectionItemsCount(int sectionItemsCount) {
            this.sectionItemsCount = sectionItemsCount;
        }

        public int getSectionPositionInGroup() {
            return sectionPositionInGroup;
        }

        public void setSectionPositionInGroup(int sectionPositionInGroup) {
            this.sectionPositionInGroup = sectionPositionInGroup;
        }
    }

    public static class ItemViewHolder extends SectionViewHolder {
        private int itemPositionInSection;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public int getItemPositionInSection() {
            return itemPositionInSection;
        }

        public void setItemPositionInSection(int itemPositionInSection) {
            this.itemPositionInSection = itemPositionInSection;
        }
    }

    public static class GroupHeaderViewHolder extends GroupViewHolder {

        public GroupHeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public boolean isGroupHeader() {
            return true;
        }
    }

    public static class SectionHeaderViewHolder extends SectionViewHolder {

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public boolean isSectionHeader() {
            return true;
        }
    }


    //Override this function to set the view type for the group header
    //The value you return here will be passes to onCreateItemViewHolder and onBindItemViewHolder as the viewType
    public int getGroupHeaderViewType(int groupIndex) {
        return 0;
    }


    //Override this function to set the view type for the group header
    //The value you return here will be passes to onCreateItemViewHolder and onBindItemViewHolder as the viewType
    //groupIndex- the section's group
    public int getSectionHeaderViewType(int groupIndex, int sectionIndex) {
        return 0;
    }

    public int getSectionItemViewType(int groupIndex, int sectionIndex, int itemIndex) {
        return 0;
    }

    /**
     * This is used to create a ViewHolder for an itemView in a given section.
     *
     * @param parent       The ViewGroup into which the new view will be added after it's  been bound to an adapter position.
     * @param itemViewType If getSectionItemViewType is overridden to return custom types, this will be the specified type
     * @return a new ItemViewHolder holding an itemView
     */
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemViewType) {
        return null;
    }

    /**
     * This creates a ViewHolder for the Section header view
     *
     * @param parent                The ViewGroup that section header view would be added to after it's been bound to an adapter position.
     * @param sectionHeaderViewType if getSectionHeaderViewType is overriden to return custom types, this will be the specified type
     * @return a new SectionHeaderViewHolder containing a GroupHeaderView
     */
    public SectionHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int sectionHeaderViewType) {
        return null;
    }

    /**
     * This is called when a ViewHolder is required for the Group header view
     *
     * @param parent              The ViewGroup that the group header view would be added to after it's been bound to an adapter position.
     * @param groupHeaderViewType if getSectionHeaderViewType is overriden to return custom types, this will be the specified type.
     * @return a new GroupHeaderViewHolder containing a GroupHeaderView
     */
    public GroupHeaderViewHolder onCreateGroupHeaderViewHolder(ViewGroup parent, int groupHeaderViewType) {
        return null;
    }

    /**
     * Binds item data to the view at a specified position
     *
     * @param holder       the holder of the view that would be bound to the item data
     * @param groupIndex   index of the group within which the item resides
     * @param sectionIndex index of the section that holds the item
     * @param itemIndex    index of the concened item in the section
     * @param itemViewType the type of the view which could be a user-provided custom type through overriding getItemViewType
     */
    public void onBindItemViewHolder(ItemViewHolder holder, int groupIndex, int sectionIndex, int itemIndex, int itemViewType) {

    }
    //This is called when you want to set a section header view at a particular position
    //groupIndex is the index of the group containing the section
    //sectionIndex is the index of the section in this group starting from 0

    /**
     * This binds header data to section header view for a section
     *
     * @param holder                the holder of the section header view that would be updated
     * @param groupIndex            index of the group within which the section header view resides
     * @param sectionIndex          index of the section that contains the header view
     * @param sectionHeaderViewType the type of the view that could be a user-supplied custom type through overriding getSectionHeaderViewType
     */
    public void onBindSectionHeaderViewHolder(SectionHeaderViewHolder holder, int groupIndex, int sectionIndex, int sectionHeaderViewType) {

    }

    /**
     * This binds the respective header data to the group header view for a group
     *
     * @param holder              the holder of the group header view that would be updated
     * @param groupIndex          the index of the group containing the header to update
     * @param groupHeaderViewType the type of the view which could be a custom type set by the user through overriding getGroupHeaderViewType
     */
    public void onBindGroupHeaderViewHolder(GroupHeaderViewHolder holder, int groupIndex, int groupHeaderViewType) {

    }


    private static class Group {
        int adapterPosition;    // adapterPosition of first item i.e the header  of this group
        int numberOfSections;   // number of items including section header + section items excluding group header
        int length;             // total number of items in groups including  group header, sections and all its items
        boolean isStickyHeader;      // if true, the group is at the top of the list i.e sticky header
        private int[] sectionIndicesByGroupPosition; // An array that holds the indices of the group's contents at each position
        // i.e. section 0 & item 0 in that section would both have indices of 0
        // at positions 0 and 1 respectively.
    }


    private static class Section {
        int adapterPosition;    // adapterPosition of first item (the header) of this section
        int groupIndex;         // this denotes the group this section belongs to where 0 is the first group in the list.
        int numberOfItems;      // number of items in the section excluding the section header. (Ideally this would be length - 1)
        int length;             // total number of items in sections including header and footer
        int indexInGroup;       // This denotes the index of the section in the group where 0 is the first section in the group
        boolean isStickyHeader;      // if true, the section is at the top of the list under the group header
    }

    private static class GroupSelectionState {
        boolean group;
        SparseBooleanArray sections = new SparseBooleanArray();
        SparseBooleanArray items = new SparseBooleanArray();
    }

    private static class SectionSelectionState {
        boolean section;
        SparseBooleanArray items = new SparseBooleanArray();
    }

    /**
     * @param groupIndex the index of the concerned group
     * @return true if the group has been scrolled off the visible screen or hidden.
     */
    public boolean isGroupHidden(int groupIndex) {
        if (hiddenGroups.containsKey(groupIndex)) {
            return hiddenGroups.get(groupIndex);
        }

        return false;
    }

    /**
     * @param groupIndex   the index of the containing group
     * @param sectionIndex the index of the concerned section within the group
     * @return true if the section has been scrolled off the visible screen or hidden.
     */
    public boolean isSectionHidden(int sectionIndex, int groupIndex) {
        if (hiddenSections.containsKey(sectionIndex)) {
            return hiddenSections.get(sectionIndex);
        }

        return false;
    }

    /**
     * @return Number of groups
     */
    public int getNumberOfGroups() {
        return 0;
    }

    public int getNumberOfItemsInSectionWithinGroup(int groupIndex) {
        return 0;
        //have a global count
        //Iterate through all the sections and for each section that has groupIndex as groupIndex
        //add the numberofItemsCount to the global count
        //Then return the global count.
    }

    /**
     * @param groupIndex index of the concerned group where the first item starts with index 0
     * @return the number of sections in the specified group. A possible count could be the number of section headers.
     */
    public int getNumberOfSectionsInGroup(int groupIndex) {
        return 0;
        //have a global count
        //Iterate through all the sections and for each section that has groupIndex as groupIndex
        //increment the global count
        //return the global count
    }

    /**
     * @param sectionIndex index of the concerned section within a group
     * @param groupIndex   index of the group that contains the concerned section
     * @return the number of items in the specified section
     */
    public int getNumberOfItemsInSection(int sectionIndex, int groupIndex) {
        return 0;
    }


    /**
     * @param GroupIndex index of the concerned group
     * @return if this group is a header at the top of the list.
     */
    public boolean isGroupSticky(int GroupIndex) {
        return false;
    }

    /**
     * @param groupIndex   index of the group where the respective section is located
     * @param sectionIndex index of the concerned section within a group
     * @return if this section is a header at the top of the list under the group's sticky header.
     */
    public boolean isSectionSticky(int sectionIndex, int groupIndex) {
        return false;
    }

    /**
     *
     * @param adapterPosition the position in the adapter which ranges from 0 to getItemCount() - 1
     * @return the index of the group that corresponds to the adapter position where 0 is the first group in the list
     */
    public int getGroupforAdapterPosition(int adapterPosition) {
        if (groups == null) {
            buildGroupIndex();
        }

        if (getItemCount() == 0) {
            return -1;
        }

        if (adapterPosition < 0 || adapterPosition >= getItemCount()) {
            throw new IndexOutOfBoundsException("The position in the adapter " + adapterPosition + " is not in range of items represented by the adapter");
        }

        return groupIndicesByAdapterPosition[adapterPosition];
    }

    /**
     *
     * @param groupIndex
     * @param adapterPosition
     * @return the index of the section that a certain pos
     */
    public int getSectionInGroupForAdapterPosition(int groupIndex, int adapterPosition) {

    }


    private void buildGroupIndex() {
        groups = new ArrayList<>();

        int i = 0;
        for (int groupIndex = 0, groupCount = getNumberOfGroups(); groupIndex < groupCount; groupIndex++) {
            Group group = new Group();
            group.adapterPosition = i;
            group.isStickyHeader = isGroupSticky(groupIndex);

            if (isGroupHidden(groupIndex)) {
                group.length = 0;
                group.numberOfSections = getNumberOfSectionsInGroup(groupIndex);
            } else {
                group.length = group.numberOfSections + getNumberOfItemsInSectionWithinGroup(groupIndex);
            }

            if (group.isStickyHeader) {
                group.length += 1; // room for f
            }

            this.groups.add(group);

            i += group.length;
        }

        totalNumberOfItems = i;

        i = 0;
        groupIndicesByAdapterPosition = new int[totalNumberOfItems];
        for (int groupIndex = 0, groupCount = getNumberOfGroups(); groupIndex < groupCount; groupIndex++) {
            Group group = groups.get(groupIndex);
            for (int pos = 0; pos < group.length; pos++) {
                groupIndicesByAdapterPosition[i + pos] = groupIndex;
            }
            i += group.length;

        }
        for (int groupIndex = 0, groupSize = getNumberOfGroups(); groupIndex < groupSize; groupIndex++) {
            buildSectionIndex(groupIndex, groupIndicesByAdapterPosition);
        }
    }

    private void buildSectionIndex(int groupIndex, int[] groupIndicesByAdapterPosition) {
        int totalNumberOfItemsInGroup;
        Group group = groups.get(groupIndex);
        sections = new ArrayList<>();

        int i = 0;
        for (int sectionIndexInGroup = 0, sectionCount = getNumberOfSectionsInGroup(groupIndex);
             sectionIndexInGroup < sectionCount; sectionIndexInGroup++) {
            Section section = new Section();
            section.adapterPosition = group.adapterPosition + i + 1;
            section.groupIndex = groupIndex;
            section.indexInGroup = sectionIndexInGroup;
            section.isStickyHeader = isSectionSticky(sectionIndexInGroup, groupIndex);

            if (isSectionHidden(sectionIndexInGroup, groupIndex)) {
                section.length = 0;
                section.numberOfItems = getNumberOfItemsInSection(sectionIndexInGroup, groupIndex);
            } else {
                section.length = section.numberOfItems = getNumberOfItemsInSection(sectionIndexInGroup, groupIndex);
            }

            if (section.isStickyHeader) {
                section.length += 1; // room for header
            }

            this.sections.add(section);

            i += section.length;
        }

        totalNumberOfItemsInGroup = i;

        i = 0;
        group.sectionIndicesByGroupPosition = new int[totalNumberOfItemsInGroup];
        for (int sectionIndexInGroup = 0, sectionCount = getNumberOfSectionsInGroup(groupIndex);
             sectionIndexInGroup < sectionCount; sectionIndexInGroup++) {
            Section section = sections.get(sectionIndexInGroup);
            for (int pos = 0; pos < section.length; pos++) {
                group.sectionIndicesByGroupPosition[i + pos] = sectionIndexInGroup;
            }

            i += section.length;
        }
    }
}


