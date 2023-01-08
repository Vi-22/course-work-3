package ru.viktoria.cw.cw3.server;

public class ServerApplication {
    public static void main(String[] args) {
        Server server = new Server(8090);
        server.run();
    }
}
