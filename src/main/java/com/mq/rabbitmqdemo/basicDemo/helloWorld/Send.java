package com.mq.rabbitmqdemo.basicDemo.helloWorld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class Send {

    @Autowired
    protected static Logger logger = LoggerFactory.getLogger(Send.class);

    public static String QUEUE_NAME = "DEMO_QUEUE";

    public void sendMessage(String message){

        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            /**
             * b:true(durable) 在服务器重启时，能否存活
             * b1:false(exclusive) 是否为当前连接的专用队列，在连接断开后，自动删除该队列
             * b2:false(autodelete) 当没有任何消费者使用时，是否自动删除该队列
             */
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);

            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

            logger.info("[x] Send Message: {}",message);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
