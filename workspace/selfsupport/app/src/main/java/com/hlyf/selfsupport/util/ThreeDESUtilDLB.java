package com.hlyf.selfsupport.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hlyf.selfsupport.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 依赖包
 * commons-codec:commons-codec:1.6
 */
public class ThreeDESUtilDLB {

    private static SecretKey getKey(String key) throws Exception {
        //
        DESedeKeySpec dks = new DESedeKeySpec(Base64.decodeBase64(key));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);
        return securekey;
    }
    /**
     * 解密
     */
    public static String decrypt(String text, String key, String charset)
            throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(2, getKey(key));
        byte[] textBytes = parseHexStr2Byte(text);
        byte[] bytes = cipher.doFinal(textBytes);
        String decryptString = new String(bytes, charset);
        return trimToEmpty(decryptString);
    }

    /**
     * 加密
     */
    public static String encrypt(String text, String key, String charset) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        SecretKey secKey = getKey(key);
        cipher.init(1, secKey);
        byte[] textBytes = text.getBytes(charset);
        byte[] bytes = cipher.doFinal(textBytes);
        String encryptBase64EncodeString = parseByte2HexStr(bytes);
        return trimToEmpty(encryptBase64EncodeString);
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = ((byte) (high * 16 + low));
        }
        return result;
    }

    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; ++i) {
            String hex = Integer.toHexString(buf[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * md5 签名
     */
    public static String md5(String text, String key) throws Exception {
        byte[] bytes = (text + key).getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 255) < 16) {
                sb.append("0");
            }
            sb.append(Long.toString((long) (bytes[i] & 255), 16));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * md5 验证签名
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        String md5Text = md5(text, key);
        return md5Text.equalsIgnoreCase(md5);
    }

    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        return uuid;
    }
    public static void main(String[] args) throws Exception {
        test01();
    }

    private static void test04() throws Exception {
        String desKey = "6l7HDfs0bumRv6dF6SPf5XXpXkWwyP36";

        String st1="{\"merchantNo\":\"XILIAN\",\"cipherJson\":" +
                "\"ae8bb26153c2db4f4cb16f51e4df3852f0fccb4d9475b03309569a7e8aa5729efb7cbae8228e6d178cc2dcbb0e82712e7840108dc5f3c4706c06bee060810d10211c56965a69ad4deda60fcf890cdbdddf0f67dc09d15d26a4fcf1b291713666dbf8b3db77151fc35d966754675e31df\",\"sign\":\"e67ca965f200495b0c545d7a679ee4bd\",\"systemId\":\"jdpay-offlinepay-isvaccess\"," +
                "\"uuid\":\"79e6f115-fbe5-4f28-8c0a-6a0facbeee41\",\"tenant\":\"1519833291\",\"storeId\":\"1001\"}";
        JSONObject jsonObject= JSON.parseObject(st1);

        st1=jsonObject.getString("cipherJson");
        System.out.println(decrypt(st1, desKey, "utf-8"));//{"fa":"a"}

        String uuid=jsonObject.getString("uuid");
        String md5Key = "D8C2313325E1E049FE19AFAC122B987F";
        String md5 = md5(st1 + uuid, md5Key);
        System.out.println(md5);//64f0c913d4655e214e7661d4dc8b73ae
        System.out.println(verify(st1 + uuid, md5Key, md5));//true

    }

    private static void test03() throws Exception {
        String desKey = "6l7HDfs0bumRv6dF6SPf5XXpXkWwyP30";

        String st1="{\"merchantNo\":\"XILIAN\",\"cipherJson\":" +
                "\"ae8bb26153c2db4f4cb16f51e4df3852f0fccb4d9475b03309569a7e8aa5729efb7cbae8228e6d178cc2dcbb0e82712e7840108dc5f3c4706c06bee060810d10211c56965a69ad4deda60fcf890cdbdddf0f67dc09d15d26a4fcf1b291713666dbf8b3db77151fc35d966754675e31df\",\"sign\":\"e67ca965f200495b0c545d7a679ee4bd\",\"systemId\":\"jdpay-offlinepay-isvaccess\"," +
                "\"uuid\":\"79e6f115-fbe5-4f28-8c0a-6a0facbeee41\",\"tenant\":\"1519833291\",\"storeId\":\"1001\"}";

        JSONObject jsonObject= JSON.parseObject(st1);

        st1=jsonObject.getString("cipherJson");
        System.out.println(decrypt(st1, desKey, "utf-8"));//{"fa":"a"}
        String uuid=jsonObject.getString("uuid");
        String md5Key = "D8C2313325E1E049FE19AFAC122B987F";
        String md5 = md5(st1 + uuid, md5Key);
        System.out.println(md5);//64f0c913d4655e214e7661d4dc8b73ae
        System.out.println(verify(st1 + uuid, md5Key, md5));//true

    }

    private static void test02() throws Exception {
        String desKey = "6l7HDfs0bumRv6dF6SPf5XXpXkWwyP30";

        String st1="ae8bb26153c2db4f4cb16f51e4df3852f0fccb4d9475b03309569a7e8aa5729efb7cbae8228e6d178cc2dcbb0e82712e7840108dc5f3c4706c06bee060810d10211c56965a69ad4deda60fcf890cdbdddf0f67dc09d15d26a4fcf1b291713666dbf8b3db77151fc35d966754675e31df";
        System.out.println(decrypt(st1, desKey, "utf-8"));//{"fa":"a"}
    }
    private static void test01() throws Exception {
        String desKey = "6l7HDfs0bumRv6dF6SPf5XXpXkWwyP30";
        String st1 = encrypt("{\"fa\":\"a\"}", desKey, "utf-8");
        System.out.println(st1); //304f594dccd16875c385e052fc718daf

        // st1="ae8bb26153c2db4f4cb16f51e4df3852f0fccb4d9475b03309569a7e8aa5729efb7cbae8228e6d178cc2dcbb0e82712e7840108dc5f3c4706c06bee060810d10211c56965a69ad4deda60fcf890cdbdddf0f67dc09d15d26a4fcf1b291713666dbf8b3db77151fc35d966754675e31df";
        System.out.println(decrypt(st1, desKey, "utf-8"));//{"fa":"a"}

        String uuid="f8e3c39d-6976-4379-a0ab-7594d934cf9e";
        String md5Key = "D8C2313325E1E049FE19AFAC122B987F";
        String md5 = md5(st1 + uuid, md5Key);
        System.out.println(md5);//64f0c913d4655e214e7661d4dc8b73ae
        System.out.println(verify(st1 + uuid, md5Key, md5));//true

        uuid=getUUID();
        System.out.println(uuid);//true
        System.out.println(uuid.length());//true
    }
}
