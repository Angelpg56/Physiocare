package edu.angelpina.physiocare.Models;

import java.util.List;

public class Record {
    private String _id;
    private Patient patient;
    private String medicalRecord;
    private List<Appointment> appointments;
    private String __v;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(String medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        StringBuilder appointments = new StringBuilder();
        if (this.appointments.isEmpty()) {
            appointments.append("Sin appointments");
        } else {
            for(Appointment a: this.appointments) {
                appointments.append(a.toString()).append(", ");
            }
        }
        return this.patient.getName() + " " + patient.getSurname() +
                ": medicalRecord='" + this.medicalRecord + '\'' +
                " - appointments='" + appointments + "';";
    }
}
