package com.lucky.utils.app;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xingguolei
 * @date 2018/4/16 18:47.
 * @Description: 获取跟当前进程相同id的 进程数\
 */
public final class CheckVirtuaUtils {

    /**
     * 获取与当前进程相同uid的进程数
     *
     * @return
     */
    public static int processCount() {
        return getAppList().size();
    }

    private static List<AndroidAppProcess> getAppList() {
        int uidInt = android.os.Process.myUid();
        List<AndroidAppProcess> appProcessList = new ArrayList<>();
        for (AndroidAppProcess androidAppProcess : AndroidProcesses.getRunningAppProcesses()) {
            if (uidInt == androidAppProcess.uid) {
                appProcessList.add(androidAppProcess);
            }
        }
        return appProcessList;
    }
}
