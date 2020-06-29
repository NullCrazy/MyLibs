package com.lucky.utils;


import java.math.BigDecimal;

/**
 * @author mylo
 * @date 2017/9/22.
 * @Description: bigDecimal计算
 * <p>
 * 代码review于2019/5/29,更新:xingguo.lei
 */
public class BigDecimalUtil {

    /**
     * 将传入的数据进行四舍五入，并且保留两位小数，末尾是0或小数点的去除
     *
     * @param d 传个double
     * @return 四舍五入后的值
     */
    public static String convertPrice(Double d) {
        int newScale = 2;
        BigDecimal decimal = new BigDecimal(d);
        BigDecimal originPrice = decimal.setScale(newScale, BigDecimal.ROUND_HALF_UP);
        char point = '.';
        char zero = '0';
        String price = originPrice.toString();
        int min = price.lastIndexOf(point);
        if (min < 1) {
            return price;
        }
        for (int i = price.length() - 1; i >= min; i--) {
            if (price.charAt(i) == zero || price.charAt(i) == point) {
                price = price.substring(0, i);
            } else {
                break;
            }
        }
        return price;
    }

    /**
     * 价格转换
     *
     * @param d 传个int
     * @return 返回你一个开心的String
     */
    public static String convertPrice(int d) {
        return convertPrice((double) d);
    }
}
