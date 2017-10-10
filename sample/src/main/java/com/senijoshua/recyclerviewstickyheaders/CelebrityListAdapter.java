package com.senijoshua.recyclerviewstickyheaders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.senijoshua.library.NestedSectionAdapter;

import java.util.ArrayList;
import java.util.List;


public class CelebrityListAdapter extends NestedSectionAdapter{

    public CelebrityListAdapter(){

    }

    public void setCelebrities(List<Celebrity> celebrities){

    }

    private class Section {
        String sectionChildTitle;
        String sectionParentTitle;
        List<Celebrity> celebrities = new ArrayList<>();
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemViewType) {
        return super.onCreateItemViewHolder(parent, itemViewType);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder holder, int sectionIndex, int itemIndex, int itemViewType) {
        super.onBindItemViewHolder(holder, sectionIndex, itemIndex, itemViewType);
    }

    @Override
    public ChildHeaderViewHolder onCreateChildHeaderViewHolder(ViewGroup parent, int childHeaderViewType) {
        return super.onCreateChildHeaderViewHolder(parent, childHeaderViewType);
    }

    @Override
    public void onBindChildHeaderViewHolder(ChildHeaderViewHolder holder, int sectionIndex, int childHeaderViewType) {
        super.onBindChildHeaderViewHolder(holder, sectionIndex, childHeaderViewType);
    }

    @Override
    public ParentHeaderViewHolder onCreateParentHeaderViewHolder(ViewGroup parent, int parentHeaderViewType) {
        return super.onCreateParentHeaderViewHolder(parent, parentHeaderViewType);
    }

    @Override
    public void onBindParentHeaderViewHolder(ParentHeaderViewHolder holder, int sectionIndex, int parentHeaderViewType) {
        super.onBindParentHeaderViewHolder(holder, sectionIndex, parentHeaderViewType);
    }

    public class ItemViewholder extends NestedSectionAdapter.ItemViewHolder{
        TextView name;

        public ItemViewholder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.itemview_tv_name);
        }
    }

    public class ChildHeaderViewHolder extends NestedSectionAdapter.ChildHeaderViewHolder{
        TextView childHeaderName;

        public ChildHeaderViewHolder(View itemView) {
            super(itemView);
            childHeaderName = (TextView) itemView.findViewById(R.id.child_header_tv_header_text);
        }
    }

    public class ParentHeaderViewHolder extends NestedSectionAdapter.ParentHeaderViewHolder{
        TextView parentHeaderName;

        public ParentHeaderViewHolder(View itemView) {
            super(itemView);
            parentHeaderName = (TextView) itemView.findViewById(R.id.parent_header_tv_header_text);
        }
    }
}
