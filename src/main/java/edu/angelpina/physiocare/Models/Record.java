package edu.angelpina.physiocare.Models;

import java.util.ArrayList;
import java.util.List;

public class Record {
    private String _id;
    private Patient patient;
    private String medicalRecord;
    private List<Appointment> appointments;
    private String __v;

    public Record() {
    }
    public Record(Patient patient, String medicalRecord) {
        this.patient = patient;
        this.medicalRecord = medicalRecord;
        this.appointments = new ArrayList<>();
    }

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
        String appointments = " appointments";
        if (this.appointments.isEmpty()) {
            appointments = "Sin" + appointments;
        } else {
                appointments = this.appointments.size() + appointments;
        }
        return this.patient.getName() + " " + patient.getSurname() +
                ": medicalRecord='" + this.medicalRecord + '\'' +
                " - " + appointments;
    }

    public String toJson() {
        return "{" +
                "\"patient\": \"" + this.patient.getId() + "\", " +
                "\"medicalRecord\": \"" + this.medicalRecord + "\"" +
                "}";
    }
}
