package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author newHeart
 * @Create 2020/2/11 17:29
 */
@Data
@ToString
@NoArgsConstructor
public class CourseView implements Serializable {

    CourseBase courseBase;
    CourseMarket courseMarket;
    CoursePic coursePic;
    TeachplanNode teachplanNode;

}
