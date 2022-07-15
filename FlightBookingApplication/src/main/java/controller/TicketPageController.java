package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import models.Flight;
import models.Passenger;
import models.Reservation;
import models.Seat;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TicketPageController {

    @FXML
    private ImageView imgAirlineLogo;

    @FXML
    private ImageView imgAppLogo;

    @FXML
    private Label lblArrAirport;

    @FXML
    private Label lblArrCity;

    @FXML
    private Label lblArrDateTime;

    @FXML
    private Label lblClassType;

    @FXML
    private Label lblDepAirport;

    @FXML
    private Label lblDepCity;

    @FXML
    private Label lblDepDateTime;

    @FXML
    private Label lblFlightID;

    @FXML
    private Label lblSelectedSeat;

    @FXML
    private Label lblPassengerName;

    @FXML
    private HBox parent;

    @FXML
    private VBox ticket;
    private Reservation reservation;

    @FXML
    void downloadTicket(ActionEvent event) {
        String depCity = reservation.getFlight().getDepAirport().getCity();
        String arrCity = reservation.getFlight().getArrAirport().getCity();
        try {
            WritableImage writableImage = ticket.snapshot(new SnapshotParameters(), null);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("Ticket_From_"+depCity+"_To_"+arrCity);
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG IMAGE","*.png")
            );

            File selectedFile = fileChooser.showSaveDialog(parent.getScene().getWindow());

            if (selectedFile != null) {
                File file = new File(selectedFile.getAbsolutePath());
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(Reservation reservation) {
        this.reservation = reservation;
        Flight flight = reservation.getFlight();
        Seat selectedSeat = reservation.getSeat();
        Passenger passenger = reservation.getAccount().getPassenger();

        String depIATA = flight.getDepAirport().getIATA();
        String depICAO = flight.getDepAirport().getICAO();
        lblDepAirport.setText((depIATA != null) ? depIATA : depICAO);
        lblDepCity.setText(flight.getDepAirport().getCity() + " - " + flight.getDepAirport().getCountry());

        String arrIATA = flight.getArrAirport().getIATA();
        String arrICAO = flight.getArrAirport().getICAO();
        lblArrAirport.setText((arrIATA != null) ? arrIATA : arrICAO);
        lblArrCity.setText(flight.getArrAirport().getCity() + " - " + flight.getArrAirport().getCountry());

        lblPassengerName.setText(passenger.getFirstname() + " " + passenger.getLastname());
        lblFlightID.setText(String.valueOf(flight.getId()));

        lblSelectedSeat.setText(selectedSeat.getColumn()+selectedSeat.getRow());
        lblClassType.setText(selectedSeat.getType() + " class");

        lblDepDateTime.setText(flight.getDepDatetime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        lblArrDateTime.setText(flight.getArrDatetime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
    }

}
