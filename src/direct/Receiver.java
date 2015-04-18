package direct;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public class Receiver {
	public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("localhost");

		Connection connection = connectionFactory.newConnection();

		Channel channel = connection.createChannel();

		
		String EXCHANGE_NAME = "E1";
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		
		String QUEUE_NAME = "Queue1";
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "key1");

		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		
		channel.basicConsume(QUEUE_NAME, true, queueingConsumer);
		
		while (true) {
			Delivery nextDelivery = queueingConsumer.nextDelivery();
			System.out.println(new String(nextDelivery.getBody()));
		}

	}
}
