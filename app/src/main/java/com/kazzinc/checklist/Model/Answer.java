package com.kazzinc.checklist.Model;

public class Answer {
    private int AnswerId;
    private int AnswerUserId;
    private int AnswerQuesId;
    private String AnswerText;
    private String AnswerDate;
    private int AnswerShift;
    private String AnswerComment;
    private String AnswerWorkPlaceName;
    private String AnswerDateTime;
    private String AnswerPhotos;

    public Answer(int ansId,int ansUserId, int quesId, String ansText, String ansDate, int ansShift, String ansComment, String ansWorkPlaceName, String ansDateTime, String ansPhotos) {
        this.AnswerId = ansId;
        this.AnswerUserId = ansUserId;
        this.AnswerQuesId = quesId;
        this.AnswerText = ansText;
        this.AnswerDate = ansDate;
        this.AnswerShift = ansShift;
        this.AnswerComment = ansComment;
        this.AnswerWorkPlaceName = ansWorkPlaceName;
        this.AnswerDateTime = ansDateTime;
        this.AnswerPhotos = ansPhotos;
    }

    public int getAnswerId() {
        return AnswerId;
    }

    public void setAnswerId(int ansId) {
        this.AnswerId = ansId;
    }

    public int getAnswerUserId() {
        return AnswerUserId;
    }

    public void setAnswerUserId(int ansUserId) {
        this.AnswerUserId = ansUserId;
    }

    public int getQuesId() {
        return AnswerQuesId;
    }

    public void setQuesId(int quesId) {
        this.AnswerQuesId = quesId;
    }

    public String getAnswerText() {
        return AnswerText;
    }

    public void setAnsText(String ansText) {
        this.AnswerText = ansText;
    }

    public String getAnswerDate() {
        return AnswerDate;
    }

    public void setAnswerDate(String ansDate) {
        this.AnswerDate = ansDate;
    }

    public int getAnswerShift() {
        return AnswerShift;
    }

    public void setAnswerShift(int ansShift) {
        this.AnswerShift = ansShift;
    }

    public String getAnswerComment() {
        return AnswerComment;
    }

    public void setAnswerComment(String ansComment) {
        this.AnswerComment = ansComment;
    }

    public String getAnswerWorkPlaceName() {
        return AnswerWorkPlaceName;
    }

    public void setAnswerWorkPlaceName(String ansWorkPlaceName) {
        this.AnswerWorkPlaceName = ansWorkPlaceName;
    }

    public String getAnswerDateTime() {
        return AnswerDateTime;
    }

    public void setAnswerDateTime(String ansDateTime) {
        this.AnswerDateTime = ansDateTime;
    }

    public String getAnswerPhotos() {
        return AnswerPhotos;
    }

    public void setAnswerPhotos(String ansPhotos) {
        this.AnswerPhotos = ansPhotos;
    }
}
