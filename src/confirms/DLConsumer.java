package confirms;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class DLConsumer {
private static final String QUEUE_NAME = "DL";

public static void main(String[] args) throws IOException {
	ConnectionFactory connectionFactory = new ConnectionFactory();
	Connection connection = connectionFactory.newConnection();
	final Channel channel = connection.createChannel();
	
//	channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	
	channel.basicConsume(QUEUE_NAME, true, new DefaultConsumer(channel){

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope,
				BasicProperties properties, byte[] body) throws IOException {
			super.handleDelivery(consumerTag, envelope, properties, body);
			
			System.out.println("reveived message in DL"+ new String(body));
		}
		
	});
}
}
