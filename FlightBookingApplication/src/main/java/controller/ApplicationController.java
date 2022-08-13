package controller;

import data.BankCardDao;
import data.FavoriteDao;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Account;
import view.Palette;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class ApplicationController implements Initializable {

    @FXML
    private StackPane content;

    @FXML
    private VBox navBarContainer;

    public static Stack<Node> searchPageStack = new Stack<>();
    public static Stack<Node> homePageStack = new Stack<>();
    public static NavigationBarController navBarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader navBarLoader = new FXMLLoader(getClass().getResource("/view/NavigationBar.fxml"));
        try {
            navBarContainer.getChildren().add(navBarLoader.load());

            navBarController = navBarLoader.getController();
            navBarController.openHome(new ActionEvent());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appLoad(Stage primaryStage){
        try {
            Parent root;
            if (Account.getCurrentUser().getPassenger() != null) {
                root = FXMLLoader.load(ApplicationController.class.getResource("/view/Application.fxml"));
                root.getStylesheets().add(ApplicationController.class.getResource("/style/Application.css").toExternalForm());
            }
            else if (Account.getCurrentUser().getAirline() != null) {
                root = FXMLLoader.load(ApplicationController.class.getResource("/view/Dashboard_Airline.fxml"));
                root.getStylesheets().add(ApplicationController.class.getResource("/style/Dashboard_Airline.css").toExternalForm());
            }
            else {
                root = FXMLLoader.load(ApplicationController.class.getResource("/view/Application.fxml"));
                root.getStylesheets().add(ApplicationController.class.getResource("/style/Application.css").toExternalForm());
            }

            Scene scene = new Scene(root, 1366, 768);

            Palette.getDefaultPalette().usePalette(scene);

            CSSFX.start();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Flight Booking Application");
            primaryStage.show();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAllCollections() {
        searchPageStack.clear();
        homePageStack.clear();
        FavoriteDao.favoritesMap.clear();
        BankCardDao.deleteCardList();
    }
}
