package com.kazzinc.checklist.Model;

public class EmlpECPKey {

    private long EMPL_ID;
    private String EMPL_KEY_EXPIRED_DATE;
    private String EMPL_KEY_DESCRIPTION;

    public EmlpECPKey(long EMPL_ID, String EMPL_KEY_EXPIRED_DATE, String EMPL_KEY_DESCRIPTION) {
        this.EMPL_ID = EMPL_ID;
        this.EMPL_KEY_EXPIRED_DATE = EMPL_KEY_EXPIRED_DATE;
        this.EMPL_KEY_DESCRIPTION = EMPL_KEY_DESCRIPTION;
    }


    public long getEMPL_ID() {
        return EMPL_ID;
    }

    public void setEMPL_ID(long EMPL_ID) {
        this.EMPL_ID = EMPL_ID;
    }

    public String getEMPL_KEY_EXPIRED_DATE() {
        return EMPL_KEY_EXPIRED_DATE;
    }

    public void setEMPL_KEY_EXPIRED_DATE(String EMPL_KEY_EXPIRED_DATE) {
        this.EMPL_KEY_EXPIRED_DATE = EMPL_KEY_EXPIRED_DATE;
    }

    public String getEMPL_KEY_DESCRIPTION() {
        return EMPL_KEY_DESCRIPTION;
    }

    public void setEMPL_KEY_DESCRIPTION(String EMPL_KEY_DESCRIPTION) {
        this.EMPL_KEY_DESCRIPTION = EMPL_KEY_DESCRIPTION;
    }
}
