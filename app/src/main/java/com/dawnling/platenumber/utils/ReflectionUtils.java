package com.dawnling.platenumber.utils;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by dawnling on 2019/3/27.
 */
public class ReflectionUtils {
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }

        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
            }
            clazz = clazz.getSuperclass();
        }
        Log.e("reflect", "get field " + fieldName + " not found in " + obj.getClass().getName());
        return null;
    }
}

