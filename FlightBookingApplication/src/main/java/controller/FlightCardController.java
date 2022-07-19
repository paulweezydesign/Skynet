package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Flight;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class FlightCardController implements Initializable {

    @FXML
    private ImageView AirlineLogo;

    @FXML
    private Label lblArrAirport;

    @FXML
    private Label lblArrCity;

    @FXML
    private Label lblArrTime;

    @FXML
    private Label lblDepAirport;

    @FXML
    private Label lblDepCity;

    @FXML
    private Label lblDepDate;

    @FXML
    private Label lblDepTime;

    @FXML
    private Label lblDuration;

    @FXML
    private HBox parent;

    private Flight flight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parent.getStylesheets().add(getClass().getResource("/style/FlightCard.css").toExternalForm());
    }

    @FXML
    void bookNow(ActionEvent event) {
        try {
            FXMLLoader flightLoader = new FXMLLoader(getClass().getResource("/view/SearchPage/SeatMap.fxml"));

            Parent page = flightLoader.load();
            VBox.setVgrow(page, Priority.ALWAYS);

            SeatMapController flightController = flightLoader.getController();
            flightController.setData(flight);

            StackPane content = (StackPane) parent.getScene().lookup("#content");
            content.getChildren().add(page);

            ApplicationController.searchPageStack.push(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setData(Flight flight) {
        this.flight = flight;

        Image defaultLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_logo_x96.png")));
        Image airLineLogo = flight.getAirline().getLogo();
        AirlineLogo.setImage((airLineLogo != null) ? airLineLogo : defaultLogo);

        lblDepAirport.setText(flight.getDepAirport().getIATA());
        lblDepCity.setText(flight.getDepAirport().getCity());
        lblDepTime.setText(flight.getDepDatetime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblDepDate.setText(flight.getDepDatetime().toLocalDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        long durationInSeconds = Duration.between(flight.getDepDatetime(), flight.getArrDatetime()).getSeconds();
        lblDuration.setText(String.format("%dh %dm", durationInSeconds / 3600, (durationInSeconds % 3600) / 60));

        lblArrAirport.setText(flight.getArrAirport().getIATA());
        lblArrCity.setText(flight.getArrAirport().getCity());
        lblArrTime.setText(flight.getArrDatetime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
