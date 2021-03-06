package com.xuecheng.manage_course.feignClient;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author newHeart
 * @Create 2020/2/11 19:41
 */
@FeignClient("XC-SERVICE-MANAGE-CMS")
public interface CmsPageClient {

    @PostMapping("/cms/page/save")
    CmsPageResult save(@RequestBody CmsPage cmsPage);

    /**
     * 一键发布页面
     */
    @PostMapping("/cms/page/postPageQuick")
    CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
