package com.hlyf.selfsupport.dao;

import android.database.Cursor;

import com.hlyf.selfsupport.domin.SysnLog;
import com.hlyf.selfsupport.domin.tDlbSnConfig;
import com.hlyf.selfsupport.util.XUtilsManager;

import org.xutils.DbManager;
import org.xutils.common.util.IOUtil;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SysnLogDao {

    private final DbManager mDbManager;

    public DbManager getmDbManager() {
        return mDbManager;
    }

    public SysnLogDao() throws DbException {
        mDbManager = XUtilsManager.getInstance().getDbManager();
    }

    //在这里
    public List<SysnLog> findSysnLogBy(SqlInfo sqlInfo) throws DbException {
        List<SysnLog> SysnLogList = new ArrayList<SysnLog>();
        Cursor cursor = XUtilsManager.getInstance().getDbManager().execQuery(sqlInfo);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    //这里写我的业务逻辑
                    SysnLog sysnLog=new SysnLog();
                    sysnLog.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    sysnLog.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                    sysnLog.setTel(cursor.getString(cursor.getColumnIndex("tel")));
                    sysnLog.setStoreId(cursor.getString(cursor.getColumnIndex("storeId")));
                    sysnLog.setSotreName(cursor.getString(cursor.getColumnIndex("sotreName")));
                    sysnLog.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
                    sysnLog.setMerchantOrderId(cursor.getString(cursor.getColumnIndex("merchantOrderId")));
                    sysnLog.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                    sysnLog.setMoney(cursor.getString(cursor.getColumnIndex("money")));
                    sysnLog.setItemData(cursor.getString(cursor.getColumnIndex("itemData")));
                    sysnLog.setPayType(cursor.getString(cursor.getColumnIndex("payType")));
                    sysnLog.setPayOrderId(cursor.getString(cursor.getColumnIndex("payOrderId")));
                    SysnLogList.add(sysnLog);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtil.closeQuietly(cursor);
            }
        }
        return SysnLogList;
    }
    /**
     * 添加数据
     * @param sysnLog
     */
    public void addOneData(SysnLog sysnLog) {
        try {
            mDbManager.save(sysnLog);
        } catch (DbException e) {
            e.printStackTrace();

        }
    }
    /**
     * <pre>
     *     删除 31天以前的单据
     * </pre>
      */
    public void deleteData(){
        try{
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, - 31);
            String preDay = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
            mDbManager.executeUpdateDelete(" DELETE FROM SysnLog WHERE createTime< '"+preDay+"' ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
