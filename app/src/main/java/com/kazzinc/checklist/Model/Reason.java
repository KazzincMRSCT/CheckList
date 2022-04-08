package com.kazzinc.checklist.Model;

public class Reason {
    private String Description;
    private String ReasonType;


    public Reason(String Description, String ReasonType) {
        this.Description = Description;
        this.ReasonType = ReasonType;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getReasonType() {
        return ReasonType;
    }

    public void setReasonType(String ReasonType) {
        this.ReasonType = ReasonType;
    }

}
