package com.kazzinc.checklist.Model;

public class Equipment {
    private int EquipmentId;
    private String EquipmentName;
    private String EquipmentArea;


    public Equipment(int EquipmentId, String EquipmentName, String EquipmentArea) {
        this.EquipmentId = EquipmentId;
        this.EquipmentName = EquipmentName;
        this.EquipmentArea = EquipmentArea;
    }

    public int getEquipmentId() {
        return EquipmentId;
    }

    public void setEquipmentId(int EquipmentId) {
        this.EquipmentId = EquipmentId;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String EquipmentName) {
        this.EquipmentName = EquipmentName;
    }

    public String getEquipmentArea() {
        return EquipmentArea;
    }

    public void setEquipmentArea(String EquipmentArea) {
        this.EquipmentArea = EquipmentArea;
    }

}
