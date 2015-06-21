package com.weishang.repeater.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.weishang.repeater.App;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 配置项管理对象
 *
 * @author momo
 * @version 从prefernceName中取值, 根据角标取值, 并设置.
 * @Date 2014/11/28
 */
public class PrefernceUtils {
    // 配置项名称
    private static final String DEFAULT_PREFERENCE = "app_config";
    private static final String DEFAULT_KEY = "config";
    private static final String DEFAULT_VALUE = "-1";
    private static SoftReference<String[]> values;

    static {
        values = new SoftReference<String[]>(null);
    }

    @SuppressLint("InlinedApi")
    public static void init(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_MULTI_PROCESS);
        String value = preferences.getString(DEFAULT_KEY, null);
        Log.i(DEFAULT_KEY, "value:" + value);
        if (TextUtils.isEmpty(value)) {
            // 初始化配置
            initPreference(preferences);
        } else {
            // 确定是否新增配置项
            ensureValue(false);
            Field[] fields = ConfigName.class.getFields();
            String[] preferenceValue = values.get();
            if (fields.length != preferenceValue.length) {
                // 添加配置项_
                if (fields.length > preferenceValue.length) {
                    String[] newValue = new String[fields.length];
                    System.arraycopy(preferenceValue, 0, newValue, 0, preferenceValue.length);
                    // 为剩余取值赋默认值
                    for (int i = preferenceValue.length; i < fields.length; i++) {
                        newValue[i] = String.valueOf(0);
                    }
                    values = new SoftReference<String[]>(newValue);
                    createValue(newValue);
                }
            }
        }
    }

    /**
     * 初始化配置项
     */
    private static void initPreference(SharedPreferences preferences) {
        Field[] fields = ConfigName.class.getFields();
        if (null != fields && 0 < fields.length) {
            int length = fields.length;
            String value = new String();
            for (int i = 0; i < length; i++) {
                if (length - 1 != i) {
                    value += DEFAULT_VALUE + "|";
                } else {
                    value += DEFAULT_VALUE;
                }
            }
            preferences.edit().putString(DEFAULT_KEY, value).commit();
        }
    }

    /**
     * 设置取值
     *
     * @param value
     */
    @SuppressLint("InlinedApi")
    private static void setValue(String value) {
        SharedPreferences preferences = App.getAppContext().getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_MULTI_PROCESS);
        preferences.edit().putString(DEFAULT_KEY, value).commit();
    }

    @SuppressLint("InlinedApi")
    public static String getValue() {
        SharedPreferences preferences = App.getAppContext().getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_MULTI_PROCESS);
        return preferences.getString(DEFAULT_KEY, null);
    }

    public static String getMyValue() {
        ensureValue(false);
        String[] preferenceValue = values.get();
        return Arrays.toString(preferenceValue);
    }

    @SuppressLint("InlinedApi")
    public static void ensureValue(boolean isClear) {
        if (isClear) {
            values = new SoftReference<String[]>(null);
        }
        String[] preferenceValue = values.get();
        if (null == preferenceValue) {
            SharedPreferences preferences = App.getAppContext().getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_MULTI_PROCESS);
            String value = preferences.getString(DEFAULT_KEY, null);
            if (!TextUtils.isEmpty(value)) {
                preferenceValue = value.split("\\|");
                values = new SoftReference<String[]>(preferenceValue);
            } else {
                initPreference(preferences);
                ensureValue(false);
            }
        }
    }

    /**
     * 生成取值 默认值 -1;
     *
     * @param allValues
     */
    private static void createValue(String[] allValues) {
        String stringValue = new String();
        for (int i = 0; i < allValues.length; i++) {
            if (allValues.length - 1 != i) {
                stringValue += allValues[i] + "|";
            } else {
                stringValue += allValues[i];
            }
        }
        setValue(stringValue);
    }

    /**
     * 根据角标获取 默认值 -1;
     *
     * @param index
     * @return
     */
    public static int getInt(boolean isClear, int index) {
        ensureValue(isClear);
        int intValue = -1;
        String[] preferenceValue = values.get();
        if (index < preferenceValue.length) {
            String value = preferenceValue[index];
            if (!TextUtils.isEmpty(value)) {
                try {
                    intValue = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    intValue = -1;
                }
            }
        }
        return intValue;
    }

    /**
     * 根据角标获取 默认值 -1;
     *
     * @param index
     * @return
     */
    public static int getInt(int index) {
        return getInt(false, index);
    }

    /**
     * 根据角标获取 默认值 -1;
     *
     * @param index
     * @return
     */
    public static long getLong(boolean isClear, int index) {
        ensureValue(isClear);
        long intValue = -1;
        String[] preferenceValue = values.get();
        if (index < preferenceValue.length) {
            String value = preferenceValue[index];
            if (!TextUtils.isEmpty(value)) {
                try {
                    intValue = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    intValue = -1;
                }
            }
        }
        return intValue;
    }

    /**
     * 根据角标获取 默认值 -1;
     *
     * @param index
     * @return
     */
    public static long getLong(int index) {
        return getLong(false, index);
    }

    /**
     * 获得boolean值
     *
     * @param index
     * @return
     */
    public static boolean getBoolean(boolean isClear, int index) {
        return 1 == getInt(isClear, index);
    }

    /**
     * 获得boolean值
     *
     * @param index
     * @return
     */
    public static boolean getBoolean(int index) {
        return 1 == getInt(false, index);
    }

    /**
     * 取反boolean值
     *
     * @param index
     * @param defaultValue 默认值
     * @return
     */
    public static boolean getRvsBoolean(boolean isClear, int index) {
        return 1 != getInt(isClear, index);
    }

    /**
     * 取反boolean值
     *
     * @param index
     * @param defaultValue 默认值
     * @return
     */
    public static boolean getRvsBoolean(int index) {
        return 1 != getInt(false, index);
    }


    /**
     * 根据角标获取 默认值 null;
     *
     * @param index
     * @return
     */
    public static String getString(boolean isClear, int index) {
        ensureValue(isClear);
        String stringValue = null;
        String[] preferenceValue = values.get();
        if (index < preferenceValue.length) {
            stringValue = preferenceValue[index];
            if (DEFAULT_VALUE.equals(stringValue)) {
                stringValue = null;
            }
        }
        return stringValue;
    }

    /**
     * 根据角标获取 默认值 null;
     *
     * @param index
     * @return
     */
    public static String getString(int index) {
        return getString(false, index);
    }

    public static void setInt(int index, int value) {
        ensureValue(false);
        String[] allValues = values.get();
        allValues[index] = String.valueOf(value);
        createValue(allValues);
    }

    public static void setLong(int index, long value) {
        ensureValue(false);
        String[] allValues = values.get();
        allValues[index] = String.valueOf(value);
        createValue(allValues);
    }

    public static void setString(int index, String value) {
        ensureValue(false);
        String[] allValues = values.get();
        allValues[index] = !TextUtils.isEmpty(value) ? value : DEFAULT_VALUE;
        createValue(allValues);
    }

    public static void setBoolean(int index, Boolean value) {
        ensureValue(false);
        String[] allValues = values.get();
        allValues[index] = String.valueOf((value ? 1 : -1));
        createValue(allValues);
    }

    /**
     * 添加样式
     * @param color
     */
    public static void addStyle(int color) {
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_MULTI_PROCESS);
        String style = preferences.getString("style", null);
        if (TextUtils.isEmpty(style)) {
            style = String.valueOf(color);
        } else {
            style += ("|" + color);
        }
        preferences.edit().putString("style", style).commit();
    }

    public static ArrayList<String> getStyles(){
        Context context = App.getAppContext();
        SharedPreferences preferences = context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_MULTI_PROCESS);
        String style = preferences.getString("style", null);
        ArrayList<String> styles=null;
        if(!TextUtils.isEmpty(style)){
            styles=new ArrayList<>();
            String[] styleValues = style.split("\\|");
            styles.addAll(Arrays.asList(styleValues));
        }
        return styles;
    }
}
