package com.lucky.utils;

/**
 * @author zhanghongjun
 * @date 17/7/11.
 * @Description: 数据转换类
 * <p>
 * 代码review于2019/5/29,更新:xingguo.lei
 */
public class PreConditionUtil {

    /**
     * 判断范型对象是否是空，如果是空就抛出空指针异常，不为空就返回该对象
     *
     * @param reference 传入的对象
     * @param <T>       数据类型范型
     * @return 返回该数据
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * 判断范型对象是否是空，如果是空就抛出空指针异常，不为空就返回该对象
     *
     * @param reference    传入的对象
     * @param errorMessage 指定的错误内容
     * @param <T>          指定类型
     * @return 返回该数据
     */
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * 传入正确条件
     *
     * @param expression true 表示传入的条件正确，false 表示传入的条件不正确，并抛出异常
     */
    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException("expression must be true");
        }
    }

    /**
     * 传入正确条件
     *
     * @param expression   true 表示传入的条件正确，false 表示传入的条件不正确，并抛出异常
     * @param errorMessage 错误内容
     */
    public static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }
}
