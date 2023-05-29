package com.gttech.maintenanceapplication.mess;
public class Mess {

    private Integer messId;
    private String messName;

    public Mess() {
    }

    public Mess(Integer messId, String messName) {
        this.messId = messId;
        this.messName = messName;
    }

    public Integer getMessId() {
        return messId;
    }

    public void setMessId(Integer messId) {
        this.messId = messId;
    }

    public String getMessName() {
        return messName;
    }

    public void setMessName(String messName) {
        this.messName = messName;
    }
}