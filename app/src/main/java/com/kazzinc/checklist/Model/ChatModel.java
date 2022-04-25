package com.kazzinc.checklist.Model;

public class ChatModel {

    private int UserTabNum;
    private String UserName;
    private String DateTime;
    private String Message;
    private int Status;
    private int Deleted;

    public ChatModel(int userTabNum, String userName, String dateTime, String message, int status, int deleted) {
        UserTabNum = userTabNum;
        UserName = userName;
        DateTime = dateTime;
        Message = message;
        Status = status;
        Deleted = deleted;
    }

    public ChatModel() {
    }

    public int getUserTabNum() {
        return UserTabNum;
    }

    public void setUserTabNum(int userTabNum) {
        UserTabNum = userTabNum;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getDeleted() {
        return Deleted;
    }

    public void setDeleted(int deleted) {
        Deleted = deleted;
    }
}
