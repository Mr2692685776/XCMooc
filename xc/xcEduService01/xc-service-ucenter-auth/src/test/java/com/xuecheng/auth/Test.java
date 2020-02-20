package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

/**
 * @Author newHeart
 * @Create 2020/2/19 16:10
 */


@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {
//    public static void main(String[] args) {
//        verify();
//    }

    @org.junit.Test
    public  void createToken(){
//        证书文件
        String key_location = "newheart.keystore";
//        密匙库密码
        String keystore_password = "newheartstore";
//        访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);
//        密匙工厂
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(resource, keystore_password.toCharArray());
//        密匙密码
        String keypassword = "newheart";
//        密匙别名
        String alias = "newheartykey";
//        密匙对
        KeyPair keyPair = factory.getKeyPair(alias, keypassword.toCharArray());
//        私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
//        定义payload信息
        HashMap<String, Object> map = new HashMap<>();
        map.put("id","6666");
        map.put("name","newheart");
        map.put("role","r1,r2");
        map.put("ext","1");
//      生成令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(aPrivate));
//      取出令牌
        String token = jwt.getEncoded();
        System.out.println(token);
    }

    public  void verify(){
//        生成的token
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJleHQiOiIxIiwicm9sZSI6InIxLHIyIiwibmFtZSI6Im5ld2hlYXJ0IiwiaWQiOiI2NjY2In0." +
                "XHCZ3cIOXyUGWbE5fkxhLKiSzgCC6FsuZBs2vOA_5un_oSe-HzQ13rk-MYc6fWrwTOTJ9Iuy4T-6wv8h0LxGpJ8ZDyP-25KRtrNUHcS0rNZ5ZYwDhYTylmIhMLKxPmz3gYOnJ" +
                "0wAZ7EPxwdfSb2i6jId4lfZM5FPUrMVUhlw8foEOMxfmZUc0Z_TSCUTCb_Fj44QmU" +
                "JtXiSccwX3qjShpLVuAY6zebIxT00ztoPqY4ocM_C6Dup3Yo6lh0uV5wUv-ttPDUDArjs8vN4XNeirPcxz" +
                "JP6sq8fAfmQAyWaKQRF9aly-EoSYux7l-YgBwXL-jF5hSHDPt4Aqpxn_JG8PbQ";
//        公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhRVTsXuSY/yqea/BmWrbkkJTJAEp7k53ikXyUZpphFYRk3OjtyYiVE7163uANsWI73fql+uxEMmEczLPdMCro/6NgiKLU07lO+Az2rS77WcUOKALg5t6JFq7lzLcEla+plBrlk9t9/3muAtYo6l+1onKxH1/6NGUaCH47f0K3v773wVODjFyVoSaQ76IiT15uuB1O0uOw0uxASdCHaFAO9nwynrySe6mvUfLJ4c4NexHiF6W897g1M3ZdZe0qkHBQHrhDfuM5nzOC0dKi7IsgKkPZ4AlNviaCJSfqQj4HffRamMEpg8diIBAObDvWBAaKA1UMC4vnwmpg70HRTQ+bwIDAQAB-----END PUBLIC KEY-----";
//       校验
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
//        获取payload信息
        String claims = jwt.getClaims();
        String encoded = jwt.getEncoded();
        System.out.println(claims);
        System.out.println(encoded);
    }
}
