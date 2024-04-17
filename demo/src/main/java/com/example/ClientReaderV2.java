package com.example;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * ClientWriter
 */

public class ClientReaderV2 {
    
        public static void main(String[] args) throws Exception {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                Connection connection = factory.newConnection();
                      Channel channel = connection.createChannel();
                      
                        //declare
                        channel.exchangeDeclare("reciever", "fanout");
                        String QUEUE_NAME =  channel.queueDeclare().getQueue();
                        channel.queueBind(QUEUE_NAME,"reciever","");
                        

                        //operations
                        String message = "READALL";
                        channel.exchangeDeclare("operations", "fanout");
                        channel.basicPublish("operations", "", null, message.getBytes());
                        System.out.println(" [x] Sent '" + message + "'");
                        
                        Map<String, Integer> lineCounts = new HashMap<>();

                        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                            String message1 = new String(delivery.getBody(), "UTF-8");
                            lineCounts.put(message1, lineCounts.getOrDefault(message1, 0) + 1);
                            
                            if (lineCounts.get(message1) > 5/2) {
                                System.out.println(message1);
                                lineCounts.put(message1, 0);
                            }
                        };
                        
                        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
              

                    }
                
                }