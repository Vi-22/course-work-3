package ru.viktoria.cw.cw3.client;

public class ClientApplication {
    public static void main(String[] args) {
        Client client1 = new Client("127.0.0.1", 8090, "Tom");
        client1.run();
    }
}
