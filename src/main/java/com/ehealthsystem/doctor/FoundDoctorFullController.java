package com.ehealthsystem.doctor;

import com.ehealthsystem.appointment.AppointmentInCreation;
import com.ehealthsystem.database.Database;
import com.ehealthsystem.healthinformation.HealthInformation;
import com.ehealthsystem.map.DoctorDistance;
import com.ehealthsystem.map.GeoCoder;
import com.ehealthsystem.tools.SceneSwitch;
import com.ehealthsystem.tools.Session;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FoundDoctorFullController {

    @FXML
    Label doctorLabel;

    @FXML
    Label addressLabel;

    @FXML
    Label specializationLabel;

    @FXML
    Label dateLabel;

    @FXML
    GridPane scheduleGridPane;

    @FXML
    Label errorLabel;

    @FXML
    private WebView mapWebView = new WebView();

    private DoctorDistance doctor = new DoctorDistance();
    private String userGeoData;
    private String doctorGeoData;
    private ArrayList<Label> timeLabelList = new ArrayList<>();
    private LocalTime selectedTime;

    public void start() throws IOException, InterruptedException, ApiException, SQLException {
        loadMap();
        loadDoctorData();
        loadSchedule();
    }


    public void setDoctor(DoctorDistance doctor) {
        this.doctor = doctor;
    }

    public void setUserGeoData(String userGeoData) {
        this.userGeoData = userGeoData;
    }

    public void setDoctorGeoData(String doctorGeoData) {
        this.doctorGeoData = doctorGeoData;
    }

    private void loadDoctorData() {
        doctorLabel.setText("Dr. " + doctor.getDoctor().getFirstName() + " " + doctor.getDoctor().getLastName());
        addressLabel.setText(doctorGeoData);
    }

    private void loadSchedule() throws SQLException {
        ArrayList<DoctorAppointment> doctorAppointmentList = Database.loadDoctorAppointments(doctor.getDoctor().getFirstName(), doctor.getDoctor().getLastName(), Session.appointment.getDate());
        dateLabel.setText(Session.appointment.getDate().toString());
        int column = 0;
        int row = 1;
        for(int i = 0; i<doctorAppointmentList.size(); i++) {
            if(doctorAppointmentList.get(i).getFree()) {
                Label time = new Label(doctorAppointmentList.get(i).getTime().toString());
                Button timeButton = new Button();
                handleTimeButton(time, timeButton);
                setStyle(time, timeButton);
                if(i % 2 == 0 && i != 0) {
                    column = 0;
                    row++;
                }
                scheduleGridPane.add(time, column, row);
                scheduleGridPane.add(timeButton, column, row);
                column++;
            } else {
                Label time = new Label(doctorAppointmentList.get(i).getTime().toString());
                setStyle(time);
                if(i % 2 == 0 && i != 0) {
                    column = 0;
                    row++;
                }
                scheduleGridPane.add(time, column, row);
                column++;
            }

        }
    }

    private void handleTimeButton(Label time, Button timeButton) {
        timeLabelList.add(time);
        String timeStr = time.getText();
        timeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (int i = 0; i<timeLabelList.size(); i++) {
                    timeLabelList.get(i).setTextFill(Color.web("#000000"));
                }
                DateTimeFormatter TimeFormatter = DateTimeFormatter.ofPattern("H:mm");
                selectedTime = LocalTime.parse(timeStr, TimeFormatter);
                time.setTextFill(Color.web("#FF0000"));
            }
        });
    }

    private void setStyle(Label time) {
        time.setStyle("-fx-font-size: 15px;");
        time.setTextFill(Color.web("#999999"));
    }

    private void setStyle(Label time, Button timeButton) {
        time.setStyle("-fx-font-size: 15px;");
        timeButton.setStyle("-fx-opacity: 0%");
        timeButton.setPrefWidth(100);
    }

    public void handleBackButton(ActionEvent event) throws IOException {
        SceneSwitch.switchTo(event, "appointment/appointmentFound-view.fxml", "Make appointment");;
    }

    public void handleSelectButton(ActionEvent event) throws IOException {
        Session.appointment.setTime(selectedTime);
        if(Session.appointment.getTime() != null) {
            SceneSwitch.switchTo(event, "appointment/appointmentFound-view.fxml", "Make appointment");
        } else {
            errorLabel.setVisible(true);
        }
    }

    public void loadMap() throws IOException, InterruptedException, ApiException {
        editFile();
        WebEngine engine = mapWebView.getEngine();
        engine.load(getClass().getResource("/com/ehealthsystem/map/map.html").toString());
    }

    public void editFile() throws IOException, InterruptedException, ApiException{
        LatLng latlng = GeoCoder.geocodeToLatLng(doctorGeoData);
        double lat = latlng.lat;
        double lng = latlng.lng;
        String newSearch = "center: new google.maps.LatLng(" + lat + "," + lng + ")";

        File htmlFile = new File("src\\main\\resources\\com\\ehealthsystem\\map\\map.html");
        BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
        System.out.println(htmlFile);
        String line = reader.readLine();
        String content = "";

        while(line != null) {
            content += line + System.lineSeparator();
            line = reader.readLine();
        }

        String modifiedContent = content.replace("center: new google.maps.LatLng()", newSearch);
        BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile));

        writer.write(modifiedContent);
        reader.close();
        writer.close();
    }
}
