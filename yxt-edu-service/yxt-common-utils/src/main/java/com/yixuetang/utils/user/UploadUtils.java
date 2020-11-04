package com.yixuetang.utils.user;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description 上传文件工具类
 * @date 2020/11/4 10:49
 */
@Component
public class UploadUtils {

    @Autowired
    private FastFileStorageClient storageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList( "image/jpeg", "image/png" );

    private static final Logger LOGGER = LoggerFactory.getLogger( UploadUtils.class );

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 校验文件的类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains( contentType )) {
            // 文件类型不合法，直接返回null
            LOGGER.info( "文件类型不合法：{}", originalFilename );
            return null;
        }
        try {
            // 校验文件的内容
            BufferedImage bufferedImage = ImageIO.read( file.getInputStream() );
            if (bufferedImage == null) {
                LOGGER.info( "文件内容不合法：{}", originalFilename );
                return null;
            }

            // 保存到服务器
            String ext = StringUtils.substringAfterLast( originalFilename, "." );
            StorePath storePath = this.storageClient.uploadFile( file.getInputStream(), file.getSize(), ext, null );

            // 生成url地址
            return "http://www.pava.run/" + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.error( "服务器内部错误：{}，所上传文件名为：{}", e.getMessage(), originalFilename );
            return null;
        }
    }

}
