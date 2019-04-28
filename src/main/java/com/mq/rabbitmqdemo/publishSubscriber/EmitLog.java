package com.mq.rabbitmqdemo.publishSubscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class EmitLog {
    private static String EXCHANGE_NAME="logs";

    Logger logger = LoggerFactory.getLogger(EmitLog.class);


    public void emitLog(String logs){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            /**
             * exchangeDeclare(String var1, String var2)
             * var1:exchangeName
             * var2:exchangeType [direct,topic,fanout,headers]
             */
            channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
            channel.basicPublish(EXCHANGE_NAME,"",null,logs.getBytes("UTF-8"));

            logger.info("[X] Send message: {}",logs);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
