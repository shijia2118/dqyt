package com.hxjt.dqyt.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hxjt.dqyt.bean.HistoryDataBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBUtils extends SQLiteOpenHelper {
    private static final String TAG = "DBUtils";

    // 数据库名称和版本
    private static final String DATABASE_NAME = "hspc.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;
    private Context context;

    // 表名和字段名
    private static final String TABLE_NAME = "history_data";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_DEVICE_TYPE = "DeviceType";
    private static final String COLUMN_DEVICE_DATA = "DeviceData";
    private static final String COLUMN_DATA_TYPE = "DataType";
    private static final String COLUMN_CREATE_TIME_STR = "CreateTimeStr";
    private static final String COLUMN_CREATE_TIME = "CreateTime";

    public DBUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // 在构造函数中复制数据库文件到应用程序的数据目录
        copyDatabaseFromAssets();
        // 打开数据库连接
        db = getWritableDatabase();
    }

    // 复制数据库文件从 assets 到应用程序的数据目录
    private void copyDatabaseFromAssets() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(DATABASE_NAME);
                OutputStream outputStream = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 在这里实现数据库的创建操作，但由于数据库已经在 assets 中存在，因此不需要执行创建表的操作
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果数据库版本发生变化，可以在这里处理数据库结构的变更
    }

    /**
     * 查询数据，支持分页
     * @param offset 起始位置
     * @param limit 查询条数
     * @return 查询结果列表
     */
    public List<HistoryDataBean> selectData(int offset, int limit) {
        List<HistoryDataBean> list = new ArrayList<>();
        String limitClause = offset + "," + limit; // 构建分页查询的限制条件

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, limitClause);

//        while (cursor.moveToNext()) {
            // 获取每一列的数据
//            String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
//            String deviceType = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_TYPE));
//            String deviceData = cursor.getString(cursor.getColumnIndex(COLUMN_DEVICE_DATA));
//            int dataType = cursor.getInt(cursor.getColumnIndex(COLUMN_DATA_TYPE));
//            String createTimeStr = cursor.getString(cursor.getColumnIndex(COLUMN_CREATE_TIME_STR));
//            String createTime = cursor.getString(cursor.getColumnIndex(COLUMN_CREATE_TIME));
//
//            // 构造 HistoryDataBean 对象并添加到列表中
//            HistoryDataBean historyDataBean = new HistoryDataBean(id, deviceType, deviceData, dataType, createTimeStr, createTime);
//            list.add(historyDataBean);
//
//            Log.d(TAG, "selectData: Retrieved - " + historyDataBean.toString());
//        }

        cursor.close();

        return list;
    }

    /**
     * 插入数据
     * @param id 数据Id
     * @param deviceType 设备类型
     * @param deviceData 设备数据
     * @param dataType 数据类型
     * @param createTimeStr 创建时间字符串
     * @param createTime 创建时间
     * @return 插入数据的行号，-1 表示插入失败
     */
    public long insertData(String id, String deviceType, String deviceData, int dataType, String createTimeStr, String createTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_DEVICE_TYPE, deviceType);
        values.put(COLUMN_DEVICE_DATA, deviceData);
        values.put(COLUMN_DATA_TYPE, dataType);
        values.put(COLUMN_CREATE_TIME_STR, createTimeStr);
        values.put(COLUMN_CREATE_TIME, createTime);

        long newRowId = db.insert(TABLE_NAME, null, values);
        if (newRowId == -1) {
            Log.e(TAG, "Error inserting data into " + TABLE_NAME);
        } else {
            Log.d(TAG, "Inserted data into " + TABLE_NAME + " with ID: " + newRowId);
        }

        return newRowId;
    }

    /**
     * 关闭数据库连接
     */
    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }
        super.close();
    }
}



