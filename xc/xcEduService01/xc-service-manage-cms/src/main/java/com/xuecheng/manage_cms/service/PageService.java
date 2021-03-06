package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_cms.config.RabbitMQConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class PageService  {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 页面查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        //分页参数
        if(page <=0){
            page = 1;
        }
        page = page -1;
        if(size<=0){
            size = 10;
        }
        Pageable pageable = PageRequest.of(page,size);

        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNoneEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        if (StringUtils.isNoneEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if (StringUtils.isNoneEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        ExampleMatcher matching = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example = Example.of(cmsPage, matching);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
//        cmsPageRepository.findAll(pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    public CmsPageResult add(CmsPage cmsPage) {
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (null==cmsPage1){
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }else {
            return new CmsPageResult(CommonCode.FAIL,null);
        }
    }

    public CmsPage findById(String id) {
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        if (cmsPage.isPresent()){
            return cmsPage.get();
        }else {
            return null;
        }
    }

    public CmsPageResult update(String id, CmsPage cmsPage) {
        CmsPage page = this.findById(id);
        if (null!=page){
            cmsPage.setPageId(id);
            CmsPage save = cmsPageRepository.save(cmsPage);
            if (save!=null){
                return new CmsPageResult(CommonCode.SUCCESS,save);
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    public ResponseResult del(String id) {
        CmsPage page = this.findById(id);
        if (null == page){
            ExceptionCast.cast(CmsCode.CMS_COURSE_IDNULL);
        }
        cmsPageRepository.deleteById(id);
        return ResponseResult.SUCCESS();
    }



//    获取数据模型
    private Map getModelByPageId(String pageId){
        CmsPage cmsPage = findById(pageId);
        if (null==cmsPage){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //通过restTemplate请求dataUrl获取模板数据
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        return entity.getBody();
    }

//    获取模板ftl
    private String getTemplateByPageId(String pageId){
//        获取页面信息
        CmsPage cmsPage = this.findById(pageId);
        if (null==cmsPage){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        Optional<CmsTemplate> opt = cmsTemplateRepository.findById(templateId);
        if (opt.isPresent()){
            CmsTemplate cmsTemplate = opt.get();

            String templateFileId = cmsTemplate.getTemplateFileId();
            if (StringUtils.isEmpty(templateFileId)){
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
            }
            //从GridFS中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //    执行静态化
    private String generateHtml(String templateContent,Map model ){
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader loader = new StringTemplateLoader();
        loader.putTemplate("template",templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(loader);

//        获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPageHtml(String pageId){
        Map model = this.getModelByPageId(pageId);
        if(model == null){
            //数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        String template = this.getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(template)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = generateHtml(template, model);
        return html;
    }

    public CmsPageResult save(CmsPage cmsPage) {

        CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (null!=page){
//            更新
            return this.update(page.getPageId(),cmsPage);
        }else {
//            新增
            return add(cmsPage);
        }

    }


    /**
     * 页面发布
     * 1. 执行页面静态化
     * 2. 保存文件
     * 3.发送消息
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
//        执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)){
            ExceptionCast.cast(CommonCode.FAIL);
        }
//        保存文件
        CmsPage page = this.saveHtml(pageId, pageHtml);
//        发送消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

        public void sendPostPage(String pageId){
            CmsPage cmsPage = findById(pageId);
            Map<String, String> map = new HashMap<>(8);
            map.put("pageId",pageId);
            String msg = JSON.toJSONString(map);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_ROUTING_CMS_POSTPAGE,cmsPage.getSiteId(),msg);
        }

    /**
     * 查询静态页面文件是否存在
     * 如果存在则删除
     * 然后保存
     * @param pageId
     * @param content
     * @return
     */
    private CmsPage saveHtml(String pageId,String content){
//        查询页面
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        CmsPage page = optional.get();
        String htmlFileId = page.getHtmlFileId();
//        如果存在则删除
        if(StringUtils.isNotEmpty(htmlFileId)){
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //保存
        InputStream inputStream = IOUtils.toInputStream(content);
        ObjectId objectId = gridFsTemplate.store(inputStream, page.getPageName());
        String fileId = objectId.toString();
        page.setHtmlFileId(fileId);
        cmsPageRepository.save(page);
        return page;
    }

    /**
     * 1. 保存页面
     * 2.页面发布：页面静态化, 存储静态文件,发送消息
     * 3. 课程路径
     *
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        
//        保存页面
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        CmsPage saveCmsPage = save.getCmsPage();
//         页面静态化
        String pageId = saveCmsPage.getPageId();
        ResponseResult responseResult = this.postPage(pageId);
        if(!responseResult.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }

//        课程路径=站点域名+站点webpath+页面webpath+页面名称
        String siteId = saveCmsPage.getSiteId();
//        获取站点
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        if (null == cmsSite){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        String pageUrl = cmsSite.getSiteDomain()+saveCmsPage.getPagePhysicalPath()+saveCmsPage.getPageName();

//       保存课程发布信息
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    private CmsSite findCmsSiteById(String id){
        Optional<CmsSite> optional = cmsSiteRepository.findById(id);
        if (optional.isPresent()){
            return optional.get();
        }else {
            return null;
        }
    }
}
