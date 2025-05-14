package edu.angelpina.physiocare.Models;

import java.util.List;

public class PatientsResponse extends BaseResponse {
    private List<Patient> resultado;

    public List<Patient> getResultado() {
        return resultado;
    }
    public void setResultado(List<Patient> resultado) {
        this.resultado = resultado;
    }
}
