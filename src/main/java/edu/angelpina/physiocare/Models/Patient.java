package edu.angelpina.physiocare.Models;
import java.util.Date;

public class Patient {
    private String _id;
    private String name;
    private String surname;
    private String email;
    private Date birthDate;
    private String address;
    private String insuranceNumber;
    private int __v;

    public Patient(String _id, String name, String surname, String email, Date birthDate, String address, String insuranceNumber, int __v) {
        this._id = _id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.insuranceNumber = insuranceNumber;
        this.__v = __v;
    }
    public Patient(String name, String surname, String email, Date birthDate, String address, String insuranceNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.insuranceNumber = insuranceNumber;
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
    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    public int getV() {
        return __v;
    }
    public void setV(int __v) {
        this.__v = __v;
    }

    @Override
    public String toString() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = (birthDate != null) ? dateFormat.format(birthDate) : "N/A";
        return this.name + " " +
                this.surname + " - " +
                this.email + " - " +
                formattedDate + " - " +
                this.address + " - " +
                this.insuranceNumber + " - " +
                this._id;
    }

    public String toJson() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"surname\":\"" + surname + "\"," +
                "\"email\":\"" + email + "\"," +
                "\"birthDate\":\"" + (birthDate != null ? dateFormat.format(birthDate) : null) + "\"," +
                "\"address\":\"" + address + "\"," +
                "\"insuranceNumber\":\"" + insuranceNumber + "\"" +
                "}";
    }
}
