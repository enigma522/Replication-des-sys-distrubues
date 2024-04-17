package com.example;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * ClientWriter
 */
public class ClientWrite {
    
        public static void main(String[] args) throws Exception {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                try (Connection connection = factory.newConnection();
                      Channel channel = connection.createChannel())
                      {
                        channel.exchangeDeclare("operations", "fanout");
                        String message = "WRITE "+args[0]+" text"+args[0]+"...";
                        channel.basicPublish("operations", "", null, message.getBytes());
                        System.out.println(" [x] Sent '" + message + "'");
                      }
              }

}