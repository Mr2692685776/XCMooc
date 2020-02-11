package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.xuecheng.framework.domain.filesystem.FileSystem;

import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * @Author newHeart
 * @Create 2020/2/11 8:42
 */
@Service
public class FileSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);

    @Autowired
    private FileSystemRepository fileSystemRepository;


    //  上传文件
    public UploadFileResult upload(MultipartFile file, String filetag, String businesskey, String metadata) {

        if (file == null) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        String fileId;
        String path;
        String fileName;
        try {
            inputStream = file.getInputStream();
            fileId = UUID.randomUUID().toString();

            fileName = file.getOriginalFilename();

            String suffix = fileName.substring(fileName.indexOf('.'),fileName.length());
            String uploadFilePath = new File(getClass().getResource("/").getPath())
                    .toString()+"\\static\\img\\";
            path = uploadFilePath + fileId+suffix;
            File upFile = new File(path);
            fileOutputStream = new FileOutputStream(upFile);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, len);
            }
            inputStream.close();
            fileOutputStream.close();
            //创建文件信息对象
            FileSystem fileSystem = new FileSystem();
            fileSystem.setFileId(fileId);
            fileSystem.setFilePath(path);
            fileSystem.setBusinesskey(businesskey);
            fileSystem.setFiletag(filetag);
            fileSystem.setFileName(fileName);
            fileSystem.setFileSize(file.getSize());
            fileSystem.setFileType(file.getContentType());

            if (StringUtils.isNotEmpty(metadata)){
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            }
            fileSystemRepository.save(fileSystem);
            return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
        } catch (IOException e) {
            e.printStackTrace();
            return new UploadFileResult(CommonCode.FAIL,null);
        }
    }
}
