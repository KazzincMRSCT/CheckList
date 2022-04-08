package com.kazzinc.checklist.Model;

public class VpnConnection {
    private String TimeWorkPhone;
    private int OnVpn;



    public VpnConnection() {
    }

    public VpnConnection(String timeWorkPhone, int onVpn) {
        TimeWorkPhone = timeWorkPhone;
        OnVpn = onVpn;
    }

    public String getTimeWorkPhone() {
        return TimeWorkPhone;
    }

    public void setTimeWorkPhone(String timeWorkPhone) {
        TimeWorkPhone = timeWorkPhone;
    }

    public int getOnVpn() {
        return OnVpn;
    }

    public void setOnVpn(int onVpn) {
        OnVpn = onVpn;
    }
}
