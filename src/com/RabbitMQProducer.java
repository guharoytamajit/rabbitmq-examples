package com;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeListener;


public class RabbitMQProducer {
  public static void main(String []args) throws Exception {


    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection conn = factory.newConnection();

    Channel channel = conn.createChannel();
    channel.addReturnListener(new ReturnListener() {
      public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("Message not handled!");

      }
    });

    String exchangeName = "tacoStand";
    String routingKey = "food.burrito";
    boolean durable = true;

    channel.exchangeDeclare(exchangeName, "direct", durable);

  //  channel.queueBind("abc", exchangeName, "food.burrito");




      byte[] messageBodyBytes = "Hi!!".getBytes();
      AMQP.BasicProperties properties = MessageProperties.PERSISTENT_TEXT_PLAIN;
  //    properties.setAppId("TacoStandCustomer");
      channel.basicPublish(exchangeName, routingKey, properties, messageBodyBytes) ;

    channel.close();
    conn.close();
  }
}