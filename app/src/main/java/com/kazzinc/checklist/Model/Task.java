package com.kazzinc.checklist.Model;

public class Task {

    private int TaskId;
    private String TaskDate;
    private int TaskShift;
    private String TaskWorkPlaceCode;
    private int TaskEquipId;
    private int TaskEmplId;
    private String TaskWorkPlaceName;
    private String TaskEquipName;
    private String TaskEmplName;
    private int TaskUserId;
    private String TaskUserName;
    private int TaskStateId;
    private String TaskStateName;
    private int TaskSignId;
    private String TaskSignName;
    private int HaveCkWP;
    private int HaveCkEq;
    private int TaskEquipTypeId;


    public Task(int TaskId, String TaskDate, int TaskShift, String TaskWorkPlaceCode, int TaskEquipId, int TaskEmplId, String TaskWorkPlaceName, String TaskEquipName, String TaskEmplName, int TaskUserId, String TaskUserName, int TaskStateId, String TaskStateName, int TaskSignId, String TaskSignName, int HaveCkWP, int HaveCkEq, int TaskEquipTypeId) {
        this.TaskId = TaskId;
        this.TaskDate = TaskDate;
        this.TaskShift = TaskShift;
        this.TaskWorkPlaceCode = TaskWorkPlaceCode;
        this.TaskEquipId = TaskEquipId;
        this.TaskEmplId = TaskEmplId;
        this.TaskWorkPlaceName = TaskWorkPlaceName;
        this.TaskEquipName = TaskEquipName;
        this.TaskEmplName = TaskEmplName;
        this.HaveCkWP = HaveCkWP;
        this.HaveCkEq = HaveCkEq;
        this.TaskEquipTypeId = TaskEquipTypeId;
    }

    public int getTaskId() {
        return TaskId;
    }
    public void setTaskId(int TaskId) {
        this.TaskId = TaskId;
    }

    public String getTaskDate() {
        return TaskDate;
    }
    public void setTaskDate(String TaskDate) {
        this.TaskDate = TaskDate;
    }

    public int getTaskShift() {
        return TaskShift;
    }
    public void setTaskShift(int TaskShift) { this.TaskShift = TaskShift;}

    public String getTaskWorkPlaceCode() {
        return TaskWorkPlaceCode;
    }
    public void setTaskWorkPlaceCode(String TaskWorkPlaceCode) {this.TaskWorkPlaceCode = TaskWorkPlaceCode;}

    public int getTaskEquipId() {
        return TaskEquipId;
    }
    public void setTaskEquipId(int TaskEquipId) {this.TaskEquipId = TaskEquipId;}

    public int getTaskEmplId() {
        return TaskEmplId;
    }
    public void setTaskEmplId(int TaskEmplId) {this.TaskEmplId = TaskEmplId;}

    public String getTaskWorkPlaceName() {
        return TaskWorkPlaceName;
    }
    public void setTaskWorkPlaceName(String TaskWorkPlaceName) {
        this.TaskWorkPlaceName = TaskWorkPlaceName;
    }

    public String getTaskEquipName() {
        return TaskEquipName;
    }
    public void setTaskEquipName(String TaskEquipName) {
        this.TaskEquipName = TaskEquipName;
    }

    public String getTaskEmplName() {
        return TaskEmplName;
    }
    public void setTaskEmplName(String TaskEmplName) {
        this.TaskEmplName = TaskEmplName;
    }

    public int getTaskUserId() {
        return TaskUserId;
    }
    public void setTaskUserId(int TaskUserId) {
        this.TaskUserId = TaskUserId;
    }

    public String getTaskUserName() {
        return TaskUserName;
    }
    public void setTaskUserName(String TaskUserName) {
        this.TaskUserName = TaskUserName;
    }

    public int getTaskStateId() {
        return TaskStateId;
    }
    public void setTaskStateId(int TaskStateId) {
        this.TaskStateId = TaskStateId;
    }

    public String getTaskStateName() {
        return TaskStateName;
    }
    public void setTaskStateName(String TaskStateName) {
        this.TaskStateName = TaskStateName;
    }

    public int getTaskSignId() {
        return TaskSignId;
    }
    public void setTaskSignId(int TaskSignId) {
        this.TaskSignId = TaskSignId;
    }

    public String getTaskSignName() {
        return TaskSignName;
    }
    public void setTaskSignName(String TaskSignName) {
        this.TaskSignName = TaskSignName;
    }

    public int getHaveCkWP() { return HaveCkWP; }
    public void setHaveCkWP(int HaveCkWP) {
        this.HaveCkWP = HaveCkWP;
    }

    public int getHaveCkEq() {
        return HaveCkEq;
    }
    public void setHaveCkEq(int HaveCkEq) {
        this.HaveCkEq = HaveCkEq;
    }

    public int getTaskEquipTypeId() {
        return TaskEquipTypeId;
    }
    public void setTaskEquipTypeId(int TaskEquipTypeId) {
        this.TaskEquipTypeId = TaskEquipTypeId;
    }
}
