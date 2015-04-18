package exclusive;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class ExclusiveProducerConsumer {

	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws java.io.IOException,
			ShutdownSignalException, ConsumerCancelledException,
			InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// queueDeclare(queue name,durable ,exclusive ,autoDelete ,arguments )

		// exclusive- queue can only be accessed from the same connection.
		//exclusive queues will be deleted when the consumer disconnects

		channel.queueDeclare(QUEUE_NAME, false, true, false, null);
		String message = "Hello World!!!!";

		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");

		QueueingConsumer consumer = new QueueingConsumer(channel);

		channel.basicConsume(QUEUE_NAME, true, consumer);
		// here we can consume message because it is produced in the same
		// connection

		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		System.out.println(" [x] Received '" + new String(delivery.getBody())
				+ "'");

		channel.close();
		connection.close();
	}
}
