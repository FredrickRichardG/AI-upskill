package com.piggymetrics.notification.domain;

import javax.persistence.*;

@Entity
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String locale;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
} 