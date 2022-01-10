package com.ehealthsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    public Button loginButton;
    public Button registrationButton;

    @FXML
    public void handleLoginButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login/login-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene loginScene = new Scene(root, 350, 450);
        stage.setTitle("Login");
        stage.setScene(loginScene);
        stage.show();
    }

    @FXML
    public void handleRegistrationButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("registration/registration-view.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene registrationScene = new Scene(root, 350, 700);
        stage.setTitle("Create Account");
        stage.setScene(registrationScene);
        stage.show();
    }

}
