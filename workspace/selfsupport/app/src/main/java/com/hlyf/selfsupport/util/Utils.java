package com.hlyf.selfsupport.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.hlyf.selfsupport.codec.binary.StringUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

public class Utils {

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
        // 转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    /**
     * 把毫秒转换成：1:20:30这里形式
     *
     * @param timeMs
     * @return
     */
    public  String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    /**
     * 判断是否是网络的资源
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri) {
        boolean reault = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                reault = true;
            }
        }
        return reault;
    }


    /**
     * 得到网络速度
     * 每隔两秒调用一次
     * @param context
     * @return
     */
    public String getNetSpeed(Context context) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)== TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed  = String.valueOf(speed) + " kb/s";
        return  netSpeed;
    }

    /**
     *
     * @param context  上下文
     * @param assertFile  文件
     */
    private static AssetManager assetManager=null;
    private static MediaPlayer player = null;

    public static void playMedia(Context context,String assertFile) {
        if(player==null){
            player = new MediaPlayer();
        }
        if(assetManager==null){
            assetManager = context.getResources().getAssets();
        }
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd(assertFile);
            player.stop();
            player.reset();
            player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步保存 保存信息到文件
     * @param result
     * @return
     */
    public static void saveFile(String result,final String fileName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                sb.append(result);
                try {
                    long timestamp = System.currentTimeMillis();
                    String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                   // String fileName = "crash-" + time + "-" + timestamp + ".log";
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        String path = "/sdcard/selfsupport/crash/";
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(path + fileName, true);
                        fos.write("\r\n".getBytes());
                        fos.write(sb.toString().getBytes());
                        fos.close();
                    }
                } catch (Exception e) {
                    Log.e("File Exception", "an error occured while writing file...", e);
                }
            }
        }).start();
    }

    public static String getVersion(Context context)//获取版本号
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static boolean allEntityfieldIsNUll(Object o){
        try{
            for(Field field:o.getClass().getDeclaredFields()){
                field.setAccessible(true);//把私有属性公有化
                Object object = field.get(o);
                if(object instanceof CharSequence){
                    if(!TextUtils.isEmpty((String)object)){
                        return false;
                    }
                }else{
                    if(object!=null){
                        return false;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }





}
