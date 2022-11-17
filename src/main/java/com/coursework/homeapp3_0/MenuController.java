package com.coursework.homeapp3_0;

import com.coursework.homeapp3_0.database.Appliance;
import com.coursework.homeapp3_0.database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    List<Appliance> appliances;

    @FXML
    private TextField timeText;
    @FXML
    private Label powerConsLabel;
    @FXML
    private Label errorLabel;

    @FXML
    public void switchToAddWindow(ActionEvent event) {
        switchWindow("AddApplianceWindow.fxml", event);
    }

    @FXML
    public void switchToReviewWindow(ActionEvent event) {
        switchWindow("ReviewWindow.fxml", event);
    }

    private void switchWindow(String window, ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(window));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void startCalculation(ActionEvent event) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        appliances = new ArrayList<>(databaseHandler.getAllPluggedFromDb());
        errorLabel.setText("");
        String output = String.format("%.1f kW/h", doCalculation());
        powerConsLabel.setText(output);

    }

    public double doCalculation() {
        double result = 0.0;
        double hour = 0.0;
        if (appliances.isEmpty()) {
            errorLabel.setText("Please, turn on at least one device");
            return result;
        } else if (timeText.getText().trim().equals("")) {
            return result;
        } else {
            try {
                hour = Double.parseDouble(timeText.getText());
                if (hour < 0) {
                    errorLabel.setText("Please, enter positive number");
                    return result;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Please, enter real or integer number");
                return result;
            }
        }
        for (Appliance app : appliances) {
            result += ((double) app.getPower() / 1000) * hour;
        }
        return result;
    }
}