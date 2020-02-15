package com.xuecheng.search.controller;

import com.xuecheng.api.es.EsCourseControllerApi;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author newHeart
 * @Create 2020/2/15 21:35
 */

@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {

    @Autowired
    private EsCourseService esCourseService;

    @Override
    @GetMapping(value="/list/{page}/{size}")
    public QueryResponseResult list(@PathVariable("page") int page,
                                    @PathVariable("size") int size,
                                    CourseSearchParam courseSearchParam) {
        return esCourseService.list(page,size,courseSearchParam);
    }
}
