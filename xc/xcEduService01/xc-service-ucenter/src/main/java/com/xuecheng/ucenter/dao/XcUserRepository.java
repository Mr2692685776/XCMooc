package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/21 8:50
 */
@Repository
public interface XcUserRepository extends JpaRepository<XcUser,String> {
    XcUser findXcUserByUsername(String username);
}
