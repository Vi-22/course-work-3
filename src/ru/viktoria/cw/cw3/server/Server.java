package ru.viktoria.cw.cw3.server;

import ru.viktoria.cw.cw3.client.Client;
import ru.viktoria.cw.cw3.common.Connection;
import ru.viktoria.cw.cw3.common.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server {
    private int port;
    private BlockingQueue<Message> messages;
    private ArrayList<Connection> connections;

    public Server(int port) {
        setPort(port);
        setMessages(new ArrayBlockingQueue<>(20)); ;
        setConnections(new ArrayList<>());
    }

    public int getPort() {
        return port;
    }

    public BlockingQueue<Message> getMessages() {
        return messages;
    }

    public void setMessages(BlockingQueue<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        Server server = new Server(55555);
        server.run();
    }

    private void run() {
        System.out.println("SERVER: 400");
        Thread messageSender = new Thread(new MessageSender());
        messageSender.start();
            while (true) {
                for (Connection connection: connections) {
                Thread messageReceiver = new Thread(new MessageReceiver(connection));
                messageReceiver.start();
            }
        }
    }

    private class MessageSender implements Runnable {
        @Override
        public void run() {
            while (true) {
                for (Message message : messages) {
                    if (message!=null) {
                        for (Connection connection: connections)
                            if (message.getSender()!= connection.getOwner()){
                                try {
                                    connection.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }
                    messages.remove(message);
                }
            }
        }
    }

    private class MessageReceiver implements Runnable {
        private Connection connection;

        public MessageReceiver(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            while (true) {
                Message clientMessage = null;
                    try {
                        clientMessage = connection.readMessage();

                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("От клиента: " + clientMessage.getSender());
                    messages.add(clientMessage);
                }
            }
        }
    }
