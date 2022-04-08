package com.kazzinc.checklist.Model;

public class Question {
    private int QuesId;
    private String QuesText;
    private int QuesType;
    private int QuesIsCritical;
    private String QuesEquipGroup;

    public Question(int QuesId, String QuesText, int QuesType, int QuesIsCritical, String QuesEquipType) {
        this.QuesId = QuesId;
        this.QuesText = QuesText;
        this.QuesType = QuesType;
        this.QuesIsCritical = QuesIsCritical;
        this.QuesEquipGroup = QuesEquipGroup;
    }

    public int getQuesId() {
        return QuesId;
    }

    public void setQuesId(int QuesId) {
        this.QuesId = QuesId;
    }

    public String getQuestext() {
        return QuesText;
    }

    public void setQuestext(String QuesText) {
        this.QuesText = QuesText;
    }

    public int getQuesType() {
        return QuesType;
    }

    public void setQuesType(int QuesType) {
        this.QuesType = QuesType;
    }

    public int getQuesIsCritical() {
        return QuesIsCritical;
    }

    public void setQuesIsCritical(int QuesIsCritical) {
        this.QuesIsCritical = QuesIsCritical;
    }

    public String getQuesEquipGroup() {
        return QuesEquipGroup;
    }

    public void setQuesEquipGroup(String QuesEquipGroup) {
        this.QuesEquipGroup = QuesEquipGroup;
    }
}