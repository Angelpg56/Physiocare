package edu.angelpina.physiocare.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RecordsController {
    private Stage stage;
    private Scene scene;
    public void goToTitle(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/edu/angelpina/physiocare/Title.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
