package com.example.ribani.parkirpintar.model;

public class Request {
    int timeReserve;
    String layout, park;

    public int getTimeReserve() {
        return timeReserve;
    }

    public void setTimeReserve(int timeReserve) {
        this.timeReserve = timeReserve;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }
}
