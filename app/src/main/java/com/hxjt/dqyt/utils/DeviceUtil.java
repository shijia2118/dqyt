package com.hxjt.dqyt.utils;

import static com.hxjt.dqyt.app.Constants.CONFIG_MENU_CODES;
import static com.hxjt.dqyt.app.Constants.DEFAULT_MENU_CODES;

import com.hxjt.dqyt.R;
import com.hxjt.dqyt.app.Constants;
import com.hxjt.dqyt.bean.MenuButtonBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceUtil {

    public static List<MenuButtonBean> getConfigMenuButtons(){
        List<MenuButtonBean> result = new ArrayList<>();
        String[] menuCodes = getConfigMenus();
        for (String code : menuCodes) {
            MenuButtonBean menuButtonBean = new MenuButtonBean();
            menuButtonBean.setName(DeviceUtil.getNameOfType(code));
            menuButtonBean.setTypeCode(code);
            menuButtonBean.setResourceId(DeviceUtil.getResourceIdByType(code));
            result.add(menuButtonBean);
        }
        return result;
    }

    public static String[] getConfigMenus(){
        String[] menuCodes = SPUtil.getStringArray(CONFIG_MENU_CODES);
        if(menuCodes.length <= 0){
            return DEFAULT_MENU_CODES;
        }
        return menuCodes;
    }

    /**
     * 根据设备类型获取设备图片
     * @param type
     * @return drawable
     */
    public static int getDeviceImageByType(String type) {
        switch (type){
            case "wsdcgq":return R.drawable.device_wsd;
            case "zscgq":return R.drawable.device_zs;
            case "ywcgq":return R.drawable.device_yg;
            case "sjcgq":return R.drawable.device_sj;
            case "clzscgq":return R.drawable.device_clzs;
            case "zdjccgq":return R.drawable.device_zdcgq;
            case "bpq":return R.drawable.device_bpq;
            case "jcq":return R.drawable.device_hgq;
            case "ymcsy":return R.drawable.device_ymcsy;
            case "sk645":return R.drawable.devoce_sk645;

            default:return -1;
        }
    }

    public static int getResourceIdByType(String type){
        if(type.equals(Constants.BPQ)){
            return R.drawable.icon_bpq_blue;
        } else if(type.equals(Constants.DLQ)){
            return R.drawable.icon_dlq_blue;
        } else if(type.equals(Constants.WSD_CGQ)){
            return R.drawable.icon_cgq_blue;
        } else if(type.equals(Constants.ZDJC_CGQ)){
            return R.drawable.icon_cgq_blue;
        } else if(type.equals(Constants.CLZS_CGQ)){
            return R.drawable.icon_cgq_blue;
        } else if(type.equals(Constants.SJ_BSQ)){
            return R.drawable.icon_bsq_blue;
        } else if(type.equals(Constants.YM_CSY)){
            return R.drawable.icon_bsq_blue;
        } else if(type.equals(Constants.YW_CGQ)){
            return R.drawable.icon_cgq_blue;
        } else if(type.equals(Constants.SK645)){
            return R.drawable.icon_dlq_blue;
        } else if(type.equals(Constants.ZS_CGQ)){
            return R.drawable.icon_cgq_blue;
        } else {
            return  -1;
        }
    }

    /**
     * 根据设备类型获取类型名称
     * @param type
     * @return
     */
    public static String getNameOfType(String type){
        switch (type){
            case "wsdcgq":return "温湿度\n传感器";
            case "zscgq":return "噪声\n传感器";
            case "ywcgq":return "烟雾\n传感器";
            case "sjcgq":return "水浸\n变送器";
            case "bpq":return "变频器";
//            case "dlq":return "断路器";
            case "clzscgq":return "齿轮转速\n传感器";
            case "zdjccgq":return "震动监测\n传感器";
            case "ymcsy":return "液面\n测试仪";
            case "sk645":return "塑壳645";
            case "jcq":return "接触器";
            default:return "";
        }
    }

    /**
     * 根据设备类型获取操作按钮组
     * @param deviceType
     * @return
     */
    public static String[] getOperationButtonsByType(String deviceType){
        switch (deviceType){
            case "wsdcgq":return new String[]{"修改名称","删除","遥测"};
            case "zscgq":return new String[]{"修改名称","删除","遥测"};
            case "ywcgq":return new String[]{"修改名称","删除","遥测"};
            case "sjcgq":return new String[]{"修改名称","删除","遥测"};
            case "bpq":return new String[]{"修改名称","删除","遥测","正转运行","反转运行","停机","频率设置"};
            case "dlq":return new String[]{"修改名称","删除","遥测"};
            case "clzscgq":return new String[]{"修改名称","删除","遥测"};
            case "zdjccgq":return new String[]{"修改名称","删除","遥测"};
            case "ymcsy":return new String[]{"修改名称","删除","遥测"};
            case "sk645":return new String[]{"修改名称","删除","遥测","分闸","合闸"};
            case "jcq":return new String[]{"修改名称","删除","读取"};
            default:return new String[]{"修改名称","删除","遥测"};
        }
    }

    /**
     * 根据设备类型获取设备状态类型
     * @param deviceType
     * @return
     */
    public static Map<String, Object>[] getDeviceStatusByType(String deviceType){
        switch (deviceType){
            case "wsdcgq":
                Map<String, Object>[] wsdcgqArray = new HashMap[3];
                wsdcgqArray[0] = new HashMap<>();
                wsdcgqArray[0].put("title", "通道");
                wsdcgqArray[0].put("resource_id", R.drawable.icon_channel);
                wsdcgqArray[0].put("tag", "td");

                wsdcgqArray[1] = new HashMap<>();
                wsdcgqArray[1].put("title", "温度(°C)");
                wsdcgqArray[1].put("resource_id", R.drawable.icon_temp);
                wsdcgqArray[1].put("tag", "wd");

                wsdcgqArray[2] = new HashMap<>();
                wsdcgqArray[2].put("title", "湿度(°C)");
                wsdcgqArray[2].put("resource_id", R.drawable.icon_shidu);
                wsdcgqArray[2].put("tag", "sd");
                return wsdcgqArray;
            case "ywcgq":
                Map<String, Object>[] ywcgqArray = new HashMap[3];

                ywcgqArray[0] = new HashMap<>();
                ywcgqArray[0].put("title", "通道");
                ywcgqArray[0].put("resource_id", R.drawable.icon_channel);
                ywcgqArray[0].put("tag", "td");

                ywcgqArray[1] = new HashMap<>();
                ywcgqArray[1].put("title", "报警器状态");
                ywcgqArray[1].put("resource_id", R.drawable.icon_baojing);
                ywcgqArray[1].put("tag", "bjQstatus");

                ywcgqArray[2] = new HashMap<>();
                ywcgqArray[2].put("title", "报警延时");
                ywcgqArray[2].put("resource_id", R.drawable.icon_bj_delay);
                ywcgqArray[2].put("tag", "bjDelayed");
                return ywcgqArray;
            case "zdjccgq":
                Map<String, Object>[] zdjccgqArray = new HashMap[16];
                zdjccgqArray[0] = new HashMap<>();
                zdjccgqArray[0].put("title", "X轴振动速度");
                zdjccgqArray[0].put("resource_id", R.drawable.icon_x);
                zdjccgqArray[0].put("tag", "vx");

                zdjccgqArray[1] = new HashMap<>();
                zdjccgqArray[1].put("title", "Y轴振动速度");
                zdjccgqArray[1].put("resource_id", R.drawable.icon_y);
                zdjccgqArray[1].put("tag", "vy");

                zdjccgqArray[2] = new HashMap<>();
                zdjccgqArray[2].put("title", "Z轴振动速度");
                zdjccgqArray[2].put("resource_id", R.drawable.icon_z);
                zdjccgqArray[2].put("tag", "vz");

                zdjccgqArray[3] = new HashMap<>();
                zdjccgqArray[3].put("title", "X轴角度振动角度");
                zdjccgqArray[3].put("resource_id", R.drawable.icon_x_zdjd);
                zdjccgqArray[3].put("tag", "adx");

                zdjccgqArray[4] = new HashMap<>();
                zdjccgqArray[4].put("title", "Y轴角度振动角度");
                zdjccgqArray[4].put("resource_id", R.drawable.icon_y_zdjd);
                zdjccgqArray[4].put("tag", "ady");

                zdjccgqArray[5] = new HashMap<>();
                zdjccgqArray[5].put("title", "Z轴角度振动角度");
                zdjccgqArray[5].put("resource_id", R.drawable.icon_z_zdjd);
                zdjccgqArray[5].put("tag", "adz");

                zdjccgqArray[6] = new HashMap<>();
                zdjccgqArray[6].put("title", "产品温度(°C)");
                zdjccgqArray[6].put("resource_id", R.drawable.icon_temp);
                zdjccgqArray[6].put("tag", "temp");

                zdjccgqArray[7] = new HashMap<>();
                zdjccgqArray[7].put("title", "X轴振动位移");
                zdjccgqArray[7].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[7].put("tag", "dx");

                zdjccgqArray[8] = new HashMap<>();
                zdjccgqArray[8].put("title", "Y轴振动位移");
                zdjccgqArray[8].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[8].put("tag", "dy");

                zdjccgqArray[9] = new HashMap<>();
                zdjccgqArray[9].put("title", "Z轴振动位移");
                zdjccgqArray[9].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[9].put("tag", "dz");

                zdjccgqArray[10] = new HashMap<>();
                zdjccgqArray[10].put("title", "X轴振动频率");
                zdjccgqArray[10].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[10].put("tag", "hzx");

                zdjccgqArray[11] = new HashMap<>();
                zdjccgqArray[11].put("title", "Y轴振动频率");
                zdjccgqArray[11].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[11].put("tag", "hzy");

                zdjccgqArray[12] = new HashMap<>();
                zdjccgqArray[12].put("title", "Z轴振动频率");
                zdjccgqArray[12].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[12].put("tag", "hzz");

                zdjccgqArray[13] = new HashMap<>();
                zdjccgqArray[13].put("title", "X轴振动位移(高速模式)");
                zdjccgqArray[13].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[13].put("tag", "fdnfx");

                zdjccgqArray[14] = new HashMap<>();
                zdjccgqArray[14].put("title", "Y轴振动位移(高速模式)");
                zdjccgqArray[14].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[14].put("tag", "fdnfy");

                zdjccgqArray[15] = new HashMap<>();
                zdjccgqArray[15].put("title", "Z轴振动位移(高速模式)");
                zdjccgqArray[15].put("resource_id", R.drawable.icon_zdwy);
                zdjccgqArray[15].put("tag", "fdnfz");

                return zdjccgqArray;
            case "zscgq":
                Map<String, Object>[] zscgqArray = new HashMap[2];
                zscgqArray[0] = new HashMap<>();
                zscgqArray[0].put("title", "通道");
                zscgqArray[0].put("resource_id", R.drawable.icon_channel);
                zscgqArray[0].put("tag", "td");

                zscgqArray[1] = new HashMap<>();
                zscgqArray[1].put("title", "噪声值");
                zscgqArray[1].put("resource_id", R.drawable.icon_zsz);
                zscgqArray[1].put("tag", "zSvalue");
                return zscgqArray;
            case "sjcgq":
                Map<String, Object>[] sjbsqArray = new HashMap[3];
                sjbsqArray[0] = new HashMap<>();
                sjbsqArray[0].put("title", "通道");
                sjbsqArray[0].put("resource_id", R.drawable.icon_channel);
                sjbsqArray[0].put("tag", "td");

                sjbsqArray[1] = new HashMap<>();
                sjbsqArray[1].put("title", "水浸状态1");
                sjbsqArray[1].put("resource_id", R.drawable.icon_sjzt);
                sjbsqArray[1].put("tag", "D");

                sjbsqArray[2] = new HashMap<>();
                sjbsqArray[2].put("title", "水浸状态2");
                sjbsqArray[2].put("resource_id", R.drawable.icon_sjzt);
                sjbsqArray[2].put("tag", "sjStatus2");
                return sjbsqArray;

            case "clzscgq":
                Map<String, Object>[] clzscgqArray = new HashMap[14];
                clzscgqArray[0] = new HashMap<>();
                clzscgqArray[0].put("title", "IN1转速值");
                clzscgqArray[0].put("resource_id", R.drawable.icon_zhuansuzhi);
                clzscgqArray[0].put("tag", "iN1Zsz");

                clzscgqArray[1] = new HashMap<>();
                clzscgqArray[1].put("title", "IN2转速值");
                clzscgqArray[1].put("resource_id", R.drawable.icon_zhuansuzhi);
                clzscgqArray[1].put("tag", "iN2Zsz");

//                clzscgqArray[2] = new HashMap<>();
//                clzscgqArray[2].put("title", "IN1转速原始值");
//                clzscgqArray[2].put("resource_id", R.drawable.icon_zhuansuzhi);
//                clzscgqArray[2].put("tag", "iN1YZsz");
//
//                clzscgqArray[3] = new HashMap<>();
//                clzscgqArray[3].put("title", "IN2转速原始值");
//                clzscgqArray[3].put("resource_id", R.drawable.icon_zhuansuzhi);
//                clzscgqArray[3].put("tag", "iN2YZsz");

                clzscgqArray[2] = new HashMap<>();
                clzscgqArray[2].put("title", "开关量输入状态1");
                clzscgqArray[2].put("resource_id", R.drawable.icon_kgsrzt);
                clzscgqArray[2].put("tag", "kglsrzt1");

                clzscgqArray[3] = new HashMap<>();
                clzscgqArray[3].put("title", "开关量输入状态2");
                clzscgqArray[3].put("resource_id", R.drawable.icon_kgsrzt);
                clzscgqArray[3].put("tag", "kglsrzt2");

                clzscgqArray[4] = new HashMap<>();
                clzscgqArray[4].put("title", "IN1测量速度选择");
                clzscgqArray[4].put("resource_id", R.drawable.icon_clsdxz);
                clzscgqArray[4].put("tag", "iN1Clsd");

                clzscgqArray[5] = new HashMap<>();
                clzscgqArray[5].put("title", "IN1显示小数位");
                clzscgqArray[5].put("resource_id", R.drawable.icon_plxs);
                clzscgqArray[5].put("tag", "iN1Xsw");

                clzscgqArray[6] = new HashMap<>();
                clzscgqArray[6].put("title", "IN1每转感应点数");
                clzscgqArray[6].put("resource_id", R.drawable.icon_plxs);
                clzscgqArray[6].put("tag", "iN1Mzgyds");

                clzscgqArray[7] = new HashMap<>();
                clzscgqArray[7].put("title", "IN1倍率小数位");
                clzscgqArray[7].put("resource_id", R.drawable.icon_plxs);
                clzscgqArray[7].put("tag", "iN1Blxsw");

                clzscgqArray[8] = new HashMap<>();
                clzscgqArray[8].put("title", "IN1倍率值");
                clzscgqArray[8].put("resource_id", R.drawable.icon_blz);
                clzscgqArray[8].put("tag", "iN1Blz");

                clzscgqArray[9] = new HashMap<>();
                clzscgqArray[9].put("title", "IN2测量速度选择");
                clzscgqArray[9].put("resource_id", R.drawable.icon_clsdxz);
                clzscgqArray[9].put("tag", "iN2Clsd");

                clzscgqArray[10] = new HashMap<>();
                clzscgqArray[10].put("title", "IN2显示小数位");
                clzscgqArray[10].put("resource_id", R.drawable.icon_plxs);
                clzscgqArray[10].put("tag", "iN2Xsw");

                clzscgqArray[11] = new HashMap<>();
                clzscgqArray[11].put("title", "IN2每转感应点数");
                clzscgqArray[11].put("resource_id", R.drawable.icon_plxs);
                clzscgqArray[11].put("tag", "iN2Mzgyds");

                clzscgqArray[12] = new HashMap<>();
                clzscgqArray[12].put("title", "IN2倍率小数位");
                clzscgqArray[12].put("resource_id", R.drawable.icon_plxs);
                clzscgqArray[12].put("tag", "iN2Blxsw");

                clzscgqArray[13] = new HashMap<>();
                clzscgqArray[13].put("title", "IN2倍率值");
                clzscgqArray[13].put("resource_id", R.drawable.icon_blz);
                clzscgqArray[13].put("tag", "iN2Blz");
                return clzscgqArray;
            case "ymcsy":
                Map<String, Object>[] ymcsyArray = new HashMap[4];
                ymcsyArray[0] = new HashMap<>();
                ymcsyArray[0].put("title", "单点套压(Mpa)");
                ymcsyArray[0].put("resource_id", R.drawable.icon_ddty);
                ymcsyArray[0].put("tag", "ddty");

                ymcsyArray[1] = new HashMap<>();
                ymcsyArray[1].put("title", "单点声速(m/s)");
                ymcsyArray[1].put("resource_id", R.drawable.icon_ddss);
                ymcsyArray[1].put("tag", "ddsy");

                ymcsyArray[2] = new HashMap<>();
                ymcsyArray[2].put("title", "单点液面深度(米)");
                ymcsyArray[2].put("resource_id", R.drawable.icon_ymsd);
                ymcsyArray[2].put("tag", "ddymsd");

                ymcsyArray[3] = new HashMap<>();
                ymcsyArray[3].put("title", "测试时间");
                ymcsyArray[3].put("resource_id", R.drawable.icon_sjc);
                ymcsyArray[3].put("tag", "Cssk");

                return ymcsyArray;
            case "bpq":
                Map<String, Object>[] bpqArray = new HashMap[26];
                bpqArray[0] = new HashMap<>();
                bpqArray[0].put("title", "运行频率");
                bpqArray[0].put("resource_id", R.drawable.icon_plzs);
                bpqArray[0].put("tag", "yxpl");

                bpqArray[1] = new HashMap<>();
                bpqArray[1].put("title", "设定频率");
                bpqArray[1].put("resource_id", R.drawable.icon_plzs);
                bpqArray[1].put("tag", "sdpl");

                bpqArray[2] = new HashMap<>();
                bpqArray[2].put("title", "母线电压(V)");
                bpqArray[2].put("resource_id", R.drawable.icon_mxdy);
                bpqArray[2].put("tag", "mxdy");

                bpqArray[3] = new HashMap<>();
                bpqArray[3].put("title", "输出电压(V)");
                bpqArray[3].put("resource_id", R.drawable.icon_voltage);
                bpqArray[3].put("tag", "scdy");

                bpqArray[4] = new HashMap<>();
                bpqArray[4].put("title", "输出电流(A)");
                bpqArray[4].put("resource_id", R.drawable.icon_current);
                bpqArray[4].put("tag", "scdl");

                bpqArray[5] = new HashMap<>();
                bpqArray[5].put("title", "运行转速");
                bpqArray[5].put("resource_id", R.drawable.icon_zhuansuzhi);
                bpqArray[5].put("tag", "yxzs");

                bpqArray[6] = new HashMap<>();
                bpqArray[6].put("title", "输出功率(KW)");
                bpqArray[6].put("resource_id", R.drawable.icon_power);
                bpqArray[6].put("tag", "scgl");

                bpqArray[7] = new HashMap<>();
                bpqArray[7].put("title", "输出转矩");
                bpqArray[7].put("resource_id", R.drawable.icon_zhuanju);
                bpqArray[7].put("tag", "sczj");

                bpqArray[8] = new HashMap<>();
                bpqArray[8].put("title", "闭环设定");
                bpqArray[8].put("resource_id", R.drawable.icon_bhsd);
                bpqArray[8].put("tag", "bhsd");

                bpqArray[9] = new HashMap<>();
                bpqArray[9].put("title", "闭环反馈");
                bpqArray[9].put("resource_id", R.drawable.icon_bhfk);
                bpqArray[9].put("tag", "bhfk");

                bpqArray[10] = new HashMap<>();
                bpqArray[10].put("title", "输入IO状态");
                bpqArray[10].put("resource_id", R.drawable.icon_srzt);
                bpqArray[10].put("tag", "sriozt");

                bpqArray[11] = new HashMap<>();
                bpqArray[11].put("title", "输出IO状态");
                bpqArray[11].put("resource_id", R.drawable.icon_srzt);
                bpqArray[11].put("tag", "sciozt");

                bpqArray[12] = new HashMap<>();
                bpqArray[12].put("title", "模拟量输入1");
                bpqArray[12].put("resource_id", R.drawable.icon_mnlsr);
                bpqArray[12].put("tag", "mnlsr1");

                bpqArray[13] = new HashMap<>();
                bpqArray[13].put("title", "模拟量输入2");
                bpqArray[13].put("resource_id", R.drawable.icon_mnlsr);
                bpqArray[13].put("tag", "mnlsr2");

                bpqArray[14] = new HashMap<>();
                bpqArray[14].put("title", "模拟量输入3");
                bpqArray[14].put("resource_id", R.drawable.icon_mnlsr);
                bpqArray[14].put("tag", "mnlsr3");

                bpqArray[15] = new HashMap<>();
                bpqArray[15].put("title", "模拟量输入4");
                bpqArray[15].put("resource_id", R.drawable.icon_mnlsr);
                bpqArray[15].put("tag", "mnlsr4");

                bpqArray[16] = new HashMap<>();
                bpqArray[16].put("title", "读高速脉冲1输入");
                bpqArray[16].put("resource_id", R.drawable.icon_dmcmnl);
                bpqArray[16].put("tag", "dgsmc1sr");

                bpqArray[17] = new HashMap<>();
                bpqArray[17].put("title", "读高速脉冲2输入");
                bpqArray[17].put("resource_id", R.drawable.icon_dmcmnl);
                bpqArray[17].put("tag", "dgsmc2sr");

                bpqArray[18] = new HashMap<>();
                bpqArray[18].put("title", "读多段速当前段数");
                bpqArray[18].put("resource_id", R.drawable.icon_dddqds);
                bpqArray[18].put("tag", "ddsdqds");

                bpqArray[19] = new HashMap<>();
                bpqArray[19].put("title", "外部长度值");
                bpqArray[19].put("resource_id", R.drawable.icon_cdz);
                bpqArray[19].put("tag", "wbcdz");

                bpqArray[20] = new HashMap<>();
                bpqArray[20].put("title", "外部计数值");
                bpqArray[20].put("resource_id", R.drawable.icon_jsz);
                bpqArray[20].put("tag", "wbjsz");

                bpqArray[21] = new HashMap<>();
                bpqArray[21].put("title", "转矩设定值");
                bpqArray[21].put("resource_id", R.drawable.icon_zhuanju);
                bpqArray[21].put("tag", "zjsdz");

                bpqArray[22] = new HashMap<>();
                bpqArray[22].put("title", "变频器状态字1");
                bpqArray[22].put("resource_id", R.drawable.icon_kgsrzt);
                bpqArray[22].put("tag", "BpqZtz1Text");

                bpqArray[23] = new HashMap<>();
                bpqArray[23].put("title", "变频器状态字2");
                bpqArray[23].put("resource_id", R.drawable.icon_kgsrzt);
                bpqArray[23].put("tag", "bpqZtz2");

                bpqArray[24] = new HashMap<>();
                bpqArray[24].put("title", "变频器故障代码");
                bpqArray[24].put("resource_id", R.drawable.icon_gzdm);
                bpqArray[24].put("tag", "BpqgzdmText");

                bpqArray[25] = new HashMap<>();
                bpqArray[25].put("title", "变频器识别代码");
                bpqArray[25].put("resource_id", R.drawable.icon_gzdm);
                bpqArray[25].put("tag", "bpqsbdm");
                return bpqArray;

            case "dlq":
                Map<String, Object>[] dlqArray = new HashMap[69];

                dlqArray[0] = new HashMap<>();
                dlqArray[0].put("title", "在线状态");
                dlqArray[0].put("resource_id", R.drawable.icon_online);
                dlqArray[0].put("tag", "DeviceStatus");

                dlqArray[1] = new HashMap<>();
                dlqArray[1].put("title", "通道");
                dlqArray[1].put("resource_id", R.drawable.icon_channel);
                dlqArray[1].put("tag", "chl");

                dlqArray[2] = new HashMap<>();
                dlqArray[2].put("title", "设备SN");
                dlqArray[2].put("resource_id", R.drawable.icon_sn);
                dlqArray[2].put("tag", "sn");

                dlqArray[3] = new HashMap<>();
                dlqArray[3].put("title", "IMSI");
                dlqArray[3].put("resource_id", R.drawable.icon_imei);
                dlqArray[3].put("tag", "IMSI");

                dlqArray[4] = new HashMap<>();
                dlqArray[4].put("title", "IMEI");
                dlqArray[4].put("resource_id", R.drawable.icon_imei);
                dlqArray[4].put("tag", "IMEI");

                dlqArray[5] = new HashMap<>();
                dlqArray[5].put("title", "SIM卡号");
                dlqArray[5].put("resource_id", R.drawable.icon_sim);
                dlqArray[5].put("tag", "ccid");

                dlqArray[6] = new HashMap<>();
                dlqArray[6].put("title", "集中器核校验");
                dlqArray[6].put("resource_id", R.drawable.icon_jzqhjy);
                dlqArray[6].put("tag", "hejiaoyanJ");

                dlqArray[7] = new HashMap<>();
                dlqArray[7].put("title", "开关状态");
                dlqArray[7].put("resource_id", R.drawable.icon_fz);
                dlqArray[7].put("tag", "kaiguan");

                dlqArray[8] = new HashMap<>();
                dlqArray[8].put("title", "总路异常码");
                dlqArray[8].put("resource_id", R.drawable.icon_ycm);
                dlqArray[8].put("tag", "zongluyichangma");

                dlqArray[9] = new HashMap<>();
                dlqArray[9].put("title", "A相异常码");
                dlqArray[9].put("resource_id", R.drawable.icon_ycm);
                dlqArray[9].put("tag", "axiangyichangma");

                dlqArray[10] = new HashMap<>();
                dlqArray[10].put("title", "B相异常码");
                dlqArray[10].put("resource_id", R.drawable.icon_ycm);
                dlqArray[10].put("tag", "bxiangyichangma");

                dlqArray[11] = new HashMap<>();
                dlqArray[11].put("title", "C相异常码");
                dlqArray[11].put("resource_id", R.drawable.icon_ycm);
                dlqArray[11].put("tag", "cxiangyichangma");

                dlqArray[12] = new HashMap<>();
                dlqArray[12].put("title", "零线异常码");
                dlqArray[12].put("resource_id", R.drawable.icon_ycm);
                dlqArray[12].put("tag", "lingxianyichangma");

                dlqArray[13] = new HashMap<>();
                dlqArray[13].put("title", "总电压(V)");
                dlqArray[13].put("resource_id", R.drawable.icon_voltage);
                dlqArray[13].put("tag", "zongdianya");

                dlqArray[14] = new HashMap<>();
                dlqArray[14].put("title", "ab相电压(V)");
                dlqArray[14].put("resource_id", R.drawable.icon_voltage);
                dlqArray[14].put("tag", "abdianya");

                dlqArray[15] = new HashMap<>();
                dlqArray[15].put("title", "ac相电压(V)");
                dlqArray[15].put("resource_id", R.drawable.icon_voltage);
                dlqArray[15].put("tag", "acdianya");

                dlqArray[16] = new HashMap<>();
                dlqArray[16].put("title", "bc相电压(V)");
                dlqArray[16].put("resource_id", R.drawable.icon_voltage);
                dlqArray[16].put("tag", "bcdianya");

                dlqArray[17] = new HashMap<>();
                dlqArray[17].put("title", "总电流(A)");
                dlqArray[17].put("resource_id", R.drawable.icon_current);
                dlqArray[17].put("tag", "zongdianliu");

                dlqArray[18] = new HashMap<>();
                dlqArray[18].put("title", "总电量(KWH)");
                dlqArray[18].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[18].put("tag", "zongdianliang");

                dlqArray[19] = new HashMap<>();
                dlqArray[19].put("title", "总有功功率(KW)");
                dlqArray[19].put("resource_id", R.drawable.icon_yggl);
                dlqArray[19].put("tag", "zongyougonggonglv");

                dlqArray[20] = new HashMap<>();
                dlqArray[20].put("title", "总无功功率(KW)");
                dlqArray[20].put("resource_id", R.drawable.icon_wggl);
                dlqArray[20].put("tag", "zongwugonggonglv");

                dlqArray[21] = new HashMap<>();
                dlqArray[21].put("title", "总功率因数");
                dlqArray[21].put("resource_id", R.drawable.icon_glys);
                dlqArray[21].put("tag", "zonggonglvyinshu");

                dlqArray[22] = new HashMap<>();
                dlqArray[22].put("title", "总漏电");
                dlqArray[22].put("resource_id", R.drawable.icon_leakage);
                dlqArray[22].put("tag", "zongloudian");

                dlqArray[23] = new HashMap<>();
                dlqArray[23].put("title", "A相电压(V)");
                dlqArray[23].put("resource_id", R.drawable.icon_voltage);
                dlqArray[23].put("tag", "axiangdianya");

                dlqArray[24] = new HashMap<>();
                dlqArray[24].put("title", "A相电流(A)");
                dlqArray[24].put("resource_id", R.drawable.icon_current);
                dlqArray[24].put("tag", "axiangdianliu");

                dlqArray[25] = new HashMap<>();
                dlqArray[25].put("title", "A相有功功率(KW)");
                dlqArray[25].put("resource_id", R.drawable.icon_yggl);
                dlqArray[25].put("tag", "axiangyougonggonglv");

                dlqArray[26] = new HashMap<>();
                dlqArray[26].put("title", "A相无功功率(KW)");
                dlqArray[26].put("resource_id", R.drawable.icon_wggl);
                dlqArray[26].put("tag", "axiangwugonggonglv");

                dlqArray[27] = new HashMap<>();
                dlqArray[27].put("title", "A相功率因数");
                dlqArray[27].put("resource_id", R.drawable.icon_glys);
                dlqArray[27].put("tag", "axianggonglvyinshu");

                dlqArray[28] = new HashMap<>();
                dlqArray[28].put("title", "A相温度(°C)");
                dlqArray[28].put("resource_id", R.drawable.icon_temp);
                dlqArray[28].put("tag", "axiangwendu");

                dlqArray[29] = new HashMap<>();
                dlqArray[29].put("title", "B相电压(V)");
                dlqArray[29].put("resource_id", R.drawable.icon_voltage);
                dlqArray[29].put("tag", "bxiangdianya");

                dlqArray[30] = new HashMap<>();
                dlqArray[30].put("title", "B相电流(A)");
                dlqArray[30].put("resource_id", R.drawable.icon_current);
                dlqArray[30].put("tag", "bxiangdianliu");

                dlqArray[31] = new HashMap<>();
                dlqArray[31].put("title", "B相有功功率(KW)");
                dlqArray[31].put("resource_id", R.drawable.icon_yggl);
                dlqArray[31].put("tag", "bxiangyougonggonglv");

                dlqArray[32] = new HashMap<>();
                dlqArray[32].put("title", "B相无功功率(KW)");
                dlqArray[32].put("resource_id", R.drawable.icon_wggl);
                dlqArray[32].put("tag", "bxiangwugonggonglv");

                dlqArray[33] = new HashMap<>();
                dlqArray[33].put("title", "B相功率因数");
                dlqArray[33].put("resource_id", R.drawable.icon_glys);
                dlqArray[33].put("tag", "bxianggonglvyinshu");

                dlqArray[34] = new HashMap<>();
                dlqArray[34].put("title", "B相温度(°C)");
                dlqArray[34].put("resource_id", R.drawable.icon_temp);
                dlqArray[34].put("tag", "bxiangwendu");

                dlqArray[35] = new HashMap<>();
                dlqArray[35].put("title", "C相电压(V)");
                dlqArray[35].put("resource_id", R.drawable.icon_voltage);
                dlqArray[35].put("tag", "cxiangdianya");

                dlqArray[36] = new HashMap<>();
                dlqArray[36].put("title", "C相电流(A)");
                dlqArray[36].put("resource_id", R.drawable.icon_current);
                dlqArray[36].put("tag", "cxiangdianliu");

                dlqArray[37] = new HashMap<>();
                dlqArray[37].put("title", "C相有功功率(KW)");
                dlqArray[37].put("resource_id", R.drawable.icon_yggl);
                dlqArray[37].put("tag", "cxiangyougonggonglv");

                dlqArray[38] = new HashMap<>();
                dlqArray[38].put("title", "C相无功功率(KW)");
                dlqArray[38].put("resource_id", R.drawable.icon_wggl);
                dlqArray[38].put("tag", "cxiangwugonggonglv");

                dlqArray[39] = new HashMap<>();
                dlqArray[39].put("title", "C相功率因数");
                dlqArray[39].put("resource_id", R.drawable.icon_glys);
                dlqArray[39].put("tag", "cxianggonglvyinshu");

                dlqArray[40] = new HashMap<>();
                dlqArray[40].put("title", "C相温度(°C)");
                dlqArray[40].put("resource_id", R.drawable.icon_temp);
                dlqArray[40].put("tag", "cxiangwendu");

                dlqArray[41] = new HashMap<>();
                dlqArray[41].put("title", "N线电流(A)");
                dlqArray[41].put("resource_id", R.drawable.icon_current);
                dlqArray[41].put("tag", "nxiandianliu");

                dlqArray[42] = new HashMap<>();
                dlqArray[42].put("title", "N线温度(°C)");
                dlqArray[42].put("resource_id", R.drawable.icon_temp);
                dlqArray[42].put("tag", "nxianwendu");

                dlqArray[43] = new HashMap<>();
                dlqArray[43].put("title", "电弧异常次数");
                dlqArray[43].put("resource_id", R.drawable.icon_exception_num);
                dlqArray[43].put("tag", "dhYc");

                dlqArray[44] = new HashMap<>();
                dlqArray[44].put("title", "信号强度");
                dlqArray[44].put("resource_id", R.drawable.icon_xhqd);
                dlqArray[44].put("tag", "xinhaoqiangdu");

                dlqArray[45] = new HashMap<>();
                dlqArray[45].put("title", "微断在线台数");
                dlqArray[45].put("resource_id", R.drawable.icon_wdzxts);
                dlqArray[45].put("tag", "weiduanzaixiantaishu");

                dlqArray[46] = new HashMap<>();
                dlqArray[46].put("title", "参考信号功率(KW)");
                dlqArray[46].put("resource_id", R.drawable.icon_xhckgl);
                dlqArray[46].put("tag", "rsrp");

                dlqArray[47] = new HashMap<>();
                dlqArray[47].put("title", "接收信号质量");
                dlqArray[47].put("resource_id", R.drawable.icon_jsxhzl);
                dlqArray[47].put("tag", "rsrq");

                dlqArray[48] = new HashMap<>();
                dlqArray[48].put("title", "零序电压(V)");
                dlqArray[48].put("resource_id", R.drawable.icon_voltage);
                dlqArray[48].put("tag", "lingxudianya");

                dlqArray[49] = new HashMap<>();
                dlqArray[49].put("title", "总电量L(KWH)");
                dlqArray[49].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[49].put("tag", "zongdianliangL");

                dlqArray[50] = new HashMap<>();
                dlqArray[50].put("title", "总电量H(KWH)");
                dlqArray[50].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[50].put("tag", "zongdianliangH");

                dlqArray[51] = new HashMap<>();
                dlqArray[51].put("title", "开关总次数");
                dlqArray[51].put("resource_id", R.drawable.icon_kgzcs);
                dlqArray[51].put("tag", "kaiguandongzuocishu");

                dlqArray[52] = new HashMap<>();
                dlqArray[52].put("title", "设备当前时间");
                dlqArray[52].put("resource_id", R.drawable.icon_sbdqsj);
                dlqArray[52].put("tag", "deviceDqTime");

                dlqArray[53] = new HashMap<>();
                dlqArray[53].put("title", "信号信噪比");
                dlqArray[53].put("resource_id", R.drawable.icon_xzb);
                dlqArray[53].put("tag", "snr");

                dlqArray[54] = new HashMap<>();
                dlqArray[54].put("title", "物理小区标识");
                dlqArray[54].put("resource_id", R.drawable.icon_wlxqbs);
                dlqArray[54].put("tag", "pci");

                dlqArray[55] = new HashMap<>();
                dlqArray[55].put("title", "信号覆盖等级");
                dlqArray[55].put("resource_id", R.drawable.icon_xhfgdj);
                dlqArray[55].put("tag", "ecl");

                dlqArray[56] = new HashMap<>();
                dlqArray[56].put("title", "小区id");
                dlqArray[56].put("resource_id", R.drawable.icon_wlxqbs);
                dlqArray[56].put("tag", "cellid");

                dlqArray[57] = new HashMap<>();
                dlqArray[57].put("title", "死锁状态");
                dlqArray[57].put("resource_id", R.drawable.icon_sisuo);
                dlqArray[57].put("tag", "sszt");

                dlqArray[58] = new HashMap<>();
                dlqArray[58].put("title", "闭锁状态");
                dlqArray[58].put("resource_id", R.drawable.icon_bisuo);
                dlqArray[58].put("tag", "bsStatus");

                dlqArray[59] = new HashMap<>();
                dlqArray[59].put("title", "闭锁原因");
                dlqArray[59].put("resource_id", R.drawable.icon_bisuo);
                dlqArray[59].put("tag", "bsYuanyi");

                dlqArray[60] = new HashMap<>();
                dlqArray[60].put("title", "A相电量(KWH)");
                dlqArray[60].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[60].put("tag", "axiangDianLian");

                dlqArray[61] = new HashMap<>();
                dlqArray[61].put("title", "B相电量(KWH)");
                dlqArray[61].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[61].put("tag", "bxiangDianLian");

                dlqArray[62] = new HashMap<>();
                dlqArray[62].put("title", "C相电量(KWH)");
                dlqArray[62].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[62].put("tag", "cxiangDianLian");

                dlqArray[63] = new HashMap<>();
                dlqArray[63].put("title", "正向总有功电量(KWH)");
                dlqArray[63].put("resource_id", R.drawable.icon_ygdl);
                dlqArray[63].put("tag", "zhengXiangZygdl");

                dlqArray[64] = new HashMap<>();
                dlqArray[64].put("title", "正向总无功电量(KWH)");
                dlqArray[64].put("resource_id", R.drawable.icon_wgdl);
                dlqArray[64].put("tag", "zhengXiangZwgdl");

                dlqArray[65] = new HashMap<>();
                dlqArray[65].put("title", "反向总有功电量(KWH)");
                dlqArray[65].put("resource_id", R.drawable.icon_ygdl);
                dlqArray[65].put("tag", "fanXiangZygdl");

                dlqArray[66] = new HashMap<>();
                dlqArray[66].put("title", "反向总无功电量(KWH)");
                dlqArray[66].put("resource_id", R.drawable.icon_wgdl);
                dlqArray[66].put("tag", "fanXiangZwgdl");

                dlqArray[67] = new HashMap<>();
                dlqArray[67].put("title", "频率");
                dlqArray[67].put("resource_id", R.drawable.icon_plzs);
                dlqArray[67].put("tag", "pinLv");

                dlqArray[68] = new HashMap<>();
                dlqArray[68].put("title", "上一次总电量(KWH)");
                dlqArray[68].put("resource_id", R.drawable.icon_total_electricity);
                dlqArray[68].put("tag", "zongdianliangLast");
                return dlqArray;
            case "sk645":
                Map<String, Object>[] sk645Array = new HashMap[20];
                sk645Array[0] = new HashMap<>();
                sk645Array[0].put("title", "闸位状态");
                sk645Array[0].put("resource_id", R.drawable.icon_normal);
                sk645Array[0].put("tag", "KaiguanStatus");

                sk645Array[1] = new HashMap<>();
                sk645Array[1].put("title", "A相电压(V)");
                sk645Array[1].put("resource_id", R.drawable.icon_voltage);
                sk645Array[1].put("tag", "DqAxiangDianYa");

                sk645Array[2] = new HashMap<>();
                sk645Array[2].put("title", "B相电压(V)");
                sk645Array[2].put("resource_id", R.drawable.icon_voltage);
                sk645Array[2].put("tag", "DqBxiangDianYa");

                sk645Array[3] = new HashMap<>();
                sk645Array[3].put("title", "C相电压(V)");
                sk645Array[3].put("resource_id", R.drawable.icon_voltage);
                sk645Array[3].put("tag", "DqCxiangDianYa");

                sk645Array[4] = new HashMap<>();
                sk645Array[4].put("title", "A相电流(A)");
                sk645Array[4].put("resource_id", R.drawable.icon_current);
                sk645Array[4].put("tag", "DqAxiangDianLiu");

                sk645Array[5] = new HashMap<>();
                sk645Array[5].put("title", "B相电流(A)");
                sk645Array[5].put("resource_id", R.drawable.icon_current);
                sk645Array[5].put("tag", "DqBxiangDianLiu");

                sk645Array[6] = new HashMap<>();
                sk645Array[6].put("title", "C相电流(A)");
                sk645Array[6].put("resource_id", R.drawable.icon_current);
                sk645Array[6].put("tag", "DqCxiangDianLiu");

                sk645Array[7] = new HashMap<>();
                sk645Array[7].put("title", "瞬时总有功功率(KW)");
                sk645Array[7].put("resource_id", R.drawable.icon_yggl);
                sk645Array[7].put("tag", "SsZongYouGongLv");

                sk645Array[8] = new HashMap<>();
                sk645Array[8].put("title", "瞬时A有功功率(KW)");
                sk645Array[8].put("resource_id", R.drawable.icon_yggl);
                sk645Array[8].put("tag", "SsAYouGongLv");

                sk645Array[9] = new HashMap<>();
                sk645Array[9].put("title", "瞬时B有功功率(KW)");
                sk645Array[9].put("resource_id", R.drawable.icon_yggl);
                sk645Array[9].put("tag", "SsBYouGongLv");

                sk645Array[10] = new HashMap<>();
                sk645Array[10].put("title", "瞬时C有功功率(KW)");
                sk645Array[10].put("resource_id", R.drawable.icon_yggl);
                sk645Array[10].put("tag", "SsCYouGongLv");

                sk645Array[11] = new HashMap<>();
                sk645Array[11].put("title", "瞬时总无功功率(KW)");
                sk645Array[1].put("resource_id", R.drawable.icon_glys);
                sk645Array[11].put("tag", "SsZongWuGongLv");

                sk645Array[12] = new HashMap<>();
                sk645Array[12].put("title", "瞬时A无功功率(KW)");
                sk645Array[12].put("resource_id", R.drawable.icon_wggl);
                sk645Array[12].put("tag", "SsAWuGongLv");

                sk645Array[13] = new HashMap<>();
                sk645Array[13].put("title", "瞬时B无功功率(KW)");
                sk645Array[13].put("resource_id", R.drawable.icon_wggl);
                sk645Array[13].put("tag", "SsBWuGongLv");

                sk645Array[14] = new HashMap<>();
                sk645Array[14].put("title", "瞬时C无功功率(KW)");
                sk645Array[14].put("resource_id", R.drawable.icon_wggl);
                sk645Array[14].put("tag", "SsCWuGongLv");

                sk645Array[15] = new HashMap<>();
                sk645Array[15].put("title", "总功率因数");
                sk645Array[15].put("resource_id", R.drawable.icon_glys);
                sk645Array[15].put("tag", "ZongGongLvYinShu");

                sk645Array[16] = new HashMap<>();
                sk645Array[16].put("title", "A相功率因数");
                sk645Array[16].put("resource_id", R.drawable.icon_glys);
                sk645Array[16].put("tag", "AxiangGongLvYinShu");

                sk645Array[17] = new HashMap<>();
                sk645Array[17].put("title", "B相功率因数");
                sk645Array[17].put("resource_id", R.drawable.icon_glys);
                sk645Array[17].put("tag", "BxiangGongLvYinShu");

                sk645Array[18] = new HashMap<>();
                sk645Array[18].put("title", "C相功率因数");
                sk645Array[18].put("resource_id", R.drawable.icon_glys);
                sk645Array[18].put("tag", "CxiangGongLvYinShu");

                sk645Array[19] = new HashMap<>();
                sk645Array[19].put("title", "总有功电能");
                sk645Array[19].put("resource_id", R.drawable.icon_total_electricity);
                sk645Array[19].put("tag", "ZongYgdn");

                return sk645Array;

            case "jcq":
                Map<String, Object>[] jcqArray = new HashMap[1];
                jcqArray[0] = new HashMap<>();
                jcqArray[0].put("title", "运行状态");
                jcqArray[0].put("resource_id", R.drawable.icon_ddty);
                jcqArray[0].put("tag", "Data");
                return jcqArray;

            default:return new HashMap[0];
        }
    }

    /**
     * 移除Map中首个元素
     * @param originalArray
     * @return
     */
    public static Map<String, Object>[] removeFirstElement(Map<String, Object>[] originalArray) {
        if (originalArray == null || originalArray.length == 0) {
            return originalArray; // 如果原数组为空，直接返回原数组
        }

        // 创建一个新的数组，其长度比原数组少一个
        Map<String, Object>[] newArray = new Map[originalArray.length - 1];

        // 将原数组中的元素（从第二个元素开始）复制到新数组
        System.arraycopy(originalArray, 1, newArray, 0, originalArray.length - 1);

        return newArray;
    }


}
