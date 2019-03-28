package com.deytola.assignment;

public class Message {
    private String text;  //message to be displayed
    private boolean isPublisher; //is message published by user?


    public Message (String text, boolean isPublisher){
        this.text = text;
        this.isPublisher = isPublisher;
    }

    public String getText(){
        return text;
    }

    public boolean isUserPublisher(){
        return isPublisher;
    }
}
