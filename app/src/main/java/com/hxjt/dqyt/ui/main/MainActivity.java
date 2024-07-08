package com.hxjt.dqyt.ui.main;

import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.app.Constants.RECEIVED_MESSAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.easysocket.EasySocket;
import com.easysocket.interfaces.conn.IConnectionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hxjt.dqyt.R;
import com.hxjt.dqyt.adapter.MyAdapter;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.bean.DeviceInfoListBean;
import com.hxjt.dqyt.ui.system.SystemSetActivity;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.SPUtil;
import com.hxjt.dqyt.utils.TcpUtil;
import com.hxjt.dqyt.utils.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


public class MainActivity extends BaseActivity<MainPresenter> implements MainView{

    private ImageView tcpStatusImg;
    private String deviceNo; //网关编号
    private ImageView iv_oil_gif;
    private boolean isExpanded = true;
    private LineChart mLineChart;

    LinearLayout emptyView ;
    private String mTimeValue;
    private MyAdapter mMyAdapter;
    private Handler handler;

    private GridView gridView;
    private final List<DeviceInfoBean> mDevices = new ArrayList<>();

    private final Handler shrinkHandler = new Handler();
    private final Runnable shrinkRunnable = this::shrinkImage;

    List<Map<String,Double>> points;
    private OilGraphDialog oilGraphDialog;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        tcpStatusImg = findViewById(R.id.tv_connect_status);
        gridView = findViewById(R.id.grid_view);
        emptyView = findViewById(R.id.empty_view);

        TextView tvTitle = findViewById(R.id.tv_title);
        LinearLayout llBack = findViewById(R.id.ll_back);
        TextView tv_reload = findViewById(R.id.tv_reload);
        LinearLayout llSystemSet = findViewById(R.id.ll_system_set);
        TextView tvCloseApp = findViewById(R.id.tv_close_app);
        LinearLayout ll_right = findViewById(R.id.ll_right);
        iv_oil_gif = findViewById(R.id.iv_oil_gif);

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
        tv_reload.setOnClickListener(onReload);
        iv_oil_gif.setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    handleImageClick();
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        iv_oil_gif.setClickable(true);
        iv_oil_gif.setFocusable(true);

        loadImage(iv_oil_gif);

        // 默认展开
        expandImage();

        EventBus.getDefault().register(this);

        IConnectionManager connectionManager = EasySocket.getInstance().getDefconnection();
        int connectionStatus = connectionManager.getConnectionStatus();
        Log.d("当前连接状态:",connectionStatus+"");

        if(connectionStatus == 2){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }

        /************************* mock ***********************/
//        Map<String,Object> map = new HashMap<>();
//        map.put("chl",1);
//        map.put("dev_type","bpq");
//        map.put("addr","123");
//        map.put("name","变频器");
//        emptyView.setVisibility(View.GONE);
//        DeviceInfoBean deviceInfoBean = DeviceInfoBean.fromMap(map);
//        mDevices.add(deviceInfoBean);
//
//        map.put("chl",1);
//        map.put("dev_type","sk645");
//        map.put("addr","123");
//        map.put("name","液面测试仪");
//        emptyView.setVisibility(View.GONE);
//        DeviceInfoBean deviceInfoBean2 = DeviceInfoBean.fromMap(map);
//        mDevices.add(deviceInfoBean2);
//
//        map.put("chl",1);
//        map.put("dev_type","jcq");
//        map.put("addr","123");
//        map.put("name","旁路接触器");
//        emptyView.setVisibility(View.GONE);
//        DeviceInfoBean deviceInfoBean3 = DeviceInfoBean.fromMap(map);
//        mDevices.add(deviceInfoBean3);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DBUtils.testInsert();
//            }
//        }).start();
        /************************* mock ***********************/
        points = new ArrayList<>();
    }

    @Subscriber(tag = CONNECTION_CHANGED)
    public void onTcpConnectionChanged(boolean isConnect){
        if(isConnect){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }
    }

    @Subscriber(tag = RECEIVED_MESSAGE)
    public void onReceivedMessage(String data){
        if(TextUtils.isEmpty(data))
            return;

        Map<String,Object> map = JsonUtil.toMap(data);
        String cmdType = (String) map.get("TcpCmdType");

        if(cmdType == null) return;

        String zbd = (String) map.get("Zbd");

        if(cmdType.equals("103")){
            //说明是 设备列表接口
            hideLoading();
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }

            Boolean success = (Boolean) map.get("Success");
            if(success != null && success){
                Map<String,Object> infoMap = (Map) map.get("Data");
                DeviceInfoListBean deviceInfoListBean = DeviceInfoListBean.fromMap(infoMap);
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

                    //判断是否箱子中接了继电器
                    boolean hasJdq = deviceInfoListBean.getAddrList_bsmio() != null && !deviceInfoListBean.getAddrList_bsmio().isEmpty();
                    SPUtil.enableJdq(hasJdq);

                    if(mDevices.isEmpty()){
                        emptyView.setVisibility(View.VISIBLE);
                        gridView.setEmptyView(emptyView);
                    } else {
                        if(mMyAdapter != null){
                            mMyAdapter.update(MainActivity.this,mDevices,mTimeValue,deviceNo);
                        }
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }
        } else if(!TextUtils.isEmpty(zbd) && oilGraphDialog != null){
            //抽油机数据统计表
            points = JsonUtil.parseJsonToMapList(zbd);
            oilGraphDialog.setData();
        } else if(SPUtil.hasJdq() && cmdType.equals("bsmio")){
            //外接变频器
            for(DeviceInfoBean infoBean : mDevices){
                if(infoBean.getDev_type().equals(Constants.JCQ)){
                    String jcq = "断开";
                    //外接接触器
                    String scd1 = (String) map.get("srd_3");
                    if(scd1!=null&&scd1.equals("1")){
                        jcq = "运行";
                    }
                    mTimeValue = jcq;
                    mMyAdapter.updateTimeValue(infoBean,mTimeValue);
                    break;
                }
            }
        } else if(mDevices != null && !mDevices.isEmpty()){
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
                        } else if(deviceType.equals(Constants.JCQ) && !SPUtil.hasJdq()){
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
    protected void onResume() {
        super.onResume();
        TcpUtil tcpUtil = new TcpUtil();
        tcpUtil.getAllDevices();

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
        shrinkHandler.removeCallbacks(shrinkRunnable);
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
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

    /**
     * 重新加载列表
     */
    private final View.OnClickListener onReload = v -> {
        showLoading("正在加载...");
        TcpUtil tcpUtil = new TcpUtil();
        tcpUtil.getAllDevices();
        after5sHandle();
    };

    private void handleImageClick() {
        if(oilGraphDialog == null) {
            oilGraphDialog = new OilGraphDialog(context);
        }
        new XPopup.Builder(MainActivity.this)
                .dismissOnBackPressed(false) // 按返回键是否关闭弹窗，默认为true
                .dismissOnTouchOutside(true) // 点击外部是否关闭弹窗，默认为true
                .asCustom(oilGraphDialog)
                .show();
        if (!isExpanded) {
            expandImage();
        }
    }

    private void expandImage() {
        iv_oil_gif.animate()
                .translationX(0)
                .setDuration(300)
                .start();
        isExpanded = true;

        // 延迟3秒后自动收缩
        shrinkHandler.removeCallbacks(shrinkRunnable);
        shrinkHandler.postDelayed(shrinkRunnable, 3000);
    }


    private void shrinkImage() {
        iv_oil_gif.animate()
                .translationX((float) iv_oil_gif.getWidth() * 2/3)
                .setDuration(300)
                .start();
        isExpanded = false;
    }

    /**
     *  5s后，若tcp无返回，则:
     *  停止收消息、关闭loading、所有下方指令为false
     */
    private void after5sHandle(){
        handler = new Handler();
        handler.postDelayed(() -> {
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            hideLoading();
            ToastUtil.s("操作超时");
        }, 5000);
    }

    /**
     * 用grild加载图片
     */
    public void loadImage(ImageView imageView){
        Glide.with(this)
                .asGif()
                .load(R.drawable.oid_field)
                .into(imageView);
    }

    class OilGraphDialog extends CenterPopupView {

        private TextView tv_x_unit;
        private TextView tv_y_unit;

        //注意：自定义弹窗本质是一个自定义View，但是只需重写一个参数的构造，其他的不要重写，所有的自定义弹窗都是这样。
        public OilGraphDialog(@NonNull Context context) {
            super(context);
        }

        // 返回自定义弹窗的布局
        @Override
        protected int getImplLayoutId() {
            return R.layout.oid_graph_dialog;
        }

        // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
        @Override
        protected void onCreate() {
            super.onCreate();

            ImageView iv_close = findViewById(R.id.iv_close);
            mLineChart = findViewById(R.id.lc_chart);
            tv_x_unit = findViewById(R.id.tv_x_unit);
            tv_y_unit = findViewById(R.id.tv_y_unit);

            iv_close.setOnClickListener(v -> {
                dismiss();
                oilGraphDialog = null;
            });
            //mock
//            points.add(new HashMap() {{ put("a_x", 0.0); put("a_y", 48.17); }});
//            points.add(new HashMap() {{ put("b_x", 0.54); put("b_y", 50.19); }});
//            points.add(new HashMap() {{ put("c_x", 3.0); put("c_y", 50.19); }});
//            points.add(new HashMap() {{ put("d_x", 2.46); put("d_y", 48.17); }});

            setData();
        }

        public void setData() {
            if (points == null || points.isEmpty()) {
                tv_x_unit.setVisibility(GONE);
                tv_y_unit.setVisibility(GONE);
                mLineChart.setNoDataText("暂无数据");
                mLineChart.clear(); // 清除任何现有数据
                mLineChart.invalidate(); // 刷新图表
                return;
            }
            // 创建所有点的 Entry 列表
            tv_x_unit.setVisibility(VISIBLE);
            tv_y_unit.setVisibility(VISIBLE);
            List<Entry> allEntries = new ArrayList<>();
            for (Map<String, Double> map : points) {
                for (Map.Entry<String, Double> entry : map.entrySet()) {
                    if (entry.getKey().endsWith("_x")) {
                        String keyPrefix = entry.getKey().substring(0, entry.getKey().indexOf('_'));
                        Double x0 = entry.getValue();
                        Double y0 = map.get(keyPrefix + "_y");

                        if (x0 == null || y0 == null) {
                            return;
                        }
                        double x = x0;
                        double y = y0;

                        allEntries.add(new Entry((float) x, (float) y));
                    }
                }
            }

            if(allEntries.size() != 4) return;

            List<Entry> maxEntries = new ArrayList<>();
            List<Entry> minEntries = new ArrayList<>();

            maxEntries.add(allEntries.get(0));
            maxEntries.add(allEntries.get(1));
            maxEntries.add(allEntries.get(2));

            minEntries.add(allEntries.get(0));
            minEntries.add(allEntries.get(3));
            minEntries.add(allEntries.get(2));

            // 创建最大值的数据集
            LineDataSet maxDataSet = new LineDataSet(maxEntries, "最大值");
            maxDataSet.setColor(Color.RED);
            maxDataSet.setValueTextColor(Color.RED);
            maxDataSet.setValueTextSize(14);
            maxDataSet.setMode(LineDataSet.Mode.LINEAR);
            maxDataSet.setDrawValues(true);
            maxDataSet.setDrawCircles(true);
            maxDataSet.setCircleColor(Color.RED);

            // 创建最小值的数据集
            LineDataSet minDataSet = new LineDataSet(minEntries, "最小值");
            minDataSet.setColor(Color.BLACK);
            minDataSet.setValueTextColor(Color.BLACK);
            minDataSet.setValueTextSize(14);
            minDataSet.setMode(LineDataSet.Mode.LINEAR);
            minDataSet.setDrawValues(true);
            minDataSet.setDrawCircles(true);
            minDataSet.setCircleColor(Color.BLACK);

            // 创建 LineData 对象并设置数据集
            LineData lineData = new LineData();
            lineData.addDataSet(maxDataSet);
            lineData.addDataSet(minDataSet);
            mLineChart.setData(lineData);

            // X轴设置
            XAxis xAxis = mLineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X轴显示在底部
            xAxis.setTextSize(14);
            xAxis.setGranularity(1f); // 设置最小间隔为1，防止标签的重叠
            xAxis.setLabelCount(points.size(), true); // 设置标签数量与数据点数量一致

            // Y轴设置
            YAxis rightAxis = mLineChart.getAxisRight();
            rightAxis.setEnabled(false); // 右侧Y轴禁用

            YAxis leftAxis = mLineChart.getAxisLeft();
            leftAxis.setTextSize(14);

            // 设置图例
            Legend legend = mLineChart.getLegend();
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setTextColor(Color.BLACK);

            // 刷新图表
            mLineChart.invalidate();
        }

        // 设置最大宽度，看需要而定，
        @Override
        protected int getMaxWidth() {
            return super.getMaxWidth();
        }

        // 设置最大高度，看需要而定
        @Override
        protected int getMaxHeight() {
            return super.getMaxHeight();
        }

        // 设置自定义动画器，看需要而定
        @Override
        protected PopupAnimator getPopupAnimator() {
            return super.getPopupAnimator();
        }

        /**
         * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
         *
         * @return
         */
        protected int getPopupWidth() {
            return 0;
        }

        /**
         * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
         *
         * @return
         */
        protected int getPopupHeight() {
            return 0;
        }

    }

}