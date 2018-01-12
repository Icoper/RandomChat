package com.example.dmitriysamoilov.randomchat.model;


public class ChatMessage {
    private String text;
    private String name;
    private String uid;

    public ChatMessage() {
    }

    public ChatMessage(String text, String name, String uid) {
        this.text = text;
        this.name = name;
        this.uid = uid;

    }



    public String getUid() {
        return uid;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
