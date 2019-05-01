package com.mq.rabbitmqdemo.basicDemo.delayQueues;

import com.mq.rabbitmqdemo.utils.RabbitUtils;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Publish {
    static Logger logger = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(RabbitUtils.USER_NAME);
        factory.setPassword(RabbitUtils.USER_PASSWORD);
        factory.setHost(RabbitUtils.IP_ADDRESS);
        factory.setPort(RabbitUtils.PORT);
        factory.setVirtualHost(RabbitUtils.VIRTUAL_HOST);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare("dlExchange","direct");
            channel.exchangeDeclare("exactExchange","direct",true,false,false,null);

            Map<String, Object> queueMap = new HashMap<>();
            queueMap.put("x-dead-letter-exchange","dlExchange");
            queueMap.put("x-message-ttl",5000);

            channel.queueDeclare("dlQueue",true, false, false, null);
            channel.queueBind("dlQueue","dlExchange","routing");

            channel.queueDeclare("exactQueue",true,false,false,queueMap);
            channel.queueBind("exactQueue","exactExchange","routing");

            String message = "HELLO,What's your name?";
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .build();


            channel.confirmSelect();

            SortedSet<Long> unConfirmSet = new TreeSet<>();
            for (int i = 0; i < 10; i++) {
                Long seq = channel.getNextPublishSeqNo();
                channel.basicPublish("exactExchange","routing",basicProperties,message.getBytes("UTF-8"));
                unConfirmSet.add(seq);
            }

            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long l, boolean b) throws IOException {
                    logger.info("unConfirm set before: {}",unConfirmSet);
                    logger.info("message seq: {}, multiple: {}",l,b);
                    if (b){
                        unConfirmSet.headSet(l+1).clear();
                    } else {
                        unConfirmSet.remove(l);
                    }
                    logger.info("unConfirm set after: {}",unConfirmSet);

                }

                @Override
                public void handleNack(long l, boolean b) throws IOException {
                    logger.info("unConfirm set: {}",unConfirmSet);
                    logger.info("message seq: {}, multiple: {}",l,b);
                }
            });

            logger.info("Date: {}, Send message: {}",new Date(), message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
