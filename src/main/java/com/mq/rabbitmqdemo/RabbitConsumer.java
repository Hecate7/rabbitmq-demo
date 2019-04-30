package com.mq.rabbitmqdemo;

import com.mq.rabbitmqdemo.utils.RabbitUtils;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitConsumer {

    static Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{new Address(RabbitUtils.IP_ADDRESS,RabbitUtils.PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(RabbitUtils.USER_NAME);
        factory.setPassword(RabbitUtils.USER_PASSWORD);
        Connection connection = factory.newConnection(addresses);

        final Channel channel = connection.createChannel();
        /**
         * 如果消费者在同一个信道上订阅了另一个队列，酒无法再声明队列了
         * 必须先取消订阅，然后将信道置为“传输”模式，才能声明队列
         */
        channel.queueBind(RabbitUtils.QUEUE_NAME_DEMO1,RabbitUtils.EXCHANGE_NAME_DEMO1,RabbitUtils.ROUTING_KEY_DEMO1);
        channel.basicQos(32);

        /**
         * consumerTag:消费者标签
         * delivery[_envelope,_properties,_body]
         */
        DeliverCallback callback = (consumerTag,delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            logger.info("[X] Receiver message: {}", message);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /**
             * 当autoAck设置为false，队列中的消息分成了两个部分：一部分是等待投递给消费者的消息，一部分是已经投递给消费者但还没收到消费者确认信号的消息
             * 如果一直没收到消费者的确认信号，并且消费者已经断开连接，则会安排该消息重新进入列队
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            /**
             * 明确拒绝当前消息，一次只能拒绝一条消息
             * channel.basicReject(delivery.getEnvelope().getDeliveryTag(),requeue);
             * requeue:为true，会重新将这条消息存入队列；为false,会立即把消息从队列中移除
             */


            /**
             * 请求RabbitMQ重新发送还未被确认的消息
             * channel.basicRecover(requeue);
             * requeue:为false,同一条消息会分配给与之前不同的消费者，默认为true
             */
        };
        /**
         * 推模式
         */
        channel.basicConsume(RabbitUtils.QUEUE_NAME_DEMO1,false,callback,consumerTag->{});
        /**
         * 拉模式，如果只想从队列获得单条消息而不是持续订阅，采用basicGet进行消费
         */
        /*GetResponse response = channel.basicGet(RabbitUtils.QUEUE_NAME_DEMO1,false);
        channel.basicAck(response.getEnvelope().getDeliveryTag(),false);*/
        Thread.sleep(5000);

    }
}
