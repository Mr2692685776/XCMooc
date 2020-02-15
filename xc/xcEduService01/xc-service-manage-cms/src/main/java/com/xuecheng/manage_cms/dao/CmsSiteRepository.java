package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/15 10:24
 */
@Repository
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
