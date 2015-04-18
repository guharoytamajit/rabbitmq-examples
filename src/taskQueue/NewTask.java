package taskQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

	private static final String TASK_QUEUE_NAME = "task_queue";

	public static void main(String[] argv) throws java.io.IOException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// queueDeclare(queue name,durable ,exclusive ,autoDelete ,arguments )
		// queue - the name of the queue
		// durable - true if we are declaring a durable queue (the queue will
		// survive a server restart)
		// exclusive - true if we are declaring an exclusive queue (restricted
		// to this connection)
		// autoDelete - true if we are declaring an autodelete queue (server
		// will delete it when no longer in use)
		// arguments - other properties (construction arguments) for the queue
		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

		String message = getMessage(argv);
		// basicPublish(exchange,routingKey,props,body)
		// exchange - the exchange to publish the message to
		// routingKey - the routing key
		// props - other properties for the message - routing headers etc
		// body - the message body
		channel.basicPublish("", TASK_QUEUE_NAME,
				MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}

	private static String getMessage(String[] strings) {
		if (strings.length < 1)
			return "Hello World!";
		return joinStrings(strings, " ");
	}

	private static String joinStrings(String[] strings, String delimiter) {
		int length = strings.length;
		if (length == 0)
			return "";
		StringBuilder words = new StringBuilder(strings[0]);
		for (int i = 1; i < length; i++) {
			words.append(delimiter).append(strings[i]);
		}
		return words.toString();
	}
}