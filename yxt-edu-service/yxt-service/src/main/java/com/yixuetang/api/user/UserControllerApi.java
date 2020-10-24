package com.yixuetang.api.user;

import com.yixuetang.entity.response.QueryResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块的api
 * @date 2020/10/23 16:05
 */
@Api(value = "用户模块管理接口", description = "用户模块管理接口，提供用户的增、删、改、查")
public interface UserControllerApi {

    @ApiOperation("查询所有用户")
    QueryResponse findAll();

    @ApiOperation("查询所有角色")
    QueryResponse findAllRoles();
}
