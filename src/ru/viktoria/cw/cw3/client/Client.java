package ru.viktoria.cw.cw3.client;

import ru.viktoria.cw.cw3.common.Connection;
import ru.viktoria.cw.cw3.common.Message;
import ru.viktoria.cw.cw3.server.Server;

import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Scanner;


public class Client {
    private String name;
    private String ip;
    private int port;
    private Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public Client(String ip, int port, String name) {
        this.setIp(ip);
        this.setPort(port);
        this.setName(name);
        this.createNewConnection();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void createNewConnection() {
        try {
            Socket clientSocket = new Socket(this.ip, this.port);
            this.connection = new Connection(clientSocket, this.name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    protected void run() {
        Thread messageReceiver = new Thread(new MessageReceiver());
        messageReceiver.start();
        Thread messageSender = new Thread(new MessageSender());
        messageSender.start();
    }


    private class MessageSender implements Runnable {
        private final Scanner scanner = new Scanner(System.in);

        @Override
        public void run() {
            while (true) {
                System.out.println("Введите ваше сообщение");
                String text = this.scanner.nextLine();
                if (text.equals("/exit")) {
                    System.exit(0);
                }
                Message message = new Message(Client.this.getName(), text);
                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Message inbox = connection.readMessage();
                    System.out.println(inbox.getDateTime() +
                            " сообщение от " +
                            inbox.getSender() + ": " +
                            inbox.getText());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

