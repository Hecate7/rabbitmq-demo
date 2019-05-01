package com.mq.rabbitmqdemo.basicDemo.delayQueues;

import com.mq.rabbitmqdemo.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Consumer {
    static Logger logger = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(RabbitUtils.USER_NAME);
        factory.setPassword(RabbitUtils.USER_PASSWORD);
        factory.setHost(RabbitUtils.IP_ADDRESS);
        factory.setPort(RabbitUtils.PORT);
        factory.setVirtualHost(RabbitUtils.VIRTUAL_HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueBind("dlQueue","dlExchange","");
        channel.basicQos(5);

        DeliverCallback callback = (consumerTag,delivery) -> {
            String message = new String(delivery.getBody(),"UTF-8");
            logger.info("date: {}, Receive message: {}",new Date(),message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),true);
        };
        channel.basicConsume("dlQueue",false,callback,consumerTag -> {});
    }

}
