package com.example.emailwrapper.model;

public class EmailDTO {
    private String id;
    private String from;
    private String subject;
    private String body;
    private String date;

    public EmailDTO() {
    }

    public EmailDTO(String id, String from, String subject, String body, String date) {
        this.id = id;
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
