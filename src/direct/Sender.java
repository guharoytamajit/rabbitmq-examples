package direct;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender {
public static void main(String[] args) throws IOException {
	ConnectionFactory connectionFactory = new ConnectionFactory();
	connectionFactory.setHost("localhost");
	
	Connection connection = connectionFactory.newConnection();
	Channel channel = connection.createChannel();
	
String EXCHANGE_NAME = "E1";
channel.exchangeDeclare(EXCHANGE_NAME, "direct");
	
	
	String MESSAGE = "Hello w orld";
	channel.basicPublish(EXCHANGE_NAME, "key1", null, MESSAGE.getBytes());
	channel.close();
	connection.close();
}
}
