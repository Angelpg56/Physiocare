package edu.angelpina.physiocare.Models;

public class RecordResponse extends BaseResponse {
    private Record resultado;

    public Record getResultado() {
        return resultado;
    }
    public void setResultado(Record resultado) {
        this.resultado = resultado;
    }
}
