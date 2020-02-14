package com.xuecheng.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author newHeart
 * @Create 2020/2/14 14:04
 */

/**
 * 主要配置交换机Exchange，队列Queue
 */
@Configuration
public class RabbitmqConfig {

    //队列名
    public static final String QUEUE_EMAIL = "emailMQ";
    public static final String QUEUE_SMS = "smsMQ";
    //交换机名
    public static final String EXCHANGE_DIRECT_INFORM="exchange_direct_inform";
    //    routingkey
    public static final String ROUTINGKEY_EMAIL="inform_email";
    public static final String ROUTINGKEY_SMS="inform_sms";

//    注入交换机
    @Bean(EXCHANGE_DIRECT_INFORM)
    public Exchange exchange(){
        return new DirectExchange(EXCHANGE_DIRECT_INFORM,false,false);
    }

//    注入email队列
    @Bean(QUEUE_EMAIL)
    public Queue QUEUE_EMAIL(){
        return new Queue(QUEUE_EMAIL,true);
    }

//    注入sms队列
    @Bean(QUEUE_SMS)
    public Queue QUEUE_SMS(){
        return new Queue(QUEUE_SMS,true);
    }

//    绑定
    @Bean
    public Binding BINDING_QUEUE_EMAIL(@Qualifier(EXCHANGE_DIRECT_INFORM)Exchange exchange,
                                       @Qualifier(QUEUE_EMAIL)Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_SMS(@Qualifier(EXCHANGE_DIRECT_INFORM)Exchange exchange,
                                     @Qualifier(QUEUE_SMS)Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }
}
