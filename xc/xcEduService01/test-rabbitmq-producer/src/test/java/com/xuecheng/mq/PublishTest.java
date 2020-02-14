package com.xuecheng.mq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author newHeart
 * @Create 2020/2/14 10:49
 */
public class PublishTest {

    //队列
    private static final String QUEUE_EMAIL = "emailMQ";
    private static final String QUEUE_SMS = "smsMQ";
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

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
            //建立TCP新连接
            connection = connectionFactory.newConnection();
            //创建会话通道,生产者和mq服务所有通信都在channel通道中完成
            channel = connection.createChannel();
            /**
             * 声明交换机
             * 参数明细：
             * 交换机的类型
             * fanout：对应的rabbitmq的工作模式是 publish/subscribe
             * direct：对应的Routing	工作模式
             * topic：对应的Topics工作模式
             * headers： 对应的headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            //声明队列，如果队列在mq 中没有则要创建
            channel.queueDeclare(QUEUE_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_SMS,true,false,false,null);
            //交换机绑定队列
            channel.queueBind(QUEUE_EMAIL,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_SMS,EXCHANGE_FANOUT_INFORM,"");

            for (int i = 0; i <5 ; i++) {
                //消息内容
                String message = "happy new year";
                channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
                System.out.println("newheart send Msg --> "+message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            //先关闭通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
