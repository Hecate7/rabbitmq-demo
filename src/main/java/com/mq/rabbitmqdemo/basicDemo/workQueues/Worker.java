package com.mq.rabbitmqdemo.basicDemo.workQueues;

import com.mq.rabbitmqdemo.basicDemo.helloWorld.Send;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Worker {
    protected static Logger logger = LoggerFactory.getLogger(Send.class);

    public static String QUEUE_NAME = "DURABLE_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        final Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        logger.info("[*] Waiting for message");

        /**
         * unacknowledged messages
         */
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(),"UTF-8");
            logger.info("[X] Received message: {}",message);

            try {
                doTask(message);
            } finally {
                logger.info("[*] Done!");
                /**
                 * var1:该消息的index
                 * var3：是否批量一次性ack所有小于deliveryTag的消息
                 */
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            }
        };

        /**
         * basicConsume(queueName,autoAck,DeliverCallback,CancelCallback)
         */
        channel.basicConsume(QUEUE_NAME,false,deliverCallback,consumerTag->{});
    }

    public static void doTask(String message){
        for (char c : message.toCharArray()){
            if (c == '.'){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
