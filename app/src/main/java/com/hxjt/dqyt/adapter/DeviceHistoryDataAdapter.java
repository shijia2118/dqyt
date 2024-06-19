package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.JsonUtil;
import com.lxj.xpopup.XPopup;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeviceHistoryDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HistoryDataBean> dataList;
    private List<String> headers;
    private static Context mContext;

    public DeviceHistoryDataAdapter(List<String> headers, List<HistoryDataBean> dataList,Context mContext) {
        this.headers = headers;
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device_history_data, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HistoryDataBean data = dataList.get(position);
        Map<String,Object> map = JsonUtil.toMap(data.getDeviceData());
        ((ItemViewHolder) holder).bind(data,map, headers,position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // 添加一个更新数据的方法
    public void updateData(List<HistoryDataBean> newDataList) {
        this.dataList = newDataList;
        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemContainer;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemContainer = itemView.findViewById(R.id.item_container);
        }

        public void bind(HistoryDataBean historyDataBean,Map<String, Object> data, List<String> headers,int position) {
            itemContainer.removeAllViews();
            for (String header : headers) {
                TextView textView = new TextView(itemView.getContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                textView.setGravity(Gravity.CENTER);
                String text = "";
                if(header.equals("序号")){
                    text = "" + (position + 1);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            mContext.getResources().getDimensionPixelSize(R.dimen.dp_30),
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                } else if(header.equals("创建时间")){
                    text = historyDataBean.getCreateTimeStr();
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            mContext.getResources().getDimensionPixelSize(R.dimen.dp_80),
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                } else if(header.equals("操作")) {
                    text = "查看详情";
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            mContext.getResources().getDimensionPixelSize(R.dimen.dp_50),
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.button));
                    textView.setOnClickListener(v -> new XPopup.Builder(mContext).asConfirm(
                            "历史数据详情",
                            data.toString(),
                            null,
                            "关闭",
                            null,
                            null,
                            true).show());
                } else {
                    String title = DeviceUtil.getHistoryDataKeyByTitle(header);
                    String value = (String) data.get(title);
                    if(title.equals("SjStatus1") || title.equals("SjStatus2")){
                        if(Objects.equals(value, "0")){
                            if(value.equals("0")){
                                text = "正常";
                            } else if(value.equals("1")){
                                text = "有水";
                            }
                        }
                    } else {
                        text = value;
                    }
                }
                textView.setText(text);
                itemContainer.addView(textView);
            }
        }
    }
}
