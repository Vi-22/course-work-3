package ru.viktoria.cw.cw3.server;

import ru.viktoria.cw.cw3.common.Connection;
import ru.viktoria.cw.cw3.common.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private int port;
    private BlockingQueue<Message> messages;
    private CopyOnWriteArrayList<Connection> connections;

    public Server(int port) {
        this.setPort(port);
        setMessages(new ArrayBlockingQueue<>(20));
        setConnections(new CopyOnWriteArrayList<>());
    }
    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    public void setMessages(BlockingQueue<Message> messages) {
        this.messages = messages;
    }

    public void setConnections(CopyOnWriteArrayList<Connection> connections) {
        this.connections = connections;
    }

    public void setPort(int port) {
        this.port = port;
    }

    void run() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("SERVER: 400");
            Thread messageSender = new Thread(new MessageSender());
            messageSender.start();
            while (true) {
                Socket socket = serverSocket.accept();
                Connection newConnection = new Connection(socket, "Server");
                connections.add(newConnection);
                for (Connection connection : connections) {
                    Thread messageReceiver = new Thread(new MessageReceiver(connection));
                    messageReceiver.start();
                }
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private class MessageSender implements Runnable {
        @Override
        public void run() {
            while (true) {
                for (Message message : messages) {
                    if (message!=null) {
                        for (Connection connection: connections)
                            if ((!message.getSender().equals(connection.getOwner()))){
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
