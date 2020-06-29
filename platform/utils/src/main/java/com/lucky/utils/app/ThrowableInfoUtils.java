package com.lucky.utils.app;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @创建时间: 2018/4/16 18:47.
 * @author : xingguolei
 * @功能描述:  異常信息堆棧信息
 * @页面进入路径:
 */
public final class ThrowableInfoUtils {

    public static String getThrowableStr(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        LineNumberReader reader = new LineNumberReader(new StringReader(
                sw.toString()));
        StringBuffer buffer = new StringBuffer();
        try {
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line).append("\r\n");
                line = reader.readLine();
            }
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        return buffer.toString();
    }
}
