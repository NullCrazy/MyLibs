package com.common.update;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @Description: 升级工具类
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2019-12-05 14:37
 */
public class UpdateUtils {
    /**
     * 判断安装是否成功的标志
     */
    private static String installFailTag = "Failure";

    /**
     * @param apkPath 安装APP的路径
     * @return 是否成功
     */
    public static boolean slientInstall(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        BufferedReader successStream = null;
        Process process = null;
        try {
            // 申请 su 权限
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行 pm install 命令
            String chmodCommand = "chmod 777 " + apkPath + "\n";
            dataOutputStream.write(chmodCommand.getBytes(Charset.forName("UTF-8")));
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("UTF-8")));
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorMsg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                errorMsg.append(line);
            }
            StringBuilder successMsg = new StringBuilder();
            successStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // 读取命令执行结果
            while ((line = successStream.readLine()) != null) {
                successMsg.append(line);
            }
            // 如果执行结果中包含 Failure 字样就认为是操作失败，否则就认为安装成功
            if (!(errorMsg.toString().contains(installFailTag) || successMsg.toString().contains(installFailTag))) {
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
                if (successStream != null) {
                    successStream.close();
                }
                File file = new File(apkPath);
                file.deleteOnExit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 判断设备是否root
     *
     * @return true 已root,false 未root
     */
    public static boolean isRoot() {
        File file = null;
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"};
        try {
            for (String path : paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {
                    return true;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }
}
