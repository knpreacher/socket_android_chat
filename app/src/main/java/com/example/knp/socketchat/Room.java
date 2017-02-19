package com.example.knp.socketchat;

import android.graphics.Bitmap;

/**
 * Created by knp on 2/17/17.
 */

public class Room {
    private Bitmap image;
    private String header;
    private String lastMessage;
    private int type;

    public Room(){}

    public Room(String header,String lastMessage,int type){
        this.header = header;
        this.lastMessage = lastMessage;
        this.type = type;
    }
    public Room(Bitmap image,String header,String lastMessage,int type){
        this.image = image;
        this.header = header;
        this.lastMessage = lastMessage;
        this.type = type;
    }

    public Bitmap getImage() {
        return image;

    }

    public int getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
