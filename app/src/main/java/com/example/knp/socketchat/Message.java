package com.example.knp.socketchat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by knp on 2/8/17.
 */

public class Message implements Parcelable{

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

    protected Message(Parcel in) {
        type = in.readInt();
        sender = in.readString();
        text = in.readString();
        time = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(sender);
        dest.writeString(text);
        dest.writeString(time);
    }
}
