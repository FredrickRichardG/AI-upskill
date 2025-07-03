package com.piggymetrics.course.domain;

public class OutboxEvent {
    private String id;
    private String eventType;
    private String payload;
    private boolean sent;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }
} 