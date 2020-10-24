package com.yixuetang.utils.exception;

import com.yixuetang.entity.exception.CustomException;
import com.yixuetang.entity.response.code.ResponseCode;

/**
 * @author Colin
 * @version 1.0.0
 * @description 自定义异常抛出工具类
 * @date 2020/10/23 16:30
 */
public class ExceptionThrowUtils {

    // 抛出自定义异常
    public static void cast(ResponseCode responseCode) {
        throw new CustomException( responseCode );
    }

}
