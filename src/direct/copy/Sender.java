package direct.copy;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;

public class Sender {
	public static void main(String[] args) throws IOException {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("localhost");

		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();

		String EXCHANGE_NAME = "E1";
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");

		channel.basicPublish(EXCHANGE_NAME, "green.apple", true,null, "Green Aopple".getBytes());
		channel.basicPublish(EXCHANGE_NAME, "red.apple", true,null, "Red Apple".getBytes());
		channel.basicPublish(EXCHANGE_NAME, "red.cherry", true,null, "Cherry".getBytes());
		System.out.println("messages sent");
		channel.close();
		connection.close();
	}
}
