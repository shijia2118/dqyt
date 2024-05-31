package com.hxjt.dqyt.ui.system;


import static com.hxjt.dqyt.app.Constants.CONNECTION_CHANGED;
import static com.hxjt.dqyt.app.Constants.IP_ADDRESS;
import static com.hxjt.dqyt.app.Constants.PORT;
import static com.hxjt.dqyt.app.Constants.RECEIVED_MESSAGE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.easysocket.EasySocket;
import com.easysocket.interfaces.conn.IConnectionManager;
import com.hxjt.dqyt.R;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.base.BaseActivity;
import com.hxjt.dqyt.bean.DeviceInfoBean;
import com.hxjt.dqyt.bean.MenuButtonBean;
import com.hxjt.dqyt.utils.DeviceUtil;
import com.hxjt.dqyt.utils.JsonUtil;
import com.hxjt.dqyt.utils.SPUtil;
import com.hxjt.dqyt.utils.TcpUtil;
import com.hxjt.dqyt.utils.ToastUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSetActivity extends BaseActivity<SystemSetPresenter> implements SystemSetView{

    private TextView tvIpSet;
    private TextView tvPortSet;
    private TextView tvTcpTitle;
    private TextView tvTcpSwitch;
    private ImageView tcpStatusImg;

    private String deviceType;
    private String chl;
    private Dialog inputDialog;
    private Handler handler;
    private boolean isOpenTcp = false;
    private boolean isCloseTcp = false;

    @Override
    protected SystemSetPresenter createPresenter() {
        return new SystemSetPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_set;
    }

    @Override
    public void initData() {}

    @Override
    public void initView() {
        LinearLayout llBack = findViewById(R.id.ll_back);
        tcpStatusImg = findViewById(R.id.tv_connect_status);
        LinearLayout llIpSet = findViewById(R.id.ll_ip_set);
        tvIpSet = findViewById(R.id.tv_ip_set);
        LinearLayout llPortSet = findViewById(R.id.ll_port_set);
        tvPortSet = findViewById(R.id.tv_port_set);

        tvTcpSwitch = findViewById(R.id.tv_tcp_switch);
        tvTcpTitle = findViewById(R.id.tv_tcp_title);
        TextView tvAddDevice = findViewById(R.id.tv_add_device);
        TextView tvUpdatePwd = findViewById(R.id.tv_update_pwd);
        RadioGroup radioGroup = findViewById(R.id.rg_dlq_type);
        RadioButton rb_sk = findViewById(R.id.rb_sk);
        RadioButton rb_lc = findViewById(R.id.rb_lc);
        TextView tv_reset_pwd = findViewById(R.id.tv_reset_pwd);

        llBack.setOnClickListener(v -> finish());
        llIpSet.setOnClickListener(ipSetListener);
        llPortSet.setOnClickListener(portSetListener);
        tvTcpSwitch.setOnClickListener(tcpSwitchListener);
        tvAddDevice.setOnClickListener(addDeviceListener);
        tvUpdatePwd.setOnClickListener(onUpdatePwd);
        radioGroup.setOnCheckedChangeListener(radioGroupCheckedChangeListener);
        tv_reset_pwd.setOnClickListener(onResetPassword);

        IConnectionManager connectionManager = EasySocket.getInstance().getDefconnection();
        int connectionStatus = connectionManager.getConnectionStatus();

        if(connectionStatus == 2){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
            tvTcpSwitch.setText("关闭");
            tvTcpTitle.setText("关闭TCP");
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
            tvTcpSwitch.setText("开启");
            tvTcpTitle.setText("开启TCP");
        }

        String dlqType = SPUtil.getString(Constants.DLQ_TYPE,"sk");
        if(dlqType.equals("sk")){
            rb_sk.setChecked(true);
            rb_lc.setChecked(false);
        } else if(dlqType.equals("lc")){
            rb_sk.setChecked(false);
            rb_lc.setChecked(true);
        }

        String ip = SPUtil.getString(IP_ADDRESS, "");
        tvIpSet.setText(ip);
        String port = SPUtil.getString(PORT, "");
        tvPortSet.setText(port);

        EventBus.getDefault().register(this);
    }

    @Subscriber(tag = CONNECTION_CHANGED)
    public void onTcpConnectionChanged(boolean isConnect){

        if(isConnect && isOpenTcp){
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            isOpenTcp = false;
            hideLoading();
            ToastUtil.s("TCP连接成功");
        } else if(!isConnect && isCloseTcp){
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            isCloseTcp = false;
            hideLoading();
            ToastUtil.s("TCP已关闭");
        }

        if(isConnect){
            tcpStatusImg.setImageResource(R.drawable.icon_connect);
            tvTcpSwitch.setText("关闭");
            tvTcpTitle.setText("关闭TCP");
        } else {
            tcpStatusImg.setImageResource(R.drawable.icon_disconnect);
            tvTcpSwitch.setText("开启");
            tvTcpTitle.setText("开启TCP");
        }
    }

    @Subscriber(tag = RECEIVED_MESSAGE)
    public void onReceivedMessage(String data){
        Map<String,Object> map = JsonUtil.toMap(data);
        String cmdType = (String) map.get("TcpCmdType");

        if(cmdType != null){
            String message = (String) map.get("Message");
            Boolean success = (Boolean) map.get("Success");

            if(cmdType.equals("100")){
                hideLoading();
                if(message != null){
                    ToastUtil.s(message);
                }
                if(success!=null&& success){
                    inputDialog.dismiss();
                }
            }
        }
    }

    View.OnClickListener ipSetListener = v -> showInputDialog(0);

    View.OnClickListener portSetListener = v -> showInputDialog(1);

    /**
     * 开启或关闭TCP
     */
    View.OnClickListener tcpSwitchListener = v -> {
        if(tvTcpSwitch.getText().equals("开启")){
            isOpenTcp = true;
            showLoading("正在开启...");
            EasySocket.getInstance().connect();
        } else {
            showLoading("正在关闭...");
            isCloseTcp = true;
            EasySocket.getInstance().disconnect(false);
        }
        after5sHandle();
    };

    RadioGroup.OnCheckedChangeListener radioGroupCheckedChangeListener = (group, checkedId) -> {
        switch (checkedId) {
            case R.id.rb_sk:
                SPUtil.putString(Constants.DLQ_TYPE,"sk");
                break;
            case R.id.rb_lc:
                SPUtil.putString(Constants.DLQ_TYPE,"lc");
                break;
        }
    };

    View.OnClickListener addDeviceListener = v -> addDeviceDialog();

    View.OnClickListener onUpdatePwd = v -> {
        String savedPwd = SPUtil.getString(Constants.PASSWORD_FHZ,"");
        if(savedPwd.isEmpty()){
            showSetPwdDialog();
        } else {
            showUpdateDialog();
        }
    };

    View.OnClickListener onResetPassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showResetDialog();
        }
    };

    /**
     * 重置密码
     */
    private void showResetDialog() {
        Dialog inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.dialog_input);
        inputDialog.setCancelable(true);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        EditText input = inputDialog.findViewById(R.id.et_input);

        TextView title = inputDialog.findViewById(R.id.tv_title);
        title.setText("重置密码");
        input.setHint("请输入管理员密码");

        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (TextUtils.isEmpty(input.getText().toString())) {
                ToastUtil.s("请输入管理员密码");
            } else {
                if(!input.getText().toString().equals("hx888888")){
                    ToastUtil.s("管理员密码错误");
                } else {
                    Log.d("msg",input.getText().toString());
                    SPUtil.putString(Constants.PASSWORD_FHZ,"");
                    ToastUtil.s("密码已还原");
                    inputDialog.dismiss();
                }
            }
        });
        inputDialog.show();
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

        String ip = SPUtil.getString(IP_ADDRESS, "");
        String port = SPUtil.getString(PORT, "");

        if(type == 0){
            input.setText(ip);
        } else if(type == 1){
            input.setText(port);
        }
        input.setSelection(input.getText().length());
        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (TextUtils.isEmpty(input.getText().toString())) {
                ToastUtil.s("请输入"+ (type == 0 ? "IP地址" : "端口号"));
            } else {
                if(type == 0){
                    SPUtil.putString(IP_ADDRESS,input.getText().toString());
                    tvIpSet.setText(input.getText().toString());
                } else {
                    SPUtil.putString(PORT,input.getText().toString());
                    tvPortSet.setText(input.getText().toString());
                }
                Log.d("msg",input.getText().toString());
                ToastUtil.s("设置成功");
                inputDialog.dismiss();

            }
        });
        inputDialog.show();
    }

    private void showSetPwdDialog() {
        Dialog inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.update_pwd_dialog);
        inputDialog.setCancelable(true);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        EditText firstPwd = inputDialog.findViewById(R.id.et_old_pwd);
        EditText secondPwd = inputDialog.findViewById(R.id.et_new_pwd);

        firstPwd.setHint("请输入密码");
        secondPwd.setHint("请再次输入密码");

        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (TextUtils.isEmpty(firstPwd.getText().toString())) {
                ToastUtil.s("请输入密码");
            } else if(TextUtils.isEmpty(secondPwd.getText().toString())){
                ToastUtil.s("请再次输入密码");
            } else if(!secondPwd.getText().toString() .equals(firstPwd.getText().toString())){
                ToastUtil.s("两次输入的密码不一致");
            } else {
                SPUtil.putString(Constants.PASSWORD_FHZ,firstPwd.getText().toString());
                ToastUtil.s("密码设置成功");
                inputDialog.dismiss();
            }
        });
        inputDialog.setOnShowListener(dialog -> {
            firstPwd.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(firstPwd, InputMethodManager.SHOW_IMPLICIT);
        });
        firstPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // 移动焦点到第二个输入框
                secondPwd.requestFocus();
                return true;
            }
            return false;
        });

        secondPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                return true;
            }
            return false;
        });
        inputDialog.show();
    }

    private void showUpdateDialog() {
        Dialog inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.update_pwd_dialog);
        inputDialog.setCancelable(true);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        EditText etOldPwd = inputDialog.findViewById(R.id.et_old_pwd);
        EditText etNewPwd = inputDialog.findViewById(R.id.et_new_pwd);

        String oldPwd = SPUtil.getString(Constants.PASSWORD_FHZ, "1");
        inputDialog.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            if (TextUtils.isEmpty(etOldPwd.getText().toString())) {
                ToastUtil.s("请输入旧密码");
            } else if(TextUtils.isEmpty(etNewPwd.getText().toString())){
                ToastUtil.s("请输入新密码");
            } else if(!oldPwd.equals(etOldPwd.getText().toString())){
                ToastUtil.s("旧密码错误");
            } else {
                SPUtil.putString(IP_ADDRESS,etNewPwd.getText().toString());
                ToastUtil.s("修改成功");
                inputDialog.dismiss();
            }
        });
        inputDialog.setOnShowListener(dialog -> {
            etOldPwd.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etOldPwd, InputMethodManager.SHOW_IMPLICIT);
        });
        etOldPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // 移动焦点到第二个输入框
                etNewPwd.requestFocus();
                return true;
            }
            return false;
        });

        etNewPwd.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                return true;
            }
            return false;
        });
        inputDialog.show();
    }

    private void addDeviceDialog() {

        inputDialog = new Dialog(this);
        inputDialog.setContentView(R.layout.add_device_dialog);
        inputDialog.setCancelable(false);
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        EditText name = inputDialog.findViewById(R.id.et_name);
        EditText code = inputDialog.findViewById(R.id.et_code);
        Spinner spinnerType = inputDialog.findViewById(R.id.sp_type);
        Spinner spinnerChannel = inputDialog.findViewById(R.id.sp_channel);
        TextView cancel = inputDialog.findViewById(R.id.tv_cancel);
        TextView confirm = inputDialog.findViewById(R.id.tv_confirm);

        // 从 MENU_TYPES 中提取选项
        List<String> connectTypes = new ArrayList<>();
        List<MenuButtonBean> configMenus = DeviceUtil.getConfigMenuButtons();
        for (MenuButtonBean deviceInfo : configMenus) {
            connectTypes.add(deviceInfo.getName().replace("\n",""));
        }

        deviceType = (String) configMenus.get(0).getTypeCode(); //设备类型，默认第一个
        chl = "1";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, connectTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        /// 选择设备类型
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MenuButtonBean selectedItem = configMenus.get(position);
                deviceType = (String) selectedItem.getTypeCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /// 选择通道
        spinnerChannel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chl = String.valueOf((position + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancel.setOnClickListener(v ->inputDialog.dismiss());

        confirm.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name.getText().toString())) {
                ToastUtil.s("请输入设备名称");
            }
        });

        confirm.setOnClickListener(v -> {
            String deviceName = name.getText().toString();
            String deviceCode = code.getText().toString();

            if(TextUtils.isEmpty(deviceName)){
                ToastUtil.s("请输入设备名称");
            } else  if(TextUtils.isEmpty(deviceCode)){
                ToastUtil.s("请输入设备编号");
            } else {
                Map<String,Object> map = new HashMap<>();
                map.put("chl",chl);
                map.put("dev_type",deviceType);
                map.put("addr",deviceCode);
                map.put("name",deviceName);
                DeviceInfoBean infoBean = DeviceInfoBean.fromMap(map);

                showLoading("正在添加...");

                new Thread(() -> {
                    TcpUtil tcpUtil = new TcpUtil();
                    tcpUtil.addDevice(infoBean);
                }).start();

            }
        });
        inputDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     *  5s后，若tcp无返回，则:
     *  停止收消息、关闭loading、所有下发指令为false
     */
    private void after5sHandle(){
        handler = new Handler();
        handler.postDelayed(() -> {
            if(handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            hideLoading();
            orderToFalse();
            ToastUtil.s("操作失败");
        }, 5000);
    }

    private void orderToFalse(){
        isOpenTcp = isCloseTcp = false;
    }
}