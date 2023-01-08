package ru.viktoria.cw.cw3.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class Connection implements AutoCloseable {
    private final String owner;
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public Connection(Socket socket, String owner) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        inputStream = new ObjectInputStream(this.socket.getInputStream());
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void sendMessage(Message message) throws IOException {
        message.setDateTime();
        outputStream.writeObject(message);
        outputStream.flush();
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        return (Message) inputStream.readObject();
    }


    @Override
    public void close() throws Exception {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
