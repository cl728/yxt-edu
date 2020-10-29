package com.yixuetang.intercept;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 白名单配置类
 * @date 2020/10/29 20:38
 */
@ConfigurationProperties(prefix = "yxt.filter")
@Data
public class FilterProperties {

    private List<String> allowPaths;

}
