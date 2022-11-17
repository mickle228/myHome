package com.coursework.homeapp3_0;

import com.coursework.homeapp3_0.database.Appliance;
import com.coursework.homeapp3_0.database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddController {
    private Parent root;
    private final String[] appliances = new String[] {"microwave", "refrigerator", "washing machine"};

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private ChoiceBox<String> choiseBox;
    @FXML
    private TextField companyText;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField modelText;
    @FXML
    private TextField powerText;

    @FXML
    void createAppliance(ActionEvent event) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        Appliance appliance;
        int power = 0;
        if (!modelText.getText().trim().isEmpty()
                && !companyText.getText().trim().isEmpty() && !powerText.getText().trim().isEmpty()) {
            try {
                power = Integer.parseInt(powerText.getText().trim());
                if (power < 0){
                    errorLabel.setText("Please, enter positive number");
                    return;
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Please, enter integer number of power");
                return;
            }
        }
        else{
            errorLabel.setText("Text fields do not have to be empty!!");
            return;
        }
        appliance = new Appliance(choiseBox.getValue().trim(), modelText.getText().trim(),
                companyText.getText().trim(), power, "off");
        try {
            databaseHandler.AddApplianceInDb(appliance);
        } catch (SQLException e) {
            errorLabel.setText("Something go wrong, please try again");
            e.printStackTrace();
        }
        errorLabel.setText("");
        companyText.clear();
        modelText.clear();
        powerText.clear();
    }

    @FXML
    public void switchToMenu(ActionEvent event){
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Menu.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void initialize() {
        choiseBox.getItems().addAll(appliances);
        choiseBox.setValue("microwave");
    }
}
