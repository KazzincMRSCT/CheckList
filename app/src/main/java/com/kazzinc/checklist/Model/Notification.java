package com.kazzinc.checklist.Model;

public class Notification {
    private int NotifyId;
    private String NotifyText;
    private String NotifyDate;
    private String NotifyType;

    public Notification() {
    }

    public Notification(int notifyId, String notifyText, String notifyDate, String notifyType) {
        NotifyId = notifyId;
        NotifyText = notifyText;
        NotifyDate = notifyDate;
        NotifyType = notifyType;
    }

    public int getNotifyId() {
        return NotifyId;
    }

    public void setNotifyId(int notifyId) {
        NotifyId = notifyId;
    }

    public String getNotifyText() {
        return NotifyText;
    }

    public void setNotifyText(String notifyText) {
        NotifyText = notifyText;
    }

    public String getNotifyDate() {
        return NotifyDate;
    }

    public void setNotifyDate(String notifyDate) {
        NotifyDate = notifyDate;
    }

    public String getNotifyType() {
        return NotifyType;
    }

    public void setNotifyType(String notifyType) {
        NotifyType = notifyType;
    }
}
