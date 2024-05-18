package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.utils.TextUtil;

import java.math.BigDecimal;
import java.util.Map;

public class DeviceStatusAdapter extends BaseAdapter {
    private Context mContext;
    private Map<String,Object>[] mLabels;
    private Map<String,Object> mValueMap;

    public DeviceStatusAdapter(Context context, Map<String,Object>[] labels,Map<String,Object> valueMap) {
        mContext = context;
        mLabels = labels;
        mValueMap = valueMap;
    }

    @Override
    public int getCount() {
        return mLabels.length;
    }

    @Override
    public Object getItem(int position) {
        return mLabels[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // 如果convertView为空，使用LayoutInflater加载布局文件
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_status, parent, false);

            // 初始化ViewHolder，并查找子视图
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.image_view);
            viewHolder.textViewTop = convertView.findViewById(R.id.tv_value);
            viewHolder.textViewBottom = convertView.findViewById(R.id.tv_title);

            // 将ViewHolder保存到convertView中
            convertView.setTag(viewHolder);
        } else {
            // 如果convertView不为空，直接从convertView中获取ViewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        LinearLayout itemLayout = (LinearLayout) convertView;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int gridWidth = screenWidth - mContext.getResources().getDimensionPixelSize(R.dimen.dp_120);
        int itemWidth = gridWidth / 5;
        int itemHeight = itemWidth *3 / 8;

        // 设置 item 的宽高
        ViewGroup.LayoutParams layoutParams = itemLayout.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemHeight;
        itemLayout.setLayoutParams(layoutParams);

        Integer drawable = (Integer) mLabels[position].get("resource_id");
        if(drawable != null && drawable != -1){
            viewHolder.imageView.setImageResource(drawable); // 设置左侧图片
        }

        // 设置文本内容
        viewHolder.textViewBottom.setText((String) mLabels[position].get("title"));

        String tag = (String) mLabels[position].get("tag");

        if(mValueMap != null){
            for (Map.Entry<String, Object> entry : mValueMap.entrySet()) {
                if (TextUtil.isEqualIgnoreCase(entry.getKey(),tag)) {
                    String value = entry.getValue() + "";
                    if(!value.equals("null")){
                        if(TextUtil.isEqualIgnoreCase(entry.getKey(),"deviceStatus")){
                            //在线状态
                            if(value.equals("1")){
                                value = "在线";
                                viewHolder.imageView.setImageResource(R.drawable.icon_online);
                            } else {
                                value = "离线";
                                viewHolder.imageView.setImageResource(R.drawable.icon_offline);
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"sjStatus1")){
                            //水浸状态1
                            if(value.equals("0")){
                                value = "正常";
                            } else if(value.equals("1")){
                                value = "有水";
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"sjStatus2")){
                            //水浸状态2
                            if(value.equals("0")){
                                value = "正常";
                            } else if(value.equals("1")){
                                value = "有水";
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"bsStatus")){
                            //闭锁状态
                            if(value.equals("1")){
                                value = "闭锁";
                            } else if(value.equals("0")){
                                value = "解锁";
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"iN1Clsd")||TextUtil.isEqualIgnoreCase(entry.getKey(),"iN2Clsd")){
                            //IN1,IN2 测量速度选择
                            if(value.equals("0")){
                                value = "高速";
                            } else if(value.equals("1")){
                                value = "中速";
                            }else if(value.equals("2")){
                                value = "低速";
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"yxStatus")){
                            //运行状态
                            BigDecimal bd = new BigDecimal(value);
                            int intValue = bd.intValue();
                            if(intValue == 1){
                                value = "正转运行";
                            } else if(intValue== 2){
                                value = "反转运行";
                            }else if(intValue == 3){
                                value = "正转点动";
                            }else if(intValue == 4){
                                value = "反转点动";
                            }else if(intValue == 5){
                                value = "停机";
                            }else if(intValue == 6){
                                value = "紧急停机";
                            }else if(intValue == 7){
                                value = "故障复位";
                            }else if(intValue == 8){
                                value = "点动停止";
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"jsff")){
                            //IN1,IN2 测量速度选择
                            if(value.equals("1")){
                                value = "接箍法";
                            } else if(value.equals("2")){
                                value = "音速法";
                            }else if(value.equals("3")){
                                value = "音标法";
                            }
                        } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"bpqZtz1") || TextUtil.isEqualIgnoreCase(entry.getKey(),"bpqZtz2")){
                            //运行状态
                            if(value.equals("1")){
                                value = "正转运行中";
                            } else if(value.equals("2")){
                                value = "反转运行中";
                            }else if(value.equals("3")){
                                value = "变频器停机中";
                            }else if(value.equals("4")){
                                value = "变频器故障中 ";
                            }else if(value.equals("5")){
                                value = "变频器POFF状态";
                            }
                        } else  if(TextUtil.isEqualIgnoreCase(entry.getKey(),"bjQstatus")){
                            //报警器状态
                            if(value.equals("0")){
                                value = "正常";
                            } else if(value.equals("1")){
                                value = "报警";
                            }
                        } else  if(TextUtil.isEqualIgnoreCase(entry.getKey(),"kaiguan")){
                            //开关状态
                            if(value.equals("0")){
                                value = "分闸";
                                viewHolder.imageView.setImageResource(R.drawable.icon_fz);
                            } else if(value.equals("1")){
                                value = "合闸";
                                viewHolder.imageView.setImageResource(R.drawable.icon_hz);
                            }
                        } else  if(TextUtil.isEqualIgnoreCase(entry.getKey(),"Data")){
                            //接触器运行状态
                            if(value.equals("0")){
                                value = "无信号";
                                viewHolder.imageView.setImageResource(R.drawable.icon_xhckgl);
                            } else if(value.equals("1")){
                                value = "有信号";
                                viewHolder.imageView.setImageResource(R.drawable.icon_xhckgl);
                            }
                        }
                        viewHolder.textViewTop.setText(value);
                    }
                    break;
                }
            }
        }
        return convertView;
    }

    // ViewHolder类用于缓存子视图
    static class ViewHolder {
        ImageView imageView;
        TextView textViewTop;
        TextView textViewBottom;
    }

    public void update(Context context, Map<String,Object>[] labels,Map<String,Object> valueMap){
        mContext = context;
        mLabels = labels;
        mValueMap = valueMap;
        notifyDataSetChanged();
    }
}


