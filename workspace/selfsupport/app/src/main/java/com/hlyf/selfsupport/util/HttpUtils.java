package com.hlyf.selfsupport.util;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;



/**
 * Created by jc on 2017/7/4.
 */

public class HttpUtils
{

    // 请求成功的回调 返回字符串
    public interface RequestCallBack
    {
        void success(String successResult);

        void fail(String failResult);
    }

    // 请求成功的回调 返回字节数组
    public interface BytesCallBack
    {
        void onResponse(byte[] result);
    }

    // 请求成功的回调 返回bitmap
    public interface BitmapCallBack
    {
        void onResponse(Bitmap bitmap);
    }
    // 请求成功的回调 返回json对象
    public interface JsonCallBack
    {
        void onResponse(JSONObject jsonObject);
    }


    public  static HttpUtils httpUtils;   //防止多个线程访问时
    private static OkHttpClient client;
    private Handler handler;

    private HttpUtils()
    {
        handler = new Handler(Looper.getMainLooper());
        client = getOkHttpClient();
    }

    public static OkHttpClient getOkHttpClient()
    {
        if (client == null)
        {
            //synchronized (HttpUtils.class)
            //{
                if (client == null)
                {
                    okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder();
                    ClientBuilder.readTimeout(30, TimeUnit.SECONDS);// 读取超时
                    ClientBuilder.connectTimeout(10, TimeUnit.SECONDS);// 连接超时
                    ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);// 写入超时

                    client = ClientBuilder.build();
                }
            //}
        }
        return client;
    }

    //采用单例模式获取对象
    public static HttpUtils getInstance() {
        HttpUtils instance = null;
        if (httpUtils == null) {
            synchronized (HttpUtils.class) {                //同步代码块
                if (instance == null) {
                    instance = new HttpUtils();
                    httpUtils = instance;
                }
            }
        }
        return httpUtils;
    }

    /**
     * Get请求
     *
     * @param url
     * @param callback
     */
    public static void doGet(String url, Callback callback)
    {
        Request request = new Request.Builder().url(url).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     *
     * @param url
     * @param mapParams
     * @param callBack
     */
    public static void doGet(String url, Map<String, String> mapParams, final RequestCallBack callBack)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(url);

        if( mapParams != null)
        {
            sb.append("?");
            for (String key : mapParams.keySet())
            {
                sb.append(key);
                sb.append("=");
                sb.append(mapParams.get(key));
                sb.append("&");
            }


            sb.delete(sb.lastIndexOf("&"),sb.lastIndexOf("&")+1);

        }
        Request request = new Request.Builder().url(sb.toString()).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //断网时会触发  网络请求失败
                httpUtils.returnResult("request_fail",callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.e("httpUtils",response.body().string());
                    httpUtils.returnResult(response.body().string(),callBack);
                }
                else
                {
                    //服务器 不响应时
                    httpUtils.returnResult("request_fail",callBack);
                }
            }
        });
    }

    /**
     * Post请求发送键值对数据
     *
     * @param url
     * @param mapParams
     * @param callBack
     */
    public static void doPost(String url, Map<String, String> mapParams, final RequestCallBack callBack)
    {
        FormBody.Builder builder = new FormBody.Builder();
        if( mapParams != null)
        {
            for (String key : mapParams.keySet())
            {
                builder.add(key, mapParams.get(key));
            }
        }

        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //断网时会触发  网络请求失败
                httpUtils.returnResult("request_fail",callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    httpUtils.returnResult(response.body().string(),callBack);
                }
                else
                {
                    //服务器 不响应时
                    httpUtils.returnResult("request_fail",callBack);
                }

            }
        });
    }

    /**
     * Post请求发送键值对数据
     *
     * @param url
     * @param mapParams
     */
    public static String doSyncPost(String url, Map<String, String> mapParams)
    {
        String responseStr = "request_fail";
        FormBody.Builder builder = new FormBody.Builder();
        if( mapParams != null)
        {
            for (String key : mapParams.keySet())
            {
                builder.add(key, mapParams.get(key));
            }
        }

        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Call call = getOkHttpClient().newCall(request);
        try
        {
            Response response = call.execute();
            if(response.isSuccessful())
            {
                responseStr = response.body().string();
            }
        }
        catch (IOException e)
        {
            Log.e("httpUtils","请求出错");
            e.printStackTrace();
        }


        return  responseStr;
    }

    /**
     * Post请求发送JSON数据
     *
     * @param url
     * @param jsonParams
     * @param callback
     */
    public static void doPost(String url, String jsonParams, Callback callback)
    {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
            jsonParams);
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public static void doFile(String url, String pathName, String fileName, Callback callback)
    {
        // 判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        // 创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(
            MultipartBody.FORM).addFormDataPart(MEDIA_TYPE.type(), fileName,
                RequestBody.create(MEDIA_TYPE, new File(pathName)));
        // 发出请求参数
        Request request = new Request.Builder().header("Authorization",
            "Client-ID " + "9199fdef135c122").url(url).post(builder.build()).build();

        Call call = getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 根据文件路径判断MediaType
     *
     * @param path
     * @return
     */
    private static String judgeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 下载文件
     * 
     * @param url
     * @param fileDir
     * @param fileName
     */
    public static void downFile(String url, final String fileDir, final String fileName)
    {
        Request request = new Request.Builder().url(url).build();
        Call call = getOkHttpClient().newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {

            }

            @Override
            public void onResponse(Call call, Response response)
                throws IOException
            {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    File file = new File(fileDir, fileName);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }
        });
    }

    private void returnResult(final String resultStr , final RequestCallBack callBack)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        if(resultStr.equals("request_fail"))
                        {
                            callBack.fail(resultStr);
                        }
                        else
                        {
                            callBack.success(resultStr);
                        }


                    } catch (Exception e) {
                        Log.e("httpUtils","回调返回请求结果错误");
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
