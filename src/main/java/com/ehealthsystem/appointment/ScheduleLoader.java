package com.ehealthsystem.appointment;

import com.ehealthsystem.doctor.Doctor;
import com.ehealthsystem.doctor.DoctorTimeSlot;
import com.ehealthsystem.tools.Session;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import javax.activation.UnsupportedDataTypeException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class ScheduleLoader {
    @FXML
    GridPane scheduleGridPane;

    @FXML
    protected
    Label errorLabel;

    private ArrayList<Label> timeLabelList = new ArrayList<>();
    protected LocalTime selectedTime;

    protected void loadSchedule(LocalDate date, Doctor doctor, Label dateLabel, Button primaryActionButton) throws SQLException, UnsupportedDataTypeException {
        scheduleGridPane.getChildren().remove(1, scheduleGridPane.getChildren().size()); //clear grid pane, except for date label
        ArrayList<DoctorTimeSlot> doctorTimeSlotList = DoctorTimeSlot.getFreeTimeSlots(date, doctor);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Session.datePatternUI);
        dateLabel.setText(date.format(dateFormatter));

        int column = 0;
        int row = 1;
        if (doctorTimeSlotList.size() == 0) {
            errorLabel.setText("This doctor has no free appointments during the specified time range");
            errorLabel.setVisible(true);
            primaryActionButton.setDisable(true);
            return;
        } else { //initial state, or another date was selected => undo
            errorLabel.setVisible(false);
            primaryActionButton.setDisable(false);
        }
        for(int i = 0; i< doctorTimeSlotList.size(); i++, column++) {
            //Prepare UI
            Label time = new Label(doctorTimeSlotList.get(i).getTime().format(DateTimeFormatter.ofPattern(Session.timePatternSchedule)));
            Button timeButton = new Button();
            boolean free = doctorTimeSlotList.get(i).getFree();
            if(free) {
                handleTimeButton(time, timeButton);
            }
            setStyle(time, timeButton, free);

            if(i % 5 == 0 && i != 0) {
                //Go to next row
                column = 0;
                row++;
            }

            //Add to UI
            scheduleGridPane.add(time, column, row);
            scheduleGridPane.add(timeButton, column, row);
        }
    }

    private void handleTimeButton(Label time, Button timeButton) {
        timeLabelList.add(time);
        String timeStr = time.getText();
        // dynamically add the event handler for the buttons
        timeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (int i = 0; i<timeLabelList.size(); i++) {
                    timeLabelList.get(i).setTextFill(Color.web("#000000"));
                }
                time.setTextFill(Color.web("#FF0000"));
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Session.timePatternSchedule);
                selectedTime = LocalTime.parse(timeStr, timeFormatter);
            }
        });
    }

    /**
     * set style for the free appointment labels and the buttons
     * @param time
     * @param timeButton
     */
    private static void setStyle(Label time, Button timeButton, boolean free) {
        if (!free) time.setTextFill(Color.web("#999999"));
        time.setStyle("-fx-font-size: 15px;");
        timeButton.setStyle("-fx-opacity: 0%");
        timeButton.setPrefWidth(100);
    }
}
