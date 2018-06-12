package com.orez.teamup.teamup;

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String messageTime;

    public ChatMessage(String _messageText, String _messageUser){
        this.messageText = _messageText;
        this.messageUser = _messageUser;
    }

    public ChatMessage(String _messageText, String _messageUser, String _messageTime){
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

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }
}
