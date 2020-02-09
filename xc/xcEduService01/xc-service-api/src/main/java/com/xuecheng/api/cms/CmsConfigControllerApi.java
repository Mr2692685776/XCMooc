package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author newHeart
 * @Create 2020/2/9 14:13
 */
@Api(value = "cms配置接口",description = "cms配置接口，提供页面查")
public interface CmsConfigControllerApi {

    @ApiOperation("根据id,查询配置")
    CmsConfig getmodel(String id);
}
