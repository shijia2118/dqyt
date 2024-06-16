package com.hxjt.dqyt.ui.detail;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.adapter.ViewPagerAdapter;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.base.BasePresenter;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.bean.GetDataType;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.TcpClient;
import com.hxjt.dqyt.utils.TcpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceHistoryDataActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private TextView pageNumberTextView;
    private ViewPagerAdapter adapter;
    private List<List<HistoryDataBean>> dataList;
    private DeviceInfoBean deviceInfoBean;
    private Handler handler;

    private LinearLayout llBack;
    private ImageView tcpStatusImg;

    private int currentPage = 1;
    private int totalPages = 1;
    static private final int PAGESIZE = 9;

    private int dataType = 0;
    private String startDt;
    private String endDt;
    LinearLayout emptyView ;
    private TextView tv_reload;

    private int getDataType;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_history_data;
    }

    @Override
    public void onConnectionStatusChanged(boolean isConnected) {
        displayWithTcpStatus(isConnected);
    }

    @Override
    public void initData() {}

    @Override
    public void initView() {
        llBack = findViewById(R.id.ll_back);
        tcpStatusImg = findViewById(R.id.tv_connect_status);
        viewPager = findViewById(R.id.viewPager);
        pageNumberTextView = findViewById(R.id.pageNumberTextView);
        emptyView = findViewById(R.id.empty_view);
        tv_reload = findViewById(R.id.tv_reload);

        llBack.setOnClickListener(v -> finish());
        tv_reload.setOnClickListener(onReload);

        Intent intent = getIntent();
        deviceInfoBean = intent.getParcelableExtra("device_info_bean");

        dataList = new ArrayList<>();
        adapter = new ViewPagerAdapter(this, dataList);
        viewPager.setAdapter(adapter);

        TcpClient.getInstance().setDataReceivedListener(dataReceivedListener);

        showLoading("正在加载...");
        getDataType = GetDataType.GETDATA;
        currentPage = 1;
        getDeviceHistoryDataList();
        after3sHandle();
    }

    /**
     * 重新加载
     */
    private final View.OnClickListener onReload = v -> {
        showLoading("正在加载...");
        currentPage = 1;
        getDataType = GetDataType.GETDATA;
        getDeviceHistoryDataList();
        after3sHandle();
    };

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

    private void loadNextPage() {

        adapter.notifyDataSetChanged(); // 通知适配器数据集发生了变化

        currentPage++; // 更新当前页码
        updatePageNumber(); // 更新页码显示
    }



    private void updatePageNumber() {
        pageNumberTextView.setText("Page " + currentPage);
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
            hideLoading();
            viewPager.setVisibility(View.GONE);
            pageNumberTextView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }, 3000);
    }

    /**
     * 接收开发板发送的消息
     */
    TcpClient.DataReceivedListener dataReceivedListener = new TcpClient.DataReceivedListener() {
        @Override
        public void onDataReceived(String data) {
            if(data == null) return;
            if(!data.startsWith("[")) return;

            hideLoading();
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }

            List<HistoryDataBean> result = JsonUtil.parseHistoryData(data);

            if(getDataType == GetDataType.GETDATA){
                dataList.clear();
                if(result.isEmpty()){
                    viewPager.setVisibility(View.GONE);
                    pageNumberTextView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    dataList.add(result);
                    viewPager.setVisibility(View.VISIBLE);
                    pageNumberTextView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    adapter.updatePages(dataList);
                }
            }
        }
    };
}
