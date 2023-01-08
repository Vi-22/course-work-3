package ru.viktoria.cw.cw3.client;

import ru.viktoria.cw.cw3.common.Connection;
import ru.viktoria.cw.cw3.common.Message;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


public class Client {
    private String name;
    private String ip;
    private int port;
    private Connection connection;
    private final Scanner scanner;

    public Client(String ip, int port, String name) {
        this.setIp(ip);
        this.setPort(port);
        this.setName(name);
        this.createNewConnection();
        this.scanner = new Scanner(System.in);
    }
    public void createNewConnection() {
        try {
            this.connection = new Connection(new Socket(ip, port), this.name);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        Client client1 = new Client("37.252.94.233", 49239, "Tom");
        client1.run();
    }

    private void run() {
        Thread messageReceiver = new Thread(new MessageReceiver());
        messageReceiver.start();
        Thread messageSender = new Thread(new MessageSender());
        messageSender.start();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
                    throw new RuntimeException();
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
