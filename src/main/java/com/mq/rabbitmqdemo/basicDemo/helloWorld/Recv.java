package com.mq.rabbitmqdemo.basicDemo.helloWorld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {

    protected static Logger logger = LoggerFactory.getLogger(Send.class);

    public static String QUEUE_NAME = "DEMO_QUEUE";

    /**
     * 使用throws而非try
     * want the process to stay alive while the consumer is listening asynchronously for messages to arrive
     * @param args
     * @throws IOException
     * @throws TimeoutException
     */
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        logger.info("[*] wait for receiving messages!");

        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            String message = new String(delivery.getBody(),"UTF-8");
            logger.info("[X] Received Message: {}",message);
        };

        channel.basicConsume(QUEUE_NAME,true,deliverCallback,consumerTag->{});

    }
}
