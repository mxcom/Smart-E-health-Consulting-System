package com.ehealthsystem.appointment;

import com.ehealthsystem.doctor.specialization.Specialization;
import com.ehealthsystem.tools.SceneSwitch;
import com.ehealthsystem.tools.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AppointmentInformationController implements Initializable {

    @FXML
    DatePicker datePicker;

    @FXML
    Slider searchDistanceSlider;

    @FXML
    TextField healthProblemField;

    @FXML
    ComboBox doctorChoiceBox;

    @FXML
    Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Specialization specialization = new Specialization();
            String[] categories = specialization.getSpecializationList().toArray(new String[0]);
            doctorChoiceBox.getItems().setAll(categories);
            datePicker.setValue(LocalDate.now());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleContinueButton(ActionEvent event) throws IOException {
        if(validateDate() && validateHealthProblem() && validateSpecialization()) {
            Session.appointment.setDate(datePicker.getValue());
            Session.appointment.setDistance(searchDistanceSlider.getValue());
            Session.appointment.setSpecialization(doctorChoiceBox.getValue().toString());
            SceneSwitch.switchTo(event, "appointment/appointmentFound-view.fxml", "Make appointment");
        } else {
            errorLabel.setVisible(true);
        }
    }

    public void handleBackButton(ActionEvent event) throws IOException {
        SceneSwitch.switchTo(event, "appointment/appointmentHealth-view.fxml", "Make appointment");
    }

    public boolean validateDate() {
        LocalDate date;
        try {
            date = datePicker.getValue(); //null if no date selected
            if (date.isBefore(LocalDate.now())) { //throws NullPointerException if date is null
                errorLabel.setText("You can't make appointments for the past. Please use a date in the future or today.");
                return false;
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            errorLabel.setText("You didn't select a date.");
            return false;

        }
    }

    public boolean validateHealthProblem() {
        if(healthProblemField.getText().isBlank()) {
            errorLabel.setText("Please describe your health problem.");
            return false;
        } else {
            return true;
        }
    }

    public boolean validateSpecialization() {
        if(doctorChoiceBox.getValue() == null) {
            errorLabel.setText("Please choose a doctor specialization.");
            return false;
        } else {
            return true;
        }
    }
}
