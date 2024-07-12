package com.hxjt.dqyt.adapter;

import static com.hxjt.dqyt.app.Constants.BPQ;
import static com.hxjt.dqyt.app.Constants.DLQ_TYPE;
import static com.hxjt.dqyt.app.Constants.SK645;
import static com.hxjt.dqyt.app.Constants.YM_CSY;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.SPUtil;
import com.hxjt.dqyt.utils.TextUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeviceHistoryDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HistoryDataBean> dataList;
    private List<String> headers;
    private Context mContext;

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

    class ItemViewHolder extends RecyclerView.ViewHolder {
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
                    if(historyDataBean.getDeviceType().equals("bsmio") || historyDataBean.getDeviceType().equals(SK645)
                            || historyDataBean.getDeviceType().equals(YM_CSY) || historyDataBean.getDeviceType().equals(BPQ)) {
                        textView.setOnClickListener(v -> new XPopup.Builder(mContext)
                                .dismissOnBackPressed(true) // 按返回键是否关闭弹窗，默认为true
                                .dismissOnTouchOutside(true) // 点击外部是否关闭弹窗，默认为true
                                .asCustom(new HistoryDetailDialog(mContext,data,historyDataBean.getDeviceType()))
                                .show());
                    } else {
                        textView.setOnClickListener(v -> new XPopup.Builder(mContext).asConfirm(
                                "历史数据详情",
                                data.toString(),
                                null,
                                "关闭",
                                null,
                                null,
                                true).show());
                    }
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

    class HistoryDetailDialog extends CenterPopupView {

        private ListView listView;
        private Map<String,Object> detailMap;
        private String deviceType;

        public HistoryDetailDialog(@NonNull Context context,@NonNull Map<String,Object> detailMap,String deviceType) {
            super(context);
            this.detailMap = detailMap;
            this.deviceType = deviceType;
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.history_detail_dialog;
        }

        // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
        @Override
        protected void onCreate() {
            super.onCreate();

            listView = findViewById(R.id.tv_list_view);
            TextView tv_title = findViewById(R.id.tv_title);

            String title = DeviceUtil.getNameOfType(deviceType);
            tv_title.setText(title+"_历史数据详情");

            Map<String,Object>[] mDatas = DeviceUtil.getDeviceStatusByType(deviceType);

            if(deviceType.equals(SK645) && SPUtil.getString(DLQ_TYPE,"sk").equals("lc")){
                //量测去掉"闸位状态"
                mDatas = DeviceUtil.removeFirstElement(mDatas);
            }

            HistoryAdapter adapter = new HistoryAdapter(mContext, mDatas, detailMap);
            listView.setDivider(null);
            listView.setAdapter(adapter);
        }
    }

    public static class HistoryAdapter extends BaseAdapter {

        private Context context;
        private Map<String,Object> detailMap;
        private final Map<String,Object>[] mDatas;

        public HistoryAdapter(Context context, Map<String,Object>[] mDatas,Map<String,Object> detailMap) {
            this.context = context;
            this.mDatas = mDatas;
            this.detailMap = detailMap;
        }

        @Override
        public int getCount() {
            return mDatas.length;
        }

        @Override
        public Object getItem(int position) {
            return mDatas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.history_detail_item, parent, false);
            }

            Map<String,Object> map = mDatas[position];
            String title = (String) map.get("title");
            String tag = (String) map.get("tag");

            if(title==null) title = "";
            if(tag == null) tag = "";

            LinearLayout ll_item = convertView.findViewById(R.id.ll_item);
            TextView titleTextView = convertView.findViewById(R.id.title);
            TextView valueTextView = convertView.findViewById(R.id.value);

            boolean isEven = position% 2 == 0;

            ll_item.setBackgroundColor(
                    isEven ? context.getResources().getColor(R.color.button)
                            : context.getResources().getColor(R.color.button3));

            titleTextView.setText(title);

            for (Map.Entry<String, Object> entry : detailMap.entrySet()) {
                if (TextUtil.isEqualIgnoreCase(entry.getKey(),tag)) {
                    String result = entry.getValue() + "";
                    if(result.equals("串口控制继电器")){
                        result = "接触器";
                    }
                    if(TextUtil.isEqualIgnoreCase(entry.getKey(),"deviceStatus")){
                        //在线状态
                        if(result.equals("1")){
                            result = "在线";
                        } else {
                            result = "离线";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"sjStatus1")){
                        //水浸状态1
                        if(result.equals("0")){
                            result = "正常";
                        } else if(result.equals("1")){
                            result = "有水";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"sjStatus2")){
                        //水浸状态2
                        if(result.equals("0")){
                            result = "正常";
                        } else if(result.equals("1")){
                            result = "有水";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"bsStatus")){
                        //闭锁状态
                        if(result.equals("1")){
                            result = "闭锁";
                        } else if(result.equals("0")){
                            result = "解锁";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"iN1Clsd")||TextUtil.isEqualIgnoreCase(entry.getKey(),"iN2Clsd")){
                        //IN1,IN2 测量速度选择
                        if(result.equals("0")){
                            result = "高速";
                        } else if(result.equals("1")){
                            result = "中速";
                        }else if(result.equals("2")){
                            result = "低速";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"yxStatus")){
                        //运行状态
                        BigDecimal bd = new BigDecimal(result);
                        int intValue = bd.intValue();
                        if(intValue == 1){
                            result = "正转运行";
                        } else if(intValue== 2){
                            result = "反转运行";
                        }else if(intValue == 3){
                            result = "正转点动";
                        }else if(intValue == 4){
                            result = "反转点动";
                        }else if(intValue == 5){
                            result = "停机";
                        }else if(intValue == 6){
                            result = "紧急停机";
                        }else if(intValue == 7){
                            result = "故障复位";
                        }else if(intValue == 8){
                            result = "点动停止";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"jsff")){
                        //IN1,IN2 测量速度选择
                        if(result.equals("1")){
                            result = "接箍法";
                        } else if(result.equals("2")){
                            result = "音速法";
                        }else if(result.equals("3")){
                            result = "音标法";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"bpqZtz1") || TextUtil.isEqualIgnoreCase(entry.getKey(),"bpqZtz2")){
                        //运行状态
                        if(result.equals("1")){
                            result = "正转运行中";
                        } else if(result.equals("2")){
                            result = "反转运行中";
                        }else if(result.equals("3")){
                            result = "变频器停机中";
                        }else if(result.equals("4")){
                            result = "变频器故障中 ";
                        }else if(result.equals("5")){
                            result = "变频器POFF状态";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"bjQstatus")){
                        //报警器状态
                        if(result.equals("0")){
                            result = "正常";
                        } else if(result.equals("1")){
                            result = "报警";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"kaiguan")){
                        //开关状态
                        if(result.equals("0")){
                            result = "分闸";
                        } else if(result.equals("1")){
                            result = "合闸";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"Data")){
                        //接触器运行状态
                        if(result.equals("1")){
                            result = "断开";
                        } else if(result.equals("0")){
                            result = "运行";
                        }
                    } else if(TextUtil.isEqualIgnoreCase(entry.getKey(),"srd_3")){
                        //接触器运行状态
                        if(result.equals("1")){
                            result = "运行";
                        } else if(result.equals("0")){
                            result = "断开";
                        }
                    }
                    valueTextView.setText(result);
                    break;
                }
            }

            return convertView;
        }
    }

}


