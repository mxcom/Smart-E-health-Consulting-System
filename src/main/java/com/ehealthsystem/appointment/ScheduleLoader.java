package com.ehealthsystem.appointment;

import com.ehealthsystem.doctor.Doctor;
import com.ehealthsystem.doctor.DoctorTimeSlot;
import com.ehealthsystem.tools.Session;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class ScheduleLoader {
    protected LocalTime selectedTime;

    public void setSelectedTime(LocalTime selectedTime) {
        this.selectedTime = selectedTime;
    }

    public static void loadSchedule(LocalDate date, Doctor doctor, GridPane scheduleGridPane, Label dateLabel, ArrayList<Label> timeLabelList, ScheduleLoader loader) throws SQLException {
        scheduleGridPane.getChildren().clear(); //clear grid pane
        ArrayList<DoctorTimeSlot> doctorTimeSlotList = DoctorTimeSlot.getFreeTimeSlots(date, doctor);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Session.datePatternUI);
        dateLabel.setText(date.format(dateFormatter));

        int column = 0;
        int row = 1;
        if (doctorTimeSlotList.size() == 0) {
            //TODO add
            //errorLabel.setText("This doctor has no free appointments during the specified time range");
            //errorLabel.setVisible(true);
            return;
        }
        for(int i = 0; i< doctorTimeSlotList.size(); i++, column++) {
            //Prepare UI
            Label time = new Label(doctorTimeSlotList.get(i).getTime().toString());
            Button timeButton = new Button();
            if(doctorTimeSlotList.get(i).getFree()) {
                handleTimeButton(time, timeButton, timeLabelList, loader);
                setStyle(time, timeButton);
            } else {
                setStyle(time);
            }

            if(i % 5 == 0 && i != 0) {
                //Go to next row
                column = 0;
                row++;
            }

            //Add to UI
            scheduleGridPane.add(time, column, row);
            if(doctorTimeSlotList.get(i).getFree()) {
                scheduleGridPane.add(timeButton, column, row);
            }
        }
    }

    private static void handleTimeButton(Label time, Button timeButton, ArrayList<Label> timeLabelList, ScheduleLoader loader) {
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
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Session.timePatternUI);
                loader.setSelectedTime(LocalTime.parse(timeStr, timeFormatter));
            }
        });
    }

    /**
     * set the style for the not free appointment labels
     * @param time
     */
    private static void setStyle(Label time) {
        time.setStyle("-fx-font-size: 15px;");
        time.setTextFill(Color.web("#999999"));
    }

    /**
     * set style for the free appointment labels and the buttons
     * @param time
     * @param timeButton
     */
    private static void setStyle(Label time, Button timeButton) {
        time.setStyle("-fx-font-size: 15px;");
        timeButton.setStyle("-fx-opacity: 0%");
        timeButton.setPrefWidth(100);
    }
}
