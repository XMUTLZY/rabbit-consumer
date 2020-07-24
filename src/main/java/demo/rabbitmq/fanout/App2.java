package demo.rabbitmq.fanout;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App2 {
    private static final String FANOUT_EXCHANGE_NAME = "fanout_exchange";
    private static final String CONSUMER_NAME = "fanout_consumer";
    public static void main( String[] args ) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        // 声明通道名称和类型
        channel.exchangeDeclare(FANOUT_EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, FANOUT_EXCHANGE_NAME, "test");
        System.out.println("等待接收消息");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(CONSUMER_NAME + " 接收到消息 '" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
