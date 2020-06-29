package com.lucky.utils.aes;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author : 杜宗宁
 * @date : 2018/7/31
 * @Description: Base64解码器
 */
public class Base64Decoder extends FilterInputStream {
    /**
     * 计算时的字符表
     */
    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    /**
     * A mapping between char values and six-bit integers
     */
    private static final int[] INTS = new int[128];

    /**
     * 64位算法
     */
    private static final int BIT = 64;

    static {
        for (int i = 0; i < BIT; i++) {
            INTS[CHARS[i]] = i;
        }
    }

    /**
     * 字符长度
     */
    private int charCount;
    /**
     * 过渡值
     */
    private int carryOver;

    /***
     * 构造一个新的Base64解码器，读取给定的输入
     *
     * @param in 输入流
     */
    private Base64Decoder(InputStream in) {
        super(in);
    }

    /***
     * 对数据进行解码
     *
     * @return 解码后的字符
     * @exception IOException 发生异常所抛出的IO异常
     *
     */
    @Override
    public int read() throws IOException {
        // Read the next non-whitespace character
        char asc = '=';
        int two = 2;
        int three = 3;
        int x;
        do {
            x = in.read();
            if (x == -1) {
                return -1;
            }
        } while (Character.isWhitespace((char) x));
        charCount++;
        if (asc == x) {
            return -1;
        }
        x = INTS[x];
        int mode = (charCount - 1) % 4;
        if (mode == 0) {
            carryOver = x & 63;
            return read();
        } else if (mode == 1) {
            int decoded = ((carryOver << 2) + (x >> 4)) & 255;
            carryOver = x & 15;
            return decoded;
        }
        // Third char use previous four bits and first four new bits,
        // save last two bits
        else if (mode == two) {
            int decoded = ((carryOver << 4) + (x >> 2)) & 255;
            carryOver = x & 3;
            return decoded;
        }
        // Fourth char use previous two bits and all six new bits
        else if (mode == three) {
            int decoded = ((carryOver << 6) + x) & 255;
            return decoded;
        }
        return -1;
    }

    /***
     * 将解码数据读入字节数组并返回实际数字
     * @param buf 读取数据的缓冲区
     * @param off 数据的起始偏移量
     * @param len 要读取的最大字节数
     * @return 读取的实际字节数，如果输入结束，则返回-1
     * @exception IOException 发生错误 所抛出的io异常
     *
     */
    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (buf.length < (len + off - 1)) {
            throw new IOException("The input buffer is too small: " + len + " bytes requested starting at offset " + off + " while the buffer " + " is only " + buf.length + " bytes long.");
        }
        int i;
        for (i = 0; i < len; i++) {
            int x = read();
            if (x == -1 && i == 0) {
                return -1;
            } else if (x == -1) {
                break;
            }
            buf[off + i] = (byte) x;
        }
        return i;
    }

    /***
     * 返回给定编码字符串的解码
     *
     * @param encoded 要解码的字符串
     * @return 编码字符串的解码形式
     */
    public static String decode(String encoded) {
        if (TextUtils.isEmpty(encoded)) {
            return "";
        }
        return new String(decodeToBytes(encoded));
    }

    /***
     * 返回给定编码字符串的解码形式，以字节为单位。
     *
     * @param encoded 要解码的字符串
     * @return 编码字符串的解码形式
     */
    public static byte[] decodeToBytes(String encoded) {
        byte[] bytes = encoded.getBytes();
        Base64Decoder in = new Base64Decoder(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) (bytes.length * 0.75));
        try {
            byte[] buf = new byte[4 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
