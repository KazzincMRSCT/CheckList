package com.kazzinc.checklist.Model;

public class WorkPlace {

    private String WorkPlaceCode;
    private String WorkPlaceName;
    private String WorkPlaceGroupName;
    private String WorkPlaceGroupCode;
    private int WorkPlaceIsActive;

    public WorkPlace(String workPlaceCode, String workPlaceName, String workPlaceGroupName, String workPlaceGroupCode, int WorkPlaceIsActive) {
        this.WorkPlaceCode = workPlaceCode;
        this.WorkPlaceName = workPlaceName;
        this.WorkPlaceGroupName = workPlaceGroupName;
        this.WorkPlaceGroupCode = workPlaceGroupCode;
        this.WorkPlaceIsActive = WorkPlaceIsActive;
    }

    public String getWorkPlaceCode() {
        return WorkPlaceCode;
    }

    public String getWorkPlaceName() {
        return WorkPlaceName;
    }

    public void setWorkPlaceName(String WorkPlaceName) {
        this.WorkPlaceName = WorkPlaceName;
    }

    public String getWorkPlaceGroupName() {
        return WorkPlaceGroupName;
    }

    public void setWorkPlaceGroupName(String WorkPlaceGroupName) {
        this.WorkPlaceGroupName = WorkPlaceGroupName;
    }
    public String getWorkPlaceGroupCode() {
        return WorkPlaceGroupCode;
    }

    public void setWorkPlaceGroupCode(String WorkPlaceGroupCode) {
        this.WorkPlaceGroupCode = WorkPlaceGroupCode;
    }
    public int getWorkPlaceIsActive() {
        return WorkPlaceIsActive;
    }

    public void setWorkPlaceIsActive(int WorkPlaceIsActive) {
        this.WorkPlaceIsActive = WorkPlaceIsActive;
    }



}
