package pl.pawelskrzypkowski.controller;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.CreateExercises;
import pl.pawelskrzypkowski.application.JPAHolder;
import com.google.common.io.ByteStreams;
import pl.pawelskrzypkowski.application.Main;
import pl.pawelskrzypkowski.controller.utility.AlertUtility;
import pl.pawelskrzypkowski.controller.utility.FirstStartControllerUtility;
import pl.pawelskrzypkowski.controller.utility.LogsControllerUtility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.pawelskrzypkowski.model.user.Log;
import pl.pawelskrzypkowski.model.user.User;
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
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
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
		LOG.trace("Starting user create");
		Float[] logInFloat = null;
		boolean fail = false;
		TextField[] logTable = { setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh,
				setCalf };
		try{
			logInFloat = LogsControllerUtility.textFieldArrayToFloatArray(logTable);
			LOG.trace("Converting user first log to array");
		} catch (NumberFormatException e){// jesli zly format liczby
			fail = true;
			AlertUtility.noNumberValue();
			LOG.warn("No number log value");
		}
		if (login.getText().isEmpty() == true || password.getText().isEmpty() == true || setFirstName.getText().isEmpty() == true || setLastName.getText().isEmpty() == true
				|| birthDay.getValue() == null || birthMonth.getValue() == null || birthYear.getValue() == null) {
			AlertUtility.noRequiredData();
			fail = true;
			LOG.warn("Not good personal data");
		}
		if (login.getText().length() < 6 || password.getText().length() < 6) {
			AlertUtility.toShortCredentials();
			fail = true;
			password.clear();
			LOG.warn("Not good password and/or login");
		}
		EntityManager entityManager = JPAHolder.getEntityManager();
		TypedQuery<User> query = entityManager.createQuery("select u from User u where u.login=:login", User.class);
		query.setParameter("login", login.getText());
		try{
			query.getSingleResult();
			AlertUtility.loginExists();
			fail = true;
			password.clear();
			LOG.warn("Existing user name");
		} catch (NoResultException e){}//brak uzytkownika o podanym loginie

		if (!fail) {// jesli mozna zarejestrować
			LOG.trace("Starting user registration");
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
				LOG.trace("User registered");
			} catch (ParseException e) {
				AlertUtility.dateError();
				LOG.warn("Can't parse user birth date");
			} catch (NoSuchAlgorithmException e) {
				AlertUtility.hashAlgorithmError();
				LOG.error("Can't hash user pass");
			} catch (IOException e) {
				AlertUtility.basicAvatarSaveError();
				LOG.error("Can't save basic avatar");
			}
		}
	}

	/**
	 * Metoda inicjalizująca, tworząca widok okna oraz listenery
	 */
	public void initialize(URL url, ResourceBundle rb) {
		LOG.trace("Create registration window");
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
		LOG.trace("Registration window created");
	}
}
