package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CoursePublishResult;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.feignClient.CmsPageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Author newHeart
 * @Create 2020/2/10 15:10
 */
@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Resource
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (null == teachplan || StringUtils.isEmpty(teachplan.getCourseid())) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            parentid = getTeachplanRoot(courseid);
        }
//        取出父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //父结点
        Teachplan teachplanParent = teachplanOptional.get();
        //父结点级别
        String parentGrade = teachplanParent.getGrade();
        //设置父结点
        teachplan.setParentid(parentid);
        teachplan.setStatus("0");//未发布
        if (parentGrade.equals("1")) {
            teachplan.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplan.setGrade("3");
        }
        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程根结点，如果没有则添加根结点
    public String getTeachplanRoot(String courseId) {
        //校验课程id
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        //取出课程计划根结点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() == 0) {
            //新增一个根结点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");//1级
            teachplanRoot.setStatus("0");//未发布
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();
    }

    public CoursePic findCoursepic(String courseId) {
        Optional<CoursePic> coursePic = coursePicRepository.findById(courseId);
        if (coursePic.isPresent()){
            return coursePic.get();
        }else {
            return null;
        }
    }

    public void saveCoursePic(String courseId, String pic) {
        CoursePic coursePic = new CoursePic();
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
    }

    public ResponseResult deleteCoursePic(String courseId) {
        coursePicRepository.deleteById(courseId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        Optional<CourseBase> one = courseBaseRepository.findById(id);
        if (one.isPresent()){
            courseView.setCourseBase(one.get());
        }
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()){
            courseView.setCourseMarket(courseMarketOptional.get());
        }
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()){
            courseView.setCoursePic(picOptional.get());
        }
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    //根据id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if(baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_DENIED_DELETE);
        return null;
    }

    public CoursePublishResult preview(String id) {
        CourseBase base = findCourseBaseById(id);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(base.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id

        CmsPageResult cp = cmsPageClient.save(cmsPage);
        if (null==cp){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        String pageId = cp.getCmsPage().getPageId();
        String pageUrl = previewUrl+pageId;
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    /**
     * 课程发布
     * 1. 查询课程信息
     * 2. 生成页面信息
     * 3. 发布页面
     * 4. 更新课程状态
     * 5. 返回结果
     * @param
     * @return
     */
    public CoursePublishResult publish(String courseId) {
//       查询课程信息
        CourseBase courseBase = this.findCourseBaseById(courseId);
//        生成页面信息，并发布页面
        CmsPostPageResult cmsPostPageResult = publish_page(courseId);
//        更新状态
        if (!cmsPostPageResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
//        更新课程状态
        this.saveCoursePubState(courseId);
//        返回结果
        return new CoursePublishResult(CommonCode.SUCCESS,cmsPostPageResult.getPageUrl());

    }
//    更新课程状态
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBase = this.findCourseBaseById(courseId);
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }

    public CmsPostPageResult publish_page(String courseId){
//        生成页面信息
        CourseBase one = this.findCourseBaseById(courseId);
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageName(courseId+".html");
        cmsPage.setPageAliase(one.getName());
        cmsPage.setPageWebPath(publish_page_webpath);
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);
        CmsPostPageResult result = cmsPageClient.postPageQuick(cmsPage);
        return result;

    }
}
