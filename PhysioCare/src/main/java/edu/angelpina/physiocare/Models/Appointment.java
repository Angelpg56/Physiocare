package edu.angelpina.physiocare.Models;

import java.util.Date;

public class Appointment {
    private String _id;
    private Date date;
    private Physio physio;
    private String diagnosis;
    private String treatment;
    private String observations;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Physio getPhysio() {
        return physio;
    }

    public void setPhysio(Physio physio) {
        this.physio = physio;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return "date=" + this.date +
                ": physio=" + this.physio.getName() + " " + this.physio.getSurname() +
                " - diagnosis='" + this.diagnosis + '\'' +
                " - treatment='" + this.treatment + '\'' +
                " - observations='" + this.observations + '\'';
    }

    public String toJson() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return "{" +
                "\"date\":\"" + (this.date != null ? dateFormat.format(this.date) : null) + "\"," +
                "\"physio\":\"" + this.physio.toJson() + "\"," +
                "\"diagnosis\":\"" + this.diagnosis + "\"," +
                "\"treatment\":\"" + this.treatment + "\"," +
                "\"observations\":\"" + this.observations + "\"" +
                "\"_id\":\"" + this._id + "\"" +
                "}";
    }
}
