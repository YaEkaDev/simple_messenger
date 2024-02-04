package com.example.messenger;

public class Message
{
    private String text;
    private String senderId;
    private String recieverId;
    private String id;

    public Message(String text, String senderId, String recieverId, String id) {
        this.text = text;
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.id = id;

    }

    public Message() {
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }
}
