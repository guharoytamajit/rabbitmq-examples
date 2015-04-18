package autodelete;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AutoDeleteProducer {

	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws java.io.IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// queueDeclare(queue name,durable ,exclusive ,autoDelete ,arguments )

		// autoDelete - true if we are declaring an autodelete queue (server
		// will delete it when no longer in use) ie.queue will be deleted when
		// number of consumer becomes zero.

		channel.queueDeclare(QUEUE_NAME, false, false, true, null);
		String message = "Hello World!!!!";

			
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");


		channel.close();
		connection.close();
	}
}