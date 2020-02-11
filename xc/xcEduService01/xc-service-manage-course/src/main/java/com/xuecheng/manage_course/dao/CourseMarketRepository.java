package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/11 17:35
 */
@Repository
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
}
