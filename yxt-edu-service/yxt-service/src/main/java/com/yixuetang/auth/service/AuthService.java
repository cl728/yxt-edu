package com.yixuetang.auth.service;

/**
 * @author Colin
 * @version 1.0.0
 * @description 认证模块服务层接口
 * @date 2020/10/23 21:07
 */
public interface AuthService {

    /**
     * 校验用户名和密码的合法性并生成 token
     *
     * @param username   用户名
     * @param password   密码
     * @param rememberMe 是否勾选了记住我 true是 false不是
     * @param userType   用户类型 1管理员 2普通用户
     * @return 生成的 token 信息
     */
    String authByPwd(String username, String password, boolean rememberMe, int userType);

    /**
     * 校验邮箱验证码的正确性并生成 token
     *
     * @param email      邮箱地址
     * @param code       验证码
     * @param rememberMe 是否勾选了记住我 true是 false不是
     * @param userType   用户类型 1管理员 2普通用户
     * @return 生成的 token 信息
     */
    String authByEmail(String email, String code, boolean rememberMe, int userType);

    /**
     * 校验手机验证码的正确性并生成 token
     *
     * @param phone      手机号码
     * @param code       验证码
     * @param rememberMe 是否勾选了记住我 true是 false不是
     * @param userType   用户类型 1管理员 2普通用户
     * @return 生成的 token 信息
     */
    String authByPhone(String phone, String code, boolean rememberMe, int userType);
}
