package com.hlyf.selfsupport.exception;

/**
 * Created by Administrator on 2019-07-15.
 */
public class testException {
    public static void main(String[] args) {
        try {
            beforeMethod2("", "", "", "");
            System.out.println("1");
            beforeMethod("", "", "", "");

        } catch (ApiSysException e) {
            e.printStackTrace();
            System.out.println(e.getExceptionEnum().getCode());
        }
    }

    public static void beforeMethod(String appId, String appSecret, String requestMethod, String meituanurl) throws ApiSysException {
        if("POST".equals(requestMethod) || "GET".equals(requestMethod)){

        }else {
            throw new ApiSysException(ErrorEnum.JSON_ANALYZING_FAIL);
        }
        if("".equals(appId) || "".equals(appSecret) ||  !meituanurl.contains("http")){
            throw new ApiSysException(ErrorEnum.SSCO001001);
        }
    }

    public static void beforeMethod2(String appId, String appSecret, String requestMethod, String meituanurl) throws ApiSysException {
        if("POST".equals(requestMethod) || "GET".equals(requestMethod)){

        }else {
            throw new ApiSysException(ErrorEnum.SSCO008002);
        }
        if("".equals(appId) || "".equals(appSecret) ||  !meituanurl.contains("http")){
            throw new ApiSysException(ErrorEnum.SSCO008002);
        }
    }
}
