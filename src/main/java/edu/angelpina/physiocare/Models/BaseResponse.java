package edu.angelpina.physiocare.Models;

public class BaseResponse {
    private boolean ok;
    private String error;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getError() {
        return error;
    }
}
