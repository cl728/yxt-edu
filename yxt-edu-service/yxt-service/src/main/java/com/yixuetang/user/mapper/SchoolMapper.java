package com.yixuetang.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yixuetang.entity.user.School;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Colin
 * @version 1.0.0
 * @description 用户模块学校实体类持久层接口
 * @date 2020/10/25 15:14
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
}
