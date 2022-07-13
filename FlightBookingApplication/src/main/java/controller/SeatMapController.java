package controller;

import data.SeatDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import models.Account;
import models.Flight;
import models.Seat;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SeatMapController implements Initializable {

    @FXML
    private Button btnBack;

    @FXML
    private HBox confirmationWindow;

    @FXML
    private Label lblAirline;

    @FXML
    private Label lblArrAirport;

    @FXML
    private Label lblArrCity;

    @FXML
    private Label lblArrDate;

    @FXML
    private Label lblArrTime;

    @FXML
    private Label lblBusinessPrice;

    @FXML
    private Label lblDepAirport;

    @FXML
    private Label lblDepCity;

    @FXML
    private Label lblDepDate;

    @FXML
    private Label lblDepTime;

    @FXML
    private Label lblEconomyPrice;

    @FXML
    private Label lblFirstPrice;

    @FXML
    private Label lblFirstname;

    @FXML
    private Label lblFromCode;

    @FXML
    private Label lblSeat;

    @FXML
    private Label lblSelectedSeat;

    @FXML
    private Label lblToCode;

    @FXML
    private StackPane parent;

    @FXML
    private GridPane seatMap;

    private final ToggleGroup seatGroup = new ToggleGroup();

    private Flight flight;
    private Seat selectedSeat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parent.getStylesheets().add(getClass().getResource("/style/SeatMap.css").toExternalForm());

        seatGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            //when a seat is selected set the confirmationWindow visibility to true, otherwise set it to false
            confirmationWindow.setVisible(newValue != null);
            selectedSeat = (Seat) seatGroup.getSelectedToggle();
            lblSeat.setText(selectedSeat.getColumn() + selectedSeat.getRow());
        }));
    }

    public void setData(Flight flight) {
        this.flight = flight;
        lblDepAirport.setText(flight.getDepAirport().getName());
        lblDepCity.setText(flight.getDepAirport().getCity() + " - " + flight.getDepAirport().getCountry());
        lblDepDate.setText(flight.getDepDatetime().toLocalDate().toString());
        lblDepTime.setText(flight.getDepDatetime().toLocalTime().toString());

        lblAirline.setText(flight.getAirline().getName());

        lblArrAirport.setText(flight.getArrAirport().getName());
        lblArrCity.setText(flight.getArrAirport().getCity() + " - " + flight.getArrAirport().getCountry());
        lblArrDate.setText(flight.getArrDatetime().toLocalDate().toString());
        lblArrTime.setText(flight.getArrDatetime().toLocalTime().toString());

        lblFirstPrice.setText(flight.getFirstPriceFormatted());
        lblBusinessPrice.setText(flight.getBusinessPriceFormatted());
        lblEconomyPrice.setText(flight.getEconomyPriceFormatted());

        lblFirstname.setText(Account.getCurrentUser().getPassenger().getFirstname());
        String depIATA = flight.getDepAirport().getIATA();
        String depICAO = flight.getDepAirport().getICAO();
        lblFromCode.setText((depIATA != null) ? depIATA : depICAO);
        String arrIATA = flight.getArrAirport().getIATA();
        String arrICAO = flight.getArrAirport().getICAO();
        lblToCode.setText((arrIATA != null) ? arrIATA : arrICAO);

        fillSeatMap();
    }

    @FXML
    private void cancelSelected() {
        seatGroup.selectToggle(null);
    }

    @FXML
    private void confirmSelected() {
        try {
            FXMLLoader paymentLoader = new FXMLLoader(getClass().getResource("/view/SearchPage/PaymentPage.fxml"));
            Parent page = paymentLoader.load();
            VBox.setVgrow(page, Priority.ALWAYS);

            PaymentPageController paymentController = paymentLoader.getController();

            paymentController.setData(flight, selectedSeat);

            StackPane content = (StackPane) parent.getScene().lookup("#content");
            content.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        StackPane content = (StackPane) parent.getScene().lookup("#content");
        int recentChild = content.getChildren().size() - 1;
        content.getChildren().remove(recentChild);
    }

    private void fillSeatMap() {
        SeatDao seatDao = new SeatDao();
        ArrayList<Seat> seatList = new ArrayList<>(seatDao.readAll());

        // add a row contains Columns numbering
        char ref = 'A';
        for (int col = 0; col <= 6; col++) {
            if (col == 3) {continue;}
            Label lbl = new Label(Character.toString(ref++));
            lbl.setMinSize(40, 40);
            lbl.setAlignment(Pos.CENTER);
            seatMap.add(lbl, col, 0);
        }

        int id = 0;
        final int nbColumn = 6;
        int nbRows = seatList.size() / nbColumn;
        int seatRow = 1;

        for (int row = 1; row <= nbRows; row++) {
            for (int col = 0; col <= nbColumn; col++) {
                // add a column contains row numbering
                if (col == 3) {
                    Label lbl = new Label(seatRow++ + "");
                    lbl.setMinSize(40, 40);
                    lbl.setAlignment(Pos.CENTER);
                    seatMap.add(lbl, col, row);
                    continue;
                }

                // add the exit labels
                if (row == 8 || row == 12) {
                    Label leftExit = new Label("◀ EXIT");
                    Label rightExit = new Label("EXIT ▶");
                    leftExit.setMinSize(40, 40);
                    rightExit.setMinSize(40, 40);
                    rightExit.setAlignment(Pos.BASELINE_RIGHT);
                    seatMap.add(leftExit, 0, row);
                    seatMap.add(rightExit, 6, row);

                    nbRows++;
                    break;
                }

                Seat seat = seatList.get(id++);
                seatGroup.getToggles().add(seat);

                //disable the reserved seats
                if (seat.isReserved(flight)) {
                    seat.getStyleClass().add("UnavailableSeatIcon");
                    seat.setDisable(true);
                }

                //add the seat to the setMap
                seatMap.add(seat, col, row);
            }
        }
    }
}
