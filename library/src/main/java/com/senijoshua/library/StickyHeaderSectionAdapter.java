package com.senijoshua.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class StickyHeaderSectionAdapter extends RecyclerView.Adapter<StickyHeaderSectionAdapter.GroupViewHolder> {

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

    public static class ItemViewHolder extends SectionViewHolder{
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

    public int getNumberOfGroups(){
        return 0;
    }

    public int getNumberOfSectionsInGroup(){
        return 0;
    }

    public boolean doesGroupHaveHeader(){
        return false;
    }

    //Override this function to set the view type for the group header
    //The value you return here will be passes to onCreateItemViewHolder and onBindItemViewHolder as the viewType
    public int getGroupHeaderViewType(int groupIndex) {
        return 0;
    }

    public int getNumberofItemsinSection(){
        return 0;
    }

    public boolean doesSectionHaveHeader(){
        return false;
    }

    //Override this function to set the view type for the group header
    //The value you return here will be passes to onCreateItemViewHolder and onBindItemViewHolder as the viewType
    //groupIndex- the section's group
    public int getSectionHeaderViewType(int groupIndex, int sectionIndex){
        return 0;
    }

    public int getSectionItemViewType(int groupIndex, int sectionIndex, int itemIndex){
        return 0;
    }

    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemViewType) {
        return null;
    }

    public SectionHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int sectionHeaderViewType) {
        return null;
    }

    public GroupHeaderViewHolder onCreateGroupHeaderViewHolder(ViewGroup parent, int groupHeaderViewType) {
        return null;
    }

    //This is called when you want to set a section header view at a particular position
    //groupIndex is the index of the group containing the section
    //sectionIndex is the index of the section in this group starting from 0
    public void onBindSectionHeaderViewHolder(SectionHeaderViewHolder holder, int groupIndex, int sectionIndex, int sectionHeaderViewType){

    }
    //This displays the header for a particular group at a particular position
    public void onBindGroupHeaderViewHolder(GroupHeaderViewHolder holder, int groupIndex, int groupHeaderViewType){

    }
    public void onBindSectionViewHolder(SectionViewHolder holder, int groupIndex, int sectionIndex, int sectionViewType){

    }

    public void onBindItemViewHolder(ItemViewHolder holder, int sectionIndex, int itemIndex, int itemViewType){

    }

    public int getGroupforAdapterPosition(int position){

    }
}


