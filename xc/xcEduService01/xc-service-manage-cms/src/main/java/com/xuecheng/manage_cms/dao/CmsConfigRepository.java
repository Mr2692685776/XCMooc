package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/9 14:07
 */

@Repository
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
