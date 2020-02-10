package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author newHeart
 * @Create 2020/2/10 17:05
 */
@Repository
public interface TeachplanRepository extends JpaRepository<Teachplan,String> {

    List<Teachplan> findByCourseidAndParentid(String courseId,String parentId);
}
