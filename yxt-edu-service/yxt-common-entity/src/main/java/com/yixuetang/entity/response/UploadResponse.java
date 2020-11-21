package com.yixuetang.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 使用 wangEditor 上传文件的响应结果
 * @date 2020/11/21 17:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    private int errno;

    private List<String> data;

}
