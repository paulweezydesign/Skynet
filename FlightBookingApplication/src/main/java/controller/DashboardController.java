package controller;

import data.AirlineDao;
import data.AirportDao;
import data.FlightDao;
import data.ReservationDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Account;
import models.Airport;
import models.Flight;
import org.controlsfx.control.SearchableComboBox;
import view.Palette;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class DashboardController implements Initializable {

    @FXML
    private StackPane parent;
    @FXML
    private Button actionButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private StackPane actionPanel;

    @FXML
    private ImageView airlineLogo;

    @FXML
    private SearchableComboBox<Airport> arrAirport;

    @FXML
    private SearchableComboBox<String> arrCity;

    @FXML
    private SearchableComboBox<String> arrCountry;

    @FXML
    private DatePicker arrDate;

    @FXML
    private Spinner<Integer> arrHour;

    @FXML
    private Spinner<Integer> arrMinute;

    @FXML
    private TableColumn<Flight, String> colArrAirport;

    @FXML
    private TableColumn<Flight, String> colArrDateTime;

    @FXML
    private TableColumn<Flight, String> colCapacity;

    @FXML
    private TableColumn<Flight, String> colDepAirport;

    @FXML
    private TableColumn<Flight, String> colDepDateTime;

    @FXML
    private TableColumn<Flight, Integer> colId;

    @FXML
    private SearchableComboBox<Airport> depAirport;

    @FXML
    private SearchableComboBox<String> depCity;

    @FXML
    private SearchableComboBox<String> depCountry;

    @FXML
    private DatePicker depDate;

    @FXML
    private Spinner<Integer> depHour;

    @FXML
    private Spinner<Integer> depMinute;

    @FXML
    private TableView<Flight> flightTable;

    @FXML
    private Label lblAirlineName;

    @FXML
    private Label lblTitle;

    @FXML
    private TextField priceBusinessClass;

    @FXML
    private TextField priceEconomyClass;

    @FXML
    private TextField priceFirstClass;

    @FXML
    private TextField priceLuggage;

    @FXML
    private TextField priceWeight;

    @FXML
    private TextField serachBar;

    @FXML
    private ToggleButton themeButton;

    private FilteredList<Flight> results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlightDao flightDao = new FlightDao();
        results = new FilteredList<>(FXCollections.observableList(flightDao.read(Account.getCurrentUser().getAirline())), flight -> true);
        setData();
        findFlight();

        if (Palette.getDefaultPalette().equals(Palette.DarkPalette)) {
            themeButton.setSelected(true);
        }
    }

    private void setData() {
        airlineLogo.setImage(Account.getCurrentUser().getAirline().getLogo());
        lblAirlineName.setText(Account.getCurrentUser().getAirline().getName());

        depCountry.setItems(AirportDao.getCountryList());
        arrCountry.setItems(AirportDao.getCountryList());

        depCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> depCity.setItems(AirportDao.getCityList(newValue)));
        arrCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> arrCity.setItems(AirportDao.getCityList(newValue)));

        depCity.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> depAirport.setItems(AirportDao.getAirportList(newValue)));
        arrCity.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> arrAirport.setItems(AirportDao.getAirportList(newValue)));

        depAirport.setCellFactory((ListView<Airport> listView) -> new ListCell<>() {

            @Override
            public void updateItem(Airport item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });
        depAirport.setButtonCell(new ListCell<>(){
            @Override
            public void updateItem(Airport item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        arrAirport.setCellFactory((ListView<Airport> listView) -> new ListCell<>() {
            @Override
            public void updateItem(Airport item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });
        arrAirport.setButtonCell(new ListCell<>(){
            @Override
            public void updateItem(Airport item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });

        {
            SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
            depHour.setValueFactory(hourValueFactory);
            SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
            depMinute.setValueFactory(minuteValueFactory);
        }

        {
            SpinnerValueFactory<Integer> hourValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
            arrHour.setValueFactory(hourValueFactory);
            SpinnerValueFactory<Integer> minuteValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
            arrMinute.setValueFactory(minuteValueFactory);
        }

        {
            allowDoubleOnly(priceFirstClass);
            allowDoubleOnly(priceBusinessClass);
            allowDoubleOnly(priceEconomyClass);
            allowDoubleOnly(priceLuggage);
            allowDoubleOnly(priceWeight);
        }

        {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colDepAirport.setCellValueFactory(flight -> new SimpleStringProperty(flight.getValue().getDepAirport().getName()));
            colDepDateTime.setCellValueFactory(flight -> new SimpleStringProperty(flight.getValue().getDepDatetime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            colArrAirport.setCellValueFactory(flight -> new SimpleStringProperty(flight.getValue().getArrAirport().getName()));
            colArrDateTime.setCellValueFactory(flight -> new SimpleStringProperty(flight.getValue().getArrDatetime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            colCapacity.setCellValueFactory(flight -> {
                ReservationDao reservationDao = new ReservationDao();
                return new SimpleStringProperty(reservationDao.countReservations(flight.getValue()) + "/120");
            });
            flightTable.setItems(results);

            flightTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                editButton.setDisable(newValue == null);
                deleteButton.setDisable(newValue == null);
            });

            flightTable.setRowFactory(tableView -> {
                TableRow<Flight> row = new TableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    int index = row.getIndex();
                    if (tableView.getSelectionModel().isSelected(index)) {
                        tableView.getSelectionModel().clearSelection();
                        event.consume();
                    }
                });
                return row;
            });
        }


    }

    private void allowDoubleOnly(TextField textField) {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        textField.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> pattern.matcher(change.getControlNewText()).matches() ? change : null));
    }

    @FXML
    void openAddTab() {
        actionPanel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        actionPanel.setVisible(true);
        flightTable.getSelectionModel().clearSelection();

        lblTitle.setText("New Flight");

        actionButton.setText("ADD");
        actionButton.setOnAction(e -> addFlight());
    }

    private void addFlight() {
        FlightDao flightDao = new FlightDao();

        Flight flight = new Flight();

        flight.setAirline(Account.getCurrentUser().getAirline().getId());

        flight.setDepAirport(depAirport.getValue());
        flight.setArrAirport(arrAirport.getValue());

        flight.setDepDatetime(LocalDateTime.of(depDate.getValue(), LocalTime.of(depHour.getValue(), depMinute.getValue())));
        flight.setArrDatetime(LocalDateTime.of(arrDate.getValue(), LocalTime.of(arrHour.getValue(), arrMinute.getValue())));

        flight.setFirstPrice(Double.parseDouble(priceFirstClass.getText()));
        flight.setBusinessPrice(Double.parseDouble(priceBusinessClass.getText()));
        flight.setEconomyPrice(Double.parseDouble(priceEconomyClass.getText()));
        flight.setLuggagePrice(Double.parseDouble(priceLuggage.getText()));
        flight.setWeightPrice(Double.parseDouble(priceWeight.getText()));

        flightDao.create(flight);
        ObservableList<Flight> source = (ObservableList<Flight>) results.getSource();
        source.add(0, flight);
    }

    @FXML
    void openEditTab() {
        actionPanel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        actionPanel.setVisible(true);

        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();

        lblTitle.setText("Flight #" + selectedFlight.getId());

        //departure data
        {
            depCountry.getSelectionModel().select(selectedFlight.getDepAirport().getCountry());
            depCity.getSelectionModel().select(selectedFlight.getDepAirport().getCity());
            depAirport.getItems().forEach(airport -> {
                if (airport.getId() == selectedFlight.getDepAirport().getId()) {
                    depAirport.getSelectionModel().select(airport);
                }
            });

            depDate.setValue(selectedFlight.getDepDatetime().toLocalDate());
            depHour.getValueFactory().setValue(selectedFlight.getDepDatetime().getHour());
            depMinute.getValueFactory().setValue(selectedFlight.getDepDatetime().getMinute());
        }

        //arrival data
        {
            arrCountry.getSelectionModel().select(selectedFlight.getArrAirport().getCountry());
            arrCity.getSelectionModel().select(selectedFlight.getArrAirport().getCity());
            arrAirport.getItems().forEach(airport -> {
                if (airport.getId() == selectedFlight.getArrAirport().getId()) {
                    arrAirport.getSelectionModel().select(airport);
                }
            });

            arrDate.setValue(selectedFlight.getArrDatetime().toLocalDate());
            arrHour.getValueFactory().setValue(selectedFlight.getArrDatetime().getHour());
            arrMinute.getValueFactory().setValue(selectedFlight.getArrDatetime().getMinute());
        }

        //pricing data
        {
            priceFirstClass.setText(String.valueOf(selectedFlight.getFirstPrice()));
            priceBusinessClass.setText(String.valueOf(selectedFlight.getBusinessPrice()));
            priceEconomyClass.setText(String.valueOf(selectedFlight.getEconomyPrice()));
            priceLuggage.setText(String.valueOf(selectedFlight.getLuggagePrice()));
            priceWeight.setText(String.valueOf(selectedFlight.getWeightPrice()));
        }

        actionButton.setText("UPDATE");
        actionButton.setOnAction(e -> updateFlight(selectedFlight));
    }

    private void updateFlight(Flight flight) {
        FlightDao flightDao = new FlightDao();

        flight.setAirline(Account.getCurrentUser().getAirline().getId());

        flight.setDepAirport(depAirport.getValue());
        flight.setArrAirport(arrAirport.getValue());

        flight.setDepDatetime(LocalDateTime.of(depDate.getValue(), LocalTime.of(depHour.getValue(), depMinute.getValue())));
        flight.setArrDatetime(LocalDateTime.of(arrDate.getValue(), LocalTime.of(arrHour.getValue(), arrMinute.getValue())));

        flight.setFirstPrice(Double.parseDouble(priceFirstClass.getText()));
        flight.setBusinessPrice(Double.parseDouble(priceBusinessClass.getText()));
        flight.setEconomyPrice(Double.parseDouble(priceEconomyClass.getText()));
        flight.setLuggagePrice(Double.parseDouble(priceLuggage.getText()));
        flight.setWeightPrice(Double.parseDouble(priceWeight.getText()));

        flightDao.update(flight.getId(), flight);
        flightTable.refresh();
    }

    @FXML
    void deleteFlight() {
        FlightDao flightDao = new FlightDao();
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();

        flightDao.delete(selectedFlight.getId());
        ObservableList<Flight> source = (ObservableList<Flight>) results.getSource();
        source.remove(selectedFlight);
    }

    @FXML
    void closePanel() {
        actionPanel.setPrefWidth(0);
        actionPanel.setVisible(false);
        flightTable.getSelectionModel().clearSelection();
        //clear departure data
        {
            depCountry.getSelectionModel().clearSelection();

            depDate.setValue(null);
            depHour.getValueFactory().setValue(0);
            depMinute.getValueFactory().setValue(0);
        }

        //clear arrival data
        {
            arrCountry.getSelectionModel().clearSelection();

            arrDate.setValue(null);
            arrHour.getValueFactory().setValue(0);
            arrMinute.getValueFactory().setValue(0);
        }

        //clear pricing data
        {
            priceFirstClass.setText(String.valueOf(0));
            priceBusinessClass.setText(String.valueOf(0));
            priceEconomyClass.setText(String.valueOf(0));
            priceLuggage.setText(String.valueOf(0));
            priceWeight.setText(String.valueOf(0));
        }
    }

    @FXML
    void changeTheme() {
        if (themeButton.isSelected()) {
            Palette.setDefaultPalette(Palette.DarkPalette);
            Palette.getDefaultPalette().usePalette(parent.getScene());
        }
        else {
            Palette.setDefaultPalette(Palette.LightPalette);
            Palette.getDefaultPalette().usePalette(parent.getScene());
        }
    }

    @FXML
    void Logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Signin.fxml"));

            Scene scene = new Scene(root);
            Palette.getDefaultPalette().usePalette(scene);

            Stage oldStage = (Stage) parent.getScene().getWindow();
            oldStage.close();

            Stage stage = new Stage(StageStyle.UNIFIED);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle("Flight Booking Application");
            stage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void uploadeImage(ActionEvent event) {
        AirlineDao airlineDao = new AirlineDao();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG IMAGE","*.png")
        );

        File picturesDir = new File(System.getProperty("user.home") + "\\pictures");
        if (picturesDir.exists()) {
            fileChooser.setInitialDirectory(picturesDir);
        }

        File selectedFile = fileChooser.showOpenDialog(parent.getScene().getWindow());

        if (selectedFile != null) {
            Image logo = new Image(selectedFile.getAbsolutePath());
            airlineLogo.setImage(logo);
            Account.getCurrentUser().getAirline().setLogo(logo);
            airlineDao.update(Account.getCurrentUser().getAirline().getId(), Account.getCurrentUser().getAirline());
        }
    }

    private void findFlight() {

        serachBar.textProperty().addListener((observableValue, oldValue, newValue) -> {

            results.setPredicate(flight -> {

                if (newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                if (String.valueOf(flight.getId()).equals(keyword)) {
                    return true;
                }
                else if (flight.getDepAirport().getName().toLowerCase().contains(keyword)) {
                    return true;
                }
                else if (flight.getDepAirport().getCountry().toLowerCase().contains(keyword)) {
                    return true;
                }
                else if (flight.getDepAirport().getCity().toLowerCase().contains(keyword)) {
                    return true;
                }
                else if (flight.getDepAirport().getIATA().toLowerCase().equals(keyword)) {
                    return true;
                }
                else if (flight.getDepAirport().getICAO().toLowerCase().equals(keyword)) {
                    return true;
                }
                else if (flight.getArrAirport().getName().toLowerCase().contains(keyword)) {
                    return true;
                }
                else if (flight.getArrAirport().getCountry().toLowerCase().contains(keyword)) {
                    return true;
                }
                else if (flight.getArrAirport().getCity().toLowerCase().contains(keyword)) {
                    return true;
                }
                else if (flight.getArrAirport().getIATA().toLowerCase().equals(keyword)) {
                    return true;
                }
                else if (flight.getArrAirport().getICAO().toLowerCase().equals(keyword)) {
                    return true;
                }
                else {
                    return false;
                }
            });
        });
    }
}
