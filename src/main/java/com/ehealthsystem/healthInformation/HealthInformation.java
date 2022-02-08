package com.ehealthsystem.healthInformation;

/**
 * Represents the users health information
 */
public class HealthInformation {

    private String ICD, disease, medication;

    /**
     * set the attributes when creating a new object
     * @param ICD
     * @param disease
     * @param medication
     */
    public HealthInformation(String ICD, String disease, String medication) {
        this.ICD = ICD;
        this.disease = disease;
        this.medication = medication;
    }

    /**
     * get ICD returned
     * @return
     */
    public String getICD() {
        return ICD;
    }

    /**
     * get the disease returned
     * @return
     */
    public String getDisease() {
        return disease;
    }

    /**
     * get the medication returned
     * @return
     */
    public String getMedication() {
        return medication;
    }

    /**
     * set the ICD
     * @param ICD
     */
    public void setICD(String ICD) {
        this.ICD = ICD;
    }

    /**
     * set the disease
     * @param disease
     */
    public void setDisease(String disease) {
        this.disease = disease;
    }

    /**
     * set the medication
     * @param medication
     */
    public void setMedication(String medication) {
        this.medication = medication;
    }

}
