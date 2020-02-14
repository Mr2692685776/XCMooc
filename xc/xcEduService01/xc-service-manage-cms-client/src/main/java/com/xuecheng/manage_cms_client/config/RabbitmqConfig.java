package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author newHeart
 * @Create 2020/2/14 20:06
 */
@Configuration
public class RabbitmqConfig {

    //队列bean名
    public static  final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
//    交换机名
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    @Value("${xuecheng.mq.queue}")
    public  String queue_cms_postpage_name;
    @Value("${xuecheng.mq.routingKey}")
    public  String routingKey;

    /**
     * 配置交换机
     * @return
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    /**
     * 配置queue
     * @return
     */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue queue(){
        return new Queue(queue_cms_postpage_name);
    }

    @Bean
    public Binding binding(@Qualifier(EX_ROUTING_CMS_POSTPAGE)Exchange exchange,
                           @Qualifier(QUEUE_CMS_POSTPAGE)Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();

    }



}
