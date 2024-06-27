package com.hxjt.dqyt.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hxjt.dqyt.app.App;
import com.hxjt.dqyt.bean.HistoryDataBean;
import com.hxjt.dqyt.bean.HistoryDataBean_;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class DBUtils {

    /**
     * 插入实时值
     * @param json
     */
    public static void insert(String json) {

        BoxStore mBoxStore = App.getBoxStore();
        if(mBoxStore == null) return;

        Map<String,Object> map = JsonUtil.toMap(json);
        if(map != null && !map.isEmpty()){
            String msgType = (String) map.get("MsgType");
            if(msgType != null && msgType.equals("1")){

                String inertTime = (String) map.get("CreateTimeStr");
                Log.d("当前插入的数据时间:",inertTime + "");

                Double doubleId = (Double) map.get("Id");

                if(doubleId == null) return;

                Box<HistoryDataBean> historyDataBeanBox = mBoxStore.boxFor(HistoryDataBean.class);

                long id = doubleId.longValue();

                String deviceType = (String) map.get("DeviceType");
                String deviceData = (String) map.get("DeviceData");
                Double doubleDataType = (Double) map.get("DataType");
                String createTimeStr = (String) map.get("CreateTimeStr");
                String createTime = (String) map.get("CreateTime");

                int dataType = 1;

                if(doubleDataType != null) {
                    dataType = doubleDataType.intValue();
                }

                //需要插入到数据库
                HistoryDataBean dataBean = new HistoryDataBean();

                dataBean.setDeviceData(deviceData);
                dataBean.setId(id);
                dataBean.setCreateTimeStr(createTimeStr);
                dataBean.setDataType(dataType);
                dataBean.setCreateTime(createTime);
                dataBean.setDeviceType(deviceType);
                historyDataBeanBox.put(dataBean);
                Log.d("当前共有数据:",historyDataBeanBox.getAll().size() + "条");
            }
        }
    }


    public static List<HistoryDataBean> query(int pageIndex, int pageSize, String startDt, String endDt, String deviceType) {
        BoxStore mBoxStore = App.getBoxStore();
        if (mBoxStore == null) return new ArrayList<>();

        Box<HistoryDataBean> historyDataBeanBox = mBoxStore.boxFor(HistoryDataBean.class);

        // 计算偏移量
        int offset = (pageIndex - 1) * pageSize;

        // 创建查询并设置限制和偏移
        QueryBuilder<HistoryDataBean> queryBuilder = historyDataBeanBox.query();

        // 添加时间筛选条件
        if (startDt != null && !startDt.isEmpty() && endDt != null && !endDt.isEmpty()) {
            queryBuilder
                    .greater(HistoryDataBean_.CreateTime, startDt, QueryBuilder.StringOrder.CASE_SENSITIVE)
                    .less(HistoryDataBean_.CreateTime, endDt, QueryBuilder.StringOrder.CASE_SENSITIVE);
        } else if (startDt != null && !startDt.isEmpty()) {
            queryBuilder.greater(HistoryDataBean_.CreateTime, startDt,QueryBuilder.StringOrder.CASE_SENSITIVE);
        } else if (endDt != null && !endDt.isEmpty()) {
            queryBuilder.less(HistoryDataBean_.CreateTime, endDt, QueryBuilder.StringOrder.CASE_SENSITIVE);
        }

        // 添加设备类型筛选条件
        if (deviceType != null && !deviceType.isEmpty()) {
            queryBuilder.equal(HistoryDataBean_.DeviceType, deviceType, QueryBuilder.StringOrder.CASE_SENSITIVE);
        }

        queryBuilder.orderDesc(HistoryDataBean_.id);

        // 构建查询
        Query<HistoryDataBean> query = queryBuilder.build();

        // 执行查询并返回结果
        return query.find(offset, pageSize);
    }

    public static List<HistoryDataBean> export(String deviceType) {
        BoxStore mBoxStore = App.getBoxStore();
        if (mBoxStore == null) return new ArrayList<>();

        Box<HistoryDataBean> historyDataBeanBox = mBoxStore.boxFor(HistoryDataBean.class);

        // 创建查询并设置限制和偏移
        QueryBuilder<HistoryDataBean> queryBuilder = historyDataBeanBox.query();

        // 添加设备类型筛选条件
        if (deviceType != null && !deviceType.isEmpty()) {
            queryBuilder.equal(HistoryDataBean_.DeviceType, deviceType, QueryBuilder.StringOrder.CASE_SENSITIVE);
        }

        queryBuilder.orderDesc(HistoryDataBean_.id);

        // 构建查询
        Query<HistoryDataBean> query = queryBuilder.build();

        // 执行查询并返回结果
        return query.find();
    }

    public static void delete(String json) {
        BoxStore mBoxStore = App.getBoxStore();
        if(mBoxStore == null) return;

        Map<String,Object> map = JsonUtil.toMap(json);

        if(map != null && !map.isEmpty()){
            String msgType = (String) map.get("MsgType");
            if(msgType != null && msgType.equals("2")){
                String dateString = (String) map.get("CreateTime");
                if(dateString == null) return;

                Box<HistoryDataBean> historyDataBeanBox = mBoxStore.boxFor(HistoryDataBean.class);

                Query<HistoryDataBean> query = historyDataBeanBox.query()
                        .less(HistoryDataBean_.CreateTime, dateString,QueryBuilder.StringOrder.CASE_SENSITIVE) // createTime属性小于指定时间的数据
                        .build();

                // 执行删除操作
                query.remove();
            }
        }
    }

    public static void testInsert() {

        BoxStore mBoxStore = App.getBoxStore();
        if(mBoxStore == null) return;

        for(int i = 0; i<20000;i++){
            String deviceType = "sjcgq";
            int dataType = 1;

            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
            String createTimeStr = formatter.format(now);
            String createTime = formatter.format(now) + ".000";

            Map<String,Object> map = new HashMap<>();
            map.put("Id",null);
            map.put("DeviceId","FC0FE737C3B5");
            map.put("DeviceCode","4");
            map.put("DeviceName","水浸设备");
            map.put("CreateTime",createTimeStr);
            map.put("UpdateTime",null);
            map.put("DeviceStatus",null);
            map.put("Td","3");
            map.put("SjStatus1","0");
            map.put("SjStatus2","0");
            map.put("CurLmd",null);
            map.put("BJDelayed",null);
            map.put("SHowzd",null);
            map.put("TcpCmdType","sjcgq");
            map.put("TcpDataType",null);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();
            String deviceData = gson.toJson(map);

            HistoryDataBean dataBean = new HistoryDataBean();

            dataBean.setDeviceData(deviceData);
            dataBean.setCreateTimeStr(createTimeStr);
            dataBean.setDataType(dataType);
            dataBean.setCreateTime(createTime);
            dataBean.setDeviceType(deviceType);

            Log.d("插入第"+(i+1)+"条数据:",dataBean.toJson());
            if(i>=19999){
                Log.d("数据插入完成，共插入"+(i+1)+"条数据","");
            }
            Box<HistoryDataBean> historyDataBeanBox = mBoxStore.boxFor(HistoryDataBean.class);
            historyDataBeanBox.put(dataBean);
        }

    }


}
