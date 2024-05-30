package com.hxjt.dqyt.app;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.bean.MenuButtonBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author by DELL
 * @date on 2018/2/6
 * @describe
 */

public class Constants {
    public static final String IP_ADDRESS = "ip_address"; //ip地址
    public static final String PORT = "port"; //端口号

    public static final String PASSWORD_FHZ = "password_fhz"; //分合闸密码

    public static final String DEFAULT_IP_ADDRESS = "192.168.1.100"; //TCP默认IP
    public static final String DEFAULT_PORT = "6789"; //TCP默认端口号
    public static final String CONFIG_MENU_CODES = "config_menu_codes"; //已配置的菜单
    public static final String DLQ_TYPE = "dlq_type";

    public static final String RECEIVED_MESSAGE = "received_message";
    public static final String CONNECTION_CHANGED = "connection_changed";

    public static final String[] ALL_MENU_CODES = new String[]{
            "bpq",
            "wsdcgq",
            "zscgq",
            "ywcgq",
            "clzscgq",
            "zdjccgq",
            "sjcgq",
            "ymcsy",
            "sk645",
    };

    public static final String[] DEFAULT_MENU_CODES = new String[]{
            "bpq",
            "wsdcgq",
            "zscgq",
            "ywcgq",
            "clzscgq",
            "zdjccgq",
            "sjcgq",
            "ymcsy",
    };

    public static final String BPQ ="bpq" ; //变频器
    public static final String DLQ ="dlq" ; //断路器
    public static final String WSD_CGQ ="wsdcgq" ; //温湿度传感器
    public static final String ZS_CGQ ="zscgq" ; //噪声传感器
    public static final String YW_CGQ ="ywcgq" ; //烟雾传感器
    public static final String CLZS_CGQ ="clzscgq" ; //齿轮转速传感器
    public static final String ZDJC_CGQ ="zdjccgq" ; //震动监测传感器
    public static final String SJ_BSQ ="sjcgq" ; //水浸变送器
    public static final String YM_CSY ="ymcsy" ; //液面测试仪
    public static final String SK645 ="sk645" ; //塑壳
    public static final String JCQ ="jcq" ; //接触器


    public  List<MenuButtonBean> MENU_BUTTON_BEAN_LIST = new ArrayList<MenuButtonBean>() {{
        add(createMenu("变频器", R.drawable.icon_bpq_blue,"bpq"));
//        add(createMenu("断路器", R.drawable.icon_dlq_blue,"dlq"));
        add(createMenu("温湿度\n传感器", R.drawable.icon_cgq_blue,"wsdcgq"));
        add(createMenu("噪声\n传感器", R.drawable.icon_cgq_blue,"zscgq"));
        add(createMenu("烟雾\n传感器", R.drawable.icon_cgq_blue,"ywcgq"));
        add(createMenu("齿轮转速\n传感器", R.drawable.icon_cgq_blue,"clzscgq"));
        add(createMenu("震动监测\n传感器", R.drawable.icon_cgq_blue,"zdjccgq"));
        add(createMenu("水浸\n变送器", R.drawable.icon_bsq_blue,"sjcgq"));
        add(createMenu("液面\n测试仪", R.drawable.icon_bsq_blue,"ymcsy"));
    }};

    // 创建菜单项的方法
    private static MenuButtonBean createMenu(String text, int resourceId,String typeCode) {
        HashMap<String, Object> menu = new HashMap<>();
        menu.put("name", text);
        menu.put("resourceId", resourceId);
        menu.put("typeCode", typeCode); // 默认选中

        return MenuButtonBean.fromMap(menu);
    }









}
