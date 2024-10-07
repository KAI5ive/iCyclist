package com.novo.zealot.DB;

/**
 * Created by Novo on 2019/5/27.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.novo.zealot.Bean.DayRecord;
import com.novo.zealot.Bean.RunRecord;
import com.novo.zealot.Utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ZealotDBOpenHelper extends SQLiteOpenHelper {
    public final String TAG = "ZealotDBOpenHelper";
    public static final String TABLE_NAME = "ZealotRecord";

    public static final String CREATE_DB = "create table ZealotRecord(" +
            "id integer primary key autoincrement, " +
            "uuid text, " +
            "date date, " +
            "distance double, " +
            "duration int, " +
            "avgSpeed double, " +
            "startTime date, " +
            "endTime date);";

    public ZealotDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    /**
     * 添加记录至Database
     *
     * @param runRecord the record you want to add to database
     * @return if added successfully, then return true. Otherwise, return false.
     */
    public boolean addRecord(RunRecord runRecord) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("uuid", runRecord.getUuid());
        values.put("date", runRecord.getDate());
        values.put("distance", runRecord.getDistance());
        values.put("duration", runRecord.getDuration());
        values.put("avgSpeed", runRecord.getAvgSpeed());
        values.put("startTime", DateUtil.getFormattedTime(runRecord.getStartTime()));
        values.put("endTIme", DateUtil.getFormattedTime(runRecord.getEndTime()));
        long result = db.insert(TABLE_NAME, null, values);
        values.clear();

        if (result == -1) {
            Log.d(TAG, runRecord.getUuid() + " failed to add");
            return false;
        } else {
            Log.d(TAG, runRecord.getUuid() + " added successfully");
            return true;
        }

    }


    /**
     * 查询记录
     *
     * @param queryDate the date you want to query
     * @return a list of RunRecords
     */
    public List<RunRecord> queryRecord(String queryDate) {
        List<RunRecord> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        //若不输入日期，则返回全部数据
        if (queryDate == null) {
            String sql = "select * from " + TABLE_NAME + " order by startTime desc";
            cursor = db.rawQuery(sql, null);
        } else {
            String sql = "select * from " + TABLE_NAME + " where date = ? order by startTime desc";
            cursor = db.rawQuery(sql, new String[]{queryDate});
        }

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                //提取数据
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                int duration = cursor.getInt(cursor.getColumnIndex("duration"));
                double avgSpeed = cursor.getDouble(cursor.getColumnIndex("avgSpeed"));
                Date startTime = DateUtil.strToTime(cursor.getString(cursor.getColumnIndex("startTime")));
                Date endTime = DateUtil.strToTime(cursor.getString(cursor.getColumnIndex("endTime")));

                //初始化
                RunRecord runRecord = new RunRecord();
                runRecord.setUuid(uuid);
                runRecord.setDate(date);
                runRecord.setDistance(distance);
                runRecord.setDuration(duration);
                runRecord.setAvgSpeed(avgSpeed);
                runRecord.setStartTime(startTime);
                runRecord.setEndTime(endTime);

                //添加进结果
                results.add(runRecord);
            }
        }

        return results;

    }

    /**
     * 返回所有数据
     * 调用queryRecord(null)
     *
     * @return a list of all RunRecords
     */
    public List<RunRecord> queryRecord() {

        List<RunRecord> results = queryRecord(null);

        return results;
    }

    /**
     * 查询最远距离
     *
     * @return the result of the farthest distance in the database
     */
    public double queryBestDistance() {
        String sql = "select max(distance) from " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        double result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getDouble(0);
        }
        cursor.close();
        return result;
    }

    /**
     * 查询最快速度
     *
     * @return the result of the highest speed in the database
     */
    public double queryBestSpeed() {
        String sql = "select max(avgSpeed) from " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        double result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getDouble(0);
        }
        cursor.close();
        return result;
    }

    /**
     * 查询最长时间
     *
     * @return the result of the longest duration in the database
     */
    public int queryBestTime() {
        String sql = "select max(duration) from " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);

        }
        cursor.close();
        return result;
    }

    /**
     * 查询跑步总距离
     *
     * @return sum of running distance
     */
    public int queryAllDistance() {
        String sql = "select sum(distance) from " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);

        }
        cursor.close();
        return result;
    }

    /**
     * 查询跑步总天数
     *
     * @return number of running days
     */
    public int queryNumOfDays() {
        String sql = "select count(date) from " + TABLE_NAME + " group by date";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);

        }
        cursor.close();
        return result;
    }

    /**
     * 查询跑步总次数
     *
     * @return number of running times
     */
    public int queryNumOfTimes() {
        String sql = "select count(*) from " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);

        }
        cursor.close();
        return result;
    }

    /**
     * 删除所有数据
     */
    public void deleteAllData() {
        String sql = "delete from " + TABLE_NAME + " where 1 = 1 ";
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
    }


    /**
     * 返回待查询月份每天的运动距离
     *
     * @param year  待查年份
     * @param month 待查月份
     * @return A List of DayRecord
     */
    public List<DayRecord> queryDayRecord(String year, String month) {
        /**
         * 数据一定要按X轴升序排，否则图标会崩溃
         */
        String sql = "select distinct date from " + TABLE_NAME
                + " where substr(date(date),1,7) = ? order by date asc";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{year + "-" + month});

        Log.d(TAG, "cursor size: " + cursor.getCount());

        List<DayRecord> result = new LinkedList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String tempDate = cursor.getString(0);
                List<RunRecord> dayList = queryRecord(tempDate);

                Log.d(TAG, "dayList size: " + dayList.size());
                //得到日
                Date date = DateUtil.strToDate(tempDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                float day = (float) calendar.get(Calendar.DATE);
                Log.d(TAG, "day : " + day);

                //得到总距离
                float allDistance = 0;
                for (RunRecord record :
                        dayList) {
                    allDistance += record.getDistance();
                }
                DayRecord dayRecord = new DayRecord();
                dayRecord.setDay(day);
                dayRecord.setDistance(allDistance);
                result.add(dayRecord);
            }
        }
        cursor.close();
        Log.d(TAG, "SQL : " + sql);
        Log.d(TAG, "year + month : " + year + month);
        Log.d(TAG, "result size: " + result.size());
        return result;

    }

}
