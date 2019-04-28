package com.mq.rabbitmqdemo.workQueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class NewTask {

    private static String QUEUE_NAME = "DURABLE_QUEUE";

    Logger logger = LoggerFactory.getLogger(NewTask.class);

    public void NewTask(String message){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            /**
             * declare queue as durable
             */
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);

            /**
             * MessageProperties.PERSISTENT_TEXT_PLAIN
             * mark message as persistent
             * a short time window When RabbitMQ has accepted a message and hasn't saved it
             */
            channel.basicPublish("",QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));

            logger.info("[X] Send Message: {}",message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
