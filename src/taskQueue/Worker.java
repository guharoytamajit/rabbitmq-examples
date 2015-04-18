package taskQueue;

import java.io.IOException;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class Worker {

	private static final String TASK_QUEUE_NAME = "task_queue";

	public static void main(String[] argv) throws java.io.IOException,
			java.lang.InterruptedException {

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
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		int prefetchCount = 1;
		// prefetchCount : maximum number of messages that the server will
		// deliver, 0 if unlimited
		channel.basicQos(prefetchCount);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		// basicConsume(queue,autoAck,callback)
		// queue - the name of the queue
		// autoAck - true if the server should consider messages acknowledged
		// once delivered; false if the server should expect explicit
		// acknowledgements
		// callback - an interface to the consumer object
		channel.basicConsume(TASK_QUEUE_NAME, false, consumer);

		while (true) {
			// This will wait until next message arrives(Synchronous)
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());

			System.out.println(" [x] Received '" + message + "'");
			doWork(message);
			System.out.println(" [x] Done");
			// basicAck(long ,boolean ) Acknowledge one or several received
			// messages.
			// deliveryTag - the tag from the received AMQP.Basic.GetOk or
			// AMQP.Basic.Deliver
			// multiple - true to acknowledge all messages up to and including
			// the supplied delivery tag; false to acknowledge just the supplied
			// delivery tag.
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}

	private static void doWork(String task) throws InterruptedException {
		for (char ch : task.toCharArray()) {
			if (ch == '.')
				Thread.sleep(1000);
		}
	}
}
