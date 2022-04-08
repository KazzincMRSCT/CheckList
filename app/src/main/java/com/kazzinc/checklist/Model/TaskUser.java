package com.kazzinc.checklist.Model;

public class TaskUser {
    private int TaskUserId;
    private String TaskUserName;
    private String TaskUserLogin;
    private String TaskUserPassword;
    private int TaskUserAreaId;
    private String TaskUserAreaName;
    private String TaskUserRole;
    private String TaskUserEmail;

    public TaskUser(int TaskUserId, String TaskUserName, String TaskUserLogin, String TaskUserPassword, Integer TaskUserAreaId, String TaskUserAreaName, String TaskUserRole, String TaskUserEmail ) {
        this.TaskUserId = TaskUserId;
        this.TaskUserName = TaskUserName;
        this.TaskUserLogin = TaskUserLogin;
        this.TaskUserPassword = TaskUserPassword;
        this.TaskUserAreaId = TaskUserAreaId;
        this.TaskUserAreaName = TaskUserAreaName;
        this.TaskUserRole = TaskUserRole;
        this.TaskUserEmail = TaskUserEmail;
    }

    public int getUserId() {
        return TaskUserId;
    }

    public void setUserId(int TaskEmplId) {
        this.TaskUserId = TaskEmplId;
    }

    public String getUserName() {
        return TaskUserName;
    }

    public void setUserName(String TaskUserName) {
        this.TaskUserName = TaskUserName;
    }

    public String getUserLogin() {
        return TaskUserLogin;
    }

    public void setUserLogin(String TaskUserLogin) {
        this.TaskUserLogin = TaskUserLogin;
    }

    public String getUserPassword() {
        return TaskUserPassword;
    }

    public void setUserPassword(String TaskUserPassword) {
        this.TaskUserPassword = TaskUserPassword;
    }

    public int getUserAreaId() {
        return TaskUserAreaId;
    }

    public void setUserAreaId(int TaskUserAreaId) {
        this.TaskUserAreaId = TaskUserAreaId;
    }

    public String getUserAreaName() {
        return TaskUserAreaName;
    }

    public void setUserAreaName(String TaskUserAreaName) {
        this.TaskUserAreaName = TaskUserAreaName;
    }

    public String getUserRole() {
        return TaskUserRole;
    }

    public void setUserRole(String TaskUserRole) {
        this.TaskUserRole = TaskUserRole;
    }

    public String getUserEmail() {
        return TaskUserEmail;
    }

    public void setUserEmail(String TaskUserEmail) {
        this.TaskUserEmail = TaskUserEmail;
    }


}
