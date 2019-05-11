package com.hzy.cnn.CustomView.Utils;

import java.lang.reflect.Field;

/**
 * Created by 胡泽宇 on 2018/8/12.
 * 设置反射属性工具类
 */

public class FieldUtils {
    /***
     * 通过反射获得类的属性值方法
     * @param obj 实例
     * @param cls   类名
     * @param str   属性名
     * @return 获得属性值
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static  Object getField(Object obj, Class cls, String str)
            throws NoSuchFieldException, IllegalAccessException
    {
        //获取反射属性通用方法 obj为实体类 cls为类类型，str为属性名
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }
    /***
     *
     * @param obj1 实例
     * @param aClass 类名
     * @param fieldName 属性名
     * @param fieldValue   预设值
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static void setField(Object obj1, Class aClass, String fieldName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
            Field declaredField = aClass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(obj1, fieldValue);
    }
}
