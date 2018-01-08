package application;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
/**
 * Klasa do obsługi uruchomienia aplikacji
 * @author Paweł
 *
 */
public class Main extends Application {
	public static Stage stage;
	/**
	 * Metoda decydująca czy utworzyć scenę do normalnego użytkowania czy stworzyć obiekt FirstStart
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = FXMLLoader.load(getClass().getResource("/LoginPage.fxml"));
			Scene scene = new Scene(root, 400, 340);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("goGym - Logowanie");

			primaryStage.getIcons().add(new Image((getClass().getResource("/images/icon.png").toExternalForm())));
			primaryStage.show();
			stage = primaryStage;
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Informacja");
			alert.setHeaderText("");
			alert.setContentText("Błąd: " + e.getMessage());
			alert.showAndWait();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
