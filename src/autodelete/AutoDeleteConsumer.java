package autodelete;

import java.io.InputStreamReader;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class AutoDeleteConsumer {
	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws java.io.IOException,
			java.lang.InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, true, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		QueueingConsumer consumer = new QueueingConsumer(channel);
		// basicConsume(queue,autoAck,callback)
		// queue - the name of the queue
		// autoAck - true if the server should consider messages acknowledged
		// once delivered; false if the server should expect explicit
		// acknowledgements
		// callback - an interface to the consumer object
		channel.basicConsume(QUEUE_NAME, true, consumer);

		
		//This will wait until next message arrives(Synchronous)
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());
		System.out.println(" [x] Received '" + message + "'");
		InputStreamReader reader = new InputStreamReader(System.in);
		System.out.println("press enter to close channel");
		reader.read();
		//once the connection is closed the queue will be deleted
		channel.close();
		connection.close();
		System.out.println("channel and connection closed");
		}
	}
