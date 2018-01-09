package controller;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.CreateExercises;
import application.JPAHolder;
import com.google.common.io.ByteStreams;
import controller.utility.AlertUtility;
import controller.utility.FirstStartControllerUtility;
import controller.utility.LogsControllerUtility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.user.Log;
import model.user.User;
import org.hibernate.engine.jdbc.BlobProxy;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Klasa - kontroler rejestracji nowego użytkownika
 * @author Paweł
 *
 */
public class FirstStartController implements Initializable {
	@FXML
	private VBox vb;
	@FXML
	private ComboBox<Integer> birthDay;
	@FXML
	private ComboBox<String> birthMonth;
	@FXML
	private ComboBox<Integer> birthYear;
	@FXML
	private TextField login;
	@FXML
	private PasswordField password;
	@FXML
	private TextField setFirstName;
	@FXML
	private TextField setLastName;
	@FXML
	private TextField setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh, setCalf;
	@FXML
	private Button createUser;

	/**
	 * Metoda przechwytujaca tworzenie nowego uzytkownika
	 * @param event
	 */
	public void handleCreateUser(ActionEvent event) {
		Float[] logInFloat = null;
		boolean fail = false;
		TextField[] logTable = { setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh,
				setCalf };
		try{
			logInFloat = LogsControllerUtility.textFieldArrayToFloatArray(logTable);
		} catch (NumberFormatException e){// jesli zly format liczby
			fail = true;
			AlertUtility.noNumberValue();
		}
		if (login.getText().isEmpty() == true || password.getText().isEmpty() == true || setFirstName.getText().isEmpty() == true || setLastName.getText().isEmpty() == true
				|| birthDay.getValue() == null || birthMonth.getValue() == null || birthYear.getValue() == null) {
			AlertUtility.noRequiredData();
			fail = true;
		}
		if (login.getText().length() < 6 || password.getText().length() < 6) {
			AlertUtility.toShortCredentials();
			fail = true;
			password.clear();
		}
		EntityManager entityManager = JPAHolder.getEntityManager();
		TypedQuery<User> query = entityManager.createQuery("select u from User u where u.login=:login", User.class);
		query.setParameter("login", login.getText());
		try{
			query.getSingleResult();
			AlertUtility.loginExists();
			fail = true;
			password.clear();
		} catch (NoResultException e){}//brak uzytkownika o podanym loginie

		if (fail == false) {// jesli mozna zarejestrować
			try {
				Date birthDate = FirstStartControllerUtility.changeToDateType(birthDay.getValue(), birthMonth.getValue(), birthYear.getValue());
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				byte[] hash = messageDigest.digest(password.getText().getBytes(StandardCharsets.UTF_8));
				String encode = Base64.getEncoder().encodeToString(hash);
				ClassLoader classloader = Thread.currentThread().getContextClassLoader();
				InputStream defaultImage = classloader.getResourceAsStream("images/avatar.png");
				byte[] data = ByteStreams.toByteArray(defaultImage);
				User user = User.builder().firstName(setFirstName.getText()).lastName(setLastName.getText()).
						birthDate(birthDate).login(login.getText()).password(encode).
						avatar(BlobProxy.generateProxy(data)).logs(new ArrayList<>()).build();
				Log log = new Log(logInFloat);
				user.addLog(log);
				user.saveUser();
				AlertUtility.registered();
				Stage stage = (Stage) vb.getScene().getWindow();// zamykanie
																// okna
																// rejestracji
				stage.close();
				CreateExercises start = new CreateExercises();
				start.start();
			} catch (ParseException e) {
				AlertUtility.dateError();
			} catch (NoSuchAlgorithmException e) {
				AlertUtility.hashAlgorithmError();
			} catch (IOException e) {
				AlertUtility.basicAvatarSaveError();
			}
		}
	}

	/**
	 * Metoda inicjalizująca, tworząca widok okna oraz listenery
	 */
	public void initialize(URL url, ResourceBundle rb) {
		birthDay.setItems(FirstStartControllerUtility.getDayOptions());
		birthMonth.setItems(FirstStartControllerUtility.getMonthOptions());
		for (int i = 1920; i <= Calendar.getInstance().get(Calendar.YEAR); i++)
			birthYear.getItems().add(i);
		login.textProperty().addListener((observable, oldValue, newValue) -> {
			if (FirstStartControllerUtility.checkNameCorrectness(login.getText()) == false)
				login.setText(oldValue);
		});
		setFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (FirstStartControllerUtility.checkNameCorrectness(setFirstName.getText()) == false)
				setFirstName.setText(oldValue);
		});
		setLastName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (FirstStartControllerUtility.checkNameCorrectness(setLastName.getText()) == false)
				setLastName.setText(oldValue);
		});
		TextField[] logTable = { setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh,
				setCalf };
		for (TextField field : logTable) {// sprawdzam dla kazdej pozycji z logs
											// czy znajduja sie prawidlowe znaki
			field.textProperty().addListener((observable, oldValue, newValue) -> {
				if (FirstStartControllerUtility.checkFloatCorrectness(field.getText()) == false)
					field.setText(oldValue);
			});
		}
		createUser.setOnAction(this::handleCreateUser);
	}
}
