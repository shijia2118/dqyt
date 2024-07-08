package com.hxjt.dqyt.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.ui.main.MainActivity;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.TextUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;

import java.util.ArrayList;
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
//                    if(historyDataBean.getDeviceType().equals(Constants.JCQ)) {
//                        textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                new XPopup.Builder(mContext)
//                                        .dismissOnBackPressed(true) // 按返回键是否关闭弹窗，默认为true
//                                        .dismissOnTouchOutside(true) // 点击外部是否关闭弹窗，默认为true
//                                        .asCustom(new HistoryDetailDialog(mContext))
//                                        .show();
//                            }
//                        });
//                    } else {
//                        textView.setOnClickListener(v -> new XPopup.Builder(mContext).asConfirm(
//                                "历史数据详情",
//                                data.toString(),
//                                null,
//                                "关闭",
//                                null,
//                                null,
//                                true).show());
//                    }
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
                            text = "正常";
                        } else {
                            text = "有水";
                        }
                    } else if(title.equals("BJQstatus")){
                        if(Objects.equals(value, "0")){
                            text = "正常";
                        } else {
                            text = "报警";
                        }
                    } else if(title.equals("data")){
                        if(Objects.equals(value, "1")){
                            text = "断开";
                        } else {
                            text = "运行";
                        }
                    } else if(title.equals("srd_3")) {
                        if(Objects.equals(value, "1")){
                            text = "运行";
                        } else {
                            text = "断开";
                        }

                    } else if(title.equals("BpqgzdmText")){
                        if(TextUtils.isEmpty(value)) {
                            text = "";
                        } else {
                            text = value;
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

    static class HistoryDetailDialog extends CenterPopupView{

        private ListView historyListView;
        private List<HistoryItem> historyItems;

        public HistoryDetailDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.history_detail_dialog;
        }

        // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
        @Override
        protected void onCreate() {
            super.onCreate();

            historyListView = findViewById(R.id.history_list_view);

            // 初始化数据
            historyItems = new ArrayList<>();
            historyItems.add(new HistoryItem("Title 1", "Value 1"));
            historyItems.add(new HistoryItem("Title 2", "Value 2"));

            HistoryAdapter adapter = new HistoryAdapter(mContext, historyItems);
            historyListView.setAdapter(adapter);
        }
    }

    private static class HistoryItem {
        String title;
        String value;

        HistoryItem(String title, String value) {
            this.title = title;
            this.value = value;
        }
    }

    private static class HistoryAdapter extends ArrayAdapter<HistoryItem> {

        public HistoryAdapter(Context context, List<HistoryItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_detail_item, parent, false);
            }

            HistoryItem currentItem = getItem(position);

            TextView titleTextView = convertView.findViewById(R.id.title);
            TextView valueTextView = convertView.findViewById(R.id.value);

            titleTextView.setText(currentItem.title);
            valueTextView.setText(currentItem.value);

            return convertView;
        }
    }
}


