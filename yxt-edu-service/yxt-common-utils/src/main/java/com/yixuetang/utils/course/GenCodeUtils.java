package com.yixuetang.utils.course;

import com.yixuetang.utils.user.NumberUtils;

import java.util.Random;

/**
 * @author Colin
 * @version 1.0.0
 * @description 六位随机码生成工具类
 * @date 2020/10/31 11:39
 */
public class GenCodeUtils {
    /**
     * 自定义进制(0,1没有加入,容易与o,l混淆)
     */
    private static final char[] r = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};

    /**
     * (不能与自定义进制有重复)
     */
    private static final char b = 'o';

    /**
     * 进制长度
     */
    private static final int binLen = r.length;

    /**
     * 序列最小长度
     */
    private static final int s = 6;

    /**
     * 生成六位随机码
     *
     * @return 随机码
     */
    public static String genRandomCode() {
        StringBuilder randomCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            // 52个字母与6个大小写字母间的符号；范围为91~96
            int value = (int) (Math.random() * 58 + 65);
            while (value >= 91 && value <= 96)
                value = (int) (Math.random() * 58 + 65);
            randomCode.append( (char) value );
        }
        return randomCode.toString();
    }

    public static void main(String[] args) {
        System.out.println( GenCodeUtils.genRandomCode() );
    }
}
