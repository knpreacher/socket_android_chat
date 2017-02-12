package com.example.knp.socketchat;

/**
 * Created by knp on 2/8/17.
 */

public class Message {

    private int type;
    private String sender;
    private String text;
    private String time;

    public Message(){}

    public Message(String sender){
        this.sender = sender;
    }
    public Message(int type){
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
