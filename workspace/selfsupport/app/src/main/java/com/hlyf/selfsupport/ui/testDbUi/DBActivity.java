package com.hlyf.selfsupport.ui.testDbUi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.dao.UserInfoDao;
import com.hlyf.selfsupport.domin.testdomin.UserInfo;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.hlyf.selfsupport.util.UIUtils.toast;

@ContentView(R.layout.activity_db)
public class DBActivity extends Activity {

    @ViewInject(R.id.tv)
    private TextView tv;

    private UserInfoDao mUserInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_db);
        x.view().inject(this);

        try {
            mUserInfoDao = new UserInfoDao();
            toast("数据库操作初始化成功了 哈哈哈哈",false);
        } catch (DbException e) {
            toast("数据库操作初始化失败了 哈哈哈哈",false);
            e.printStackTrace();
        }
    }

    /**
     * 增加数据
     *
     * @param view
     */
    @Event(R.id.btn_save)
    private void save(View view) {
        List<UserInfo> list = new ArrayList<>();
        UserInfo userInfo;
        for (int i = 0; i < 50; i++) {
            userInfo = new UserInfo();
            //useInfo.setId(1); //可以不设置，主键
            userInfo.setName("糖糖" + i);
            userInfo.setAge(10 + i);
            userInfo.setSex("女");
            if(i<10){
                userInfo.setTest("zmy "+i);
            }
            list.add(userInfo);
        }
        mUserInfoDao.addData(list);
        toast("数据插入成功了",false);
    }

    /**
     * 删除数据
     *
     * @param view
     */
    @Event(R.id.btn_delete)
    private void delete(View view) {
        mUserInfoDao.deleteAllData(); //删除所有数据

        toast("数据删除成功了",false);
    }

    /**
     * 更新数据
     *
     * @param view
     */
    @Event(R.id.btn_update)
    private void update(View view) {
        //1.修改表中的某一条数据
        UserInfo userInfo = new UserInfo();
        userInfo.setId(10);
        userInfo.setName("dd");
        userInfo.setAge(25);
        mUserInfoDao.updateData(userInfo, "name", "age");

        //2.修改表中的某些数据
        KeyValue nameValue = new KeyValue("name", "李钦");
        KeyValue ageValue = new KeyValue("age", "123");
        WhereBuilder whereBuilder1 = WhereBuilder.b("id", "=", "20");
        WhereBuilder whereBuilder2 = WhereBuilder.b("id", "=", "30");
        mUserInfoDao.updateData2(whereBuilder1, whereBuilder2, nameValue, ageValue);

        toast("数据更新成功了",false);
    }

    /**
     * 查找数据
     *
     * @param view
     */
    @Event(R.id.btn_find)
    private void find(View view) {
        //查询全部数据
        List<UserInfo> allList = mUserInfoDao.findAllData(UserInfo.class);

        //条件查询
        // List<UserInfo> list = db.selector(UserInfo.class).where("name", "=", "剑圣1").findAll();
        //WhereBuilder whereBuilder = WhereBuilder.b("name", "=", "糖糖5");
        //List<UserInfo> allList = mUserInfoDao.findDataByCondition(UserInfo.class, whereBuilder);

        if (allList != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allList.size(); i++) {
                UserInfo info = allList.get(i);
                sb.append(info.getId() + ",").append(info.getName() + ",").append(info.getAge() + ",")
                        .append(info.getSex()).append("\n");
            }
            tv.setText(sb);
        }

        toast("数据查询成功了",false);
    }

    @Event(R.id.btn_find2)
    private void find2(View view) {
        //查询全部数据
        Cursor cursor = null;
        try {
            cursor = mUserInfoDao.getmDbManager().execQuery("select * from UserInfo1 ");
        } catch (DbException e) {
            e.printStackTrace();
            toast("出错了",false);
        }
        List<UserInfo> allList = new ArrayList<>();
        while (cursor.moveToNext()){
            UserInfo hd=new UserInfo();
            hd.setId(cursor.getInt(cursor.getColumnIndex("id")));
            hd.setName(cursor.getString(cursor.getColumnIndex("name")));
            hd.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            hd.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            hd.setTest(cursor.getString(cursor.getColumnIndex("test")));
            allList.add(hd);
        }
        if (cursor!=null){
            cursor.close();
        }

        if (allList != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allList.size(); i++) {
                UserInfo info = allList.get(i);
                sb.append(info.getId() + ",").append(info.getName() + ",").append(info.getAge() + ",")
                        .append(info.getSex()).append(info.getTest()).append("\n");
            }
            tv.setText(sb);
        }

        toast("数据查询成功了",false);
    }

    @Event(R.id.btn_find3)
    private void find3(View view) {
        //查询全部数据
        Cursor cursor = null;
        try {
            cursor = mUserInfoDao.getmDbManager().execQuery("select * from UserInfo1 ");
        } catch (DbException e) {
            e.printStackTrace();
            toast("出错了",false);
        }
        List<UserInfo> allList = new ArrayList<>();
        while (cursor.moveToNext()){
            UserInfo hd=new UserInfo();
            hd.setId(cursor.getInt(cursor.getColumnIndex("id")));
            hd.setName(cursor.getString(cursor.getColumnIndex("name")));
            hd.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            hd.setAge(cursor.getInt(cursor.getColumnIndex("age")));
            allList.add(hd);
        }

        if (cursor!=null){
            cursor.close();
        }

        if (allList != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allList.size(); i++) {
                UserInfo info = allList.get(i);
                sb.append(info.getId() + ",").append(info.getName() + ",").append(info.getAge() + ",")
                        .append(info.getSex()).append("\n");
            }
            tv.setText(sb);
        }

        toast("数据查询成功了",false);
    }

}
