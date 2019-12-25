package com.hlyf.selfsupport.util;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.hlyf.selfsupport.ui.welcomeActive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.hlyf.selfsupport.util.UIUtils.runOnUiThread;


/**
 * author : HLQ
 * e-mail : 925954424@qq.com
 * time   : 2018/01/17
 * desc   : 获取mac 兼容6.0获取 以及4g环境下获取失败
 * version: 1.0
 */
public class MobileUtils {

    /**
     * 获取失败默认返回值
     */
    public static final String ERROR_MAC_STR = "02:00:00:00:00:00";

    // Wifi 管理器
    private static WifiManager mWifiManager;

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context){
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                imei = tm.getDeviceId();
            }else {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * 实例化WifiManager对象
     *
     * @param context 当前上下文对象
     * @return
     */
    private static WifiManager getInstant(Context context) {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        return mWifiManager;
    }

    /**
     * 开启wifi
     */
    public static void getStartWifiEnabled() {
        // 判断当前wifi状态是否为开启状态
        if (!mWifiManager.isWifiEnabled()) {
            // 打开wifi 有些设备需要授权
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 获取手机设备MAC地址
     * MAC地址：物理地址、硬件地址，用来定义网络设备的位置
     * modify by heliquan at 2018年1月17日
     *
     * @param context
     * @return
     */
    public static String getMobileMAC(Context context) {
        mWifiManager = getInstant(context);
        // 如果当前设备系统大于等于6.0 使用下面的方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getAndroidHighVersionMac();
        } else { // 当前设备在6.0以下
            return getAndroidLowVersionMac(mWifiManager);
        }
    }

    /**
     * Android 6.0 设备兼容获取mac
     * 兼容原因：从Android 6.0之后，Android 移除了通过WiFi和蓝牙API来在应用程序中可编程的访问本地硬件标示符。
     * 现在WifiInfo.getMacAddress()和BluetoothAdapter.getAddress()方法都将返回：02:00:00:00:00:00
     *
     * @return
     */
    public static String getAndroidHighVersionMac() {
        String str = "";
        String macSerial = "";
        try {
            // 由于Android底层基于Linux系统 可以根据shell获取
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
                macSerial = getAndroidVersion7MAC();
            }
        }
        return macSerial;
    }

    /**
     * Android 6.0 以下设备获取mac地址 获取失败默认返回：02:00:00:00:00:00
     *
     * @param wifiManager
     * @return
     */
    @NonNull
    private static String getAndroidLowVersionMac(WifiManager wifiManager) {
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String mac = wifiInfo.getMacAddress();
            if (TextUtils.isEmpty(mac)) {
                return ERROR_MAC_STR;
            } else {
                return mac;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mac", "get android low version mac error:" + e.getMessage());
            return ERROR_MAC_STR;
        }
    }

    /**
     * 兼容7.0获取不到的问题
     *
     * @return
     */
    public static String getAndroidVersion7MAC() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            Log.e("mac", "get android version 7.0 mac error:" + e.getMessage());
        }
        return ERROR_MAC_STR;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    private static File apkFile;
    private static ProgressDialog prodialog;

    //下载apk的地址
    public static void downloadAPK(Context context,String uploadUrl) {
        //1). 主线程, 显示提示视图: ProgressDialog
        prodialog = new ProgressDialog(context);
        prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        prodialog.setTitle("程序下载中...........");
        prodialog.setCancelable(false);
        prodialog.show();

        //准备用于保存APK文件的File对象 : /storage/sdcard/Android/package_name/files/xxx.apk
        apkFile = new File(context.getExternalFilesDir(null), "update.apk");

        //2). 启动分线程, 请求下载APK文件, 下载过程中显示下载进度
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //1. 得到连接对象
                    String path = uploadUrl;
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //2. 设置
                    //connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(10000);
                    //3. 连接
                    connection.connect();
                    //4. 请求并得到响应码200
                    int responseCode = connection.getResponseCode();
                    if(responseCode==200) {
                        //设置dialog的最大进度
                        prodialog.setMax(connection.getContentLength());


                        //5. 得到包含APK文件数据的InputStream
                        InputStream is = connection.getInputStream();
                        //6. 创建指向apkFile的FileOutputStream
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        //7. 边读边写
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while((len=is.read(buffer))!=-1) {
                            fos.write(buffer, 0, len);
                            //8. 显示下载进度
                            prodialog.incrementProgressBy(len);

                        }

                        fos.close();
                        is.close();
                    }
                    //9. 下载完成, 关闭, 进入3)
                    connection.disconnect();

                    //3). 主线程, 移除dialog, 启动安装
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prodialog.dismiss();
                            installAPK(context,apkFile);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //09-05 12:59:20.553: I/ActivityManager(1179): Displayed com.android.packageinstaller/.PackageInstallerActivity: +282ms
    }

    public static boolean installAPK(Context context, File apkFile) {
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, "com.hlyf.selfsupport.fileprovider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                context.startActivity(intent);
            }
            return true;
        } else {
            return false;
        }
    }

}

