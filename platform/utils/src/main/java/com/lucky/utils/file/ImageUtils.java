package com.lucky.utils.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    /**
     * 获取图片的旋转角度
     *
     * @param file
     * @return
     */
    public static int getBitmapDegree(String file) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(file);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                    default:break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap compressBitmapByBound(Context context, Uri uri,
                                               float compressWidth, float compressHeight) {
        InputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                is = null;
            }
            int w = options.outWidth;
            int h = options.outHeight;
            float ww = compressWidth;
            float hh = compressHeight;
            int be = 1;
            if (w > h && w > ww) {
                be = (int) Math.ceil(options.outWidth * 1.0f / ww);
            } else if (w < h && h > hh) {
                be = (int) Math.ceil(options.outHeight * 1.0f / hh);
            }
            if (be <= 0) {
                be = 1;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = be;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri compressBitmapByQuanlity(Bitmap bitmap,
                                               long compressFileSize, String folder, String file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int options = 100;
            bitmap.compress(CompressFormat.JPEG, options, baos);
            while (baos.toByteArray().length > compressFileSize) {
                if (options <= 10) {
                    options -= 1;
                    if (options <= 0) {
                        break;
                    }
                } else {
                    options -= 10;
                }
                baos.reset();
                bitmap.compress(CompressFormat.JPEG, options, baos);
            }
            File compressFile = new File(folder, file);
            boolean result = FileUtil.saveBytes2CachePath(baos.toByteArray(), compressFile.getPath());
            if (result) {
                return Uri.fromFile(compressFile);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float degree) {
        Matrix m = new Matrix();
        m.setRotate(degree,
                (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (degree == 90) {
            targetX = bitmap.getHeight();
            targetY = 0;
        } else {
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap2);
        canvas.drawBitmap(bitmap, m, paint);
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return bitmap2;
    }

    /**
     * sd卡文件转为bitmap
     *
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static Bitmap file2Bitmap(String file, int width, int height) {
        FileInputStream fs = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (width > 0 && height > 0) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file, options);
                int dimen = Math.min(width, height);
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options,
                        dimen, dimen);
            }
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[32 * 1024];
            File f = new File(file);
            if (f.exists()) {
                fs = new FileInputStream(f);
                if (fs != null) {
                    return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void saveBitma2Disk(Bitmap bitmap, String folder, String filename) {
        saveBitma2Disk(bitmap, folder, filename, CompressFormat.JPEG);
    }

    public static void saveBitma2Disk(Bitmap bitmap, String folder, String filename, CompressFormat format) {
        String filepath = folder;
        if (!filepath.endsWith(File.separator)) {
            filepath += File.separator;
        }
        filepath += filename;
        try {
            FileUtil.createFolderDirectory(folder);
            File file = new File(filepath);
            if (file.exists()) {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(format, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}
