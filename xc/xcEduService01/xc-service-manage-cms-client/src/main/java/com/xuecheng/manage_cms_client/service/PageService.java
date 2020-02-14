package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @Author newHeart
 * @Create 2020/2/14 20:41
 */
@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 下载发布的页面到理物页面
     * @param pageId
     */
    public void savePageToServerPath(String pageId){
//        查询页面
        Optional<CmsPage> pageOptional = cmsPageRepository.findById(pageId);
        if (!pageOptional.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_COURSE_IDNULL);
        }
        CmsPage page = pageOptional.get();
//       获取页面信息：站点，静态页面id
        String siteId = page.getSiteId();
//       根据siteId，查询站点信息
        CmsSite cmsSite = this.getCmsSiteById(siteId);
        if (null == cmsSite){
            ExceptionCast.cast(CommonCode.FAIL);
        }
//      获取理物页面路径
        String pagePath = cmsSite.getSiteWebPath() + page.getPagePhysicalPath() + page.getPageName();
//      根据静态页面id查询
        InputStream inputStream = this.getFileById(page.getHtmlFileId());
        //将html文件保存到服务器物理路径上
        FileOutputStream fileOutputStream = null;
        try {
             fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CmsSite getCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()){
            return optional.get();
        }else {
            return null;
        }
    }

    //根据文件id从GridFS中查询文件内容
    public InputStream getFileById(String fileId){
        //文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
