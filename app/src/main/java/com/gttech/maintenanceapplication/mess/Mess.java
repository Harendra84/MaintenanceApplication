package com.gttech.maintenanceapplication.mess;
public class Mess {

    private String messId;
    private String messName;

    public Mess() {
    }

    public Mess(String messId, String messName) {
        this.messId = messId;
        this.messName = messName;
    }

    public String getMessId() {
        return messId;
    }

    public void setMessId(String messId) {
        this.messId = messId;
    }

    public String getMessName() {
        return messName;
    }

    public void setMessName(String messName) {
        this.messName = messName;
    }
}