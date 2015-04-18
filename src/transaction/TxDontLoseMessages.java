package transaction;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;

public class TxDontLoseMessages {
	final static int MSG_COUNT = 1000;
	final static String QUEUE_NAME = "tx-test";
	static ConnectionFactory connectionFactory;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		connectionFactory = new ConnectionFactory();

		(new Thread(new Consumer())).start();
		(new Thread(new Publisher())).start();
	}

	static class Publisher implements Runnable {
		public void run() {
			try {
				long startTime = System.currentTimeMillis();

				Connection conn = connectionFactory.newConnection();
				Channel ch = conn.createChannel();
				ch.queueDeclare(QUEUE_NAME, true, false, true, null);
				//Enables TX mode on this channel.
				ch.txSelect();
				for (int i = 0; i < MSG_COUNT; ++i) {
					ch.basicPublish("", QUEUE_NAME,
							MessageProperties.PERSISTENT_BASIC,
							"nop".getBytes());
					//Commits a TX transaction on this channel.
					ch.txCommit();
				}
				ch.close();
				conn.close();

				long endTime = System.currentTimeMillis();
				System.out.printf("Test took %.3fs\n",
						(float) (endTime - startTime) / 1000);
			} catch (Throwable e) {
				System.out.println("foobar :(");
				System.out.print(e);
			}
		}
	}

	static class Consumer implements Runnable {
		public void run() {
			try {
				Connection conn = connectionFactory.newConnection();
				Channel ch = conn.createChannel();
				ch.queueDeclare(QUEUE_NAME, true, false, true, null);
				QueueingConsumer qc = new QueueingConsumer(ch);
				ch.basicConsume(QUEUE_NAME, true, qc);
				for (int i = 0; i < MSG_COUNT; ++i) {
					qc.nextDelivery();
				//	System.out.printf("Consumed %d\n", i);
				}
				ch.close();
				conn.close();
			} catch (Throwable e) {
				System.out.println("Whoosh!");
				System.out.print(e);
			}
		}
	}

}
