package com.hxjt.dqyt.ui.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easysocket.EasySocket;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.IConnectionManager;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.utils.LogUtil;
import com.hxjt.dqyt.R;
import com.hxjt.dqyt.adapter.MyAdapter;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.bean.DeviceInfoListBean;
import com.hxjt.dqyt.ui.system.SystemSetActivity;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.TcpUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView ,ISocketActionListener{

    private ImageView tcpStatusImg;
    private TextView tvTitle;
    private TextView tvDeviceNo;
    private LinearLayout llBack;

    private String deviceNo; //网关编号

    private DeviceInfoListBean deviceInfoListBean;

    LinearLayout emptyView ;
    private LinearLayout llSystemSet;
    private TextView tvCloseApp;
    private LinearLayout ll_right;
    private String mTimeValue;
    private MyAdapter mMyAdapter;
    private LinearLayout ll_connect;
    private TextView tv_reload;

    private GridView gridView;
    private List<DeviceInfoBean> mDevices = new ArrayList<>();

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {}

    @Override
    public void initView() {
        tcpStatusImg = findViewById(R.id.tv_connect_status);
        tvTitle = findViewById(R.id.tv_title);
        tvDeviceNo = findViewById(R.id.tv_device_no);
        llBack = findViewById(R.id.ll_back);
        gridView = findViewById(R.id.grid_view);
        emptyView = findViewById(R.id.empty_view);
        llSystemSet = findViewById(R.id.ll_system_set);
        tvCloseApp = findViewById(R.id.tv_close_app);
        ll_right = findViewById(R.id.ll_right);
        ll_connect = findViewById(R.id.ll_connect);
        tv_reload = findViewById(R.id.tv_reload);

        llBack.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("智慧油田综合管控平台");
        tvTitle.setTextColor(getResources().getColor(R.color.black));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.sp_20));

        ll_right.setPadding(0,0,getResources().getDimensionPixelSize(R.dimen.dp_30),0);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/hylxtj.ttf");
        tvTitle.setTypeface(typeface);

        GridView gridView = findViewById(R.id.grid_view);

        mMyAdapter = new MyAdapter(MainActivity.this, mDevices,mTimeValue,deviceNo);
        emptyView.setVisibility(View.VISIBLE);
        gridView.setAdapter(mMyAdapter);

        llSystemSet.setVisibility(View.VISIBLE);
        tvCloseApp.setVisibility(View.GONE);
        llSystemSet.setOnClickListener(onSystemSetListener);
        tvCloseApp.setOnClickListener(onCloseApp);

        EventBus.getDefault().register(this);

        /************************* mock ***********************/
//        Map<String,Object> map = new HashMap<>();
//        map.put("chl",1);
//        map.put("dev_type","bpq");
//        map.put("addr","123");
//        map.put("name","温湿度传感器");
//        emptyView.setVisibility(View.GONE);
//        DeviceInfoBean deviceInfoBean = DeviceInfoBean.fromMap(map);
//        mDevices.add(deviceInfoBean);
////
//        map.put("chl",1);
//        map.put("dev_type","sk645");
//        map.put("addr","123");
//        map.put("name","温湿度传感器");
//        emptyView.setVisibility(View.GONE);
//        DeviceInfoBean deviceInfoBean2 = DeviceInfoBean.fromMap(map);
//        mDevices.add(deviceInfoBean2);
        /************************* mock ***********************/


    }

    @Subscriber(tag = "connect_status")
    public void onTcpConnectionChanged(boolean isConnect){
        if(isConnect){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        IConnectionManager connectionManager = EasySocket.getInstance().getDefconnection();
        int connectionStatus = connectionManager.getConnectionStatus();
        LogUtil.d("当前连接状态:",connectionStatus+"");

        if(connectionStatus == 2){
            TcpUtil tcpUtil = new TcpUtil();
            tcpUtil.getAllDevices();
        }
        mMyAdapter.updateStatus();
        mMyAdapter.updateDlqImg();
    }

    @Override
    public void onBackPressed() {
        // 留空，不调用 super.onBackPressed() 以禁用返回按钮
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * '系统设置'按钮点击事件监听
     */
    private final View.OnClickListener onSystemSetListener = v -> {
        Intent intent = new Intent(this, SystemSetActivity.class);
        startActivity(intent);
    };

    /**
     * 退出app
     */
    private final View.OnClickListener onCloseApp = v -> {};

    @Override
    public void onSocketConnSuccess(SocketAddress socketAddress) {
        LogUtil.d("端口" + socketAddress.getPort() + "---> 连接成功");
    }

    @Override
    public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
    }

    @Override
    public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
        LogUtil.d(socketAddress.getPort() + "端口" + "---> socket断开连接，是否需要重连：" + isNeedReconnect);
        tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
    }

    @Override
    public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
        LogUtil.d(socketAddress.getPort() + "端口" + "SocketActionListener收到数据-->" + originReadData.getBodyString());
    }

    @Override
    public void onSocketResponse(SocketAddress socketAddress, String data) {
        LogUtil.d(socketAddress.getPort() + "端口" + "SocketActionListener收到数据-->" + data);
        if(data == null) return;

        Map<String,Object> map = JsonUtil.toMap(data);
        String cmdType = (String) map.get("TcpCmdType");

        if(cmdType == null) return;

        if(cmdType.equals(Constants.JCQ)){
            Log.d("收到的tcp数据:",data);
        }

        if(cmdType.equals("103")){
            //说明是 设备列表接口
            Boolean success = (Boolean) map.get("Success");
            if(success != null && success){
                Map<String,Object> infoMap = (Map) map.get("Data");
                deviceInfoListBean = DeviceInfoListBean.fromMap(infoMap);
                if(deviceInfoListBean != null){
                    mDevices.clear();

                    deviceNo = deviceInfoListBean.getDeviceNo();

                    if(deviceInfoListBean.getAddrList_sk645() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_sk645());
                    }

                    if(deviceInfoListBean.getAddrList_Bpq() !=null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_Bpq());
                    }

                    if(deviceInfoListBean.getAddrList_dlq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_dlq());
                    }

                    if(deviceInfoListBean.getAddrList_Ymcsy() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_Ymcsy());
                    }

                    if(deviceInfoListBean.getAddrList_sjcgq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_sjcgq());
                    }

                    if(deviceInfoListBean.getAddrList_wsdcgq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_wsdcgq());
                    }

                    if(deviceInfoListBean.getAddrList_clzscgq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_clzscgq());
                    }

                    if(deviceInfoListBean.getAddrList_ywcgq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_ywcgq());
                    }

                    if(deviceInfoListBean.getAddrList_zdjccgq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_zdjccgq());
                    }

                    if(deviceInfoListBean.getAddrList_zscgq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_zscgq());
                    }

                    if(deviceInfoListBean.getAddrList_jcq() != null){
                        mDevices.addAll(deviceInfoListBean.getAddrList_jcq());
                    }

                    if(mDevices.isEmpty()){
                        emptyView.setVisibility(View.VISIBLE);
                        gridView.setEmptyView(emptyView);
                    } else {
                        if(mMyAdapter != null){
                            mMyAdapter.update(MainActivity.this,mDevices,mTimeValue,deviceNo);
                        }
                        emptyView.setVisibility(View.GONE);
//                                gridView.setAdapter(mMyAdapter);
                    }
                }
            }
        } else  if(mDevices != null && !mDevices.isEmpty()){
            for(DeviceInfoBean infoBean : mDevices){
                String deviceType = infoBean.getDev_type();
                if (deviceType!=null) {
                    String deviceCode = (String) map.get("DeviceCode");

                    if(deviceType.equals(Constants.DLQ)){
                        deviceCode = (String) map.get("SN");
                    }

                    //收到的tcp数据包属于当前设备
                    if(deviceCode != null && deviceCode.equals(infoBean.getAddr()) && cmdType.equals(deviceType)) {
                        if(deviceType.equals(Constants.SK645)){
                            mTimeValue = (String) map.get("SsZongYouGongLv");
                        } else if(deviceType.equals(Constants.ZS_CGQ)){
                            mTimeValue = (String) map.get("ZSvalue");
                        } else if(deviceType.equals(Constants.YW_CGQ)) {
                            String s = (String)map.get("BJQstatus");
                            if(s!=null){
                                if(s.equals("0")){
                                    mTimeValue = "正常";
                                } else {
                                    mTimeValue = "报警";
                                }
                            }
                        } else if(deviceType.equals(Constants.YM_CSY)){
                            mTimeValue = (String) map.get("Ddymsd");
                        } else if(deviceType.equals(Constants.SJ_BSQ)){
                            StringBuilder stringBuffer = new StringBuilder();
                            String sjStatus1 = (String)map.get("SjStatus1");
                            if(sjStatus1 != null){
                                String status = "正常";
                                if(sjStatus1.equals("1")){
                                    status = "有水";
                                }
                                stringBuffer.append(status);
                            }
                            String sjStatus2 = (String)map.get("SjStatus2");
                            if(sjStatus2 != null){
                                String status = "正常";
                                if(sjStatus2.equals("1")){
                                    status = "有水";
                                }
                                stringBuffer.append("/").append(status);
                            }
                            mTimeValue = stringBuffer.toString();
                        } else if(deviceType.equals(Constants.CLZS_CGQ)){
                            StringBuilder stringBuilder = new StringBuilder();
                            String iN1Zsz = (String) map.get("IN1Zsz");
                            if(iN1Zsz != null){
                                stringBuilder.append(iN1Zsz);
                            }
                            String iN2Zsz = (String) map.get("IN2Zsz");
                            if(iN2Zsz != null){
                                stringBuilder.append("/").append(iN2Zsz);
                            }
                            mTimeValue = stringBuilder.toString();
                        } else if(deviceType.equals(Constants.ZDJC_CGQ)){
                            StringBuilder stringBuilder = new StringBuilder();
                            String vx = (String) map.get("VX");
                            if(vx != null){
                                stringBuilder.append(vx);
                            }
                            String vy = (String) map.get("VY");
                            if(vy != null){
                                stringBuilder.append("/").append(vy);
                            }
                            String vz = (String) map.get("VZ");
                            if(vz != null){
                                stringBuilder.append("/").append(vz);
                            }
                            mTimeValue = stringBuilder.toString();
                        } else if(deviceType.equals(Constants.WSD_CGQ)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            String wd = (String) map.get("Wd");
                            if(wd != null) {
                                stringBuilder.append(wd);
                            }
                            String sd = (String) map.get("Sd");
                            if(sd != null){
                                stringBuilder.append("/").append(sd);
                            }
                            mTimeValue = stringBuilder.toString();
                        }  else if(deviceType.equals(Constants.BPQ)){
                            mTimeValue = (String) map.get("Yxpl");
                        } else if(deviceType.equals(Constants.DLQ)){
                            mTimeValue = (String) map.get("");
                        } else if(deviceType.equals(Constants.JCQ)){
                            String jcq = "断开";
                            String result = (String) map.get("data");
                            if(result!=null&&result.equals("0")){
                                jcq = "运行";
                            }
                            mTimeValue = jcq;
                        }
                        mMyAdapter.updateTimeValue(infoBean,mTimeValue);
                    }
                }
            }
        }
    }

    @Override
    public void onSocketResponse(SocketAddress socketAddress, byte[] readData) {

    }
}