package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author newHeart
 * @Create 2020/2/10 15:13
 */
@Mapper
public interface TeachplanMapper {

    TeachplanNode selectList(@Param("courseId") String courseId);

}
