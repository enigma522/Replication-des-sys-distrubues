package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rabbitmq.client.*;


public class Replica {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
                if (args.length < 1) {
                    System.out.println("Usage: java id");
                    System.exit(1);
                }
                channel.exchangeDeclare("operations", "fanout");
                String QUEUE_NAME =  channel.queueDeclare().getQueue();
                channel.queueBind(QUEUE_NAME,"operations","");
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    if (message.contains("WRITE")) {
                        System.out.println(" [x] Writing to file...");
        
                        File directory = new File("./rep" + args[0]);
                        if (!directory.exists()) {
                            directory.mkdirs(); 
                        }
                        try (FileWriter writer = new FileWriter("./rep"+args[0]+"/file.txt", true)) { 
                            String text = message.split("WRITE ")[1];
                            writer.write(text + "\n");
                        } catch (IOException e) {
                            e.printStackTrace(); 
                        }
                    }
                    else if (message.contains("READLAST")) {
                        System.out.println(" [x] Reading from file...");
                        try {
                            File file = new File("./rep"+args[0]+"/file.txt");
                            if (file.exists()) {
                                System.out.println(" [x] File exists");
                                System.out.println(" [x] Reading last line...");
                                String lastLine = "";
                                String line;
                                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
                                while ((line = reader.readLine()) != null) {
                                    lastLine = line;
                                }
                                reader.close();
                                channel.exchangeDeclare("reciever", "fanout");
                                channel.basicPublish("reciever", "", null, lastLine.getBytes());
                                System.out.println(" [x] Sent '" + lastLine + "'");
                            } else {
                                System.out.println(" [x] File does not exist");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (message.contains("READALL")) {
                        System.out.println(" [x] Reading from file...");
                        try {
                            File file = new File("./rep"+args[0]+"/file.txt");
                            if (file.exists()) {
                                System.out.println(" [x] File exists");
                                System.out.println(" [x] Reading all lines...");
                                channel.exchangeDeclare("reciever", "fanout");
                                String line;
                                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
                                while ((line = reader.readLine()) != null) {
                                    
                                    channel.basicPublish("reciever", "", null, line.getBytes());
                                    System.out.println(" [x] Sent '" + line + "'");
                                    
                                }
                                reader.close();
                                
                            } else {
                                System.out.println(" [x] File does not exist");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    
                };
                
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
                
              }
}

    
