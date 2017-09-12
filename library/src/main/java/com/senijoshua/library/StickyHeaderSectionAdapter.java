package com.senijoshua.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class StickyHeaderSectionAdapter extends RecyclerView.Adapter<StickyHeaderSectionAdapter.ViewHolder> {

    @Override
    public StickyHeaderSectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(StickyHeaderSectionAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private int group;
        private int section;
        private int sectionItemsCount;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public boolean isGroupHeader() {
            return false;
        }

        public boolean isSectionHeader() {
            return false;
        }

        public int getGroup() {
            return group;
        }

        private void setGroup(int group) {
            this.group = group;
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

        void setsectionItemsCount(int sectionItemsCount) {
            this.sectionItemsCount = sectionItemsCount;
        }

        public int getItemViewBaseType() {
            return SectioningAdapter.unmaskBaseViewType(getItemViewType());
        }

        public int getItemViewUserType() {
            return SectioningAdapter.unmaskUserViewType(getItemViewType());
        }
    }
}
