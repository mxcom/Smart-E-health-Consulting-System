package com.ehealthsystem.appointment;

import com.ehealthsystem.tools.SceneSwitch;
import com.ehealthsystem.tools.Session;
import com.ehealthsystem.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AppointmentUserController implements Initializable {

    @FXML
    Label usernameLabel;

    @FXML
    Label emailLabel;

    @FXML
    Label firstNameLabel;

    @FXML
    Label lastNameLabel;

    @FXML
    Label streetLabel;

    @FXML
    Label houseNumberLabel;

    @FXML
    Label zipCodeLabel;

    @FXML
    Label birthdayLabel;

    @FXML
    Label genderLabel;

    @FXML
    Label insuranceNameLabel;

    @FXML
    Label privateInsuranceLabel;

    @FXML
    Button cancelButton;

    @FXML
    ProgressBar progressBar;

    /**
     * First method called when scene is switched.
     * Used to load the user information
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserDetails();
    }

    /**
     * Switch scene to primary
     *
     * @param event event triggered when cancel button is pressed
     * @throws IOException if the FXMLLoader can't find .fxml file
     */
    public void handleCancelButton(ActionEvent event) throws IOException {
        SceneSwitch.switchTo(event, "primary/primary-view.fxml", "E-Health System");
    }

    /**
     * Switch scene to next appointment view
     *
     * @param event
     * @throws IOException
     */
    public void handleContinueButton(ActionEvent event) throws IOException {
        SceneSwitch.switchTo(event, "appointment/appointmentHealth-view.fxml", "Make appointment");
    }

    /**
     * Load user information when button is pressed
     * @throws SQLException
     */
    public void loadUserDetails() {
        User currentUser = Session.user;
        setUsernameTextField(currentUser.getUsername());
        setEmailTextField(currentUser.getMail());
        setFirstNameTextField(currentUser.getFirstName());
        setLastNameTextField(currentUser.getLastName());
        setStreetTextField(currentUser.getStreet());
        setHouseNoTextField(currentUser.getHouseNo());
        setZipTextField(currentUser.getZipCode());
        setBirthdayTextField(currentUser.getBirthDate());
        setGenderTextField(currentUser.getGender());
        setInsuranceNameTextField(currentUser.getInsuranceName());
        setPrivateInsuranceTextField(currentUser.isPrivateInsurance());
    }

    /**
     * Set Username Text Field to user information in DB
     * @param username
     */
    private void setUsernameTextField(String username) {
        usernameLabel.setText(username);
    }

    /**
     * Set Email Text Field to user information in DB
     * @param email
     */
    private void setEmailTextField(String email) {
        emailLabel.setText(email);
    }

    /**
     * Set First Name Text Field to user information in DB
     * @param firstName
     */
    private void setFirstNameTextField(String firstName) {
        firstNameLabel.setText(firstName);
    }

    /**
     * Set Last Name Text Field to user information in DB
     * @param lastName
     */
    private void setLastNameTextField(String lastName) {
        lastNameLabel.setText(lastName);
    }

    /**
     * Set Street Text Field to user information in DB
     * @param street
     */
    private void setStreetTextField(String street) {
        streetLabel.setText(street);
    }

    /**
     * Set House Number Text Field to user information in DB
     * @param houseNo
     */
    private void setHouseNoTextField(String houseNo) {
        houseNumberLabel.setText(houseNo);
    }

    /**
     * Set Zip Text Field to user information in DB
     * @param zip
     */
    private void setZipTextField(String zip) {
        zipCodeLabel.setText(zip);
    }

    /**
     * Set Date Picker to birthday in DB
     * @param birthday
     */
    private void setBirthdayTextField(LocalDate birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Session.datePatternUI);
        birthdayLabel.setText(birthday.format(formatter));
    }

    /**
     * Set Gender Text Field to user information in DB
     * @param gender
     */
    private void setGenderTextField(String gender) {
        genderLabel.setText(gender);
    }

    private void setInsuranceNameTextField(String insuranceName) {
        insuranceNameLabel.setText(insuranceName);
    }

    /**
     * Set private insurance Check Box to user information in DB
     * @param privateInsurance
     */
    private void setPrivateInsuranceTextField(boolean privateInsurance) {
        privateInsuranceLabel.setText(privateInsurance ? "Yes" : "No");
    }
}

