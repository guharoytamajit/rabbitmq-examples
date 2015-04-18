package helloworld;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {

	private final static String QUEUE_NAME = "hello";

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
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		String message = "Hello World!!!!";
		// basicPublish(exchange,routingKey,props,body)
		// exchange - the exchange to publish the message to
		// routingKey - the routing key
		// props - other properties for the message - routing headers etc
		// body - the message body
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}
}