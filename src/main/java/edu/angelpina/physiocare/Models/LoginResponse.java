package edu.angelpina.physiocare.Models;

public class LoginResponse {
    private boolean ok;
    private String token;

    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
