package confirm;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class ConfirmDontLoseMessages {
    static int msgCount =1000;
    final static String QUEUE_NAME = "confirm-test";
    static ConnectionFactory connectionFactory;

    public static void main(String[] args)
        throws IOException, InterruptedException
    {
        if (args.length > 0) {
                msgCount = Integer.parseInt(args[0]);
        }

        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        // Consume msgCount messages.
        (new Thread(new Consumer())).start();
        
        // Publish msgCount messages and wait for confirms.
        (new Thread(new Publisher())).start();
    }

    @SuppressWarnings("ThrowablePrintedToSystemOut")
    static class Publisher implements Runnable {
        public void run() {
            try {
                long startTime = System.currentTimeMillis();

                // Setup
                Connection conn = connectionFactory.newConnection();
                Channel ch = conn.createChannel();
                //durable queue
                ch.queueDeclare(QUEUE_NAME, true, false, false, null);
                //Enables publisher acknowledgements on this channel
                ch.confirmSelect();

                // Publish
                for (long i = 0; i < msgCount; ++i) {
                    ch.basicPublish("", QUEUE_NAME,
                                    MessageProperties.PERSISTENT_BASIC,
                                    "nop".getBytes());
                }
				/*
				 * Wait until all messages published since the last call have
				 * been either ack'd or nack'd by the broker. If any of the
				 * messages were nack'd, waitForConfirmsOrDie will throw an
				 * IOException. When called on a non-Confirm channel, it will
				 * throw an IllegalStateException.
				 */                ch.waitForConfirmsOrDie();

                // Cleanup
                ch.queueDelete(QUEUE_NAME);
                ch.close();
                conn.close();

                long endTime = System.currentTimeMillis();
                System.out.printf("Test took %.3fs\n",
                                  (float)(endTime - startTime)/1000);
            } catch (Throwable e) {
                System.out.println("foobar :(");
                System.out.print(e);
            }
        }
    }

    static class Consumer implements Runnable {
        public void run() {
            try {
                // Setup
                Connection conn = connectionFactory.newConnection();
                Channel ch = conn.createChannel();
                ch.queueDeclare(QUEUE_NAME, true, false, false, null);

                // Consume
                QueueingConsumer qc = new QueueingConsumer(ch);
                ch.basicConsume(QUEUE_NAME, true, qc);
                for (int i = 0; i < msgCount; ++i) {
                    Delivery delivery = qc.nextDelivery();
                }

                // Cleanup
                ch.close();
                conn.close();
            } catch (Throwable e) {
                System.out.println("Whoosh!");
                System.out.print(e);
            }
        }
    }
}