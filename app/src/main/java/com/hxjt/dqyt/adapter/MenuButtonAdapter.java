package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.bean.MenuButtonBean;

import java.util.List;

public class MenuButtonAdapter extends ArrayAdapter<MenuButtonBean> {

    private LayoutInflater inflater;
    private int selectedItem = 0;

    public MenuButtonAdapter(Context context, List<MenuButtonBean> menuItems) {
        super(context, 0, menuItems);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_button_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iconView = convertView.findViewById(R.id.icon_view);
            viewHolder.textView = convertView.findViewById(R.id.text_view);
            viewHolder.llButton = convertView.findViewById(R.id.ll_menu_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MenuButtonBean menuButton = getItem(position);
        if (menuButton != null) {
            viewHolder.textView.setText(menuButton.getName());
            viewHolder.iconView.setImageResource(menuButton.getResourceId());

            if (position == selectedItem) {
                viewHolder.textView.setTextColor(getContext().getResources().getColor(R.color.white));
                viewHolder.llButton.setBackground(getContext().getDrawable(R.drawable.rectangle_blue_rounded));
                viewHolder.iconView.setColorFilter(ContextCompat.getColor(getContext(), R.color.white), PorterDuff.Mode.SRC_IN);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        getContext().getResources().getDimensionPixelSize(R.dimen.dp_100), // 设置宽度为200dp
                        getContext().getResources().getDimensionPixelSize(R.dimen.dp_40)
                );
                viewHolder.llButton.setLayoutParams(layoutParams);
            } else {
                viewHolder.textView.setTextColor(getContext().getResources().getColor(R.color.black));
                viewHolder.llButton.setBackground(getContext().getDrawable(R.drawable.rectangle_gray_border));
                viewHolder.iconView.clearColorFilter();

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        getContext().getResources().getDimensionPixelSize(R.dimen.dp_90), // 设置宽度为200dp
                        getContext().getResources().getDimensionPixelSize(R.dimen.dp_40)
                );
                viewHolder.llButton.setLayoutParams(layoutParams);
            }
        }

        return convertView;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView iconView;
        TextView textView;
        LinearLayout llButton;
    }
}
