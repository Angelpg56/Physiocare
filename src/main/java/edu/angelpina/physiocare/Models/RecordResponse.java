package edu.angelpina.physiocare.Models;

import java.util.List;

public class RecordResponse extends BaseResponse {
    private List<Record> resultado;

    public List<Record> getResultado() {
        return resultado;
    }
    public void setResultado(List<Record> resultado) {
        this.resultado = resultado;
    }
}
