package application;

import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.Palette;

public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
          	Parent root = FXMLLoader.load(getClass().getResource("/view/Signin.fxml"));

			Scene scene = new Scene(root);

			Palette.setDefaultPalette(Palette.LightPalette);
			Palette.getDefaultPalette().usePalette(scene);

			CSSFX.start();
			primaryStage.initStyle(StageStyle.UNIFIED);
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Flight Booking Application");
			primaryStage.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
