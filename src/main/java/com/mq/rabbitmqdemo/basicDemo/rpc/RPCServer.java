package com.mq.rabbitmqdemo.basicDemo.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(RPC_QUEUE_NAME,false,false,false,null);
            /**
             * Purges the contents of the given queue
             */
            channel.queuePurge(RPC_QUEUE_NAME);
            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .replyTo("回调队列")
                        .build();

                String message = new String(delivery.getBody(),"UTF-8");
                String response = "receive"+message;
                channel.basicPublish("",delivery.getProperties().getReplyTo(),basicProperties,response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);

            };
        } finally {

        }

    }
}
