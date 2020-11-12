package com.yixuetang.entity.response.code;

/**
 * @author Colin
 * @version 1.0.0
 * @description 响应代码接口
 * - 通用响应代码：20000 - 29999
 * - 用户模块响应代码：30000 - 39999
 * - 授权模块响应代码：40000 - 49999
 * - 课程模块响应代码：50000 - 59999
 * - 资源模块响应代码：60000 - 69999
 * - 作业模块相应代码：70000 - 79999
 * - 公告模块相应代码：80000 - 89999
 * @date 2020/10/23 15:48
 */
public interface ResponseCode {


    // 操作是否成功
    boolean success();

    // 响应代码
    int code();

    // 响应消息
    String message();

}
