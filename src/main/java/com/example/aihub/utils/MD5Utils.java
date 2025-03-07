package com.example.aihub.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * md5加密验证工具类
 * @version 1.0
 * @author 漆廷煜
 * @since 1.0
 */
public class MD5Utils {
    private final static String SALT = "iomUrHgclTpaBQ==";

    /**
     * 生成字符串的md5校验值
     *
     * @param s 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String getMD5String(String s) {
        return DigestUtil.md5Hex(s + SALT);
    }

    /**
     * 判断字符串的md5校验码是否与一个已知的md5码相匹配
     *
     * @param password  要校验的字符串
     * @param md5PwdStr 已知的md5校验码
     * @return md5校验码是否与一个已知的md5码相匹配
     */
    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }
}