package com.coursework.homeapp3_0;

import com.coursework.homeapp3_0.database.Appliance;
import com.coursework.homeapp3_0.database.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReviewController {
    private Parent root;
    private static final String IMAGE_PATH = "src/main/resources/com/coursework/homeapp3_0/pictures/";
    File file = new File(IMAGE_PATH + "appliances.png");
    List<Appliance> appliances;
    Appliance currentAppliance;
    ToggleGroup group = new ToggleGroup();

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private ImageView imageView;
    @FXML
    private Button onButton;
    @FXML
    private Label infoLabel;
    @FXML
    private RadioButton radioShowAll;
    @FXML
    private RadioButton radioShowInRange;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField range1;
    @FXML
    private TextField range2;
    @FXML
    private ListView<Appliance> listView;

    @FXML
    void switchToMenu(ActionEvent event) {
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
    void removeAppliance(ActionEvent event) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        if (listView.getSelectionModel().isEmpty()) {
            return;
        }
        databaseHandler.removeFromDb(currentAppliance);
        appliances.remove(currentAppliance);
        infoLabel.setText("");
        file = new File(IMAGE_PATH + "appliances.png");
        onButton.setStyle("-fx-background-color: yellow");
        imageView.setImage(new Image(file.toURI().toString()));
        listView.getItems().clear();
        listView.getItems().addAll(appliances);
    }

    @FXML
    void plugInAppliance(ActionEvent event) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        if (currentAppliance == null)
            return;
        if (currentAppliance.getStatus().equals("on")) {
            onButton.setStyle("-fx-background-color: red");
            currentAppliance.setStatus("off");
        } else {
            onButton.setStyle("-fx-background-color: green");
            currentAppliance.setStatus("on");
        }
        databaseHandler.changeStatusInDb(currentAppliance);
        infoLabel.setText(currentAppliance.showCharacteristic());
    }

    @FXML
    void showInRange(ActionEvent event) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        int fRange;
        int sRange;
        radioShowAll.setToggleGroup(group);
        radioShowInRange.setToggleGroup(group);
        if (radioShowAll.isSelected()) {
            listView.getItems().clear();
            listView.getItems().addAll(appliances);
        } else {
            if (!range1.getText().trim().isEmpty() && !range2.getText().trim().isEmpty()) {
                try {
                    fRange = Integer.parseInt(range1.getText());
                    sRange = Integer.parseInt(range2.getText());
                } catch (NumberFormatException e) {
                    errorRange();
                    return;
                }
            } else {
                errorRange();
                return;
            }
            listView.getItems().clear();
            listView.getItems().addAll(databaseHandler.getAllInRange(fRange, sRange));
        }
    }

    public void errorRange() {
        errorLabel.setText("Please, enter any number of range");
        infoLabel.setText("");
        onButton.setStyle("-fx-background-color: yellow");
        file = new File(IMAGE_PATH + "appliances.png");
        imageView.setImage(new Image(file.toURI().toString()));
        listView.getItems().clear();
        radioShowAll.fire();
    }

    @FXML
    void initialize() {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        appliances = new ArrayList<>(databaseHandler.getAllFromDb());
        radioShowAll.fire();
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, appliance, t1) -> {
            currentAppliance = listView.getSelectionModel().getSelectedItem();
            if (currentAppliance != null) {
                infoLabel.setText(currentAppliance.showCharacteristic());
                file = new File(IMAGE_PATH + currentAppliance.getType() + ".png");
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                if (currentAppliance.getStatus().equals("off"))
                    onButton.setStyle("-fx-background-color: red");
                else
                    onButton.setStyle("-fx-background-color: green");
            }
        });
    }
}
