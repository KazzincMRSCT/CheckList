package com.kazzinc.checklist.Model;

public class ChatModel {

    private int UserTNFrom;
    private String UserNameFrom;
    private int UserTNTo;
    private String UserNameTo;
    private String DateTime;
    private String Message;
    private int Status;
    private int Deleted;


    public ChatModel(int userTNFrom, String userNameFrom, int userTNTo, String userNameTo, String dateTime, String message, int status, int deleted) {
        UserTNFrom = userTNFrom;
        UserNameFrom = userNameFrom;
        UserTNTo = userTNTo;
        UserNameTo = userNameTo;
        DateTime = dateTime;
        Message = message;
        Status = status;
        Deleted = deleted;
    }

    public int getUserTNFrom() {
        return UserTNFrom;
    }

    public void setUserTNFrom(int userTNFrom) {
        UserTNFrom = userTNFrom;
    }

    public String getUserNameFrom() {
        return UserNameFrom;
    }

    public void setUserNameFrom(String userNameFrom) {
        UserNameFrom = userNameFrom;
    }

    public int getUserTNTo() {
        return UserTNTo;
    }

    public void setUserTNTo(int userTNTo) {
        UserTNTo = userTNTo;
    }

    public String getUserNameTo() {
        return UserNameTo;
    }

    public void setUserNameTo(String userNameTo) {
        UserNameTo = userNameTo;
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
