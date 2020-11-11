package com.yixuetang.file.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yixuetang.entity.response.CommonResponse;
import com.yixuetang.entity.response.QueryResponse;
import com.yixuetang.entity.response.code.CommonCode;
import com.yixuetang.entity.response.code.file.FileCode;
import com.yixuetang.entity.response.result.QueryResult;
import com.yixuetang.file.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 文件上传服务层接口实现类
 * @date 2020/11/11 9:44
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FastFileStorageClient storageClient;

    // 支持的文件类型：.jpg .jpeg .png .mp4 .avi .doc .xls .pdf
    private static final List<String> CONTENT_TYPES = Arrays.asList( "image/jpeg", "image/png", "video/mpeg4",
            "video/avi", "application/msword", "application/x-xls", "application/pdf" );

    private static final Logger LOGGER = LoggerFactory.getLogger( FileServiceImpl.class );

    @Override
    public CommonResponse upload(MultipartFile file) {

        if (file == null) {
            return new CommonResponse( CommonCode.INVALID_PARAM );
        }

        String originalFilename = file.getOriginalFilename();
        // 校验文件的类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains( contentType )) {
            // 文件类型不合法
            LOGGER.info( "文件类型不合法：{}", originalFilename );
            return new CommonResponse( FileCode.CONTENT_TYPE_INVALID );
        }

        try {
            // 保存到服务器
            String ext = StringUtils.substringAfterLast( originalFilename, "." );
            StorePath storePath = this.storageClient.uploadFile( file.getInputStream(), file.getSize(), ext, null );

            // 生成url地址
            String filePath = "http://www.pava.run/" + storePath.getFullPath();

            return new QueryResponse( CommonCode.SUCCESS,
                    new QueryResult<>( Collections.singletonList( filePath ), 1 ) );
        } catch (IOException e) {
            LOGGER.error( "服务器内部错误：{}，所上传文件名为：{}", e.getMessage(), originalFilename );
            return new CommonResponse( CommonCode.SERVER_ERROR );
        }

    }
}
