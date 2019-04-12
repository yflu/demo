package com.example.demo.core.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object delFlag = getFieldValByName(getDeleteFlagFieldName(), metaObject);
        if (delFlag == null) {
            setFieldValByName(getDeleteFlagFieldName(), getDefaultDelFlagValue(), metaObject);
        }

        Object createTime = getFieldValByName(getCreateTimeFieldName(), metaObject);
        if (createTime == null) {
            setFieldValByName(getCreateTimeFieldName(), new Date(), metaObject);
        }

        Object createUser = getFieldValByName(getCreateUserFieldName(), metaObject);
        if (createUser == null) {

            //获取当前登录用户
            Object accountId = getUserId();

            setFieldValByName(getCreateUserFieldName(), accountId, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName(getUpdateTimeFieldName(), new Date(), metaObject);

        Object updateUser = getFieldValByName(getUpdateUserFieldName(), metaObject);
        if (updateUser == null) {

            //获取当前登录用户
            Object accountId = getUserId();

            setFieldValByName(getUpdateUserFieldName(), accountId, metaObject);
        }
    }

    /**
     * 获取逻辑删除字段的名称（非数据库中字段名称）
     */
    protected String getDeleteFlagFieldName() {
        return "delFlag";
    }

    /**
     * 获取逻辑删除字段的默认值
     */
    protected Object getDefaultDelFlagValue() {
        return "N";
    }

    /**
     * 获取创建时间字段的名称（非数据库中字段名称）
     */
    protected String getCreateTimeFieldName() {
        return "createTime";
    }

    /**
     * 获取创建用户字段的名称（非数据库中字段名称）
     */
    protected String getCreateUserFieldName() {
        return "createUser";
    }

    /**
     * 获取更新时间字段的名称（非数据库中字段名称）
     */
    protected String getUpdateTimeFieldName() {
        return "updateTime";
    }

    /**
     * 获取更新用户字段的名称（非数据库中字段名称）
     */
    protected String getUpdateUserFieldName() {
        return "updateUser";
    }

    /**
     * 获取用户唯一id（注意默认获取的用户唯一id为空，如果想填写则需要继承本类）
     */
    protected Object getUserId() {
        return null;
    }
}
