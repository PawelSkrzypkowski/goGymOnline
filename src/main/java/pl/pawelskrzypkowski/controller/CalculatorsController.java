package pl.pawelskrzypkowski.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.LocaleHolder;
import pl.pawelskrzypkowski.application.Main;
import pl.pawelskrzypkowski.controller.utility.AlertUtility;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pl.pawelskrzypkowski.model.user.User;
import pl.pawelskrzypkowski.model.user.utility.LogUtility;
import pl.pawelskrzypkowski.model.user.utility.UserUtility;

/**
 * Klasa obsługująca sekcję kalkulatorów aplikacji
 * @author Paweł
 *
 */
public class CalculatorsController {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	/**
	 * Metoda do obsługi obliczania BMI
	 * @param mainPage
	 * @throws InvalidClassException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void showBMI(VBox mainPage)
			throws InvalidClassException, FileNotFoundException, ClassNotFoundException, IOException {
		LOG.trace("Loading BMI window");
		Label weight = new Label(LocaleHolder.readMessage("calculators.bMI.weightLabel")), height = new Label(LocaleHolder.readMessage("calculators.bMI.heightLabel"));
		Label title = new Label(LocaleHolder.readMessage("calculators.bMI.bMILabel"));
		title.setFont(new Font(15));
		Label des[] = new Label[8];
		Label score = new Label();
		score.setVisible(false);
		score.setFont(new Font(15));
		des[0] = new Label(LocaleHolder.readMessage("calculators.description.0"));
		des[1] = new Label(LocaleHolder.readMessage("calculators.description.1"));
		des[2] = new Label(LocaleHolder.readMessage("calculators.description.2"));
		des[3] = new Label(LocaleHolder.readMessage("calculators.description.3"));
		des[4] = new Label(LocaleHolder.readMessage("calculators.description.4"));
		des[5] = new Label(LocaleHolder.readMessage("calculators.description.5"));
		des[6] = new Label(LocaleHolder.readMessage("calculators.description.6"));
		des[7] = new Label(LocaleHolder.readMessage("calculators.description.7"));
		TextField setWeight = new TextField(), setHeight = new TextField();
		Button calculate = new Button(LocaleHolder.readMessage("calculators.button.calculate"));
		User user = UserUtility.readUser();
		setWeight.setPromptText("kg");
		setWeight.setText(new Float(user.getLogs().get(user.getLogs().size() - 1).getWeight()).toString());
		setWeight.setMaxWidth(100);
		setHeight.setPromptText("cm");
		setHeight.setMaxWidth(100);
		mainPage.getChildren().clear();
		mainPage.getChildren().add(title);
		mainPage.getChildren().addAll(des);
		mainPage.getChildren().addAll(weight, setWeight, height, setHeight, calculate, score);
		calculate.setOnAction((event) -> {
			setWeight.setText(setWeight.getText().replace(',', '.'));
			setHeight.setText(setHeight.getText().replace(',', '.'));
			try {
				Double bmi = LogUtility.calculateBMI(Float.parseFloat(setWeight.getText()),
						Integer.parseInt(setHeight.getText()));
				score.setText(LocaleHolder.readMessage("calculators.bMI.score") + bmi.toString().substring(0, 4));
				score.setVisible(true);
				LOG.trace("BMI calculated");
			} catch (NumberFormatException e) {
				AlertUtility.noNumberValue();
				LOG.warn("User write no number value. Can't calculate BMI");
			}
		});
		LOG.trace("BMI window loaded");
	}
	/**
	 * Metoda do obsługi obliczania BMR
	 * @param mainPage
	 * @throws InvalidClassException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void showBMR(VBox mainPage) throws InvalidClassException, FileNotFoundException, ClassNotFoundException, IOException{
		LOG.trace("Loading BMR window");
		ToggleGroup sex = new ToggleGroup();
		RadioButton female = new RadioButton("Kobieta");
		RadioButton male = new RadioButton("Mężczyzna");
		female.setToggleGroup(sex);
		male.setToggleGroup(sex);
		female.setSelected(true);
		Label weight = new Label(LocaleHolder.readMessage("calculators.bMI.weightLabel")), height = new Label(LocaleHolder.readMessage("calculators.bMI.heightLabel")), age = new Label(LocaleHolder.readMessage("calculators.bMR.ageLabel"));
		Label title = new Label(LocaleHolder.readMessage("calculators.bMR.bMRLabel"));
		title.setFont(new Font(15));
		Label score = new Label();
		score.setVisible(false);
		score.setFont(new Font(15));
		Text descr = new Text(LocaleHolder.readMessage("calculators.bMR.description"));
		descr.setWrappingWidth(450);
		descr.setFill(Color.WHITE);
		TextField setWeight = new TextField(), setHeight = new TextField(), setAge = new TextField();
		Button calculate = new Button(LocaleHolder.readMessage("calculators.button.calculate"));
		User user = UserUtility.readUser();
		setWeight.setPromptText("kg");
		setWeight.setText(new Float(user.getLogs().get(user.getLogs().size() - 1).getWeight()).toString());
		setWeight.setMaxWidth(100);
		setHeight.setPromptText("cm");
		setHeight.setMaxWidth(100);
		setAge.setPromptText(LocaleHolder.readMessage("calculators.bMR.age"));
		setAge.setText(new Integer(user.calculateAge()).toString());
		mainPage.getChildren().clear();
		mainPage.getChildren().addAll(title, descr, female, male, weight, setWeight, height, setHeight, age, setAge,
				calculate, score);
		calculate.setOnAction((event) -> {
			boolean isFemale;
			if (female.isSelected())
				isFemale = true;
			else
				isFemale = false;
			setWeight.setText(setWeight.getText().replace(',', '.'));
			setHeight.setText(setHeight.getText().replace(',', '.'));
			setAge.setText(setAge.getText().replace(',', '.'));
			try {
				Integer bmr = LogUtility.calculateBMR(isFemale, Float.parseFloat(setWeight.getText()),
						Integer.parseInt(setHeight.getText()), Integer.parseInt(setAge.getText()));
				score.setText(LocaleHolder.readMessage("calculators.bMI.score") + bmr.toString());
				score.setVisible(true);
				LOG.trace("BMR calculated");
			} catch (NumberFormatException e) {
				AlertUtility.noNumberValue();
				LOG.warn("User write no number value. Can't calculate BMR");
			}
		});
		LOG.trace("BMR window loaded");
	}
	/**
	 * Metoda do obsługi obliczania perfekcyjnej wagi
	 * @param mainPage
	 */
	public void showPerfectWeight(VBox mainPage){
		LOG.trace("Loading Perfect Weight window");
		ToggleGroup sex = new ToggleGroup();
		RadioButton female = new RadioButton(LocaleHolder.readMessage("calculators.pW.woman"));
		RadioButton male = new RadioButton(LocaleHolder.readMessage("calculators.pW.man"));
		female.setToggleGroup(sex);
		male.setToggleGroup(sex);
		female.setSelected(true);
		Label height = new Label(LocaleHolder.readMessage("calculators.bMI.heightLabel"));
		Label title = new Label(LocaleHolder.readMessage("calculators.pW.pWLabel"));
		title.setFont(new Font(15));
		Label score = new Label();
		score.setVisible(false);
		score.setFont(new Font(15));
		Text descr = new Text(LocaleHolder.readMessage("calculators.pW.description"));
		descr.setWrappingWidth(450);
		descr.setFill(Color.WHITE);
		TextField setHeight = new TextField();
		Button calculate = new Button(LocaleHolder.readMessage("calculators.button.calculate"));
		setHeight.setPromptText("cm");
		setHeight.setMaxWidth(100);
		mainPage.getChildren().clear();
		mainPage.getChildren().addAll(title, descr, female, male, height, setHeight, calculate, score);
		calculate.setOnAction((event) -> {
			boolean isFemale;
			if (female.isSelected())
				isFemale = true;
			else
				isFemale = false;
			setHeight.setText(setHeight.getText().replace(',', '.'));
			try {
				Double pw = LogUtility.calculateBroc(isFemale, Integer.parseInt(setHeight.getText()));
				score.setText(LocaleHolder.readMessage("calculators.bMI.score") + pw.toString().substring(0, 4));
				score.setVisible(true);
				LOG.trace("Perfect weight calculated");
			} catch (NumberFormatException e) {
				AlertUtility.noNumberValue();
				LOG.warn("User write no number value. Can't calculate Perfect Weight");
			}
		});
		LOG.trace("Perfect weight window loaded");
	}
	/**
	 * Metoda do obsługi obliczania ilości tkanki tłuszczowej
	 * @param mainPage
	 * @throws InvalidClassException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void showFat(VBox mainPage) throws InvalidClassException, FileNotFoundException, ClassNotFoundException, IOException{
		LOG.trace("Loading fat calculator window");
		ToggleGroup sex = new ToggleGroup();
		RadioButton female = new RadioButton(LocaleHolder.readMessage("calculators.pW.woman"));
		RadioButton male = new RadioButton(LocaleHolder.readMessage("calculators.pW.man"));
		female.setToggleGroup(sex);
		male.setToggleGroup(sex);
		female.setSelected(true);
		Label weight = new Label(LocaleHolder.readMessage("calculators.bMI.weightLabel")), waist = new Label(LocaleHolder.readMessage("calculators.fat.waist"));
		Label title = new Label(LocaleHolder.readMessage("calculators.fat.fatLabel"));
		title.setFont(new Font(15));
		Label score = new Label();
		score.setVisible(false);
		score.setFont(new Font(15));
		Text descr = new Text(LocaleHolder.readMessage("calculators.fat.description"));
		descr.setWrappingWidth(450);
		descr.setFill(Color.WHITE);
		TextField setWeight = new TextField(), setWaist = new TextField();
		Button calculate = new Button(LocaleHolder.readMessage("calculators.button.calculate"));
		User user = UserUtility.readUser();
		setWeight.setPromptText("kg");
		setWeight.setMaxWidth(100);
		setWeight.setText(new Float(user.getLogs().get(user.getLogs().size() - 1).getWeight()).toString());
		setWaist.setPromptText("cm");
		setWaist.setMaxWidth(100);
		setWaist.setText(new Float(user.getLogs().get(user.getLogs().size() - 1).getWaist()).toString());
		mainPage.getChildren().clear();
		mainPage.getChildren().addAll(title, descr, female, male, weight, setWeight, waist, setWaist, calculate, score);
		calculate.setOnAction((event) -> {
			boolean isFemale;
			if (female.isSelected())
				isFemale = true;
			else
				isFemale = false;
			setWeight.setText(setWeight.getText().replace(',', '.'));
			setWaist.setText(setWaist.getText().replace(',', '.'));
			try {
				Double pw = LogUtility.calculateFat(isFemale, Float.parseFloat(setWeight.getText()), Float.parseFloat(setWaist.getText()));
				score.setText(LocaleHolder.readMessage("calculators.bMI.score") + pw.toString().substring(0, 4));
				score.setVisible(true);
				LOG.trace("Fat calculated");
			} catch (NumberFormatException e) {
				AlertUtility.noNumberValue();
				LOG.warn("User write no number value. Can't calculate fat");
			}
		});
		LOG.trace("Fat calculator window loaded");
	}
	/**
	 * Metoda do obsługi obliczania WHR
	 * @param mainPage
	 * @throws InvalidClassException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void showWHR(VBox mainPage) throws InvalidClassException, FileNotFoundException, ClassNotFoundException, IOException{
		LOG.trace("WHR window loading");
		Label hips = new Label(LocaleHolder.readMessage("calculators.wHR.hips")), waist = new Label(LocaleHolder.readMessage("calculators.fat.waist"));
		Label title = new Label(LocaleHolder.readMessage("calculators.wHR.wHRLabel"));
		title.setFont(new Font(15));
		Label score = new Label();
		score.setVisible(false);
		score.setFont(new Font(15));
		Text descr = new Text(LocaleHolder.readMessage("calculators.wHR.description"));
		descr.setWrappingWidth(450);
		descr.setFill(Color.WHITE);
		TextField setHips = new TextField(), setWaist = new TextField();
		Button calculate = new Button(LocaleHolder.readMessage("calculators.button.calculate"));
		User user = UserUtility.readUser();
		setHips.setPromptText("kg");
		setHips.setMaxWidth(100);
		setHips.setText(new Float(user.getLogs().get(user.getLogs().size() - 1).getHips()).toString());
		setWaist.setPromptText("cm");
		setWaist.setMaxWidth(100);
		setWaist.setText(new Float(user.getLogs().get(user.getLogs().size() - 1).getWaist()).toString());
		mainPage.getChildren().clear();
		mainPage.getChildren().addAll(title, descr, hips, setHips, waist, setWaist, calculate, score);
		calculate.setOnAction((event) -> {
			setHips.setText(setHips.getText().replace(',', '.'));
			setWaist.setText(setWaist.getText().replace(',', '.'));
			try {
				Double whr = LogUtility.calculateWHR(Float.parseFloat(setHips.getText()), Float.parseFloat(setWaist.getText()));
				score.setText(String.format(LocaleHolder.readMessage("calculators.bMI.score") + "%.2f", whr));
				score.setVisible(true);
				LOG.trace("WHR calculated");
			} catch (NumberFormatException e) {
				AlertUtility.noNumberValue();
				LOG.warn("User write no number value. Can't calculate WHR");
			}
		});
		LOG.trace("WHR widnow loaded");
	}
	/**
	 * Metoda do obsługi kalkulatorów
	 * @param mainPage
	 */
	public void createStage(VBox mainPage) {
		LOG.trace("Loading calculators window");
		mainPage.getChildren().clear();
		ImageView calc = new ImageView(LocaleHolder.readMessage("headers.calculators"));
		mainPage.getChildren().add(calc);
		mainPage.setSpacing(10);
		Button BMI = new Button(LocaleHolder.readMessage("calculators.bMI")), BMR = new Button(LocaleHolder.readMessage("calculators.bMR")),
				perfectWeight = new Button(LocaleHolder.readMessage("calculators.pW")),
				fat = new Button(LocaleHolder.readMessage("calculators.fat")), WHR = new Button(LocaleHolder.readMessage("calculators.wHR"));
		mainPage.getChildren().addAll(BMI, BMR, perfectWeight, fat, WHR);
		BMI.setOnAction((event) -> {
			try {
				showBMI(mainPage);
			} catch (ClassNotFoundException | IOException e) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(LocaleHolder.readMessage("alert.information"));
				alert.setHeaderText("");
				alert.setContentText(LocaleHolder.readMessage("alert.errorReadingFile") + e.getMessage());
				alert.showAndWait();
			}
		});
		BMR.setOnAction((event) -> {
			try {
				showBMR(mainPage);
			} catch (ClassNotFoundException | IOException e) {
				AlertUtility.errorFileReading(e);
			}
		});
		perfectWeight.setOnAction((event) -> {
			showPerfectWeight(mainPage);
		});
		fat.setOnAction((event) -> {
			try {
				showFat(mainPage);
			} catch (ClassNotFoundException | IOException e) {
				AlertUtility.errorFileReading(e);
			}
		});
		WHR.setOnAction((event) -> {
			try {
				showWHR(mainPage);
			} catch (ClassNotFoundException | IOException e) {
				AlertUtility.errorFileReading(e);
			}
		});
		LOG.trace("Calculators window loaded");
	}
}
