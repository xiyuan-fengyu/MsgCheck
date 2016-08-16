package com.xiyuan.template.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xiyuan_fengyu on 2016/7/13.
 */
public class JavaMethodUtil {

    public static <T> Object invoke(T instance, Method method, Object[] args) {
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
