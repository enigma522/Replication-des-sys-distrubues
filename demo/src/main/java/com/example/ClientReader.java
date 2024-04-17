package com.example;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * ClientWriter
 */

public class ClientReader {
    
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
                        String message = "READLAST";
                        channel.exchangeDeclare("operations", "fanout");
                        channel.basicPublish("operations", "", null, message.getBytes());
                        System.out.println(" [x] Sent '" + message + "'");

                        GetResponse response = channel.basicGet(QUEUE_NAME, false);

                        if (response == null) {
                            System.out.println("No message available in the queue.");
                            
                        } else {
            
                            byte[] body = response.getBody();
                            String message1 = new String(body, "UTF-8");
                            System.out.println("Received message: '" + message1 + "'");
                        }
              

                    }
                
                }