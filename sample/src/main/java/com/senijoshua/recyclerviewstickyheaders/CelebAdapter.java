package com.senijoshua.recyclerviewstickyheaders;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.senijoshua.library.NestedAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class CelebAdapter extends NestedAdapter {
    Locale locale = Locale.getDefault();
    static final boolean USE_DEBUG_APPEARANCE = false;

    List<Celebrity> celebrities = new ArrayList<>();
    List<Section> sections = new ArrayList<>();

    private class Section {
        String sectionChildTitle;
        String sectionParentTitle;
        List<Celebrity> celebrities = new ArrayList<>();
    }

    public class ItemViewHolder extends NestedAdapter.ItemViewHolder {
        TextView name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.itemview_tv_name);
        }
    }

    public class HeaderViewHolder extends NestedAdapter.HeaderViewHolder {
        TextView parentHeaderName;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            parentHeaderName = (TextView) itemView.findViewById(R.id.parent_header_tv_header_text);
        }
    }

    public class ChildHeaderViewHolder extends NestedAdapter.ChildHeaderViewHolder {
        TextView childHeaderName;

        public ChildHeaderViewHolder(View itemView) {
            super(itemView);
            childHeaderName = (TextView) itemView.findViewById(R.id.child_header_tv_header_text);
        }
    }

    public CelebAdapter() {
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
        Collections.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section section1, Section section2) {
                return section1.sectionParentTitle.compareToIgnoreCase(section2.sectionParentTitle);
            }
        });
        notifyAllSectionsDataSetChanged();
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
    public boolean doesSectionHaveHeader(int sectionIndex) {
        Section section = sections.get(sectionIndex);
        if (sectionIndex - 1 != -1) {
            Section previousSection = sections.get(sectionIndex - 1);
            return !previousSection.sectionParentTitle.equals(section.sectionParentTitle);
        } else {
            return true;
        }
    }

    @Override
    public boolean doesSectionHaveChildHeader(int sectionIndex) {
//        Section section = sections.get(sectionIndex);
//        if (sectionIndex - 1 != -1) {
//            Section previousSection = sections.get(sectionIndex - 1);
//            return !previousSection.sectionChildTitle.equals(section.sectionChildTitle);
//        } else {
//            return true;
//        }
        return false;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemViewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_itemview, parent, false));
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int HeaderViewType) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_parentheaderview, parent, false));
    }

    @Override
    public ChildHeaderViewHolder onCreateChildHeaderViewHolder(ViewGroup parent, int childHeaderUserType) {
        return new ChildHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_childheaderview, parent, false));
    }

    @Override
    public void onBindItemViewHolder(NestedAdapter.ItemViewHolder holder, int sectionIndex, int itemIndex, int itemViewType) {
        Section section = sections.get(sectionIndex);
        Celebrity celebrity = section.celebrities.get(itemIndex);
        ItemViewHolder vh = (ItemViewHolder) holder;
        vh.name.setText(celebrity.getName());
        super.onBindItemViewHolder(holder, sectionIndex, itemIndex, itemViewType);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindHeaderViewHolder(NestedAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
        Section section = sections.get(sectionIndex);
        HeaderViewHolder vh = (HeaderViewHolder) viewHolder;
        vh.parentHeaderName.setText(section.sectionParentTitle + "- list");
    }

    @Override
    public void onBindChildHeaderViewHolder(NestedAdapter.ChildHeaderViewHolder viewHolder, int sectionIndex, int footerType) {
        Section section = sections.get(sectionIndex);
        ChildHeaderViewHolder vh = (ChildHeaderViewHolder) viewHolder;
        vh.childHeaderName.setText(section.sectionChildTitle);
    }

    private String capitalize(String s) {
        if (s != null && s.length() > 0) {
            return s.substring(0, 1).toUpperCase(locale) + s.substring(1);
        }

        return "";
    }

    private String pad(int spaces) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            b.append(' ');
        }
        return b.toString();
    }
}
