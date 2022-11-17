module com.example.homeapp3_0 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.coursework.homeapp3_0 to javafx.fxml;
    exports com.coursework.homeapp3_0;
}