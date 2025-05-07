package edu.angelpina.physiocare.Models;

public class Physio {
    private String _id;
    private String name;
    private String surname;
    private String email;
    private String specialty;
    private String licenseNumber;
    private int __v;

    public Physio(String _id, String name, String surname, String email, String specialty, String licenseNumber, int __v) {
        this._id = _id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.__v = __v;
    }

    public Physio(String name, String surname, String email, String specialty, String licenseNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
    }

    public String getId() {
        return _id;
    }
    public void setId(String _id) {
        this._id = _id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSpecialty() {
        return specialty;
    }
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    public String getLicenseNumber() {
        return licenseNumber;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    public int getV() {
        return __v;
    }
    public void setV(int __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        return this.name + " " +
                this.surname + " - " +
                this.email + " - " +
                this.specialty + " - " +
                this.licenseNumber + " - " +
                this._id;
    }

    public String toJson() {
        return "{" +
                "\"name\":\"" + this.name + "\"," +
                "\"surname\":\"" + this.surname + "\"," +
                "\"email\":\"" + this.email + "\"," +
                "\"specialty\":\"" + this.specialty + "\"," +
                "\"licenseNumber\":\"" + this.licenseNumber + "\"" +
                "}";
    }

    public String toJsonComplete() {
        return "{" +
                "\"_id\":\"" + this._id + "\"," +
                "\"name\":\"" + this.name + "\"," +
                "\"surname\":\"" + this.surname + "\"," +
                "\"email\":\"" + this.email + "\"," +
                "\"specialty\":\"" + this.specialty + "\"," +
                "\"licenseNumber\":\"" + this.licenseNumber + "\"" +
                "\"__v\":\"" + this.__v + "\"," +
                "}";
    }
}