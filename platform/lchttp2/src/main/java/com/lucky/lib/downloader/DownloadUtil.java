package com.lucky.lib.downloader;


import androidx.annotation.NonNull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * @Description: 下载工具类
 * @Author: xmq mingqiang.xu@luckincoffee.com
 * @Date: 2019/1/24 下午10:03
 */
class DownloadUtil {
    /**
     * 关闭close
     * @param close Closeable
     */
    static void close(Closeable close){
        if (close !=null) {
            try {
                close.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改文件后缀
     * @param fileName 文件名
     * @param suffix 新的文件后缀名
     * @return 修改后的文件名
     */
    static String modifySuffix(@NonNull String fileName, @NonNull String suffix) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName + suffix;
        }
        return fileName.substring(0, index) + suffix;
    }

    /**
     * 重命名文件
     * @param filePath  文件目录
     * @param newPathName  文件名字
     * @return true：重命名成功  false:重命名失败
     */
    static boolean renameFile(@NonNull String filePath, @NonNull String newPathName) {
        File file = new File(filePath);
        File newFile = new File(newPathName);
        return !(newFile.exists() && !newFile.delete()) && file.renameTo(newFile);
    }

    /**
     * 删除下载文件
     * @param fileName 文件名
     */
    static boolean deleteDownloadFile(String path,@NonNull String fileName) {
        File file = new File(path + fileName);
        return file.exists() && file.delete();
    }
    
}
