package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author newHeart
 * @Create 2020/2/10 15:03
 */
@Api(value = "课程管理", description = "课程管理接口，提供页面的增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("课程计划查询")
    TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("获取课程基础信息")
    CoursePic findCoursePic(String courseId);

    @ApiOperation("添加课程图片")
    ResponseResult addCoursePic(String courseId,String pic);

    @ApiOperation("删除课程图片")
    ResponseResult deleteCoursePic(String courseId);
}
