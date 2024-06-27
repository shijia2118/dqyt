package com.hxjt.dqyt.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hxjt.dqyt.base.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtils {
    private static WritableFont arial14font = null;
    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";

    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(Colour.LIGHT_BLUE);
            arial14font.setUnderlineStyle(UnderlineStyle.SINGLE);

            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(Alignment.CENTRE);
            arial14format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(Alignment.CENTRE);
            arial10format.setBorder(Border.ALL, BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            arial12format.setAlignment(Alignment.CENTRE);
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public static void initExcel(String filePath, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            sheet.mergeCells(0, 0, colName.length - 1, 0);

            int dotIndex = sheetName.lastIndexOf('_');
            String title = (dotIndex == -1) ? sheetName : sheetName.substring(0, dotIndex);
            sheet.addCell(new Label(0, 0, title+"历史数据表", arial14format));
            sheet.setRowView(0, 520);

            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 1, colName[col], arial10format));
            }
            sheet.setRowView(1, 340);
            workbook.write();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> void writeObjListToExcel(List<Map<String, Object>> objList, File file, Context context) {
        new WriteExcelTask(context, file, objList).execute();
    }

    private static class WriteExcelTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private final File file;
        private final List<Map<String, Object>> objList;
        private String deviceType;

        WriteExcelTask(Context context, File file, List<Map<String, Object>> objList) {
            this.context = context;
            this.file = file;
            this.objList = objList;
            this.deviceType = deviceType;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            if (objList == null || objList.size() == 0) {
                return false;
            }

            WritableWorkbook writeBook = null;
            InputStream in = null;

            try {
                WorkbookSettings settings = new WorkbookSettings();
                settings.setEncoding(UTF8_ENCODING);

                in = new FileInputStream(file);
                Workbook workbook = Workbook.getWorkbook(in);
                writeBook = Workbook.createWorkbook(file, workbook);
                WritableSheet sheet = writeBook.getSheet(0);

                for (int j = 0; j < objList.size(); j++) {
                    Map<String, Object> rowMap = objList.get(j);
                    List<String> list = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : rowMap.entrySet()) {
                        Object value = entry.getValue();
                        list.add(value != null ? value.toString() : "");
                    }
                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 2, list.get(i), arial12format));
                        sheet.setColumnView(i, list.get(i).length() + (list.get(i).length() <= 4 ? 10 : 8));
                    }
                    sheet.setRowView(j + 1, 350);
                }
                writeBook.write();
                workbook.close();
                return true;
            } catch (IOException | BiffException | WriteException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (writeBook != null) {
                        writeBook.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d(">>>>>>>>>>>name:",""+file.getName());
            Log.d(">>>>>>>>>>>size:",""+objList.size());
            Log.d(">>>>>>>>>>>objList:",""+objList);
            ((BaseActivity) context).hideLoading();
            Toast.makeText(context, success ? "导出Excel成功" : "导出Excel失败", Toast.LENGTH_SHORT).show();
        }
    }
}
