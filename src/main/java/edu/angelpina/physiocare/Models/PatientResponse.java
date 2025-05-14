package edu.angelpina.physiocare.Models;

public class PatientResponse extends BaseResponse {
    private Patient resultado;

    public Patient getResultado() {
        return resultado;
    }
    public void setResultado(Patient resultado) {
        this.resultado = resultado;
    }
}
