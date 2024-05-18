package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.ui.detail.DeviceDetailActivity;
import com.hxjt.dqyt.utils.DeviceUtil;

public class DeviceAdapter extends BaseAdapter {
    private Context mContext;
    private DeviceInfoBean[] mDeviceArray;

    public DeviceAdapter(Context context, DeviceInfoBean[] deviceArray) {
        mContext = context;
        mDeviceArray = deviceArray;
    }

    @Override
    public int getCount() {
        return mDeviceArray.length;
    }

    @Override
    public Object getItem(int position) {
        return mDeviceArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.device_container, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.text_view);
        ImageView imageView = convertView.findViewById(R.id.image_view);
        LinearLayout llDeviceContainer = convertView.findViewById(R.id.ll_device_container);

        DeviceInfoBean deviceInfo = mDeviceArray[position];
        String displayText = deviceInfo.getName();
        textView.setText(displayText);

        int drawable = DeviceUtil.getDeviceImageByType(deviceInfo.getDev_type());
        imageView.setImageDrawable(AppCompatResources.getDrawable(mContext,drawable));

        llDeviceContainer.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DeviceDetailActivity.class);
            intent.putExtra("device_no", deviceInfo.getAddr()); // 添加需要传递的参数
            intent.putExtra("device_info_bean",deviceInfo);
            mContext.startActivity(intent);            });

        return convertView;
    }
}


