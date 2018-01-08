package controller;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.CreateExercises;
import application.JPAHolder;
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
	ObservableList<Integer> dayOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31);
	private static ObservableList<String> monthOptions = FXCollections.observableArrayList("Styczeń", "Luty", "Marzec", "Kwiecień",
			"Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień");

	public static ObservableList<String> getMonthOptions() {
		return monthOptions;
	}

	public boolean checkNameCorrectness(String name) {
		Pattern pattern = Pattern.compile("[^A-Za-z0-9żźćńółęąśŻŹĆĄŚĘŁÓŃ -]");
		Matcher matcher = pattern.matcher(name);
		if (matcher.find() == true)// jesli zostal odnaleziony znak spoza
									// zakresu a-z i A-Z -, czyli niepoprawny
			return false;
		return true;
	}

	public boolean checkFloatCorrectness(String number) {
		Pattern pattern = Pattern.compile("[^0-9,.]");
		Matcher matcher = pattern.matcher(number);
		if (matcher.find() == true)// jesli zostal odnaleziony znak spoza
									// zakresu 0-9 , .
			return false;
		return true;
	}
	/**
	 * Metoda przechwytujaca tworzenie nowego uzytkownika
	 * @param event
	 */
	public void handleCreateUser(ActionEvent event) {
		Float[] logInFloat = new Float[9];
		int i = 0;
		boolean fail = false;
		TextField[] logTable = { setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh,
				setCalf };
		for (TextField field : logTable) {
			field.setText(field.getText().replace(',', '.'));
			if (field.getText().isEmpty() == true)
				field.setText("0");
			try {
				logInFloat[i] = Float.parseFloat(field.getText());
			} catch (NumberFormatException e){// jesli zly format liczby
				fail = true;
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Informacja");
				alert.setHeaderText("");
				alert.setContentText("Wprowadzona wartość nie jest liczbą");
				alert.showAndWait();
				break;
			}
			i++;
		}
		if (login.getText().isEmpty() == true || password.getText().isEmpty() == true || setFirstName.getText().isEmpty() == true || setLastName.getText().isEmpty() == true
				|| birthDay.getValue() == null || birthMonth.getValue() == null || birthYear.getValue() == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Informacja");
			alert.setHeaderText("");
			alert.setContentText("Nie podałeś wymaganych danych");
			alert.showAndWait();
			fail = true;
		}
		if (login.getText().length() < 6 || password.getText().length() < 6) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Informacja");
			alert.setHeaderText("");
			alert.setContentText("Login i hasło muszą mieć przynajmniej 6 znaków");
			alert.showAndWait();
			fail = true;
			password.clear();
		}
		EntityManager entityManager = JPAHolder.getEntityManager();
		TypedQuery<User> query = entityManager.createQuery("select u from User u where u.login=:login", User.class);
		query.setParameter("login", login.getText());
		try{
			query.getSingleResult();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Informacja");
			alert.setHeaderText("");
			alert.setContentText("Użytkownik o podanym loginie już istnieje!");
			alert.showAndWait();
			fail = true;
			password.clear();
		} catch (NoResultException e){}//brak uzytkownika o podanym loginie

		if (fail == false) {// jesli mozna zarejestrować
			try {
				Date birthDate = changeToDateType(birthDay.getValue(), birthMonth.getValue(), birthYear.getValue());
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				byte[] hash = messageDigest.digest(password.getText().getBytes(StandardCharsets.UTF_8));
				String encode = Base64.getEncoder().encodeToString(hash);
				User user = new User(setFirstName.getText(), setLastName.getText(), birthDate, login.getText(), encode);
				Log log = new Log(logInFloat);
				user.addLog(log);
				user.saveUser();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Informacja");
				alert.setHeaderText("");
				alert.setContentText("Zostaleś zarejestrowany!");
				alert.showAndWait();
				Stage stage = (Stage) vb.getScene().getWindow();// zamykanie
																// okna
																// rejestracji
				stage.close();
				CreateExercises start = new CreateExercises();
				start.start();
			} catch (ParseException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Informacja");
				alert.setHeaderText("");
				alert.setContentText("Błąd: niepoprawna data");
				alert.showAndWait();
			} catch (NoSuchAlgorithmException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Informacja");
				alert.setHeaderText("");
				alert.setContentText("Błąd algorytmu hashującego");
				alert.showAndWait();
			}
		}
	}
	/**
	 * Metoda zmieniająca polską datę na datę w formacie Javowym
	 * @param day
	 * @param month
	 * @param year
	 * @return
	 * @throws ParseException
	 */
	public Date changeToDateType(Integer day, String month, Integer year) throws ParseException {
		switch (month) {
		case "Styczeń":
			month = "01";
			break;
		case "Luty":
			month = "02";
			break;
		case "Marzec":
			month = "03";
			break;
		case "Kwiecień":
			month = "04";
			break;
		case "Maj":
			month = "05";
			break;
		case "Czerwiec":
			month = "06";
			break;
		case "Lipiec":
			month = "07";
			break;
		case "Sierpień":
			month = "08";
			break;
		case "Wrzesień":
			month = "09";
			break;
		case "Październik":
			month = "10";
			break;
		case "Listopad":
			month = "11";
			break;
		case "Grudzień":
			month = "12";
			break;
		default:
			month = "1";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		format.setLenient(false);
		return format.parse(day.toString() + "-" + month + "-" + year.toString());
	}
	/**
	 * Metoda inicjalizująca, tworząca widok okna oraz listenery
	 */
	public void initialize(URL url, ResourceBundle rb) {
		birthDay.setItems(dayOptions);
		birthMonth.setItems(monthOptions);
		for (int i = 1920; i <= Calendar.getInstance().get(Calendar.YEAR); i++)
			birthYear.getItems().add(i);
		login.textProperty().addListener((observable, oldValue, newValue) -> {
			if (checkNameCorrectness(login.getText()) == false)
				login.setText(oldValue);
		});
		setFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (checkNameCorrectness(setFirstName.getText()) == false)
				setFirstName.setText(oldValue);
		});
		setLastName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (checkNameCorrectness(setLastName.getText()) == false)
				setLastName.setText(oldValue);
		});
		TextField[] logTable = { setWeight, setNeck, setChest, setBiceps, setWaist, setStomach, setHips, setThigh,
				setCalf };
		for (TextField field : logTable) {// sprawdzam dla kazdej pozycji z logs
											// czy znajduja sie prawidlowe znaki
			field.textProperty().addListener((observable, oldValue, newValue) -> {
				if (checkFloatCorrectness(field.getText()) == false)
					field.setText(oldValue);
			});
		}
		createUser.setOnAction(this::handleCreateUser);
	}
}
