package edu.angelpina.physiocare.Services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.angelpina.physiocare.Models.Patient;
import edu.angelpina.physiocare.Models.PatientsResponse;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.lang.reflect.Type;
import java.util.List;

public class GetPatients extends Service<List<Patient>> {
    @Override
    protected Task<List<Patient>> createTask() {
        return new Task<List<Patient>>() {
            @Override
            protected List<Patient> call() throws Exception {
                String json = ServiceResponse.getResponse(
                        "http://angelpg.es:8080", null, "GET");
                Gson gson = new Gson();
                Type type = new TypeToken<PatientsResponse>(){}.getType();
                PatientsResponse res = gson.fromJson(json, type);
                System.out.println("duro " +json);
                return res.getResultado();
            }
        };
    }
}
