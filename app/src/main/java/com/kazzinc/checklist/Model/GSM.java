package com.kazzinc.checklist.Model;

public class GSM {

    private String DateEvent;
    private String Date;
    private String Shift;
    private String EquipOut;
    private String EquipIn;
    private String EmplOut;
    private String Reason;
    private float DT;
    private float SAE15W40;
    private float SAE50;
    private float SAE10W40;
    private float T46;
    private int Deleted;
    private int SendToServer;
    private int Confirmed;


    public GSM(String DateEvent,String Date,String Shift,String EquipOut,String EquipIn,String EmplOut,String Reason,float DT,float SAE15W40,float SAE50,float SAE10W40,float T46,int Deleted,int SendToServer,int Confirmed) {
        this.DateEvent = DateEvent;
        this.Date = Date;
        this.Shift = Shift;
        this.EquipOut = EquipOut;
        this.EquipIn = EquipIn;
        this.EmplOut = EmplOut;
        this.Reason = Reason;
        this.DT = DT;
        this.SAE15W40 = SAE15W40;
        this.SAE50 = SAE50;
        this.SAE10W40 = SAE10W40;
        this.T46 = T46;
        this.Deleted = Deleted;
        this.SendToServer = SendToServer;
        this.Confirmed = Confirmed;
    }

    public String getDateEvent() {
        return DateEvent;
    }

    public void setDateEvent(String DateEvent) {
        this.DateEvent = DateEvent;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getShift() {
        return Shift;
    }

    public void setShift(String Shift) {
        this.Shift = Shift;
    }

    public String getEquipOut() { return EquipOut; }

    public void setEquipOut(String EquipOut) {
        this.EquipOut = EquipOut;
    }

    public String getEquipIn() { return EquipIn; }

    public void seEquipIn(String EquipIn) {
        this.EquipIn = EquipIn;
    }

    public String getEmplOut() { return EmplOut; }

    public void setEmplOut(String EmplOut) {
        this.EmplOut = EmplOut;
    }

    public String getReason() { return Reason; }

    public void setReason(String Reason) {
        this.Reason = Reason;
    }

    public float getDT() { return DT; }
    public void setDT(float DT) { this.DT = DT;}

    public float getSAE15W40() { return SAE15W40; }
    public void setSAE15W40(float SAE15W40) { this.SAE15W40 = SAE15W40;}

    public float getSAE50() { return SAE50; }
    public void setSAE50(float SAE50) { this.SAE50 = SAE50;}

    public float getSAE10W40() { return SAE10W40; }
    public void setSAE10W40(float SAE10W40) { this.SAE10W40 = SAE10W40;}

    public float getT46() { return T46; }
    public void setT46(float T46) { this.T46 = T46;}

    public int getDeleted() { return Deleted; }
    public void setDeleted(int Deleted) { this.Deleted = Deleted;}

    public int getSendToServer() { return SendToServer; }
    public void setSendToServer(int SendToServer) { this.SendToServer = SendToServer;}

    public int getConfirmed() { return Confirmed; }
    public void setConfirmed(int Confirmed) { this.Confirmed = Confirmed;}
}
