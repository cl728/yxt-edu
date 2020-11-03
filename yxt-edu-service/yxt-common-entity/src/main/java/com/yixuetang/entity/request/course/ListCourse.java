package com.yixuetang.entity.request.course;

import com.yixuetang.entity.request.user.AvatarUser;
import lombok.Data;

@Data
public class ListCourse {

    private Long id; // 课程主键自增id

    private String cName; // 课程名称

    private String cPic; // 课程图片

    private String cCode; // 6位课程码（由英文字母组成）

    private String schoolYear; // 学年

    private String semester; // 学期

    private String clazz; // 班级

    private Integer topNum; // 置顶字段，0代表不置顶

    private Boolean isFiled; // 是否被归档，true 是 false 不是

    private AvatarUser avatarUser;  //带头像和真名字段的user
}
