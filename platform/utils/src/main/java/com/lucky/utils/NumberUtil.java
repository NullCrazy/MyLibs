package com.lucky.utils;

import android.text.TextUtils;

/**
 * @author mylo
 * @date 2017/9/22.
 * @Description: 数据转换类
 * <p>
 * 代码review于2019/5/29,更新:xingguo.lei
 */
public class NumberUtil {

    /**
     * 把double转换成int
     *
     * @param num 需要转换的数值
     * @return 转换后的值
     */
    public static int integerFormat(double num) {
        try {
            return (int) num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断数据类型是否是integer
     *
     * @param num 需要转化的值
     * @return true 是integer，false 不是integer
     */
    public static boolean isInteger(double num) {
        try {
            if ((int) num == num) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 把string转换成int数据
     *
     * @param str 需要转换的字符串
     * @return 转换后的值
     */
    public static int stringToInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int result;
        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }
}
