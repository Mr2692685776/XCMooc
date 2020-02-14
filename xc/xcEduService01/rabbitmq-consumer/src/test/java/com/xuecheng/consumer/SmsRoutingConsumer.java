package com.xuecheng.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author newHeart
 * @Create 2020/2/14 11:57
 */
public class SmsRoutingConsumer {
    //队列名
    private static final String QUEUE_SMS = "smsMQ";
    //交换机名
    private static final String EXCHANGE_DIRECT_INFORM="exchange_direct_inform";
    //    routingkey
    private static final String ROUTINGKEY_SMS="inform_sms";


    public static void main(String[] args) {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("106.13.191.207");
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;

        try {
//            消费者和broker建立tcp连接
            connection = connectionFactory.newConnection();
//            创建会话channel
            channel = connection.createChannel();
//            声明交换机
            channel.exchangeDeclare(EXCHANGE_DIRECT_INFORM, BuiltinExchangeType.DIRECT);
//            声明队列
            channel.queueDeclare(QUEUE_SMS, true, false, false, null);
//            绑定
            channel.queueBind(QUEUE_SMS,EXCHANGE_DIRECT_INFORM,ROUTINGKEY_SMS);
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //交换机
                    String exchange = envelope.getExchange();
                    //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    //消息内容
                    String message= new String(body,"utf-8");
                    System.out.println("receive message:"+message);
                }
            };
            channel.basicConsume(QUEUE_SMS, true,defaultConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
