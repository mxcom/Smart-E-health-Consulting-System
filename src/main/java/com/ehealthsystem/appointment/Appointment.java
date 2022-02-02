package com.ehealthsystem.appointment;

import com.ehealthsystem.database.Database;
import com.ehealthsystem.doctor.Doctor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    public final String tableName = "appointment";
    private int id, doctor, minutesBeforeReminder, duration;
    private String user, healthProblemDescription;
    private LocalDate date;
    private LocalTime time;

    /**
     *
     * @param insertIntoDb true if this is a new appointment that is inserted to the database, false if the object shall solely represent an appointment that is already saved in the database
     * @param id ignored when insertToDb is true (to be precise, it is saved but overwritten after successful insert)
     * @param user
     * @param doctor
     * @param healthProblemDescription
     * @param date
     * @param time
     * @param minutesBeforeReminder
     * @param duration
     * @throws SQLException
     */
    public Appointment(boolean insertIntoDb, int id, String user, int doctor, String healthProblemDescription, LocalDate date, LocalTime time, int minutesBeforeReminder, int duration) throws SQLException {
        this.id = id;
        this.user = user;
        this.doctor = doctor;
        this.healthProblemDescription = healthProblemDescription;
        this.date = date;
        this.time = time;
        this.minutesBeforeReminder = minutesBeforeReminder;
        this.duration = duration;
        if (insertIntoDb)
            insertIntoDb();
    }

    /**
     * Inserts this appointment into the database.
     * Only to be used if a new appointment is created.
     * @throws SQLException
     */
    private void insertIntoDb() throws SQLException {
        Object[][] parameters = {
                {"user", user},
                {"doctor_id", doctor},
                {"healthProblemDescription", healthProblemDescription},
                {"date", date},
                {"time", time},
                {"minutesBeforeReminder", minutesBeforeReminder},
                {"duration", duration},
        };
        id = Database.insert(tableName, parameters);
    }

    public int getId() {
        return id;
    }

    public Doctor getDoctor() throws SQLException {
        return Database.loadDoctorFromId(doctor); //lazy-load and also don't store
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getMinutesBeforeReminder() {
        return minutesBeforeReminder;
    }

    public int getDuration() {
        return duration;
    }

    public String getUser() {
        return user;
    }

    public String getHealthProblemDescription() {
        return healthProblemDescription;
    }

    /**
     * Updates the entry for this appointment in the database
     * @param newValues
     * @throws SQLException
     */
    private void update(Object[][] newValues) throws SQLException {
        Database.update(
                tableName,
                newValues,
                new Object[][]{{"id", id}}
        );
    }

    /**
     * Deletes an appointment from the database.
     * Use to cancel an appointment.
     * @return whether deletion succeeded
     */
    public boolean delete() throws SQLException {
        String query = "DELETE FROM appointment WHERE id = ?";
        PreparedStatement statement = Database.connection.prepareStatement(query);
        statement.setInt(1, id);
        return statement.executeUpdate() == 1;
    }

    public void setDate(LocalDate date) throws SQLException {
        update(new Object[][]{{"date", date}});
        this.date = date;
    }

    public void setTime(LocalTime time) throws SQLException {
        update(new Object[][]{{"time", time}});
        this.time = time;
    }
}
