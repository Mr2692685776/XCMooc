package com.xuecheng.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @Author newHeart
 * @Create 2020/2/19 19:22
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis(){

        String key = "user_token:newheart";
        String value = "newheart";

        stringRedisTemplate.boundValueOps(key).set(value,30, TimeUnit.SECONDS);
        System.out.println(stringRedisTemplate.opsForValue().get(key));
    }
}
