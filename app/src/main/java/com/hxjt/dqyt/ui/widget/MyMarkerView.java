package com.hxjt.dqyt.ui.widget;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.hxjt.dqyt.R;

import android.content.Context;
import android.widget.TextView;

public class MyMarkerView extends MarkerView {

    private final TextView tvContent;
    private String yAxisName;

    public MyMarkerView(Context context, String yAxisName) {
        super(context, R.layout.marker_view);
        tvContent = findViewById(R.id.tvContent);
        this.yAxisName = yAxisName;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText("位移"+":  "+e.getX() + "\n\n" + yAxisName+":  "+e.getY());
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // This centers the marker view above the selected point
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

