package com.ehealthsystem.healthinformation;

import com.ehealthsystem.database.Database;
import com.ehealthsystem.tools.SceneSwitch;
import com.ehealthsystem.tools.Session;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HealthInformationController implements Initializable {

    @FXML
    TableView<HealthInformationTableView> healthTableView;

    @FXML
    TableColumn<HealthInformationTableView, String> ICD;

    @FXML
    TableColumn<HealthInformationTableView, String> healthProblem;

    @FXML
    Button deleteButton;

    HealthInformationTableView selectedRow = new HealthInformationTableView(null, null);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadHealthProblem();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHealthProblem() throws SQLException {
        ObservableList<HealthInformationTableView> healthProblems = Database.getHealthInformationForTableView(Session.user.getUsername());
        ICD.setCellValueFactory(new PropertyValueFactory<HealthInformationTableView, String>("ICD"));
        healthProblem.setCellValueFactory(new PropertyValueFactory<HealthInformationTableView, String>("healthProblem"));
        healthTableView.setItems(healthProblems);
        healthTableView.setOnMousePressed((MouseEvent event) -> {
            if (event.getClickCount() >= 1) {
                selectedRow = healthTableView.getSelectionModel().getSelectedItem();
                System.out.println(selectedRow.getICD());
                System.out.println(selectedRow.getHealthProblem());
            }
        });
    }

    public void handleDeleteButton(ActionEvent event) throws SQLException, IOException {
        if(selectedRow.getICD() != null && selectedRow.getHealthProblem() != null) {
            Database.deleteHealthInformation(selectedRow, Session.user.getUsername());
            SceneSwitch.switchTo(null, "healthInformation/healthInformation-view.fxml", "Health information");
        }
    }
}
