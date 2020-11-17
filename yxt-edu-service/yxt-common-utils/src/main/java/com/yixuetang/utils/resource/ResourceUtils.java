package com.yixuetang.utils.resource;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件工具类
 * @date 2020/11/17 16:16
 */
@Component
public class ResourceUtils {

    @Autowired
    private FastFileStorageClient storageClient;

    private static final Logger LOGGER = LoggerFactory.getLogger( ResourceUtils.class );

    public String upload(MultipartFile file) {
        StorePath storePath;
        try {
            storePath = this.storageClient.uploadFile( file.getInputStream(), file.getSize(),
                    StringUtils.substringAfterLast( file.getOriginalFilename(), "." ), null );

            // 生成url地址
            return "http://www.pava.run/" + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.error( "服务器内部错误：{}，所上传文件名为：{}", e.getMessage(), file.getOriginalFilename() );
            return null;
        }
    }

}
