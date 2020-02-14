package com.xuecheng.consumer.mq;

import com.xuecheng.consumer.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;

/**
 * @Author newHeart
 * @Create 2020/2/14 15:07
 */
@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_EMAIL})
    public void receviceEmial(String msg, Message message){
        System.out.println("receive message is:"+msg);
    }
}
