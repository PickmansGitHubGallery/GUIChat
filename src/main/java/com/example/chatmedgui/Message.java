package com.example.chatmedgui;
import java.util.Date;

public class Message {
    private User user;
    private String msg;
    private Date date;

    private User reciever; //bruges til privat beskeder

    public Message(User user, String msg, Date date, User reciever) { //Denne konstruktør bruges til at sende privat beskeder
        this.user = user;
        this.msg = msg;
        this.date = date;
        this.reciever = reciever;
    }

    public Message(User user, String msg, Date date) {
        this.user = user;
        this.msg = msg;
        this.date = date;
    }

    public Message() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getReciever() {
        return reciever;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user=" + user +
                ", msg='" + msg + '\'' +
                ", date=" + date +
                ", reciever=" + reciever +
                '}';
    }
}
