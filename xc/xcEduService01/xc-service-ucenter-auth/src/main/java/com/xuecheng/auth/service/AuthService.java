package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author newHeart
 * @Create 2020/2/20 19:40
 */
@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }
//      存入redis
        boolean b = storeToken(authToken);
        if (!b){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }
        return authToken;
    }

    private boolean storeToken (AuthToken authToken){
        String key = "user_token:"+authToken.getAccess_token();
        String s = JSON.toJSONString(authToken);
        stringRedisTemplate.opsForValue().set(key,s,tokenValiditySeconds,TimeUnit.SECONDS);
        return  stringRedisTemplate.getExpire(key)>0;
    }

    //    申请令牌
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String auth = uri + "/auth/oauth/token";
//        请求头参数
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String httpbasic = httpbasic(clientId, clientSecret);
        headers.add("Authorization", httpbasic);
//        请求body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            //当响应的值为400或401时候也要正常响应，不要抛出异常
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        Map map = null;
//        post请求，申请令牌
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(auth, HttpMethod.POST, httpEntity, Map.class);
            map = exchange.getBody();
        } catch (Exception e) {
            LOGGER.error("申请token错误");
            return null;
        }

        if (map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }
        AuthToken authToken = new AuthToken();
        String jwt_token = (String) map.get("access_token");
        String refresh_token = (String) map.get("refresh_token");
        String access_token = (String) map.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    private String httpbasic(String clientId, String clientSecret) {
        String basic = clientId + ":" + clientSecret;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode(basic.getBytes());
        return "Basic " + new String(encode);

    }
}
