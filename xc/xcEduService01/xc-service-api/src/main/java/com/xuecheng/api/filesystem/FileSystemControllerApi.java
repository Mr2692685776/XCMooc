package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author newHeart
 * @Create 2020/2/11 8:38
 */
@Api(value = "文件管理", description = "文件的上传，下载")
public interface FileSystemControllerApi {

    @ApiOperation("文件上传")
    UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata);

}
