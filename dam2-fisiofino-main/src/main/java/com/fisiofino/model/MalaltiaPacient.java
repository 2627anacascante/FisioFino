package com.fisiofino.model;

public class MalaltiaPacient {

    private String treatment;
    private String dniPacient;
    private String status;
    private String code;
    private String name;
    private int dose;
    private String unit;
    private String frequency;

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getDniPacient() {
        return dniPacient;
    }

    public void setDniPacient(String dniPacient) {
        this.dniPacient = dniPacient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDose() {
    return dose;
    }

    public void setDose(int dose) {
    this.dose = dose;
    }

    public String getUnit() {
    return unit;
    }

    public void setUnit(String unit) {
    this.unit = unit;
    }

    public String getFrequency(){
        return frequency;
    }
    public void setFrequency(String frequency){
        this.frequency = frequency;
    }

    public String getDetailsTreatment(){
        StringBuilder sb = new StringBuilder();
        sb.append(dose);
        if (unit != null && !unit.isBlank()){
            sb.append(" ").append(unit);
        }
        sb.append(frequency);
    
    return sb.toString();
    }

   public String getProblemAndTreatment() {
    StringBuilder resultado = new StringBuilder();
    resultado.append(code);
    resultado.append(treatment);
    resultado.append(dose);

    if (unit != null && !unit.isBlank()) {
        resultado.append(" ").append(unit);
    }
    resultado.append(frequency);

    return resultado.toString();
}


}
