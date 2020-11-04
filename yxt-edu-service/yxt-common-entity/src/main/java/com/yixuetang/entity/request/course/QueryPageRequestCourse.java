package com.yixuetang.entity.request.course;

import lombok.Data;

/**
 * @author Colin
 * @version 1.0.0
 * @description 课程管理页面分页查询实体类
 * @date 2020/11/4 19:26
 */
@Data
public class QueryPageRequestCourse {

    private String schoolYear;
    private String semester;
    private String courseName;
    private Integer minStudentCount;
    private Integer maxStudentCount;

}
