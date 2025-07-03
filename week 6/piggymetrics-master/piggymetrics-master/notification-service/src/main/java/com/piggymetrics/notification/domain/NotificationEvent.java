package com.piggymetrics.notification.domain;

public class NotificationEvent {
    public enum Channel { EMAIL, PUSH }

    private Channel channel;
    private String recipient; // email or device token
    private String subject;   // email subject or push title
    private String body;      // email body or push body
    private String dataJson;  // optional data for push
    private String attachment; // optional for email

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getDataJson() { return dataJson; }
    public void setDataJson(String dataJson) { this.dataJson = dataJson; }
    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }
} 