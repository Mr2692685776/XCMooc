package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/11 9:56
 */
@Repository
public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
}
