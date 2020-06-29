package com.lucky.utils.file;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * @创建时间: 2017/12/14
 * @负责人: 张红军
 */
public class BitmapUtil {

    /**
     * @创建人 张红军
     * @功能描述 将bitmap转成byte数组
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap, boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();

        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (needRecycle) {
            bitmap.recycle();
        }

        return result;
    }

    public static Bitmap compressBitmap(Bitmap source, int width, int height) {
        if (source != null) {
            if (source.getWidth() > width || source.getHeight() > height) {
                return Bitmap.createScaledBitmap(source, width, height, false);
            } else {
                return source;
            }
        }
        return null;
    }
}
