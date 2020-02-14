package com.xuecheng.mq;

import com.xuecheng.mq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author newHeart
 * @Create 2020/2/14 14:20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerBoot {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMsg(){
        String emailMsg = "你有一封邮件，请注意查收";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_DIRECT_INFORM,
                RabbitmqConfig.ROUTINGKEY_EMAIL,emailMsg);
    }
}
