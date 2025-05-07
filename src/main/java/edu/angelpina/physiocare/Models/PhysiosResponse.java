package edu.angelpina.physiocare.Models;

import java.util.List;

public class PhysiosResponse extends BaseResponse {
    private List<Physio> resultado;

    public List<Physio> getResultado() {
        return resultado;
    }
    public void setResultado(List<Physio> resultado) {
        this.resultado = resultado;
    }
}
