package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import controller.utility.AlertUtility;
import controller.utility.LogsControllerUtility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.user.Log;
import model.user.User;
/**
 * Klasa - kontroler modułu aplikacji do dodawania pomiarów ciała
 * @author Paweł
 *
 */
public class LogsController implements Initializable {
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
		Float[] logInFloat = new Float[9];
		boolean fail = false;
		try {
			logInFloat = LogsControllerUtility.textFieldArrayToFloatArray(logTable);
		} catch (NumberFormatException e){// jesli zly format liczby
			fail = true;
			AlertUtility.noNumberValue();
			}
		if (fail == false) {// jesli mozna dodac log
			User user = User.readUser();
			Log log = new Log(logInFloat);
			user.addLog(log);
			user.saveUser();
			AlertUtility.logsAdded();
		}
	}
	/**
	 * Metoda inicjalizująca i tworząca widok do dodawania pomiarów
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
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
	}
}
