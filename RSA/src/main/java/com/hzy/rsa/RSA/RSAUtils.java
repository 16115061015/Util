package com.hzy.rsa.RSA;

/**
 * User: hzy
 * Date: 2020/8/25
 * Time: 11:20 PM
 * Description:
 */
public class RSAUtils {
    static {
        System.loadLibrary("RSALib");
    }
    public static native String rsaLock();
}
