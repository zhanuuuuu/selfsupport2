package com.hlyf.selfsupport.dao;

import com.hlyf.selfsupport.domin.tDlbSnConfig;
import com.hlyf.selfsupport.util.XUtilsManager;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

public class tDlbSnConfigDao {

    private final DbManager mDbManager;

    public DbManager getmDbManager() {
        return mDbManager;
    }

    public tDlbSnConfigDao() throws DbException {
        mDbManager = XUtilsManager.getInstance().getDbManager();
    }

    /**
     * 删除全部数据
     */
    public void deleteAllData() {
        try {
            mDbManager.delete(tDlbSnConfig.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加数据
     *
     * @param tDlbSnConfig
     */
    public void addOneData(tDlbSnConfig tDlbSnConfig) {
        try {
            //mDbManager.delete(UserInfo.class);
            //mDbManager.save(list);
            //mDbManager.saveBindingId(list);
            //mDbManager.saveOrUpdate(list);
            mDbManager.save(tDlbSnConfig); //保存或更新实体类或实体类的List到数据库, 根据id和其他唯一索引判断数据是否存在.
        } catch (DbException e) {
            e.printStackTrace();

        }
    }

    /**
     * 查找一条数据
     */
    public tDlbSnConfig findOneSnConfig(Class<tDlbSnConfig> tclass) {
        try {
            tDlbSnConfig first = mDbManager.findFirst(tclass);
            return first;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
