package com.hlyf.selfsupport.domin.testdomin;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 复制代码
 * @Check check约束
 * @Column 列名
 * @Finder 一对多、多对一、多对多关系(见sample的Parent、Child中的使用)
 * @Foreign 外键
 * @Id 主键，当为int类型时，默认自增。 非自增时，需要设置id的值
 * @NoAutoIncrement 不自增
 * @NotNull 不为空
 * @Table 表名
 * @Transient 不写入数据库表结构
 * @Unique 唯一约束
 *
 * name (String)       :  表名称
 * isId (boolean)      :  是否为主键
 * autoGen (boolean)   :  是否自增(默认: false)
 * proprety (String)   :  是否为空(默认: NOT NULL)
 */
@Table(name = "UserInfo1")
public class UserInfo {

    @Column(name = "id", isId = true, autoGen = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "sex")
    private String sex;

    @Column(name = "test")
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}

