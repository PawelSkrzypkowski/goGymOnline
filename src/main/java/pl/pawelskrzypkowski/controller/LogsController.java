package pl.pawelskrzypkowski.controller;

import java.net.URL;
import java.util.ResourceBundle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.Main;
import pl.pawelskrzypkowski.controller.utility.AlertUtility;
import pl.pawelskrzypkowski.controller.utility.LogsControllerUtility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pl.pawelskrzypkowski.model.user.Log;
import pl.pawelskrzypkowski.model.user.User;
import pl.pawelskrzypkowski.model.user.utility.UserUtility;

/**
 * Klasa - kontroler modułu aplikacji do dodawania pomiarów ciała
 * @author Paweł
 *
 */
public class LogsController implements Initializable {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	@FXML
	private TextField setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh, setCalf;
	@FXML
	private Button addLog;
	private TextField[] logTable=new TextField[9];

	/**
	 * Metoda przechwyująca dodawania pomiaru
	 * @param event
	 */
	public void addLog(ActionEvent event){
		LOG.trace("Adding log");
		Float[] logInFloat = new Float[9];
		boolean fail = false;
		try {
			logInFloat = LogsControllerUtility.textFieldArrayToFloatArray(logTable);
		} catch (NumberFormatException e){// jesli zly format liczby
			fail = true;
			AlertUtility.noNumberValue();
			LOG.warn("No number value of user input. Can't add log");
		}
		if (fail == false) {// jesli mozna dodac log
			User user = UserUtility.readUser();
			Log log = new Log(logInFloat);
			user.addLog(log);
			user.saveUser();
			AlertUtility.logsAdded();
			LOG.trace("Log added");
		}
	}
	/**
	 * Metoda inicjalizująca i tworząca widok do dodawania pomiarów
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		LOG.trace("Log window loading");
		TextField[] logTable = { setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh,
				setCalf };
		this.logTable = logTable;
		for (TextField field : logTable) {// dodaje listenery
			field.textProperty().addListener((observable, oldValue, newValue) -> {
				if (!LogsControllerUtility.checkFloatCorrectness(field.getText()))
					field.setText(oldValue);
			});
		}
		addLog.setOnAction(this::addLog);
		LOG.trace("Log window loaded");
	}
}
