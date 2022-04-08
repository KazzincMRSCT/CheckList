package com.kazzinc.checklist.Model;

import java.util.ArrayList;

public class User {
    private int UserId;
    private String UserName;
    private String UserLogin;
    private String UserPassword;
    private int UserAreaId;
    private String UserRole;
    private ArrayList<Question> Questions;

    public User(int UserId, String UserName, String UserLogin, String UserPassword, Integer UserAreaId, String UserRole) {
        this.UserId = UserId;
        this.UserName = UserName;
        this.UserLogin = UserLogin;
        this.UserPassword = UserPassword;
        this.UserAreaId = UserAreaId;
        this.UserRole = UserRole;
        this.Questions = Questions;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserLogin() {
        return UserLogin;
    }

    public void setUserLogin(String UserLogin) {
        this.UserLogin = UserLogin;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String UserPassword) {
        this.UserPassword = UserPassword;
    }

    public int getUserAreaId() {
        return UserAreaId;
    }

    public void setUserAreaId(int UserAreaId) {
        this.UserAreaId = UserAreaId;
    }

    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String UserRole) {
        this.UserRole = UserRole;
    }

    public ArrayList<Question> getQuestions() {
        return Questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.Questions = Questions;
    }

}
