package confirms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Prodecer {
	private static final String QUEUE_NAME = "Q1";

	public static void main(String[] args) throws IOException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		channel.confirmSelect();
		channel.exchangeDeclare("some.exchange.name", "topic",true);
		channel.queueBind("DL", "some.exchange.name", "*");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("x-dead-letter-exchange", "some.exchange.name");
		channel.queueDeclare(QUEUE_NAME, false, false, false, map);

		channel.addConfirmListener(new ConfirmListener() {
			/*
			 * Acks represent messages handled succesfully; Nacks represent
			 * messages lost by the broker. Note, the lost messages could still
			 * have been delivered to consumers, but the broker cannot guarantee
			 * this.
			 */
			@Override
			public void handleNack(long arg0, boolean arg1) throws IOException {
				System.out.println("nack of " + arg0);

			}

			@Override
			public void handleAck(long arg0, boolean arg1) throws IOException {
				System.out.println("ack of " + arg0);

			}
		});
		channel.basicPublish("", QUEUE_NAME, null, "Hello".getBytes());
		channel.close();
		connection.close();
	}
}
