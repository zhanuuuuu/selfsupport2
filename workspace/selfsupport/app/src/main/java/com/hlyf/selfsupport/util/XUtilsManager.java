package com.hlyf.selfsupport.util;



import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

/**
 *
 */
public class XUtilsManager {

    private static XUtilsManager mInstance;

    private DbManager dbManager;



    private XUtilsManager() {
    }

    public static XUtilsManager getInstance() {
        if (mInstance == null) {
            synchronized (XUtilsManager.class) {
                if (mInstance == null) {
                    mInstance = new XUtilsManager();
                }
            }
        }
        return mInstance;
    }

    public DbManager getDbManager() throws DbException {
        if (dbManager == null) {
            initDbManager();
        }
        return dbManager;
    }

    private void initDbManager() throws DbException {
        File dbFile = new File("/sdcard/selfsupport/db");
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        DbManager.DaoConfig config = new DbManager.DaoConfig()
                .setDbDir(dbFile) //数据库路径
                .setDbName("selfsupport") //数据库名
                .setDbVersion(1) //设置数据库版本
                .setAllowTransaction(true)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: 2016/11/8 数据库更新操作


                    }
                });
        dbManager = x.getDb(config);


    }
}



