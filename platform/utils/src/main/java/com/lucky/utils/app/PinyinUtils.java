package com.lucky.utils.app;


import android.content.Context;

import com.lucky.utils.jpinyin.PinyinFormat;
import com.lucky.utils.jpinyin.PinyinHelper;

/**
 * @author xingguo.lei
 * @date 2018/12/26
 * @Description 拼音工具类
 */
public class PinyinUtils {
    /**
     * 转换单个字符
     *
     * @param context 上下文
     * @param c       需要转换的字符
     * @return 返回当前读音
     */
    public static String getCharacterPinYin(Context context, char c) {
        String[] pinyin = PinyinHelper.convertToPinyinArray(context, c, PinyinFormat.WITHOUT_TONE);

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null) {
            return null;
        }
        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];
    }

    /**
     * 转换一个字符串
     *
     * @param context 上下文
     * @param str     需要转换的字符串
     * @return 转换后的字符串
     */
    public static String getStringPinYin(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for (int i = 0; i < str.length(); ++i) {
            tempPinyin = getCharacterPinYin(context, str.charAt(i));
            if (tempPinyin == null) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            } else {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    /**
     * 获取字符串的首字母串
     *
     * @param context 上下文
     * @param str     需要转换的字母
     * @return 转换后的字母
     */
    public static String getStringLetter(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            String[] temp = PinyinHelper.convertToPinyinArray(context, array[i], PinyinFormat.WITHOUT_TONE);
            if (temp != null) {
                sb.append(temp[0].charAt(0));
            }
        }
        return sb.toString();
    }

    /**
     * 获取当前字符串的第一个小写字母
     *
     * @param context 上下文
     * @param str     需要转换的字符串
     * @return 当前第一个小写字母
     */
    public static String getFirstStringLetter(Context context, String str) {
        String letter = "";
        String noOp = " ";
        char[] array = str.toCharArray();
        String first = array[0] + "";
        if (isSingleChinese(first)) {
            String[] temp = PinyinHelper.convertToPinyinArray(context, array[0], PinyinFormat.WITHOUT_TONE);
            if (temp != null) {
                letter = temp[0].charAt(0) + "";
            }
        } else if (first.equals(noOp)) {
            letter = first;
        } else if (isSingleEnglishLetter(first)) {
            letter = first.toUpperCase();
        } else {
            letter = "#";
        }
        return letter;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param letter 当前字符串
     * @return 如果当前字符串包含中文返回true, 否则返回false
     */
    public static boolean isSingleChinese(String letter) {
        return letter.matches("[\\u4e00-\\u9fa5]+");
    }

    /**
     * 判断字符串中是否包含英文字母
     *
     * @param letter 当前需要验证的字符串
     * @return 如果当前字符串是字母返回true, 否则返回false
     */
    public static boolean isSingleEnglishLetter(String letter) {
        return letter.matches("[a-zA-Z]+");
    }
}
