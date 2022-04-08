package com.kazzinc.checklist.Model;

public class TaskDetailModify {

    private int Id;
    private int TaskId;
    private String WorkType;
    private String Unit;
    private String WP1;
    private String WP2;
    private String Equipment;
    private String Value;
    private String Fact;
    private int SingedUser;
    private int SingedEmpl;

    public TaskDetailModify(int Id, int TaskId, String WorkType, String Unit, String WP1, String WP2, String Equipment, String Value, String Fact, int SingedUser, int SingedEmpl) {
        this.Id = Id;
        this.TaskId = TaskId;
        this.WorkType = WorkType;
        this.Unit = Unit;
        this.WP1 = WP1;
        this.WP2 = WP2;
        this.Equipment = Equipment;
        this.Value = Value;
        this.Fact = Fact;
        this.SingedUser = SingedUser;
        this.SingedEmpl = SingedEmpl;
    }

    public int getId() {
        return Id;
    }
    public void setId(int Id) {
        this.Id = Id;
    }

    public int getTaskId() {
        return TaskId;
    }
    public void setTaskId(int TaskId) {
        this.TaskId = TaskId;
    }

    public String getWorkType() {
        return WorkType;
    }
    public void setWorkType(String WorkType) { this.WorkType = WorkType;}

    public String getUnit() {
        return Unit;
    }
    public void setUnit(String Unit) { this.Unit = Unit;}

    public String getWP1() {
        return WP1;
    }
    public void setWP1(String WP1) { this.WP1 = WP1;}

    public String getWP2() {
        return WP2;
    }
    public void setWP2(String WP2) { this.WP2 = WP2;}

    public String getEquipment() {
        return Equipment;
    }
    public void setEquipment(String Equipment) { this.Equipment = Equipment;}

    public String getValue() {
        return Value;
    }
    public void setValue(String Value) { this.Value = Value;}

    public String getFact() {
        return Fact;
    }
    public void setFact(String Fact) { this.Fact = Fact;}

    public int getSingedUser() {
        return SingedUser;
    }
    public void setSingedUser(int SingedUser) {
        this.SingedUser = SingedUser;
    }

    public int getSingedEmpl() {
        return SingedEmpl;
    }
    public void setSingedEmpl(int SingedEmpl) {
        this.SingedEmpl = SingedEmpl;
    }
}
