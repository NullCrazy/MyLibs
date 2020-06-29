package com.lucky.utils.aes;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author : 杜宗宁
 * @date : 2018/7/31
 * @Description: Base64编码器
 */
public class Base64Encoder extends FilterOutputStream {
    /**
     * 字符集
     */
    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    /**
     * 字符长度
     */
    private int charCount;

    /**
     * 临时变量
     */
    private int carryOver;
    /**
     * 是否每76字节换行
     */
    private boolean isWrapBreak = true;

    /***
     * 构造一个新的Base64编码器
     *
     * @param out 输出流
     */
    private Base64Encoder(OutputStream out) {
        super(out);
    }

    /***
     * 构造一个新的Base64编码器，将输出写入给定的输出流
     *
     * @param out 输出流
     *
     * @param isWrapBreak 如果为true则携带返回值，false不带
     */
    private Base64Encoder(OutputStream out, boolean isWrapBreak) {
        this(out);
        this.isWrapBreak = isWrapBreak;
    }

    /***
     * 以编码形式将给定字节写入输出流。
     *
     * @exception IOException 抛出的io异常
     */
    @Override
    public void write(int b) throws IOException {
        int temp = 57;
        int three = 3;
        int two = 2;
        if (b < 0) {
            b += 256;
        }

        if (charCount % three == 0) {
            int lookup = b >> 2;
            carryOver = b & 3;
            out.write(CHARS[lookup]);
        } else if (charCount % three == 1) {
            int lookup = ((carryOver << 4) + (b >> 4)) & 63;
            carryOver = b & 15;
            out.write(CHARS[lookup]);
        } else if (charCount % three == two) {
            int lookup = ((carryOver << 2) + (b >> 6)) & 63;
            out.write(CHARS[lookup]);
            // last six bits
            lookup = b & 63;
            out.write(CHARS[lookup]);
            carryOver = 0;
        }
        charCount++;
        if (this.isWrapBreak && charCount % temp == 0) {
            out.write('\n');
        }
    }

    /***
     *
     * 以编码形式将给定的字节数组写入输出流
     *
     * @param buf 读取数据的缓冲区
     * @param off 数据的起始偏移量
     * @param len 要读取的最大字节数
     * @exception IOException 发生错误 所抛出的io异常
     */
    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        // This could of course be optimized
        for (int i = 0; i < len; i++) {
            write(buf[off + i]);
        }
    }

    /***
     * 关闭流，必须调用以确保正确关闭
     *
     * @exception IOException 发生错误 所抛出的io异常
     *
     */
    @Override
    public void close() throws IOException {
        int three = 3;
        int two = 2;
        if (charCount % three == 1) {
            int lookup = (carryOver << 4) & 63;
            out.write(CHARS[lookup]);
            out.write('=');
            out.write('=');
            // two leftovers
        } else if (charCount % three == two) {
            int lookup = (carryOver << 2) & 63;
            out.write(CHARS[lookup]);
            out.write('=');
        }
        super.close();
    }

    /***
     * 默认是否每76字节换行
     *
     * @param bytes
     *            the bytes to encode
     * @return the encoded form of the unencoded string
     * @throws IOException 发生错误 所抛出的io异常
     * @return 编码后的字符串
     */
    public static String encode(byte[] bytes) {
        return encode(bytes, true);
    }

    /***
     * 返回给定未编码字符串的编码形式
     *
     * @param bytes 要编码的字节
     *
     * @param isWrapBreak 是否每76字节换行
     *
     * @return 未编码字符串的编码形式
     * @throws IOException 发生错误 所抛出的io异常
     * @return 编码后的字符串
     */
    public static String encode(byte[] bytes, boolean isWrapBreak) {
        ByteArrayOutputStream out = new ByteArrayOutputStream((int) (bytes.length * 1.4));
        Base64Encoder encodedOut = new Base64Encoder(out, isWrapBreak);
        try {
            encodedOut.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                encodedOut.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return out.toString();
    }
}
