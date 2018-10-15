package com.example.user.calendar3;

public class Event {
    private String title;
    private String date;
    private int hour;
    private int minute;
    private String description;
    private  String location;
    private Integer position;

    public Event(String str, int hour1, int minute1, String date1, Integer position1)
    {
        this.title = str;
        this.hour = hour1;
        this.minute = minute1;
        this.date = date1;
        this.position = position1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
