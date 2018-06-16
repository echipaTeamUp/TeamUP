package com.orez.teamup.teamup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String _messageText, String _messageUser){
        this.messageText = _messageText;
        this.messageUser = _messageUser;
    }

    public ChatMessage(String _messageText, String _messageUser, long _messageTime){
        this.messageText = _messageText;
        this.messageUser = _messageUser;
        this.messageTime = _messageTime;
    }

    public ChatMessage(){}

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public String getTime(){
        Date date = new Date(messageTime*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

}
