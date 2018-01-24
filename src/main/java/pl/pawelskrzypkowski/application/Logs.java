package pl.pawelskrzypkowski.application;

import java.io.IOException;


import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Klasa do obsługi pliku FXML używanego w Pomiarach ciała
 * @author Paweł
 *
 */
public class Logs {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	public void createStage(VBox mainPage) throws IOException {
		LOG.trace("Opening logs window");
		GridPane root =(GridPane) FXMLLoader.load(getClass().getResource("/LogFxml.fxml"), LocaleHolder.getDefaultInstance());
		mainPage.getChildren().clear();
		mainPage.setSpacing(0);
		ImageView logs = new ImageView(LocaleHolder.readMessage("headers.logs"));
		mainPage.getChildren().add(logs);
		mainPage.getChildren().add(root);
		LOG.trace("Logs window opened");
	}

}
