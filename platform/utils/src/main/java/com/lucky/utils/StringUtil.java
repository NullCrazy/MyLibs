package com.lucky.utils;

import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vate
 * @date 2017/5/22 下午1:07
 * @description: String 工具类
 **/
public class StringUtil {
    /**
     * 内容是"null"的字符串
     */
    private static final String NULL_STR = "null";
    /**
     * 内容是空的字符串
     */
    private static final String EMPTY_STR = "";

    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断的字符串
     * @return 为空返回true, 否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0 || NULL_STR.equalsIgnoreCase(str.trim());
    }

    /**
     * 将字符串null转换为空
     *
     * @param str 需要判断的字符串
     * @return 将字符串null转换为空
     */
    public static String trimNull(String str) {
        if (str == null || NULL_STR.equalsIgnoreCase(str.trim())) {
            return EMPTY_STR;
        }
        return str.trim();
    }

    /**
     * 将字符串null转换为空
     *
     * @param obj 需要判断的obj对象
     * @return 将字符串null转换为空
     */
    public static String trimNull(Object obj) {
        String str = String.valueOf(obj);
        if (str == null || NULL_STR.equalsIgnoreCase(str.trim())) {
            return NULL_STR;
        }
        return str.trim();
    }

    /**
     * 将字符串null转换为任意字符
     *
     * @param str   原始字符串
     * @param value 需要转成的目标字符串
     * @return 将字符串null转换为任意字符
     */
    public static String nullToValue(String str, String value) {
        if (str == null || NULL_STR.equalsIgnoreCase(str.trim())) {
            return value;
        }
        return str.trim();
    }

    /**
     * 将单引号 ' 转换为 双引号 "  主要为了方便构造xml内容
     *
     * @param str 需要转换的字符串
     * @return 将单引号 ' 转换为 双引号 "  主要为了方便构造xml内容
     */
    public static String transfDblQuot(String str) {
        String regex = "'";
        String replacement = "\"";
        return str.replaceAll(regex, replacement);
    }

    /**
     * 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为yyyy-MM-dd字符串格式
     *
     * @param str 需要转换的字符串
     * @return 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为yyyy-MM-dd字符串格式
     */
    public static String conv2YyyyMmDdWithSep(String str) {
        String space = " ";
        if (isEmpty(str)) {
            return EMPTY_STR;
        } else {
            return str.split(space)[0];
        }
    }

    /**
     * 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为hh:mm:ss.SSS字符串格式
     *
     * @param str 需要转换的字符串
     * @return 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为hh:mm:ss.SSS字符串格式
     */
    public static String conv2HhMmSsWithSep(String str) {
        String space = " ";
        String regex = "\\.";
        if (isEmpty(str)) {
            return EMPTY_STR;
        } else {
            return str.split(space)[1].split(regex)[0];
        }
    }

    /**
     * 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为hh:mm:ss.SSS字符串格式
     *
     * @param str 需要转换的字符串
     * @return 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为hh:mm:ss.SSS字符串格式
     */
    public static String conv2YyyyMmWithSep(String str) {
        String space = " ";
        int startIndex = 0;
        int endIndex = 7;
        if (isEmpty(str)) {
            return EMPTY_STR;
        } else {
            return str
                    .split(space)[startIndex]
                    .substring(startIndex, endIndex);
        }
    }

    /**
     * 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为yyyy-MM-dd hh:mm:ss字符串格式
     *
     * @param str 需要转换的字符串
     * @return 将yyyy-MM-dd hh:mm:ss.SSS日期格式的字符串转换为yyyy-MM-dd hh:mm:ss字符串格式
     */
    public static String conv2DateTime(String str) {
        String regex = "\\.";
        if (isEmpty(str)) {
            return EMPTY_STR;
        } else {
            return str.split(regex)[0];
        }
    }

    /**
     * 判断字符串不为空
     *
     * @param str 需要转换的字符串
     * @return true 不为空，false为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 将 " ' 字符转换为全角的字符
     *
     * @param str 需要转换的字符串
     * @return 转成后的字符串
     */
    public static String castQujiao(String str) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        String tmp = str.replaceAll("'", "‘");
        return trimNull(tmp.replaceAll("\"", "“"));
    }

    /**
     * 转换为html的编码
     *
     * @param str 需要转换的字符串
     * @return 转换为html的编码
     */
    public static String toHtmlEncode(String str) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&apos;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("\r\n", "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;");
        return str;
    }

    /**
     * 转换为xml的编码
     *
     * @param str 需要转换的字符串
     * @return 转换为xml的编码
     */
    public static String toXmlEncode(String str) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&apos;");
        str = str.replaceAll("\"", "&quot;");
        return str;
    }

    /**
     * 为长度不够指定值得整数在左边补0
     *
     * @param value  值
     * @param length 长度
     * @return 为长度不够指定值得整数在左边补0
     */
    public static String addLeftZero(int value, int length) {
        String tmp = String.valueOf(value);
        String zero = "0";
        int diff = length - tmp.length();
        for (int i = 0; i < diff; i++) {
            tmp = zero + tmp;
        }
        return tmp;
    }

    /**
     * 去掉右边的0
     *
     * @param str 需要转换的字符串
     * @return 去掉右边的0
     */
    public static String trimRightZero(String str) {
        String regex = "\\.{0,1}[0]+$";
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        return str.replaceAll(regex, EMPTY_STR);
    }

    /**
     * 首字母转大写
     *
     * @param str 需要转换的字符串
     * @return 首字母转成大写
     */
    public final static String toUpperForFirstChar(String str) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 转ISO-8859-1,UTF-8
     *
     * @param str     需要转换的字符串
     * @param srcCode 编码格式
     * @return ISO-8859-1,UTF-8
     */
    public final static String cast2Gbk(String str, String srcCode) {
        //编码格式·
        String gbk = "GBK";
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        String ret;
        try {
            ret = new String(str.getBytes(srcCode), gbk);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return EMPTY_STR;
        }
    }

    /**
     * 转换ISO-8859-1,UTF-8
     *
     * @param str 需要转换的字符串
     * @return ISO-8859-1,UTF-8 后的字符串
     */
    public final static String cast2UTF8(String str, String srcCode) {
        String utf8 = "UTF-8";
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        String ret;
        try {
            ret = new String(str.getBytes(srcCode), utf8);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return EMPTY_STR;
        }
    }

    /**
     * 将String数组转换成字符串
     *
     * @param args 需要转换的字符串
     * @return 转成的字符串
     */
    public static String switchToString(String[] args) {
        if (args == null || args.length == 0) {
            return EMPTY_STR;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
        }

        return sb.toString();
    }


    /**
     * 判断cid
     *
     * @param cId cid内容
     * @return true 是cid,false 不是cid
     */
    public final static boolean isCid(String cId) {
        String regex = "^([0-9]{14}[X]{0,1})|([0-9]{18})$";
        return Pattern.matches(regex, cId);
    }

    /**
     * 判断车牌号
     *
     * @param str 需要判断的数据
     * @return true 是车牌号，false 不是车牌号
     */
    public final static boolean isVehicleNo(String str) {
        String regex = "^(([\u0391-\uFFE5]{1})|([Ww]{1}[Jj]{1}))[0-9A-Za-z\u0391-\uFFE5]{5,30}$";
        return Pattern.matches(regex, str);
    }

    /**
     * 判断手机号
     *
     * @param str 手机号码
     * @return true 是手机号码，false 不是手机号码
     */
    public final static boolean isMobileNo(String str) {
        String regex = "^1[3|5][\\d]{9}$";
        return Pattern.matches(regex, str);
    }

    /**
     * 判断电话号码
     *
     * @param str 电话号码
     * @return true 是电话号码，false不是电话号码
     */
    public final static boolean isTelNo(String str) {
        String regex = "(^[0-9]{3,4}[0-9]{7,8}$)|(^[0-9]{7,8}$)";
        return Pattern.matches(regex, str);
    }

    /**
     * 判断邮箱
     *
     * @param email 邮箱
     * @return true 是邮箱,false 不是邮箱
     */
    public static boolean isEmail(String email) {
        String regex = "^\\w+@\\w+\\.(com\\.cn)|\\w+@\\w+\\.(com|cn)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    /**
     * double转string
     *
     * @param db 数值
     * @return 把double 转成string后的数据
     */
    public static String parseDouble2NormalString(Double db) {
        int newScale = 2;
        BigDecimal bd = new BigDecimal(db);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP)
                .toPlainString();
    }

    /**
     * double转String
     *
     * @param money 输入的钱
     * @return 转换后的钱
     */
    public static String moneyFomat(double money) {
        //精确到的最小的值
        double minMoney = 0.000001;
        int ten = 10;
        //小数点后一位
        String decimalPointOne = "#0.0";
        //小数点后两位
        String decimalPointTwo = "#0.00";

        if (money - (int) money < minMoney) {
            return EMPTY_STR + (int) money;
        }
        if (money * ten - (int) (money * ten) < minMoney) {
            return new DecimalFormat(decimalPointOne).format(money);
        } else {
            return new DecimalFormat(decimalPointTwo).format(money);
        }
    }

    /**
     * 字符串转int
     *
     * @param string 转换的数据
     * @return 转换后的数据
     */
    public static int stringToInt(String string) {
        int i = -1;

        try {
            i = Integer.parseInt(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 字符串转double
     *
     * @param str 转换的数据
     * @return 转换后的数据
     */
    public static double stringToDouble(String str) {
        int defaultZero = 0;
        if (TextUtils.isEmpty(str)) {
            return defaultZero;
        }
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return defaultZero;
        }
    }

    /**
     * 截取字符串
     *
     * @param separator 空格
     * @param str       转换的数据
     * @return 转换后的数据
     */
    public static String substringAfter(String separator, String str) {
        if (isEmpty(str)) {
            return str;
        } else if (separator == null) {
            return EMPTY_STR;
        } else {
            int pos = str.indexOf(separator);
            return pos == -1 ? EMPTY_STR : str.substring(pos + separator.length());
        }
    }
}
