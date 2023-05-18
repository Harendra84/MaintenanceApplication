package com.gttech.maintenanceapplication.mess;

public class Mess {

    private int messId;
    private String messName;

    public Mess(int messId, String messName) {
        this.messId = messId;
        this.messName = messName;
    }

    public int getMessId() {
        return messId;
    }

    public void setMessId(int messId) {
        this.messId = messId;
    }

    public String getMessName() {
        return messName;
    }

    public void setMessName(String messName) {
        this.messName = messName;
    }
}