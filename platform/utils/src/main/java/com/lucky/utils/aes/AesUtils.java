package com.lucky.utils.aes;

import android.os.Build;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author : 杜宗宁
 * @date : 2018/7/31
 * @Description:aes加密
 */
public class AesUtils {
    /**
     * 填充
     */
    private final static String HEX = "0123456789ABCDEF";
    /**
     * AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
     */
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS7Padding";
    /**
     * AES 加密
     */
    private static final String AES = "AES";
    /**
     * SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
     */
    private static final String SHA1PRNG = "SHA1PRNG";

    /**
     * 使用md5加密对key进行加密
     *
     * @param key 需要加密的key
     * @return 加密后的数据
     */
    public static String encoderKey(String key) {
        String space = "0";
        String md5Str = key;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte[] encryContext = md.digest();

            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte anEncryContext : encryContext) {
                i = anEncryContext;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append(space);
                }
                buf.append(Integer.toHexString(i));
            }
            md5Str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    /**
     * 对密钥进行处理
     *
     * @param seed 算法种子
     * @return 加密后的数据
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        //对android 9做兼容，对密钥不进行处理，避免加密解密时密钥不一致
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return seed;
        }
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        //for android
        SecureRandom sr;
        // 7.1以上的系统需要做兼容
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sr = SecureRandom.getInstance(SHA1PRNG);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sr = SecureRandom.getInstance(SHA1PRNG, new CryptoProvider());
        } else {
            sr = SecureRandom.getInstance(SHA1PRNG, "Crypto");
        }
        sr.setSeed(seed);
        //256 bits or 128 bits,192bits
        kgen.init(128, sr);
        //AES中128位密钥版本有10个加密循环，192比特密钥版本有12个加密循环，256比特密钥版本则有14个加密循环。
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }


    /**
     * 加密
     *
     * @param key       密钥
     * @param cleartext 如果是空返回的数据
     * @return 加密后的值
     */
    public static String encrypt(String key, String cleartext) {
        if (TextUtils.isEmpty(cleartext)) {
            return cleartext;
        }
        try {

            byte[] result = encrypt(key, cleartext.getBytes());
            return Base64Encoder.encode(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密算法
     *
     * @param key   加密的key
     * @param clear 默认值
     * @return 加密后的值
     */
    private static byte[] encrypt(String key, byte[] clear) throws Exception {
        byte[] raw = getRawKey(key.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(clear);
    }

    /**
     * 解密
     *
     * @param key       解密的密钥
     * @param encrypted 加密的数据
     * @return 解密后的数据
     */
    public static String decrypt(String key, String encrypted) {
        if (TextUtils.isEmpty(encrypted)) {
            return encrypted;
        }
        try {
            byte[] enc = Base64Decoder.decodeToBytes(encrypted);
            byte[] result = decrypt(key, enc);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param key       解密的密钥
     * @param encrypted 加密的byte值
     * @return 解密后的byte数组
     */
    private static byte[] decrypt(String key, byte[] encrypted) throws Exception {
        byte[] raw = getRawKey(key.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
    }

    /**
     * 二进制转字符
     *
     * @param buf 需要拼接的byte数组
     * @return 转换后的字符串
     */
    private static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    /**
     * @param sb 字符串拼接
     * @param b  字节
     */
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
