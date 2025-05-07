module com.example.completablefuture {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens edu.angelpina.physiocare to javafx.fxml;
    opens edu.angelpina.physiocare.Utils;
    exports edu.angelpina.physiocare;
    opens edu.angelpina.physiocare.Services;
    opens edu.angelpina.physiocare.Models;
    opens edu.angelpina.physiocare.Controllers to javafx.fxml;
}