package com.hxjt.dqyt.ui.detail;

import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.app.Constants.RECEIVED_MESSAGE;
import static com.hxjt.dqyt.utils.TimeUtils.formatWithLeadingZero;
import static com.hxjt.dqyt.utils.TimeUtils.getDateTimeEntity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easysocket.EasySocket;
import com.easysocket.interfaces.conn.IConnectionManager;
import com.github.gzuliyujiang.wheelpicker.DatimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity;
import com.github.gzuliyujiang.wheelpicker.widget.DatimeWheelLayout;
import com.hxjt.dqyt.R;
import com.hxjt.dqyt.adapter.DeviceHistoryDataAdapter;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.base.BasePresenter;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.bean.GetDataType;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.TcpUtil;
import com.hxjt.dqyt.utils.TimeUtils;
import com.hxjt.dqyt.utils.ToastUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceHistoryDataActivity extends BaseActivity {

    private DeviceHistoryDataAdapter adapter;
    private List<HistoryDataBean> dataList;
    private DeviceInfoBean deviceInfoBean;
    private Handler handler;

    private ImageView tcpStatusImg;

    private int currentPage = 1;
    static private final int PAGESIZE = 20;

    private int dataType = 0;
    private String startDt;
    private String endDt;
    private int getDataType;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    LinearLayout emptyView ;

    private TextView tv_start_time;
    private TextView tv_end_time;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_history_data;
    }

    @Subscriber(tag = CONNECTION_CHANGED)
    public void onTcpConnectionChanged(boolean isConnect){
        displayWithTcpStatus(isConnect);
    }

    @Override
    public void initData() {}

    @Override
    public void initView() {
        LinearLayout llBack = findViewById(R.id.ll_back);
        tcpStatusImg = findViewById(R.id.tv_connect_status);
        emptyView = findViewById(R.id.empty_view);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        Spinner spinner = findViewById(R.id.sp_type);

        llBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        deviceInfoBean = intent.getParcelableExtra("device_info_bean");

        initTableHeader();

        tv_start_time.setOnClickListener(onStartClickListener);
        tv_end_time.setOnClickListener(onEndClickListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataType = i;
                getDataType = GetDataType.GETDATA;
                currentPage = 1;
                showLoading("正在刷新...");
                getDeviceHistoryDataList();
                after8sHandle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dataList = new ArrayList<>();

        /************************* mock ***********************/
//        HistoryDataBean dataBean = new HistoryDataBean();
//        String deviceData = "{\"Id\":null,\"DeviceId\":\"FC0FE737C3B5\",\"DeviceCode\":\"2\",\"DeviceName\":\"温湿度设备\",\"CreateTime\":\"2024-06-14 13:53:57\",\"UpdateTime\":null,\"DeviceStatus\":null,\"Td\":\"3\",\"Wd\":\"26.8\",\"Sd\":\"59.2\",\"SHowzd\":\"26.8\",\"TcpCmdType\":\"wsdcgq\",\"TcpDataType\":null,\"CreateTimeStr\": \"2024-06-14 13:51:59\"}";
//        dataBean.setDeviceData(deviceData);
//        dataList.add(dataBean);
        /************************* mock ***********************/

        List<String> headers = DeviceUtil.getHistoryDataTitleByType(deviceInfoBean.getDev_type());
        adapter = new DeviceHistoryDataAdapter(headers, dataList,context);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getDataType = GetDataType.REFRESH;
            currentPage = 1;
            getDeviceHistoryDataList();
            after8sHandle();
        });

        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getDataType = GetDataType.LOADMORE;
            currentPage++;
            getDeviceHistoryDataList();
            after8sHandle();
        });

        EventBus.getDefault().register(this);

        IConnectionManager connectionManager = EasySocket.getInstance().getDefconnection();
        int connectionStatus = connectionManager.getConnectionStatus();
        if(connectionStatus == 2){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }

    }

    private void initTableHeader(){
        LinearLayout dynamicHeader = findViewById(R.id.dynamic_header);
        List<String> headers = DeviceUtil.getHistoryDataTitleByType(deviceInfoBean.getDev_type());
        dynamicHeader.removeAllViews();
        // 动态添加标题
        for (String header : headers) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            switch (header) {
                case "序号":
                    params = new LinearLayout.LayoutParams(
                            getResources().getDimensionPixelSize(R.dimen.dp_20),
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    break;
                case "创建时间":
                    params = new LinearLayout.LayoutParams(
                            getResources().getDimensionPixelSize(R.dimen.dp_60),
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    break;
                case "操作":
                    params = new LinearLayout.LayoutParams(
                            getResources().getDimensionPixelSize(R.dimen.dp_35),
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    break;
            }
            textView.setLayoutParams(params);
            textView.setText(header);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
            dynamicHeader.addView(textView);
        }
    }

    final View.OnClickListener onStartClickListener = v -> {
        try {
            showDatePickerDialog(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    };

    final View.OnClickListener onEndClickListener = v -> {
        try {
            showDatePickerDialog(1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    };

    /**
     * 日期选择器
     * @param timeType :0-开始时间 1-结束时间
     */
    private void showDatePickerDialog(int timeType) throws ParseException {
        DatimePicker picker = new DatimePicker(this);
        String time = null;
        if(timeType == 0){
            time = startDt;
        } else if(timeType == 1){
            time = endDt;
        }
        DatimeEntity defaultEntity = getDateTimeEntity(time);
        DatimeEntity beginEntity = getDateTimeEntity("2024-01-01 00:00:00.000");
        DatimeEntity endEntity = DatimeEntity.yearOnFuture(1);

        picker.setBodyWidth(500);
        DatimeWheelLayout wheelLayout = picker.getWheelLayout();
        wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY);
        wheelLayout.setTimeMode(TimeMode.HOUR_24_HAS_SECOND);
        wheelLayout.setDefaultValue(defaultEntity);
        wheelLayout.setRange(beginEntity, endEntity);
        wheelLayout.setIndicatorEnabled(true);
        wheelLayout.setIndicatorColor(getResources().getColor(R.color.button));
        wheelLayout.setIndicatorSize(getResources().getDisplayMetrics().density * 2);
        wheelLayout.setSelectedTextColor(getResources().getColor(R.color.button));
        wheelLayout.setSelectedTextBold(true);
        picker.setOnDatimePickedListener((year, month, day, hour, minute, second) -> {
            String text = "" + year+"-" + formatWithLeadingZero(month) +"-" + formatWithLeadingZero(day) +" " +
                    formatWithLeadingZero(hour)+":" + formatWithLeadingZero(minute)+":" + formatWithLeadingZero(second);
            if(timeType == 0){
                boolean isBefore = TimeUtils.isBefore(text+".000",endDt);
                if(!isBefore){
                    ToastUtil.s("开始时间不能晚于结束时间");
                    return;
                }
                startDt = text + ".000";
                tv_start_time.setText(text);
                getDataType = GetDataType.GETDATA;
                currentPage = 1;
                showLoading("正在刷新...");
                getDeviceHistoryDataList();
                after8sHandle();
            } else if(timeType == 1){
                boolean isAfter = TimeUtils.isAfter(startDt,text+".000");
                if(!isAfter){
                    ToastUtil.s("结束时间不能早于开始时间");
                    return;
                }
                endDt = text + ".000";
                tv_end_time.setText(text);
                getDataType = GetDataType.GETDATA;
                currentPage = 1;
                showLoading("正在刷新...");
                getDeviceHistoryDataList();
                after8sHandle();
            } else {
                throw new IllegalArgumentException("Invalid timeType: " + timeType);
            }
        });
        picker.show();
    }


    /**
     * 下发指令，获取历史数据
     */
    private void getDeviceHistoryDataList(){
        if(deviceInfoBean != null){
            Map<String,Object> map = new HashMap<>();
            map.put("DeviceType",deviceInfoBean.getDev_type());
            map.put("DeviceCode",deviceInfoBean.getAddr());
            map.put("DataType",dataType);
            map.put("PageIndex",currentPage);
            map.put("PageSize",PAGESIZE);
            map.put("StartDt",startDt);
            map.put("EndDt",endDt);

            TcpUtil tcpUtil = new TcpUtil();
            tcpUtil.getDeviceHistoryDataList(map);
        }
    }

    private void  displayWithTcpStatus(boolean isConnected) {
        if(isConnected){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }
    }

    /**
     * 3s后停止
     */
    private void after8sHandle(){
        handler = new Handler();
        handler.postDelayed(() -> {
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            if(getDataType == GetDataType.GETDATA){
                hideLoading();
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else if(getDataType == GetDataType.REFRESH){
                smartRefreshLayout.finishRefresh(false);
            } else if(getDataType == GetDataType.LOADMORE) {
                smartRefreshLayout.finishLoadMore(false);
            }
        }, 5000);
    }

    @Subscriber(tag = RECEIVED_MESSAGE)
    public void onReceivedMessage(String data){
        if(TextUtils.isEmpty(data)) return;
        if(!data.startsWith("[")) return;

        hideLoading();

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

        List<HistoryDataBean> result = JsonUtil.parseHistoryData(data);

        if(getDataType == GetDataType.GETDATA){
            dataList.clear();
            if(result.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                dataList.addAll(result);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.updateData(dataList);
            }
        } else if(getDataType == GetDataType.REFRESH){
            smartRefreshLayout.finishRefresh();
            dataList.clear();
            if(result.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                dataList.addAll(result);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.updateData(dataList);
            }
        } else if(getDataType == GetDataType.LOADMORE){
            if(PAGESIZE > result.size()){
                smartRefreshLayout.finishLoadMoreWithNoMoreData();
            } else {
                smartRefreshLayout.finishLoadMore();
            }
            dataList.addAll(result);
            adapter.updateData(dataList);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
