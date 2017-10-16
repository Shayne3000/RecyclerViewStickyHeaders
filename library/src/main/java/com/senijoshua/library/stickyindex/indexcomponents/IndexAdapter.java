package com.senijoshua.library.stickyindex.indexcomponents;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.senijoshua.library.R;

public class IndexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private char[] dataSet;
    private RowStyle rowStyle;


    public IndexAdapter (char[] data, RowStyle style) {
        this.dataSet = data;
        this.rowStyle = style;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sticky_row_details, parent, false);

        if (rowStyle != null) {
            if (rowStyle.getRowHeight() != -1) {
                android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = rowStyle.getRowHeight().intValue();
                params.width = rowStyle.getStickyWidth().intValue();
                view.setLayoutParams(params);
            }

            TextView index = (TextView) view.findViewById(R.id.sticky_row_details_tv_row_index);

            if (rowStyle.getTextColor() != -1) {
                index.setTextColor(rowStyle.getTextColor());
            }

            if (rowStyle.getTextSize().intValue() != -1) {
                index.setTextSize(TypedValue.COMPLEX_UNIT_PX, rowStyle.getTextSize());
            }

            if (rowStyle.getTextStyle() != -1) {
                index.setTypeface(null, rowStyle.getTextStyle());
            }
        }

        return new IndexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IndexViewHolder viewHolder = (IndexViewHolder) holder;


        viewHolder.index.setText(Character.toString(dataSet[position]));

        if (isHeader(position)) {
            viewHolder.index.setVisibility(TextView.VISIBLE);
        } else {
            viewHolder.index.setVisibility(TextView.INVISIBLE);
        }
    }


    private Boolean isHeader (int pos) {
        if (pos == 0) {
            return Boolean.TRUE;
        } else if (isSameChar(dataSet[pos-1], dataSet[pos])) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    private Boolean isSameChar (char prev, char curr) {
        if (Character.toLowerCase(prev) == Character.toLowerCase(curr)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public void setDataSet(char[] dataSet) {
        this.dataSet = dataSet;
    }

    // ViewHolder class ____________________________________________________________________________
    public static class IndexViewHolder extends RecyclerView.ViewHolder {
        TextView index;

        public IndexViewHolder (View v) {
            super (v);
            index = (TextView) v.findViewById(R.id.sticky_row_details_tv_row_index);
        }
    }

    public static class RowStyle {
        Float rowHeight;
        Float stickyWidth;
        Integer textColor;
        Float textSize;
        Integer textStyle;

        public RowStyle (Float rHeight, Float sWidth, Integer tColor, Float tSize, Integer tStyle) {
            rowHeight = rHeight;
            stickyWidth = sWidth;
            textColor = tColor;
            textSize = tSize;
            textStyle = tStyle;
        }

        public Float getStickyWidth() {
            return stickyWidth;
        }

        public void setStickyWidth(Float stickyWidth) {
            this.stickyWidth = stickyWidth;
        }

        public Float getRowHeight() {
            return rowHeight;
        }

        public void setRowHeight(Float rowHeight) {
            this.rowHeight = rowHeight;
        }

        public Integer getTextColor() {
            return textColor;
        }

        public void setTextColor(Integer textColor) {
            this.textColor = textColor;
        }

        public Float getTextSize() {
            return textSize;
        }

        public void setTextSize(Float textSize) {
            this.textSize = textSize;
        }

        public Integer getTextStyle() {
            return textStyle;
        }

        public void setTextStyle(Integer textStyle) {
            this.textStyle = textStyle;
        }
    }
}

