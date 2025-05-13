package edu.angelpina.physiocare.Controllers;

import com.google.gson.Gson;
import edu.angelpina.physiocare.Models.*;
import edu.angelpina.physiocare.Services.ServiceResponse;
import edu.angelpina.physiocare.Utils.MessageUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;

public class TitleController implements Initializable {

    public Label welcomeText;
    public Button btnLogin;
    public Button btnLogout;
    private Stage stage;
    private Scene scene;
    Gson gson = new Gson();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(!ServiceResponse.isToken()) {
            btnLogin.setDisable(false);
            btnLogout.setDisable(true);
        } else {
            btnLogin.setDisable(true);
            btnLogout.setDisable(false);

        }
    }

    @FXML
    private void goToPatients(ActionEvent event) throws IOException {
        login(event, "Patient");
    }

    @FXML
    private void goToPhysios(ActionEvent event) throws IOException {
        login(event, "Physios");
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        if(ServiceResponse.isToken()) {
            ServiceResponse.removeToken();
            btnLogin.setDisable(false);
            btnLogout.setDisable(true);
        }
        MessageUtils.showMessage("Logout", "You have been logged out successfully.");
    }

    @FXML
    private void login(ActionEvent event, String redirect) {
        if(!ServiceResponse.isToken()) {
            // Create the dialog
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Login");
            dialog.setHeaderText("Please enter your credentials");

            // Dialog buttons
            ButtonType saveButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create the form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField loginField = new TextField();
            loginField.setPromptText("Login");
            TextField passwordField = new TextField();
            passwordField.setPromptText("Password");

            grid.add(new Label("Login:"), 0, 0);
            grid.add(loginField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            // Disable the save button if fields are invalid
            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);

            // Add validation for the fields
            loginField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveButton.setDisable(!(newValue.length() >= 4) || !(passwordField.getText().length() >= 7));
            });

            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveButton.setDisable(!(newValue.length() >= 7) || !(loginField.getText().length() >= 4));
            });

            // Handle the dialog result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return new User(loginField.getText(), passwordField.getText());
                }
                return null;
            });

            Optional<User> result = dialog.showAndWait();
            result.ifPresent(user -> {
                System.out.println(user);
                String url = ServiceResponse.SERVER + "/auth/login";
                ServiceResponse.getResponseAsync(url, user.toString(), "POST")
                        .thenApply(json -> gson.fromJson(json, LoginResponse.class))
                        .thenAccept(response -> {
                            if (response.isOk()) {
                                Platform.runLater(() -> {
                                    if(!response.getUser().getRol().equals("patient")) {
                                        MessageUtils.showMessage("Success", "User Logged Correctly");
                                        ServiceResponse.setToken(response.getToken());
                                        ServiceResponse.setActualUser(response.getUser());
                                        btnLogin.setDisable(true);
                                        btnLogout.setDisable(false);

                                        if (redirect != null) {
                                            String resource = redirect.equals("Patient") ?
                                                    "/edu/angelpina/physiocare/Patients.fxml" :
                                                    "/edu/angelpina/physiocare/Physios.fxml";
                                            Parent root = null;
                                            System.out.println(resource);
                                            try {
                                                FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
                                                root = loader.load();
                                                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                                scene = new Scene(root);
                                                PatientsController controller = loader.getController();
                                                controller.setStage(stage);
                                                stage.setScene(scene);
                                                stage.show();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    } else {
                                        MessageUtils.showMessage("Unauthorised", "This user cant operate this application");
                                    }
                                });
                            } else {
                                Platform.runLater(() -> MessageUtils.showError("Error", response.getError()));
                            }
                        })
                        .exceptionally(ex -> {
                            Platform.runLater(() -> MessageUtils.showError("Error", "Failed to login"));
                            System.out.println(ex);
                            return null;
                        });
            });
        } else {
            if(redirect != null) {
                String resource = redirect.equals("Patient") ?
                        "/edu/angelpina/physiocare/Patients.fxml" :
                        "/edu/angelpina/physiocare/Physios.fxml";
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource(resource));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }
    }

    public void login(ActionEvent event) {
        login(event, null);
    }
}