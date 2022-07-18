package com.kazzinc.checklist.Model;

public class TaskEmployee {
    private int TaskEmplId;
    private String TaskEmplName;
    private String TaskEmplLogin;
    private String TaskEmplPassword;
    private int TaskEmplAreaId;
    private String TaskEmplAreaName;
    private String TaskEmplProffesion;
    private String TaskEmplRole;

    public TaskEmployee(int TaskEmplId, String TaskEmplName, String TaskEmplLogin, String TaskEmplPassword, Integer TaskEmplAreaId, String TaskEmplAreaName, String TaskEmplProffesion, String TaskEmplRole) {
        this.TaskEmplId = TaskEmplId;
        this.TaskEmplName = TaskEmplName;
        this.TaskEmplLogin = TaskEmplLogin;
        this.TaskEmplPassword = TaskEmplPassword;
        this.TaskEmplAreaId = TaskEmplAreaId;
        this.TaskEmplAreaName = TaskEmplAreaName;
        this.TaskEmplProffesion = TaskEmplProffesion;
        this.TaskEmplRole = TaskEmplRole;
    }

    public int getEmployeeId() {
        return TaskEmplId;
    }

    public void setEmployeeId(int TaskEmplId) {
        this.TaskEmplId = TaskEmplId;
    }

    public String getEmployeeName() {
        return TaskEmplName;
    }

    public void setEmployeeName(String UserName) {
        this.TaskEmplName = TaskEmplName;
    }

    public String getEmployeeLogin() {
        return TaskEmplLogin;
    }

    public void setEmployeeLogin(String TaskEmplLogin) {
        this.TaskEmplLogin = TaskEmplLogin;
    }

    public String getEmployeePassword() {
        return TaskEmplPassword;
    }

    public void setEmployeePassword(String TaskEmplPassword) {
        this.TaskEmplPassword = TaskEmplPassword;
    }

    public int getEmployeeAreaId() {
        return TaskEmplAreaId;
    }

    public void setEmployeeAreaId(int TaskEmplAreaId) {
        this.TaskEmplAreaId = TaskEmplAreaId;
    }

    public String getEmployeeProffesion() {
        return TaskEmplProffesion;
    }

    public void setEmployeeProffesion(String TaskEmplProffesion) {
        this.TaskEmplProffesion = TaskEmplProffesion;
    }

    public String getEmployeeAreaName() {
        return TaskEmplAreaName;
    }

    public void setEmployeeAreaName(String TaskEmplAreaName) {
        this.TaskEmplAreaName = TaskEmplAreaName;
    }

    public String getEmployeeRole() {
        return TaskEmplRole;
    }

    public void setEmployeeRole(String TaskEmplRole) {
        this.TaskEmplRole = TaskEmplRole;
    }

}
