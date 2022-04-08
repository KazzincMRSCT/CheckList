package com.kazzinc.checklist.Model;

public class RiskSafety {
    private String Risk;
    private String Safety;
    private String Instruction;

    public RiskSafety(String Risk, String Safety) {
        this.Risk = Risk;
        this.Safety = Safety;
        this.Instruction = Instruction;
    }

    public String getRisk() {
        return Risk;
    }

    public void setRisk(String Risk) {
        this.Risk = Risk;
    }

    public String getSafety() {
        return Safety;
    }

    public void setSafety(String Safety) {
        this.Safety = Safety;
    }

    public String getInstruction() {
        return Instruction;
    }

    public void setInstruction(String Instruction) {
        this.Instruction = Instruction;
    }

}
