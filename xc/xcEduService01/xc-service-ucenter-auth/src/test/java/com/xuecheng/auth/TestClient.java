package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

/**
 * @Author newHeart
 * @Create 2020/2/20 18:42
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    //    rest请求
    @Test
    public void testClient() {
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String auth = uri + "/auth/oauth/token";
//        请求头参数
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String httpbasic = httpbasic("XcWebApp", "XcWebApp");
        headers.add("Authorization", httpbasic);
//        请求body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", "itcast");
        body.add("password", "123");
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
//        post请求，申请令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(auth, HttpMethod.POST, httpEntity, Map.class);
        Map body1 = exchange.getBody();
        System.out.println(body1);
    }

    private String httpbasic(String clientId, String clientSecret) {
        String basic = clientId + ":" + clientSecret;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encode = encoder.encode(basic.getBytes());
        return "Basic " + new String(encode);

    }

}
