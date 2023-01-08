package ru.viktoria.cw.cw3.common;


import ru.viktoria.cw.cw3.client.Client;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private final String sender;
    private final String text;
    private LocalDateTime dateTime;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public void setDateTime() {
        dateTime = LocalDateTime.now();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}