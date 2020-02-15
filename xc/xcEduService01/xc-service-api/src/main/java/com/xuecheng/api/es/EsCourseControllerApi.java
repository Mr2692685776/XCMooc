package com.xuecheng.api.es;

import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author newHeart
 * @Create 2020/2/15 21:27
 */
@Api(value = "课程搜索",description = "课程搜索",tags = {"课程搜索"})
public interface EsCourseControllerApi {


    @ApiOperation("课程搜索")
    QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam);
}
