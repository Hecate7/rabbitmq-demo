package com.mq.rabbitmqdemo;

import com.mq.rabbitmqdemo.utils.RabbitUtils;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitProducer {

    static Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitUtils.IP_ADDRESS);
        factory.setPort(RabbitUtils.PORT);
        factory.setUsername(RabbitUtils.USER_NAME);
        factory.setPassword(RabbitUtils.USER_PASSWORD);

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();


        /**
         * exchangeDeclare(String exchange, String type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
         * internal:是否是内置的，如果设置为true，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式
         */
        Map<String, Object> exchangeMap = new HashMap<>();
        exchangeMap.put("alternate-exchange","备份Exchange_name");
        exchangeMap.put("x-dead-letter-exchange","死信交换器");
        exchangeMap.put("x-dead-letter-routing-key","dlx-routing-key,未指定使用原队列");
//        channel.exchangeDeclare(RabbitUtils.EXCHANGE_NAME_DEMO1,"direct",true,false,map);
        channel.exchangeDeclare(RabbitUtils.EXCHANGE_NAME_DEMO1,"direct",true,false,null);

        /**
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * exclusive:是否排他，如果被声明为true,该队列仅对首次声明它的连接可见，并在断开连接时自动删除，适用于一个客户端同时发送和读取消息的应用场景
         */
        Map<String, Object> queueMap = new HashMap<>();
        /** 设置队列ttl，一旦消息过期，就会从队列中抹去 */
        queueMap.put("x-message-ttl",6000);
        queueMap.put("x-max-priority",10);  //设置队列优先级
        channel.queueDeclare(RabbitUtils.QUEUE_NAME_DEMO1,true,false,false,null);
        /**
         * exchangeBind(String destination, String source, String routingKey, Map<String, Object> var4):将交换器与交换器绑定
         */
        channel.queueBind(RabbitUtils.QUEUE_NAME_DEMO1,RabbitUtils.EXCHANGE_NAME_DEMO1,RabbitUtils.ROUTING_KEY_DEMO1);

        String message = "Hello World!";
        /**
         * basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
         * mandatory:true,交换器无法根据自身的类型和路由键找到一个符合条件的队列，RabbitMQ会调用basic.return将消息返回给生产者；false,直接丢弃
         * immediate：true,如果交换器将消息路由到队列时发现队列上不存在任何消费者,那么这条消息将不会存入队列中。当与路由键匹配的所有队列都没有消费者时，RabbitMQ会调用basic.return将消息返回给生产者
         */
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder()
                .contentType("text/plain")
                .deliveryMode(2)    //持久化消息
                .priority(0)        //消息的优先级
                .expiration("60000");   //设置单条消息的ttl，即使消息过期，也不会马上从队列中抹去，等到此消息即将被消费时再判定是否过期，如果过期再进行删除

        channel.basicPublish(RabbitUtils.EXCHANGE_NAME_DEMO1,RabbitUtils.ROUTING_KEY_DEMO1,builder.build(),message.getBytes("UTF-8"));

        /** 使用mandatory参数，若不添加returnListener，可以使用备份交换器 */
//        channel.basicPublish(RabbitUtils.EXCHANGE_NAME_DEMO1,RabbitUtils.ROUTING_KEY_DEMO1,true,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
//        channel.addReturnListener((replyCode, ReplyText, exchange, RoutingKey, basicProperties, bytes) -> {
//            String messages = new String(bytes,"UTF-8");
//            logger.info("basic.return返回的结果：{}",messages);
//        });


        logger.info("[X] Send message: {}",message);

        /**
         * 消息持久化
         * 1.exchange设置durable
         * 2.queue设置durable
         * 3.message设置deliveryMode为2
         * 4.订阅消费队列将autoAck参数设置为true
         * 5.镜像队列机制
         */
    }
}
