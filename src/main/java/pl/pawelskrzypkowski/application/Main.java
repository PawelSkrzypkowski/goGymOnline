package pl.pawelskrzypkowski.application;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Klasa do obsługi uruchomienia aplikacji
 * @author Paweł
 *
 */
public class Main extends Application {
	public static Stage stage;
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	/**
	 * Metoda tworząca scene logowania
	 */
	@Override
	public void start(Stage primaryStage) {
		LOG.trace("Application start");
		try {
			VBox root = FXMLLoader.load(getClass().getResource("/LoginPage.fxml"), LocaleHolder.getDefaultInstance());
			Scene scene = new Scene(root, 400, 340);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle(LocaleHolder.getDefaultInstance().getString("loginPage.title"));

			primaryStage.getIcons().add(new Image((getClass().getResource("/images/icon.png").toExternalForm())));
			primaryStage.show();
			stage = primaryStage;
			LOG.trace("Login page loaded");
			JPAHolder.getEntityManager();
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(LocaleHolder.getDefaultInstance().getString("alert.information"));
			alert.setHeaderText("");
			alert.setContentText(LocaleHolder.getDefaultInstance().getString("alert.error") + e.getMessage());
			alert.showAndWait();
			LOG.error("Can't start app. Error: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
