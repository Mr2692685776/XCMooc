package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author newHeart
 * @Create 2020/2/9 16:42
 */
@Repository
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
