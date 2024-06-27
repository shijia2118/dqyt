package com.hxjt.dqyt.ui.detail;

import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.utils.TimeUtils.formatWithLeadingZero;
import static com.hxjt.dqyt.utils.TimeUtils.getDateTimeEntity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.hxjt.dqyt.app.App;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.base.BasePresenter;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.bean.GetDataType;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.utils.DBUtils;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.ExcelUtils;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.TimeUtils;
import com.hxjt.dqyt.utils.ToastUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class DeviceHistoryDataActivity extends BaseActivity {

    private DeviceHistoryDataAdapter adapter;
    private List<HistoryDataBean> dataList;
    private DeviceInfoBean deviceInfoBean;
    private Handler handler;

    private ImageView tcpStatusImg;
    private Button backToTop;

    private int currentPage = 1;
    static private final int PAGESIZE = 20;

    private int dataType = 1;
    private String startDt;
    private String endDt;
    private int getDataType;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    LinearLayout emptyView ;

    private TextView tv_start_time;
    private TextView tv_end_time;
    private TextView tvExportExcel;

    private static final int PERMISSION_REQUEST_CODE = 10000;

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
        backToTop = findViewById(R.id.iv_back_to_top);
        tvExportExcel = findViewById(R.id.tv_export_excel);

        llBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        deviceInfoBean = intent.getParcelableExtra("device_info_bean");

        initTableHeader();

        tv_start_time.setOnClickListener(onStartClickListener);
        tv_end_time.setOnClickListener(onEndClickListener);
        backToTop.setOnClickListener(onBackToTop);
        tvExportExcel.setOnClickListener(onExportExcelListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dataType = i;
                getDataType = GetDataType.GETDATA;
                currentPage = 1;
                showLoading("正在刷新...");
                after3sHandle();
                getDeviceHistoryDataList();
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
            after3sHandle();
            getDeviceHistoryDataList();
        });

        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getDataType = GetDataType.LOADMORE;
            currentPage++;
            after3sHandle();
            getDeviceHistoryDataList();
        });

        EventBus.getDefault().register(this);

        IConnectionManager connectionManager = EasySocket.getInstance().getDefconnection();
        int connectionStatus = connectionManager.getConnectionStatus();
        if(connectionStatus == 2){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }

        getDataType = GetDataType.GETDATA;
        currentPage = 1;
        showLoading("正在刷新...");
        after3sHandle();
        getDeviceHistoryDataList();
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
            if(header.equals("序号")){
                params = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.dp_30),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            } else if(header.equals("创建时间")) {
                params = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.dp_80),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            } else if(header.equals("操作")) {
                params = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.dp_50),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
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

    final View.OnClickListener onBackToTop = v -> {
        recyclerView.scrollToPosition(0);
    };

    final View.OnClickListener onExportExcelListener = v -> {
        if (checkPermissions()) {
            exportExcel();
        } else {
            requestPermissions();
        }
    };

    private void exportExcel(){
        showLoading("正在导出....");
        String deviceType = deviceInfoBean.getDev_type();
        if(deviceType == null) return;

        new Thread(() -> {
            List<HistoryDataBean> listBean = DBUtils.export(deviceType);

            String fileName = "";
            String[] colName = new String[0];
            List<Map<String,Object>> listMap = new ArrayList<>();

            long timestamp = System.currentTimeMillis();
            if(deviceType.equals(Constants.JCQ)){
                fileName = "接触器_"+timestamp+".xlsx";
                colName = new String[]{"序号", "运行状态", "创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String yxzt = (String) map.get("data") ;
                        if(Objects.equals(yxzt, "1")){
                            rowMap.put("yxzt","断开");
                        } else {
                            rowMap.put("yxzt","运行");
                        }
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.SJ_BSQ)){
                fileName = "水浸_"+timestamp+".xlsx";
                colName = new String[]{"序号", "水浸状态1","水浸状态2", "创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String sjzt1 = (String) map.get("sjStatus1") ;
                        String sjzt2 = (String) map.get("sjStatus2") ;

                        if(sjzt1!=null){
                            if(sjzt1.equals("0")){
                                rowMap.put("sjzt1","正常");
                            } else if(sjzt1.equals("1")){
                                rowMap.put("sjzt1","有水");
                            }else{
                                rowMap.put("sjzt1","未知");
                            }
                        } else {
                            rowMap.put("sjzt1","未知");
                        }

                        if(sjzt2!=null){
                            if(sjzt2.equals("0")){
                                rowMap.put("sjzt2","正常");
                            } else if(sjzt2.equals("1")){
                                rowMap.put("sjzt2","有水");
                            }else{
                                rowMap.put("sjzt2","未知");
                            }
                        } else {
                            rowMap.put("sjzt2","未知");
                        }

                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.WSD_CGQ)){
                fileName = "温湿度_"+timestamp+".xlsx";
                colName = new String[]{"序号", "温度(°C)","湿度(%)", "创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String wd = (String) map.get("Wd") ;
                        rowMap.put("wd",wd);
                        String sd = (String) map.get("Sd") ;
                        rowMap.put("sd",sd);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.SK645)){
                fileName = "塑壳_"+timestamp+".xlsx";
                colName = new String[]{"序号", "A相电压(V)","B相电压(V)","C相电压(V)", "A相电流(A)","B相电流(A)","B相电流(A)","总有功电能","创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String axdy = (String) map.get("DqAxiangDianYa");
                        rowMap.put("axdy",axdy);
                        String bxdy = (String) map.get("DqBxiangDianYa");
                        rowMap.put("bxdy",bxdy);
                        String cxdy = (String) map.get("DqCxiangDianYa");
                        rowMap.put("cxdy",cxdy);
                        String axdl = (String) map.get("DqAxiangDianLiu");
                        rowMap.put("axdl",axdl);
                        String bxdl = (String) map.get("DqBxiangDianLiu");
                        rowMap.put("bxdl",bxdl);
                        String cxdl = (String) map.get("DqCxiangDianLiu");
                        rowMap.put("cxdl",cxdl);
                        String zongYgdn = (String) map.get("ZongYgdn");
                        rowMap.put("zongYgdn",zongYgdn);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.BPQ)){
                fileName = "变频器_"+timestamp+".xlsx";
                colName = new String[]{"序号", "运行频率","设定频率","运行转速","输出电压","输出电流","输出功率",  "创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String yxpl = (String) map.get("Yxpl");
                        rowMap.put("yxpl",yxpl);
                        String sdpl = (String) map.get("Sdpl");
                        rowMap.put("sdpl",sdpl);
                        String yxzs = (String) map.get("Yxzs");
                        rowMap.put("yxzs",yxzs);
                        String scdy = (String) map.get("Scdy");
                        rowMap.put("scdy",scdy);
                        String scdl = (String) map.get("Scdl");
                        rowMap.put("scdl",scdl);
                        String scgl = (String) map.get("Scgl");
                        rowMap.put("scgl",scgl);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.YM_CSY)){
                fileName = "液面监测_"+timestamp+".xlsx";
                colName = new String[]{"序号", "单点套压(Mpa)","单点声速(m/s)","单点液面深度(m)","创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String Ddty = (String) map.get("Ddty");
                        rowMap.put("Ddty",Ddty);
                        String Ddsy = (String) map.get("Ddsy");
                        rowMap.put("Ddsy",Ddsy);
                        String Ddymsd = (String) map.get("Ddymsd");
                        rowMap.put("Ddymsd",Ddymsd);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.CLZS_CGQ)){
                fileName = "齿轮转速_"+timestamp+".xlsx";
                colName = new String[]{"序号", "IN1转速值","IN2转速值","创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String IN1Zsz = (String) map.get("IN1Zsz");
                        rowMap.put("IN1Zsz",IN1Zsz);
                        String IN2Zsz = (String) map.get("IN2Zsz");
                        rowMap.put("IN2Zsz",IN2Zsz);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.YW_CGQ)){
                fileName = "烟雾_"+timestamp+".xlsx";
                colName = new String[]{"序号", "报警器状态","报警延时","创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String bjQstatus = (String) map.get("BJQstatus");
                        if(bjQstatus!=null){
                            if(bjQstatus.equals("0")){
                                rowMap.put("bjQstatus","正常");
                            } else if(bjQstatus.equals("1")){
                                rowMap.put("bjQstatus","报警");
                            } else {
                                rowMap.put("bjQstatus","未知");
                            }
                        } else {
                            rowMap.put("bjQstatus","未知");
                        }
                        String BJDelayed = (String) map.get("BJDelayed");
                        rowMap.put("BJDelayed",BJDelayed);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            } else if(deviceType.equals(Constants.ZDJC_CGQ)){
                fileName = "震动监测_"+timestamp+".xlsx";
                colName = new String[]{"序号", "X轴振动速度","Y轴振动速度","Z轴振动速度","X轴振动位移","Y轴振动位移","Z轴振动位移","创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String VX = (String) map.get("VX");
                        rowMap.put("VX",VX);
                        String VY = (String) map.get("VY");
                        rowMap.put("VY",VY);
                        String VZ = (String) map.get("VZ");
                        rowMap.put("VZ",VZ);
                        String DX = (String) map.get("DX");
                        rowMap.put("DX",DX);
                        String DY = (String) map.get("DY");
                        rowMap.put("DY",DY);
                        String DZ = (String) map.get("DZ");
                        rowMap.put("DZ",DZ);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            }  else if(deviceType.equals(Constants.ZS_CGQ)){
                fileName = "噪声_"+timestamp+".xlsx";
                colName = new String[]{"序号","噪声值","创建时间"};
                for(int i=1; i<= listBean.size();i++){
                    Map<String,Object> map = JsonUtil.toMap(listBean.get(i-1).getDeviceData());
                    if(map != null && !map.isEmpty()){
                        Map<String,Object> rowMap = new LinkedHashMap<>();
                        rowMap.put("xlh",""+i);
                        String ZSvalue = (String) map.get("ZSvalue");
                        rowMap.put("ZSvalue",ZSvalue);
                        rowMap.put("time",listBean.get(i-1).getCreateTimeStr());
                        listMap.add(rowMap);
                    }
                }
            }
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            ExcelUtils.initExcel(file.getAbsolutePath(),fileName,colName);
            ExcelUtils.writeObjListToExcel(listMap,file, DeviceHistoryDataActivity.this);

        }).start();
    }


    private boolean checkPermissions() {
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ToastUtil.s("文件的读取和写入权限是必须的，否则无法导出Excel");
        }
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                ToastUtil.s("存储权限未获得，无法导出Excel");
            }
        }
    }

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
                after3sHandle();
                getDeviceHistoryDataList();
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
                after3sHandle();
                getDeviceHistoryDataList();
            } else {
                throw new IllegalArgumentException("Invalid timeType: " + timeType);
            }
        });
        picker.show();
    }


    /**
     * 下发指令，获取历史数据
     */
    private void getDeviceHistoryDataList() {
        String deviceType = deviceInfoBean.getDev_type();
        if(TextUtils.isEmpty(deviceType)) return;

        List<HistoryDataBean> result = DBUtils.query(currentPage,PAGESIZE,startDt,endDt,deviceType);

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        hideLoading();

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
    private void after3sHandle(){
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
        }, 3000);
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
