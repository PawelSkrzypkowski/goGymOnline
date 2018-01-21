package pl.pawelskrzypkowski.application;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Klasa tworząca scene do pierwszego włączenia aplikacji
 * @author Paweł
 *
 */
public class FirstStart {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	Stage secondaryStage = new Stage();
	/**
	 * Metoda tworząca scene
	 * @throws IOException
	 */
	public void newUser() throws IOException{
			LOG.trace("Opening register window");
			ResourceBundle resourceBundle = LocaleHolder.getDefaultInstance();
			VBox root = FXMLLoader.load(getClass().getResource("/FirstStart.fxml"), resourceBundle);
			Scene scene = new Scene(root,400,760);
			scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
			secondaryStage.setScene(scene);
			secondaryStage.setResizable(false);
			secondaryStage.setTitle(LocaleHolder.getDefaultInstance().getString("firstStart.title"));
			secondaryStage.getIcons().add(new Image((getClass().getResource("/images/icon.png").toExternalForm())));
			secondaryStage.show();
			LOG.trace("Register window opened");
	}
	public void closeWindow(){
		secondaryStage.close();
		LOG.trace("Register window closed");
	}
}
