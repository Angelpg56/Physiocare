module edu.angelpina.physiocare {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires kernel;
    requires layout;
    requires com.jcraft.jsch;
    requires org.slf4j;

    opens edu.angelpina.physiocare to javafx.fxml;
    opens edu.angelpina.physiocare.Utils;
    exports edu.angelpina.physiocare;
    opens edu.angelpina.physiocare.Services;
    opens edu.angelpina.physiocare.Models;
    opens edu.angelpina.physiocare.Controllers to javafx.fxml;
}