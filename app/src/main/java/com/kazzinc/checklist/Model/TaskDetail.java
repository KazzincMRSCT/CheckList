package com.kazzinc.checklist.Model;

public class TaskDetail {

    private int TDId;
    private int TDTaskId;
    private String TDWorkType;

    private String TDUnit;
    private String TDWP1;
    private String TDWP2;
    private String TDEquipment;
    private String TDValue;
    private String TDFact;

    private int TDModifySignedUser;
    private int TDModifySignedEmpl;

    public TaskDetail(int TDId, int TDTaskId, String TDWorkType, String TDUnit, String TDWP1, String TDWP2, String TDEquipment, String TDValue, String TDFact, int TDModifySignedUser, int TDModifySignedEmpl) {
        this.TDId = TDId;
        this.TDTaskId = TDTaskId;
        this.TDWorkType = TDWorkType;
        this.TDUnit = TDUnit;
        this.TDWP1 = TDWP1;
        this.TDWP2 = TDWP2;
        this.TDEquipment = TDEquipment;
        this.TDValue = TDValue;
        this.TDFact = TDFact;
        this.TDModifySignedUser = TDModifySignedUser;
        this.TDModifySignedEmpl = TDModifySignedEmpl;
    }

    public int getTDId() {
        return TDId;
    }
    public void setTDId(int TDId) {
        this.TDId = TDId;
    }

    public int getTDTaskId() {
        return TDTaskId;
    }
    public void setTDTaskId(int TDTaskId) {
        this.TDTaskId = TDTaskId;
    }

    public String getTDWorkType() {
        return TDWorkType;
    }
    public void setTDWorkType(String TDWorkType) { this.TDWorkType = TDWorkType;}

    public String getTDUnit() {
        return TDUnit;
    }
    public void setTDUnit(String TDUnit) { this.TDUnit = TDUnit;}

    public String getTDWP1() {
        return TDWP1;
    }
    public void setTDWP1(String TDWP1) { this.TDWP1 = TDWP1;}

    public String getTDWP2() {
        return TDWP2;
    }
    public void setTDWP2(String TDWP2) { this.TDWP2 = TDWP2;}

    public String getTDEquipment() {
        return TDEquipment;
    }
    public void setTDEquipment(String TDEquipment) { this.TDEquipment = TDEquipment;}

    public String getTDValue() {
        return TDValue;
    }
    public void setTDValue(String TDValue) { this.TDValue = TDValue;}

    public String getTDFact() {
        return TDFact;
    }
    public void setTDFact(String TDFact) { this.TDFact = TDFact;}

    public int getTDModifySignedUser() {
        return TDModifySignedUser;
    }
    public void setTDModifySignedUser(int TDModifySignedUser) { this.TDModifySignedUser = TDModifySignedUser; }

    public int getTDModifySignedEmpl() {
        return TDModifySignedEmpl;
    }
    public void setTDModifySignedEmpl(int TDModifySignedEmpl) { this.TDModifySignedEmpl = TDModifySignedEmpl; }
}
