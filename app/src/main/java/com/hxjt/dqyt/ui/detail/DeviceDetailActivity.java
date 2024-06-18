package com.hxjt.dqyt.ui.detail;

import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.app.Constants.DLQ_TYPE;
import static com.hxjt.dqyt.app.Constants.RECEIVED_MESSAGE;
import static com.hxjt.dqyt.app.Constants.SK645;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easysocket.EasySocket;
import com.easysocket.interfaces.conn.IConnectionManager;
import com.google.gson.Gson;
import com.hxjt.dqyt.R;
import com.hxjt.dqyt.adapter.DeviceStatusAdapter;
import com.hxjt.dqyt.adapter.TextButtonAdapter;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.ui.widget.MyCustomGridView;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.SPUtil;
import com.hxjt.dqyt.utils.TcpUtil;
import com.hxjt.dqyt.utils.TextUtil;
import com.hxjt.dqyt.utils.ToastUtil;
import com.lxj.xpopup.XPopup;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DeviceDetailActivity extends BaseActivity<DeviceDetailPresenter> implements  DeviceDetailView{

    private DeviceInfoBean deviceInfoBean;
    private ImageView tcpStatusImg;
    private TextView tvDeviceName;
    private GridView operationGridView;
    private String[] operationButtonLabels;
    private MyCustomGridView stateGridView;
    private Map<String,Object>[] stateLabels;
    private Map<String,Object> mReceivedTcpData;

    private String tempDeviceName;

    private boolean isYc = false;
    private boolean isZzyx = false;
    private boolean isFzyx = false;
    private boolean isTj = false;
    private boolean isFz = false;
    private boolean isHz = false;
    private boolean isDq = false;

    private DeviceStatusAdapter statusAdapter;

    private Handler handler;

    @Override
    protected DeviceDetailPresenter createPresenter() {
        return new DeviceDetailPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_detail;
    }

    @Override
    public void initData() {}

    @Subscriber(tag = CONNECTION_CHANGED)
    public void onTcpConnectionChanged(boolean isConnect){
        if(isConnect){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }
    }

    @Subscriber(tag = RECEIVED_MESSAGE)
    public void onReceivedMessage(String readData){
        Map<String,Object> map = JsonUtil.toMap(readData);

        String cmdType = (String) map.get("TcpCmdType");
        String message = (String) map.get("Message");
        Boolean success = (Boolean) map.get("Success");

        if(cmdType == null) return;

        if(cmdType.equals("101")){
            hideLoading();
            if(success != null && success){
                //删除成功
                finish();
            } else {
                //删除失败
                if(message != null){
                    ToastUtil.s(message);
                }
            }
        } else if(cmdType.equals("102")){
            //修改设备名称
            hideLoading();
            if(success != null && success){
                if(tempDeviceName != null){
                    deviceInfoBean.setName(tempDeviceName);
                    tvDeviceName.setText(tempDeviceName);
                }
            } else {
                //编辑失败
                if(message != null){
                    ToastUtil.s(message);
                }
            }
        } else {
            //Tcp数据包
            String deviceType = deviceInfoBean.getDev_type();
            if (deviceType!=null) {
                String deviceCode = (String) map.get("DeviceCode");

                if(deviceType.equals(Constants.DLQ)){
                    deviceCode = (String) map.get("SN");
                }

                if(deviceCode == null) return;

                // 收到的tcp数据包属于当前设备(设备类型和设备编号均一致)
                if(cmdType.equals(deviceType)  && deviceCode.equals(deviceInfoBean.getAddr())){
                    hideLoading();

                    String msg = getMsgByOperationType();
                    if(msg != null){
                        ToastUtil.s(msg);
                    }

                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    }

                    mReceivedTcpData = map;

                    // 当前设备是`量测断路器`时，
                    // 需要处理个别参数(A相电流、B相电流、C相电流)的小数点位数。
                    if(deviceType.equals(SK645) && SPUtil.getString(DLQ_TYPE,"sk").equals("lc")){
                        for (Map.Entry<String, Object> entry : mReceivedTcpData.entrySet()) {
                            String key = entry.getKey();
                            boolean needParse = TextUtil.isEqualIgnoreCase(key,"DqAxiangDianLiu") || TextUtil.isEqualIgnoreCase(key,"DqBxiangDianLiu")||
                                    TextUtil.isEqualIgnoreCase(key,"DqCxiangDianLiu");
                            if (needParse) {
                                String str = (String) entry.getValue();
                                BigDecimal value = new BigDecimal(str);
                                BigDecimal result = value.divide(new BigDecimal(10), 2, RoundingMode.HALF_UP);
                                map.put(key, result.toString());
                            }
                        }
                    }

                    statusAdapter.update(DeviceDetailActivity.this,stateLabels,mReceivedTcpData);

                    // 断路器分合闸操作后，再下发cmdType=11的指令。
                    if(deviceType.equals(SK645) && (isFz || isHz) ){
                        orderToFalse();
                        sendMessage("11");
                    }
                    orderToFalse();
                }
            }
        }
    }

    @Override
    public void initView() {
        LinearLayout llBack = findViewById(R.id.ll_back);
        tcpStatusImg = findViewById(R.id.tv_connect_status);
        TextView tvDeviceNo = findViewById(R.id.tv_device_no);
        TextView tvDeviceCode = findViewById(R.id.tv_device_code);
        tvDeviceName = findViewById(R.id.tv_device_name);
        operationGridView = findViewById(R.id.grid_view);
        stateGridView = findViewById(R.id.state_grid_view);
        stateGridView = findViewById(R.id.state_grid_view);
        ImageView ivImage = findViewById(R.id.iv_image);
        TextView tvDeviceType = findViewById(R.id.tv_device_type);

        llBack.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String deviceNo = intent.getStringExtra("device_no");
        deviceInfoBean = getIntent().getParcelableExtra("device_info_bean");

        if(deviceNo != null) {
            tvDeviceNo.setText(deviceNo);
        }

        if(deviceInfoBean != null) {
            tvDeviceCode.setText(deviceInfoBean.getAddr());
            tvDeviceName.setText(deviceInfoBean.getName());

            int drawable = DeviceUtil.getDeviceImageByType(deviceInfoBean.getDev_type());

            if(deviceInfoBean.getDev_type()!=null && deviceInfoBean.getDev_type().equals(SK645)){
                String dlqType = SPUtil.getString(DLQ_TYPE,"sk");
                if(dlqType.equals("lc")){
                    drawable = R.drawable.icon_sk_white;
                }
            }
            ivImage.setImageDrawable(getResources().getDrawable(drawable));

            String typeName = DeviceUtil.getNameOfType(deviceInfoBean.getDev_type());
            tvDeviceType.setText(typeName);
        }

        IConnectionManager connectionManager = EasySocket.getInstance().getDefconnection();
        int connectionStatus = connectionManager.getConnectionStatus();

        if(connectionStatus == 2){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }

        EventBus.getDefault().register(this);

        initOperationView();

        initStateView();

        sendMessage_all();
    }

    private void sendMessage_sk(){
        String type = SPUtil.getString(DLQ_TYPE,"sk");
        if(type.equals("sk")){
            sendMessage("1");
            new Handler(Looper.getMainLooper()).postDelayed(() -> sendMessage("11"), 500);
        } else if(type.equals("lc")){
            sendMessage("1");
            new Handler(Looper.getMainLooper()).postDelayed(() -> sendMessage("1"), 4000);
        }
    }

    private void sendMessage_bpq(){
        sendMessage("1");
        new Handler(Looper.getMainLooper()).postDelayed(() -> sendMessage("2"), 500);
    }

    private void sendMessage_clzs(){
        sendMessage("1");
        new Handler(Looper.getMainLooper()).postDelayed(() -> sendMessage("6"), 500);
    }

    private void sendMessage_all(){
        if(deviceInfoBean.getDev_type()!=null && deviceInfoBean.getDev_type().equals(Constants.YM_CSY)){
            sendMessage("2");
        } else if(deviceInfoBean.getDev_type()!=null && deviceInfoBean.getDev_type().equals(SK645)){
            sendMessage_sk();
        } else if(deviceInfoBean.getDev_type()!=null && deviceInfoBean.getDev_type().equals(Constants.BPQ)){
            sendMessage_bpq();
        } else if(deviceInfoBean.getDev_type()!=null && deviceInfoBean.getDev_type().equals(Constants.CLZS_CGQ)){
            sendMessage_clzs();
        } else {
            sendMessage("1");
        }
    }

    /**
     * 操作按钮视图
     */
    private void initOperationView(){
        if(deviceInfoBean != null){
            String deviceType = deviceInfoBean.getDev_type();

            operationButtonLabels = DeviceUtil.getOperationButtonsByType(deviceType);

            if(deviceInfoBean!=null && deviceInfoBean.getDev_type()!=null && deviceInfoBean.getDev_type().equals(SK645)){
                String dlqType = SPUtil.getString(DLQ_TYPE,"sk");
                if(dlqType.equals("lc")){
                    operationButtonLabels = new String[]{"修改名称","删除","遥测","历史数据"};
                }
            }

            TextButtonAdapter textButtonAdapter = new TextButtonAdapter(this, operationButtonLabels);
            operationGridView.setAdapter(textButtonAdapter);

            // 设置水平间距和垂直间距
            int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.dp_5);
            int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.dp_5);
            operationGridView.setHorizontalSpacing(horizontalSpacing);
            operationGridView.setVerticalSpacing(verticalSpacing);

            operationGridView.setOnItemClickListener((parent, view, position, id) -> {
                String buttonText = operationButtonLabels[position];
                if(buttonText.equals("修改名称")){
                    updateDeviceName();
                } else if(buttonText.equals("删除")){
                    deleteDevice();
                } else if(buttonText.equals("遥测")){
                    showLoading("正在遥测...");
                    isYc = true;
                    sendMessage_all();
                } else if(buttonText.equals("读取")){
                    showLoading("正在读取...");
                    isDq = true;
                    sendMessage("1");
                } else if(buttonText.equals("分闸")){
                    onFhzHandler(0);
                } else if(buttonText.equals("合闸")){
                    onFhzHandler(1);
                } else if(buttonText.equals("正转运行")){
                    new XPopup.Builder(this).asConfirm("提示", "确定需要执行正转运行吗?",
                            () -> {
                                isZzyx = true;
                                showLoading("准备正转运行...");
                                sendMessage_Bpq("1");
                            }).show();
                } else if(buttonText.equals("反转运行")){
                    new XPopup.Builder(this).asConfirm("提示", "确定需要执行反转运行吗?",
                            () -> {
                                showLoading("准备反转运行...");
                                isFzyx = true;
                                sendMessage_Bpq("2");
                            }).show();
                } else if(buttonText.equals("停机")){
                    new XPopup.Builder(this).asConfirm("提示", "确定需要停止吗?",
                            () -> {
                                showLoading("准备停机...");
                                isTj = true;
                                sendMessage_Bpq("5");
                            }).show();


                }else if(buttonText.equals("频率设置")){
                    showPlszDialog();
                }else if(buttonText.equals("历史数据")){
                    Intent intent = new Intent(this, DeviceHistoryDataActivity.class);
                    intent.putExtra("device_info_bean",deviceInfoBean);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 设备状态视图
     */
    private void initStateView(){
        if(deviceInfoBean != null){
            String deviceType = deviceInfoBean.getDev_type();

            stateLabels = DeviceUtil.getDeviceStatusByType(deviceType);

            if(deviceType.equals(SK645) && SPUtil.getString(DLQ_TYPE,"sk").equals("lc")){
                //量测去掉"闸位状态"
                stateLabels = DeviceUtil.removeFirstElement(stateLabels);
            }

            if(statusAdapter == null){
                statusAdapter = new DeviceStatusAdapter(this, stateLabels,mReceivedTcpData);
                stateGridView.setAdapter(statusAdapter);
            }

            // 设置水平间距和垂直间距
            int horizontalSpacing = getResources().getDimensionPixelSize(R.dimen.dp_5);
            int verticalSpacing = getResources().getDimensionPixelSize(R.dimen.dp_5);
            stateGridView.setHorizontalSpacing(horizontalSpacing);
            stateGridView.setVerticalSpacing(verticalSpacing);
        }
    }

    /**
     * 修改设备名称
     */
    private void updateDeviceName(){
        Dialog inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.dialog_edit_device_name);
        inputDialog.setCancelable(true);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        EditText etDeviceName = inputDialog.findViewById(R.id.et_device_name);
        etDeviceName.setText(deviceInfoBean.getName());
        if(!TextUtils.isEmpty(deviceInfoBean.getName()))
            etDeviceName.setSelection(deviceInfoBean.getName().length());
        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            Editable editable = etDeviceName.getText();
            if (TextUtils.isEmpty(editable)) {
                ToastUtil.s("请输入设备名称");
            } else if (editable.toString().equals(deviceInfoBean.getName())) {
                inputDialog.dismiss();
            } else {
                inputDialog.dismiss();

                showLoading("正在修改...");

                tempDeviceName = editable.toString();

                TcpUtil tcpUtil = new TcpUtil();

                DeviceInfoBean newInfo = deviceInfoBean;
                newInfo.setName(editable.toString());

                tcpUtil.editDevice(newInfo);
            }
        });
        inputDialog.show();
    }

    private void deleteDevice(){
        new XPopup.Builder(this).asConfirm("提示", "确定删除该设备吗?",
                () -> {
                    showLoading("正在删除...");
                    TcpUtil tcpUtil = new TcpUtil();
                    tcpUtil.deleteDevice(deviceInfoBean);
                }).show();
    }

    private String getMsgByOperationType(){
        if(isFzyx) return "正在反向运行";
        if(isZzyx) return "正在正向运行";
        if(isFz) return "分闸成功";
        if(isHz) return "合闸成功";
        if(isTj) return "停机成功";
        if (isYc) return "遥测成功";
        if(isDq) return "读取成功";
        else return null;
    }


    private void  displayWithTcpStatus(boolean isConnected) {
        if(isConnected){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
        }
    }

    private void sendMessage(String cmdType){

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

        if(deviceInfoBean != null){
            Map<String,Object> map = new HashMap<>();
            map.put("DeviceType",deviceInfoBean.getDev_type());
            map.put("DeviceCode",deviceInfoBean.getAddr());
            map.put("CmdType",cmdType);
            map.put("PayloadJson","");
            map.put("name","");

            after10sHandle();
            Gson gson = new Gson();
            String jsonString = gson.toJson(map);

            byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

            EasySocket.getInstance().upMessage(jsonBytes);
        }
    }


    private void sendMessage(String cmdType,String payloadJson){

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }

        if(deviceInfoBean != null){
            Map<String,Object> map = new HashMap<>();
            map.put("DeviceType",deviceInfoBean.getDev_type());
            map.put("DeviceCode",deviceInfoBean.getAddr());
            map.put("CmdType",cmdType);
            map.put("PayloadJson",payloadJson);
            map.put("name","");

            after10sHandle();
            Gson gson = new Gson();
            String jsonString = gson.toJson(map);

            byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

            EasySocket.getInstance().upMessage(jsonBytes);
        }
    }

    private void orderToFalse(){
        isFzyx = isFz = isZzyx = isHz = isTj = isYc = isDq = false;
    }

    /**
     *  5s后，若tcp无返回，则:
     *  停止收消息、关闭loading、所有下方指令为false
     */
    private void after10sHandle(){
        handler = new Handler();
        handler.postDelayed(() -> {
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            hideLoading();
            orderToFalse();
            ToastUtil.s("操作超时");
        }, 5000);
    }

    /**
     * 变频器下发指令
     * @param payloadJson :1正转运行 2反转运行 5停机
     */
    private void sendMessage_Bpq(String payloadJson){
        if(deviceInfoBean != null){
            Map<String,Object> map = new HashMap<>();
            map.put("DeviceType","bpq");
            map.put("DeviceCode",deviceInfoBean.getAddr());
            map.put("CmdType","8");
            map.put("PayloadJson",payloadJson);
            map.put("name","");

            Gson gson = new Gson();
            String jsonString = gson.toJson(map);

            byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);

            EasySocket.getInstance().upMessage(jsonBytes);
        }
    }

    private void onFhzHandler(int type){
        String savedPwd = SPUtil.getString(Constants.PASSWORD_FHZ,"");
        String msg = "分闸";
        if(type == 1){
            msg = "合闸";
        }
        if(savedPwd.isEmpty()){
            new XPopup.Builder(this).asConfirm("提示", "确认需要"+msg+ "吗?",
                    () -> {
                        if(type == 0){
                            showLoading("正在分闸...");
                            isFz = true;
                            sendMessage("4");
                        } else if(type == 1){
                            showLoading("正在合闸...");
                            isHz = true;
                            sendMessage("3");
                        }
                    }).show();
        } else {
            showInputDialog(type);
        }
    }

    /**
     * 输入ip或port
     * @param type 0:ip 1:port
     */
    private void showInputDialog(int type) {
        Dialog inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.dialog_input);
        inputDialog.setCancelable(true);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        EditText input = inputDialog.findViewById(R.id.et_input);
        TextView title = inputDialog.findViewById(R.id.tv_title);

        title.setText("密码验证");
        input.setHint("请输入密码");

        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            String oldPwd = SPUtil.getString(Constants.PASSWORD_FHZ,"1");
            if (TextUtils.isEmpty(input.getText().toString())) {
                ToastUtil.s("请输入密码");
            } else if(!oldPwd.equals(input.getText().toString())){
                ToastUtil.s("密码错误");
            }else {
                Log.d("msg",input.getText().toString());
                inputDialog.dismiss();
                if(type == 0){
                    showLoading("正在分闸...");
                    isFz = true;
                    sendMessage("4");
                } else if(type == 1){
                    showLoading("正在合闸...");
                    isHz = true;
                    sendMessage("3");
                }
            }
        });

        inputDialog.setOnShowListener(dialog -> {
            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        });
        inputDialog.show();
    }

    /**
     * 频率设置
     */
    private void showPlszDialog() {
        Dialog inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.dialog_input);
        TextView title = inputDialog.findViewById(R.id.tv_title);
        EditText input = inputDialog.findViewById(R.id.et_input);

        title.setText("设置频率");
        input.setHint("请输入频率值");

        inputDialog.setCancelable(true);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (TextUtils.isEmpty(input.getText().toString())) {
                ToastUtil.s("请输入频率");
            } else {
                Log.d("msg",input.getText().toString());

                int result = convertStringToInt(input.getText().toString());
                if(result == -1){
                    ToastUtil.s("请输入有效数字");
                } else {
                    inputDialog.dismiss();

                    showLoading("正在设置...");
                    String msg =  String.valueOf(result*100);
                    sendMessage("9",msg);
                }
            }
        });
        inputDialog.setOnShowListener(dialog -> {
            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        });
        inputDialog.show();
    }

    public int convertStringToInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // 输入的字符串无法转换为整数，处理异常情况
            e.printStackTrace();
            return -1; // 或者返回一个默认值，例如0或-1，或者根据需要处理
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