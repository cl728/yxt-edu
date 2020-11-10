package com.yixuetang.user.controller;

import com.yixuetang.api.user.UserControllerApi;
import com.yixuetang.entity.auth.UserInfo;
import com.yixuetang.entity.request.auth.LoginUser;
import com.yixuetang.entity.request.user.*;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.user.service.UserService;
import com.yixuetang.utils.auth.CookieUtils;
import com.yixuetang.utils.auth.JwtConfig;
import com.yixuetang.utils.auth.JwtUtils;
import com.yixuetang.utils.exception.ExceptionThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块控制层
 * @date 2020/10/23 16:07
 */
@RestController
@RequestMapping("users")
public class UserController implements UserControllerApi {

    private static final Logger LOGGER = LoggerFactory.getLogger( UserController.class );

    @Autowired
    private UserService userService;

    @Autowired
    private JwtConfig config;

    @Override
    @GetMapping("info/id/{id}")
    public QueryResponse findOneUser(@PathVariable long id, HttpServletRequest request) {
        if (checkIdIfInvalid( id, request )) return new QueryResponse( CommonCode.INVALID_PARAM, null );
        return this.userService.findOneUser( id );
    }

    @Override
    @PutMapping("info/id/{id}")
    public CommonResponse updateUser(@PathVariable long id, @RequestBody UpdateUser updateUser, HttpServletRequest request) {
        if (checkIdIfInvalid( id, request )) return new CommonResponse( CommonCode.INVALID_PARAM );
        return this.userService.updateUser( id, updateUser );
    }

    @Override
    @PutMapping("password/id/{id}")
    public CommonResponse updatePassword(@PathVariable long id, @RequestBody PasswordUser passwordUser, HttpServletRequest request) {
        if (checkIdIfInvalid( id, request )) return new CommonResponse( CommonCode.INVALID_PARAM );
        return this.userService.updatePassword( id, passwordUser );
    }

    @Override
    @PutMapping("email/id/{id}")
    public CommonResponse updateEmail(@PathVariable long id, @RequestBody EmailUser emailUser, HttpServletRequest request) {
        if (checkIdIfInvalid( id, request )) return new CommonResponse( CommonCode.INVALID_PARAM );
        return this.userService.updateEmail( id, emailUser );
    }

    @Override
    @PutMapping("phone/id/{id}")
    public CommonResponse updatePhone(@PathVariable long id, @RequestBody PhoneUser phoneUser, HttpServletRequest request) {
        if (checkIdIfInvalid( id, request )) return new CommonResponse( CommonCode.INVALID_PARAM );
        return this.userService.updatePhone( id, phoneUser );
    }

    @Override
    @PutMapping("avatar/id/{id}")
    public CommonResponse updateAvatar(@PathVariable long id, MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        if (checkIdIfInvalid( id, request )) return new CommonResponse( CommonCode.INVALID_PARAM );

        String avatar = this.userService.updateAvatar( id, file );
        if (StringUtils.isBlank( avatar )) {
            return new CommonResponse( CommonCode.FAIL );
        }

        String cookieName = id == 3 ? config.getAdminCookieName() : config.getUserCookieName();
        String token = CookieUtils.getCookieValue( request, cookieName );
        try {
            // 重新生成 token 信息
            UserInfo userInfo = JwtUtils.getInfoFromToken( token, config.getPublicKey() );
            String newToken = JwtUtils.generateToken( new UserInfo( userInfo.getId(), userInfo.getUsername(),
                            userInfo.getIsTeacher(), avatar, userInfo.getRememberMe() ),
                    config.getPrivateKey(), config.getExpire() );
            // 重新写入cookie
            CookieUtils.setCookie( request, response, cookieName, newToken,
                    // 如果用户勾选了记住我，则将 cookie 存活时间设置为 7 天
                    userInfo.getRememberMe() ? 14 * config.getCookieMaxAge() : config.getCookieMaxAge(),
                    null, true );
        } catch (Exception e) {
            LOGGER.error( "解析token信息异常！异常原因：{}", e );
            return new CommonResponse( CommonCode.SERVER_ERROR );
        }

        return new CommonResponse( CommonCode.SUCCESS );
    }

    @Override
    @GetMapping("page/{currentPage}/{pageSize}")
    public QueryResponse findByPage(@PathVariable long currentPage, @PathVariable long pageSize, QueryPageRequestUser queryPageRequestUser) {
        return this.userService.findByPage( currentPage, pageSize, queryPageRequestUser );
    }

    @Override
    @GetMapping("roles/page/{currentPage}/{pageSize}")
    public QueryResponse findRolesByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.userService.findRolesByPage( currentPage, pageSize );
    }

    @Override
    @GetMapping("schools")
    public QueryResponse findAllSchools() {
        return this.userService.findAllSchools();
    }

    @Override
    @GetMapping("schools/page/{currentPage}/{pageSize}")
    public QueryResponse findSchoolsByPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        return this.userService.findSchoolsByPage( currentPage, pageSize );
    }

    @Override
    @PostMapping("code/{sendType}/{codeType}")
    public CommonResponse sendCode(@PathVariable int sendType, @PathVariable int codeType, @RequestBody LoginUser loginUser) {
        if (sendType != 1 && sendType != 2) {
            ExceptionThrowUtils.cast( CommonCode.INVALID_PARAM );
        }
        return this.userService.sendCode( loginUser, sendType, codeType );
    }

    @Override
    @PostMapping
    public CommonResponse register(@RequestBody RegisterUser registerUser) {
        return this.userService.register( registerUser );
    }

    @Override
    @DeleteMapping("admin/id/{userId}")
    public CommonResponse delById(@PathVariable long userId) {
        return this.userService.delById( userId );
    }

    @Override
    @GetMapping("courseId/{courseId}/{currentPage}/{pageSize}")
    public QueryResponse findPageByCourseId(@PathVariable long courseId,
                                            @PathVariable long currentPage,
                                            @PathVariable long pageSize) {
        return this.userService.findPageByCourseId( courseId, currentPage, pageSize );
    }

    private boolean checkIdIfInvalid(@PathVariable long id, HttpServletRequest request) {
        String token = CookieUtils.getCookieValue( request, id == 3 ? config.getAdminCookieName() : config.getUserCookieName() );
        UserInfo userInfo;
        try {
            userInfo = JwtUtils.getInfoFromToken( token, config.getPublicKey() );
        } catch (Exception e) {
            LOGGER.error( "解析 token 信息异常！异常原因：{}", e );
            return true;
        }
        return id != userInfo.getId();
    }
}
