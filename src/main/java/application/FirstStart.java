package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * Klasa tworząca scene do pierwszego włączenia aplikacji
 * @author Paweł
 *
 */
public class FirstStart {
	Stage secondaryStage = new Stage();
	/**
	 * Metoda tworząca scene
	 * @throws IOException
	 */
	public void newUser() throws IOException{
			VBox root = FXMLLoader.load(getClass().getResource("/FirstStart.fxml"));
			Scene scene = new Scene(root,400,760);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			secondaryStage.setScene(scene);
			secondaryStage.setResizable(false);
			secondaryStage.setTitle("goGym - Rejestracja");
			secondaryStage.getIcons().add(new Image((getClass().getResource("/images/icon.png").toExternalForm())));
			secondaryStage.show();
	}
	public void closeWindow(){
		secondaryStage.close();
	}
}
