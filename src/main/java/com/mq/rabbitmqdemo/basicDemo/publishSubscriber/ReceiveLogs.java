package com.mq.rabbitmqdemo.basicDemo.publishSubscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    private static String EXCHANGE_NAME="logs";

    static Logger logger = LoggerFactory.getLogger(ReceiveLogs.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /*channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName,EXCHANGE_NAME,"");*/

        channel.exchangeDeclare(EXCHANGE_NAME,"direct");
        String queueName = channel.queueDeclare().getQueue();
        /**
         * queueBind(String var1, String var2, String var3)
         * var1:queueName
         * var2:exchangeName
         * var3:binding key
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"orange");
        channel.queueBind(queueName,EXCHANGE_NAME,"yellow");

        logger.info("[*] waiting for message!");

        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(),"UTF-8");
            logger.info("[X] Received message: {}",message);
        };

        channel.basicConsume(queueName,true,callback,consumerTag->{});
    }
}
