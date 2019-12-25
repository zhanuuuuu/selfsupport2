package com.hlyf.selfsupport.dao;

import com.hlyf.selfsupport.domin.testdomin.UserInfo;
import com.hlyf.selfsupport.util.XUtilsManager;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

public class UserInfoDao {

    private final DbManager mDbManager;

    public DbManager getmDbManager() {
        return mDbManager;
    }

    public UserInfoDao() throws DbException {
        mDbManager = XUtilsManager.getInstance().getDbManager();
    }

    /**
     * 添加数据
     *
     * @param list
     */
    public void addData(List<UserInfo> list) {
        try {
            //mDbManager.delete(UserInfo.class);
            //mDbManager.save(list);
            //mDbManager.saveBindingId(list);
            //mDbManager.saveOrUpdate(list);

            mDbManager.replace(list); //保存或更新实体类或实体类的List到数据库, 根据id和其他唯一索引判断数据是否存在.
        } catch (DbException e) {
            e.printStackTrace();

        }
    }

    /**
     * 删除全部数据
     */
    public void deleteAllData() {
        try {
            mDbManager.delete(UserInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除数据
     */
    public void deleteDataById(int id) {
        try {
            mDbManager.deleteById(UserInfo.class, id); //删除指定id数据
            //根据指定条件删除
            // mDbManager.delete(UserInfo.class, WhereBuilder.b("sex", "=", "女"));
            //mDbManager.delete(UserInfo.class, WhereBuilder.b("id", ">=", "50").and("id", "<=", "100"));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据条件删除数据
     */
    public void deleteDataByCondition(WhereBuilder whereBuilder) {
        try {
            //根据指定条件删除
            // mDbManager.delete(UserInfo.class, WhereBuilder.b("sex", "=", "女"));
            //mDbManager.delete(UserInfo.class, WhereBuilder.b("id", ">=", "50").and("id", "<=", "100"));
            mDbManager.delete(UserInfo.class, whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据:修改表中的某一条数据
     */
    public void updateData(UserInfo userInfo, String... columnNames) {
        try {
            mDbManager.update(userInfo, columnNames);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改表中的某些数据
     *
     * @param whereBuilder1
     * @param whereBuilder2
     * @param keyValue
     */
    public void updateData2(WhereBuilder whereBuilder1, WhereBuilder whereBuilder2, KeyValue... keyValue) {
        try {
            mDbManager.update(UserInfo.class, whereBuilder1.or(whereBuilder2), keyValue);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找所有数据
     */
    public List<UserInfo> findAllData(Class<UserInfo> tclass) {
        try {
            //UserInfo first = mDbManager.findFirst(tclass);查询第一条数据
            return mDbManager.findAll(tclass);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件查询
     */
    public List<UserInfo> findDataByCondition(Class<UserInfo> tclass, WhereBuilder whereBuilder) {
        try {
            List<UserInfo> allList = mDbManager.selector(tclass).where(whereBuilder).findAll();
            return allList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
