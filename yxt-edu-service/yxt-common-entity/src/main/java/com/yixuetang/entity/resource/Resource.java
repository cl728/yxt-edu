package com.yixuetang.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yixuetang.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 资源实体类
 * @date 2020/11/17 16:06
 */
@TableName("t_resource")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @TableId(type = IdType.AUTO)
    private Long id;    // 主键自增id

    private Integer type;   // 文件类型 0目录 1文件

    private String ext; // 文件扩展名，如果类型为目录，则扩展名为空

    private String name;    // 文件名称

    private String location;    // 文件存储路径，若类型为目录则存储路径为空

    @TableField("content_type")
    private String contentType; // 文件内容类型，若类型为目录则文件内容类型为空

    @TableField("create_time")
    private Date createTime;    // 目录创建/文件上传时间

    @TableField("update_time")
    private Date updateTime;    // 最近一次更新时间

    @TableField(exist = false)
    private User user;  // 用户-资源 一对多

    @TableField(exist = false)
    private Resource parentResource;    // 父级资源-资源 一对多

    @TableField(exist = false)
    private List<Resource> childResourceList; // 子级资源-资源 多对一

}
