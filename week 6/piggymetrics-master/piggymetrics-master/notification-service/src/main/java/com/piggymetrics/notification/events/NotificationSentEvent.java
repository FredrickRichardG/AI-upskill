package com.piggymetrics.notification.events;

public class NotificationSentEvent {
    private String recipient;
    private String channel;
    private String status;
    private String message;

    public NotificationSentEvent() {}
    public NotificationSentEvent(String recipient, String channel, String status, String message) {
        this.recipient = recipient;
        this.channel = channel;
        this.status = status;
        this.message = message;
    }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 