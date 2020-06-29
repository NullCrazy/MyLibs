package com.lucky.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：类型转化工具
 *
 * @author tengxs
 * @version 1.0
 */
public class TypeCastUtil {
    public static final Long toLong(String str) {
        try {
            if (StringUtil.isEmpty(str)) {
                return 0L;
            }
            return Long.valueOf(str);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static final Long toLong(Object obj) {
        try {
            if (obj == null) {
                return 0L;
            }
            return Long.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static final Float toFloat(Object obj) {
        try {
            if (obj == null) {
                return 0f;
            }
            return Float.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public static final Double toDouble(Object obj) {
        try {
            if (obj == null) {
                return 0D;
            }
            return Double.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return 0D;
        }
    }

    public static final Integer toInteger(Object obj) {
        try {
            if (obj == null) {
                return 0;
            }
            if (StringUtil.isEmpty(obj.toString())) {
                return 0;
            }
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static final Integer toInteger(String str) {
        try {
            if (StringUtil.isEmpty(str)) {
                return 0;
            }
            return Integer.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static final Short toShort(Object obj) {
        try {
            if (obj == null) {
                return 0;
            }
            if (StringUtil.isEmpty(obj.toString())) {
                return null;
            }
            return Short.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * �Ƿ�����Ч������
     *
     * @param str
     * @return
     */
    private static Pattern NUMBER_PATTERN = Pattern.compile("^[-|\\+]?\\d+(\\.\\d+)?$");
    public static final boolean isValidNumber(String str) {
        try {
            Matcher m = NUMBER_PATTERN.matcher(str.trim());
            return m.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final Integer[] toIntegerArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        Integer[] retInt = new Integer[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Integer.valueOf(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                retInt[i] = null;
            }
        }
        return retInt;
    }

    public static final int[] toBasicIntArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        int[] retInt = new int[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Integer.parseInt(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static final Short[] toShortArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        Short[] retInt = new Short[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Short.valueOf(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                retInt[i] = null;
            }
        }
        return retInt;
    }

    public static final short[] toBasicShortArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        short[] retInt = new short[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Short.parseShort(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static final Long[] toLongArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        Long[] retInt = new Long[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Long.valueOf(obj[i].toString());
                }
            } catch (Exception e) {
                retInt[i] = null;
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static final long[] toBasicLongArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        long[] retInt = new long[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Long.parseLong(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static final Float[] toFloatArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        Float[] retInt = new Float[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Float.valueOf(obj[i].toString());
                }
            } catch (Exception e) {
                retInt[i] = null;
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static final float[] toBasicFloatArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        float[] retInt = new float[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Float.parseFloat(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static final Double[] toDoubleArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        Double[] retInt = new Double[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Double.valueOf(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                retInt[i] = null;
            }
        }
        return retInt;
    }

    public static final double[] toBasicDoubleArr(Object[] obj) {
        if (obj == null) {
            return null;
        }
        double[] retInt = new double[obj.length];
        for (int i = 0; i < obj.length; i++) {
            try {
                if (obj[i] != null && StringUtil.isNotEmpty(obj[i].toString())) {
                    retInt[i] = Double.parseDouble(obj[i].toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retInt;
    }

    public static Date toDate(Object obj) throws ParseException {
        if (obj == null) {
            return null;
        }
        String objStr = obj.toString();
        if (objStr.length() == 0 || "null".equals(objStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(objStr);
    }

    /**
     * @param obj
     * @return
     * @throws Exception
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        return StringUtil.trimNull(obj.toString());
    }
}
