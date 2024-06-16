package com.hxjt.dqyt.adapter;

import static com.hxjt.dqyt.app.Constants.BPQ;
import static com.hxjt.dqyt.app.Constants.JCQ;
import static com.hxjt.dqyt.app.Constants.WSD_CGQ;
import static com.hxjt.dqyt.app.Constants.YM_CSY;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.utils.JsonUtil;

import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private Context context;
    private List<List<HistoryDataBean>> pages;

    public ViewPagerAdapter(Context context, List<List<HistoryDataBean>> pages) {
        this.context = context;
        this.pages = pages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.page_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<HistoryDataBean> page = pages.get(position);
        GridAdapter adapter = new GridAdapter(context, page);
        holder.gridView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        GridView gridView;

        ViewHolder(View itemView) {
            super(itemView);
            gridView = itemView.findViewById(R.id.gridView);
        }
    }

    private static class GridAdapter extends BaseAdapter {
        private Context context;
        private List<HistoryDataBean> items;

        GridAdapter(Context context, List<HistoryDataBean> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.device_history_data_item, parent, false);
            }

            HistoryDataBean historyDataBean = items.get(position);
            TextView tvKey1 = convertView.findViewById(R.id.tv_key1);
            TextView tvValue1 = convertView.findViewById(R.id.tv_value1);
            TextView tvKey2 = convertView.findViewById(R.id.tv_key2);
            TextView tvValue2 = convertView.findViewById(R.id.tv_value2);
            TextView tvViewMore = convertView.findViewById(R.id.tv_view_more);

            String jsonData = historyDataBean.getDeviceData();
            if(jsonData != null){
                Map<String,Object> map = JsonUtil.toMap(jsonData);
                String deviceType = (String) map.get("TcpCmdType");
                if(deviceType != null){
                    tvKey2.setVisibility(View.GONE);
                    tvValue2.setVisibility(View.GONE);
                    if(deviceType.equals(BPQ)){
                        //变频器
                        tvKey1.setText("运行频率:");
                        tvValue1.setText((String) map.get("Yxpl"));
                    } else if(deviceType.equals(YM_CSY)){
                        //液面传感器
                        tvKey1.setText("单点液面深度:");
                        tvValue1.setText((String) map.get("Ddymsd"));
                    } else if(deviceType.equals(JCQ)){
                        //接触器
                        tvKey1.setText("运行状态:");
                        String jcq = "断开";
                        String result = (String) map.get("data");
                        if(result!=null&&result.equals("0")){
                            jcq = "运行";
                        }
                        tvValue1.setText(jcq);
                    } else if(deviceType.equals(WSD_CGQ)){
                        //温湿度传感器
                        tvKey1.setText("温度:");
                        String wd = (String) map.get("Wd");
                        if(wd != null){
                            tvValue1.setText(wd);
                        }

                        tvKey2.setVisibility(View.VISIBLE);
                        tvValue2.setVisibility(View.VISIBLE);
                        tvKey2.setText("湿度:");
                        String sd = (String) map.get("Sd");
                        if(sd != null){
                            tvValue2.setText(sd);
                        }
                    }
                }
            }
            return convertView;
        }
    }

    // 更新数据的方法
    public void updatePages(List<List<HistoryDataBean>> newPages) {
        this.pages = newPages;
        notifyDataSetChanged();
    }
}


