package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.hxjt.dqyt.R;

public class TextButtonAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mButtonLabels;

    public TextButtonAdapter(Context context, String[] buttonLabels) {
        mContext = context;
        mButtonLabels = buttonLabels;
    }

    @Override
    public int getCount() {
        return mButtonLabels.length;
    }

    @Override
    public Object getItem(int position) {
        return mButtonLabels[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // 如果convertView为空，创建一个新的TextView
            textView = new TextView(mContext);
            textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.dp_30))); // 设置布局参数
            textView.setPadding(8, 8, 8, 8);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setGravity(Gravity.CENTER); // 设置文本居中
            textView.setTextColor(Color.WHITE); // 设置文本颜色
            textView.setBackgroundResource(R.drawable.shape_btn_bg); // 设置背景颜色
        } else {
            // 如果convertView不为空，直接复用
            textView = (TextView) convertView;
        }

        // 设置文本
        textView.setText(mButtonLabels[position]);

        return textView;
    }
}

