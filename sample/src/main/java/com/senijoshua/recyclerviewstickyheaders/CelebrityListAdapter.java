package com.senijoshua.recyclerviewstickyheaders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.senijoshua.library.NestedSectionAdapter;

import java.util.ArrayList;
import java.util.List;


public class CelebrityListAdapter extends NestedSectionAdapter {

    List<Celebrity> celebrities = new ArrayList<>();
    List<Section> sections = new ArrayList<>();

    public CelebrityListAdapter() {

    }

    public void setCelebrities(List<Celebrity> celebrityList) {
        //reset to initial state
        celebrities.clear();
        celebrities.addAll(celebrityList);
        sections.clear();

        //sort celebrities into two categories,
        //1.By their name and 2. By their status
        Section section = null;
        for (Celebrity celeb : celebrities) {
            if (celeb.getName().charAt(0) != 0 && celeb.getStatus() != null) {
                if (section != null) {
                    sections.add(section);
                }
                section = new Section();
                section.sectionChildTitle = String.valueOf(celeb.getName().charAt(0));
                section.sectionParentTitle = celeb.getStatus();
            }

            if (section != null) {
                section.celebrities.add(celeb);
            }
        }

        sections.add(section);
        notifyAllSectionsDataSetChanged();
    }

    private static class Section {
        String sectionChildTitle;
        String sectionParentTitle;
        List<Celebrity> celebrities = new ArrayList<>();
    }

    @Override
    public int getNumberOfSections() {
        return sections.size();
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return sections.get(sectionIndex).celebrities.size();
    }

    @Override
    public boolean doesSectionHaveChildHeader(int sectionIndex) {
        return true;
    }

    @Override
    public boolean doesSectionHaveParentHeader(int sectionIndex) {
        return true;
    }

    @Override
    public NestedSectionAdapter.ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemViewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_itemview, parent, false));
    }

    @Override
    public void onBindItemViewHolder(NestedSectionAdapter.ItemViewHolder holder, int sectionIndex, int itemIndex, int itemViewType) {
        Section section = sections.get(sectionIndex);
        Celebrity celebrity = section.celebrities.get(itemIndex);
        ItemViewHolder vh = (ItemViewHolder) holder;
        vh.name.setText(celebrity.getName());
        super.onBindItemViewHolder(holder, sectionIndex, itemIndex, itemViewType);
    }

    @Override
    public ChildHeaderViewHolder onCreateChildHeaderViewHolder(ViewGroup parent, int childHeaderViewType) {
        return new ChildHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_childheaderview, parent, false));
    }

    @Override
    public void onBindChildHeaderViewHolder(NestedSectionAdapter.ChildHeaderViewHolder holder, int sectionIndex, int childHeaderViewType) {
        Section section = sections.get(sectionIndex);
        ChildHeaderViewHolder vh = (ChildHeaderViewHolder) holder;
        vh.childHeaderName.setText(section.sectionChildTitle);
    }

    @Override
    public ParentHeaderViewHolder onCreateParentHeaderViewHolder(ViewGroup parent, int parentHeaderViewType) {
        return new ParentHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_parentheaderview, parent, false));
    }

    @Override
    public void onBindParentHeaderViewHolder(NestedSectionAdapter.ParentHeaderViewHolder holder, int sectionIndex, int parentHeaderViewType) {
        Section section = sections.get(sectionIndex);
        ParentHeaderViewHolder vh = (ParentHeaderViewHolder) holder;
        vh.parentHeaderName.setText(section.sectionParentTitle );
    }

    public class ItemViewHolder extends NestedSectionAdapter.ItemViewHolder {
        TextView name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.itemview_tv_name);
        }
    }

    public class ChildHeaderViewHolder extends NestedSectionAdapter.ChildHeaderViewHolder {
        TextView childHeaderName;

        public ChildHeaderViewHolder(View itemView) {
            super(itemView);
            childHeaderName = (TextView) itemView.findViewById(R.id.child_header_tv_header_text);
        }
    }

    public class ParentHeaderViewHolder extends NestedSectionAdapter.ParentHeaderViewHolder {
        TextView parentHeaderName;

        public ParentHeaderViewHolder(View itemView) {
            super(itemView);
            parentHeaderName = (TextView) itemView.findViewById(R.id.parent_header_tv_header_text);
        }
    }
}
