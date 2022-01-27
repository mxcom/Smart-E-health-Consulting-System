package com.ehealthsystem.tools;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitch {
    /**
     * Switch to another scene
     * @param event the JavaFX event from the controller's handler method for the trigger (e.g. button)
     * @param file the FXML file for the next scene
     * @param title the window title to display
     * @throws IOException if the file was not found
     */
    public static void switchTo(Event event, String file, String title) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitch.class.getResource("/com/ehealthsystem/" + file));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Switch to another scene and centering the window
     * @param event the JavaFX event from the controller's handler method for the trigger (e.g. button)
     * @param file the FXML file for the next scene
     * @param title the window title to display
     * @throws IOException if the file was not found
     */
    public static void switchToCentered(Event event, String file, String title) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow(); //grab current stage before it's replaced at scene switch
        switchTo(event, file, title);
        centerWindow(stage);
    }

    /**
     * Center the window
     * @param stage the JavaFX window to center
     */
    public static void centerWindow(Stage stage) {
        // center window (works on Windows, not on Chrome OS)
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }
}
