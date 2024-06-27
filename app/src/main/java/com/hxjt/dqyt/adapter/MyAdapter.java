package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.ui.detail.DeviceDetailActivity;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.SPUtil;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private  List<DeviceInfoBean> mData;
    private final LayoutInflater mInflater;
    private Context mContext;
    private String mTimeValue;
    private AlphaAnimation alphaAnimation;
    private String deviceNo;
    private DeviceInfoBean deviceInfoBean;
    private String dlqType = "sk";

    public MyAdapter(Context context, List<DeviceInfoBean> data,String timeValue,String deviceNo) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mTimeValue = timeValue;
        this.deviceNo = deviceNo;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public DeviceInfoBean getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.device_container, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.image_view);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvCode = convertView.findViewById(R.id.tv_code);
            viewHolder.tvTimeValue = convertView.findViewById(R.id.tv_time_value);
            viewHolder.tvChannel = convertView.findViewById(R.id.tv_channel);
            viewHolder.llDeviceContainer = convertView.findViewById(R.id.ll_device_container);
            viewHolder.ivDeviceStatus = convertView.findViewById(R.id.iv_device);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 绑定数据
        DeviceInfoBean device = getItem(position);

        viewHolder.tvTitle.setText(device.getName());
        viewHolder.tvCode.setText(device.getAddr());
        viewHolder.tvChannel.setText(""+device.getChl());

        int drawable = DeviceUtil.getDeviceImageByType(device.getDev_type());
        if(drawable != -1){
            viewHolder.imageView.setImageResource(drawable);
        }

        //塑壳图片
        if(device.getDev_type()!=null &&device.getDev_type().equals(Constants.SK645)){
            int img = R.drawable.devoce_sk645;
            if(dlqType!=null && dlqType.equals("lc")){
                img =R.drawable.icon_sk_white;
            }
            viewHolder.imageView.setImageResource(img);
        }

        viewHolder.llDeviceContainer.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DeviceDetailActivity.class);
            intent.putExtra("device_no", deviceNo); // 添加需要传递的参数
            intent.putExtra("device_info_bean",device);
            mContext.startActivity(intent);
        });

        if(mTimeValue != null && deviceInfoBean != null) {
            String t0 = mData.get(position).getDev_type();
            String t1 = deviceInfoBean.getDev_type();

            String c0 = mData.get(position).getAddr();
            String c1 = deviceInfoBean.getAddr();

            if(t0!=null&&t1!=null&&c0!=null&&c1!=null) {
                if(t0.equals(t1) && c0.equals(c1)){
                    viewHolder.tvTimeValue.setText(mTimeValue);

                    if (viewHolder.tvTimeValue.getTag() != null) {
                        viewHolder.tvTimeValue.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) viewHolder.tvTimeValue.getTag());
                    }

                    viewHolder.ivDeviceStatus.setVisibility(View.VISIBLE);
                    ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
                            alphaAnimation.setDuration(500);
                            alphaAnimation.setRepeatCount(Animation.INFINITE);
                            alphaAnimation.setRepeatMode(Animation.RESTART);
                            viewHolder.ivDeviceStatus.setAnimation(alphaAnimation);
                            alphaAnimation.start();

                            viewHolder.ivDeviceStatus.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    };

                    viewHolder.ivDeviceStatus.getViewTreeObserver().addOnGlobalLayoutListener(listener);
                    viewHolder.ivDeviceStatus.setTag(listener);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        viewHolder.ivDeviceStatus.clearAnimation();
                        if (viewHolder.tvTimeValue.getTag() != null) {
                            viewHolder.tvTimeValue.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) viewHolder.tvTimeValue.getTag());
                        }
                        viewHolder.ivDeviceStatus.setVisibility(View.INVISIBLE);
                    }, 3000);
                }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        TextView tvCode;
        TextView tvChannel;
        TextView tvTimeValue;
        LinearLayout llDeviceContainer;
        ImageView ivDeviceStatus;
    }

    public void update(Context context, List<DeviceInfoBean> data,String timeValue,String deviceNo){
        mData = data;
        mContext = context;
        mTimeValue = timeValue;
        this.deviceNo = deviceNo;
        notifyDataSetChanged();
    }

    public void updateTimeValue(DeviceInfoBean deviceInfoBean,String timeValue){
        this.deviceInfoBean = deviceInfoBean;
        mTimeValue = timeValue;
        notifyDataSetChanged();
    }

    public void updateDlqImg(){
        String type =SPUtil.getString(Constants.DLQ_TYPE,"sk");
        if(dlqType!=null && dlqType.equals(type)) return;

        dlqType = type;
        notifyDataSetChanged();
    }

    public void updateStatus(){
        notifyDataSetChanged();
    }

}

