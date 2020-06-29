package com.lucky.utils.file;

import android.content.Context;
import android.os.Environment;

import com.lucky.utils.LkUtils;
import com.lucky.utils.app.OSUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 描述：文件工具类
 *
 * @author tengxs
 * @version 1.0
 */
public class FileUtil {


    /**
     * 2个文件的复制
     *
     * @param outS
     * @param ins
     * @throws Exception
     */
    public static void copy(OutputStream outS, InputStream ins) throws Exception {
        byte[] b = new byte[1024];
        int realLen = ins.read(b);
        while (realLen > -1) {
            outS.write(b, 0, realLen);
            realLen = ins.read(b);
        }
    }

    /**
     * 将输入流转换成字节
     *
     * @param ins
     * @return
     * @throws Exception
     */
    public static byte[] getPostData(InputStream ins) throws Exception {
        java.io.DataInputStream servletIn = new java.io.DataInputStream(ins);
        java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
        byte[] bufferByte = new byte[256];
        int l = -1;
        while ((l = servletIn.read(bufferByte)) > -1) {
            bout.write(bufferByte, 0, l);
            bout.flush();
        }
        byte[] inByte = bout.toByteArray();
        servletIn.close();
        bout.close();
        if (inByte.length == 0) {
            return null;
        }
        return inByte;
    }

    /**
     * 拷贝文件
     *
     * @param src
     * @param dst
     * @return 是否成功
     */
    public static boolean copy(File src, File dst) {
        boolean success = false;
        try {
            int bufferSize = 4096;
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(src), bufferSize);
                out = new BufferedOutputStream(new FileOutputStream(dst), bufferSize);
                byte[] buffer = new byte[bufferSize];
                while (in.read(buffer) > 0) {
                    out.write(buffer);
                }
            } finally {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
                success = true;
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }


    /**
     * 读取assets文件夹对应文件的内容，内容以String返回，如果获取失败则返回null。在用的时候注意做好null判断
     */
    public static String getContentFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            return new String(getPostData(is));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取程序缓存的目录
     *
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath;
        try {
            boolean hasExternalStorage = false;
            boolean hasInternalStorage = false;
            String exterState = Environment.getExternalStorageState();
            hasExternalStorage = exterState.equalsIgnoreCase(Environment.MEDIA_MOUNTED);
            if (!hasExternalStorage) {
                if (OSUtils.hasGingerbread() && !Environment.isExternalStorageRemovable() &&
                        !exterState.equalsIgnoreCase(Environment.MEDIA_SHARED)) {
                    hasInternalStorage = true;
                }
            }
            if (hasExternalStorage || hasInternalStorage) {
                if (OSUtils.hasFroyo()) {
                    cachePath = context.getApplicationContext().getExternalCacheDir().getPath();
                } else {
                    cachePath = Environment.getExternalStorageDirectory().getPath() +
                            "/Android/data/" + context.getApplicationContext().getPackageName() + "/cache/";
                }
            } else {
                cachePath = context.getApplicationContext().getCacheDir().getPath();
            }
        } catch (Exception e) {
            File cacheDir = context.getApplicationContext().getCacheDir();
            if (cacheDir == null) {
                return null;
            }
            cachePath = cacheDir.getPath();
        }
        if (cachePath.endsWith(File.separator)) {
            cachePath = cachePath.substring(0, cachePath.length() - 1);
        }
        return cachePath;
    }

    /**
     * 兼容方法，建议使用无参方法
     */
    public static String getCachePath() {
        return getCachePath(LkUtils.getApp());
    }

    /**
     * 保存字节数组至文件
     *
     * @param bytes
     * @param filePath
     * @return
     */
    public static boolean saveBytes2CachePath(byte[] bytes, String filePath) {
        OutputStream out = null;
        try {
            out = openFileOutputStream(filePath);
            out.write(bytes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
        return false;
    }

    public static FileOutputStream openFileOutputStream(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File \'" + file + "\' exists but is a directory");
            }

            if (!file.canWrite()) {
                throw new IOException("File \'" + file + "\' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("File \'" + file + "\' could not be created");
            }

            file.createNewFile();
        }

        return new FileOutputStream(file);
    }

    /**
     * 创建文件目录
     *
     * @param directory
     */
    public static void createFolderDirectory(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
            }
        }
    }
}
