package ru.viktoria.cw.cw3.server;

import ru.viktoria.cw.cw3.common.Connection;
import ru.viktoria.cw.cw3.common.Message;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static int port;
    private BlockingQueue<Message> messages;
    private static CopyOnWriteArrayList<Connection> connections;

    public Server(int port) {
        this.setPort(port);
        setMessages(new ArrayBlockingQueue<>(20));
        setConnections(new CopyOnWriteArrayList<>());
    }
    public static void addConnection(Connection connection) {
        connections.add(connection);
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

    public CopyOnWriteArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(CopyOnWriteArrayList<Connection> connections) {
        this.connections = connections;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        Server server = new Server(4004);
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
                            if ((message.getSender().equals(connection.getOwner()))){
                                try {
                                    connection.sendMessage(message);
                                } catch (IOException e) {
                                    throw new RuntimeException();
                                }
                        }
                    }
                    messages.remove(message);
                }
            }
        }
    }

    private class MessageReceiver implements Runnable {
        private final Connection connection;

        public MessageReceiver(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            while (true) {
                Message message = null;
                    try {
                        message = connection.readMessage();

                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException();
                    }
                    messages.add(message);
                }
            }
        }
    }
