package com.pax.storelib.local;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 本地app管理工具类
 * Created by lhg on 2016/8/19.
 * Edit by Johnny Liu 2016/8/29
 */
public class AppManager {

    private Context ctx;

    public AppManager(Context context) {
        this.ctx = context;

    }

    /**
     * 启动应用
     *
     * @param packageName
     * @return
     */
    public void startApp(String packageName) {
        PackageManager packageManager = ctx.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ctx.startActivity(intent);
        }
    }


    /**
     * 检查apk 是否已经被安装到 PM 中
     *
     * @param packageName
     * @return
     */
    public void checkAppIsExist(String packageName){

        int count = 0;
        boolean tag =true;
        while(tag){
            if(count>=60){
                Log.e("Store", ">>install timeout.. ");
                break;
            }
            else {
                Log.e("Store", ">>install delay.. ");
                delay(1000);
            }

            for (PackageInfo packageInfo : ctx.getPackageManager().getInstalledPackages(0)) {
                if(packageInfo.packageName.equals(packageName)){
                    Log.e("Store", ">>install ok ");
                    tag=false;
                    break;
                }
            }

            count++;

        }
    }


    /**
     * 系统的和用户安装的所有app,不包含自身所在的app
     *
     * @return
     */
    public List<AppInfo> loadApp() {
        List<AppInfo> result = new ArrayList();
        //系统应用列表
        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            //非系统应用并且不是自己
            if (
                    (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                      //      && (!packageInfo.packageName.equals(ctx.getPackageName())) //自己也能更新
                    ) {
                result.add(new AppInfo(packageInfo, pm));
            }
        }
        return result;
    }


    /**
     * 静默安装
     *
     * @param tmpApkPath
     * @return
     */
    public boolean silentInstall(String tmpApkPath) {

        boolean result = false;

        File file = new File(tmpApkPath);
        if (!file.exists())
            return false;

        Uri uri = Uri.fromFile(file);
        PackageManager pm = ctx.getPackageManager();

        try {
            Class<?>[] argsClass = new Class[4];
            argsClass[0] = Uri.class;
            argsClass[1] = Class.forName("android.content.pm.IPackageInstallObserver");
            argsClass[2] = int.class;
            argsClass[3] = String.class;

            Object[] params = new Object[4];
            params[0] = uri;
            params[1] = null;
            params[2] = 2;
            params[3] = null;

            Method method = Class.forName("android.content.pm.PackageManager").getMethod("installPackage", argsClass);
            method.setAccessible(true);
            method.invoke(pm, params);

            result = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 静默卸载
     *
     * @param packageName
     * @return
     */
    public boolean silentUninstall(String packageName) {
        boolean result = false;

        try {
            PackageManager pm = ctx.getPackageManager();

            Class<?>[] argsClass = new Class[3];
            argsClass[0] = String.class;
            argsClass[1] = Class.forName("android.content.pm.IPackageInstallObserver");
            argsClass[2] = int.class;

            Object[] params = new Object[3];
            params[0] = packageName;
            params[1] = null;
            params[2] = 2;

            Method[] methods = Class.forName("android.content.pm.PackageManager").getMethods();
            int size = methods.length;
            for (int i = 0; i < size; i++) {
                if ("deletePackage".equals(methods[i].getName())) {
                    Method method = methods[i];
                    method.setAccessible(true);
                    method.invoke(pm, params);
                    break;
                }
            }

            result = true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void delay(int milliSeconds) {
        try {
            if (milliSeconds != 0)
                TimeUnit.MILLISECONDS.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
