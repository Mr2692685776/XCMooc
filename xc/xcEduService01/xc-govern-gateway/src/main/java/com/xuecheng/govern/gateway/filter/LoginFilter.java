package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author newHeart
 * @Create 2020/2/21 13:39
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    private AuthService authService;

    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 身份校验
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
//        获取请求容器
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();

//        获取token
        String token = authService.getTokenFromCookie(request);
        if (StringUtils.isEmpty(token)){
            access_denied();
        }
//        查询jwt
        String jwt = authService.getJwtFromHeader(request);
        if (StringUtils.isEmpty(jwt)){
            access_denied();
        }
//        从redis中校验身份令牌是否过期
        long expire = authService.getExpire(token);
        if (expire<0){
            access_denied();
        }
        return null;
    }

    private void access_denied() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        //           拒绝请求
        currentContext.setSendZuulResponse(false);
//            设置响应状态码
        currentContext.setResponseStatusCode(200);
        ResponseResult result = new ResponseResult(CommonCode.FAIL);
        String jsonString = JSON.toJSONString(result);
        currentContext.setResponseBody(jsonString);
        currentContext.getResponse().setContentType("application/json;charset=UTF-8");
    }
}
