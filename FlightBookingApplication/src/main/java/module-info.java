module FlightBookingApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires fr.brouillard.oss.cssfx;
    requires java.sql;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.swing;

    opens application;
    opens controller;
}