package edu.angelpina.physiocare.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TitleController {

    private Stage stage;
    private Scene scene;

    @FXML
    private void goToPatients(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/edu/angelpina/physiocare/Patients.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void goToRecords(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/edu/angelpina/physiocare/Records.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void goToPhysios(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/edu/angelpina/physiocare/Physios.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}