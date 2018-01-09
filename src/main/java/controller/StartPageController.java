package controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;


import application.Logs;
import controller.utility.AlertUtility;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import model.user.User;
import org.hibernate.engine.jdbc.BlobProxy;

/**
 * Klasa  - kontroler do obslugi okna Strony Głównej aplikacji
 * @author Paweł
 *
 */
public class StartPageController implements Initializable {
	@FXML
	private Button homeButton, plansButton, calculatorsButton, progressButton, logsButton;
	@FXML
	private ImageView refreshUserData, editAvatar, avatar;
	@FXML
	private Label userData;
	private VBox mainPage = new VBox();
	@FXML
	private HBox page;
	@FXML
	private VBox root;
	/**
	 * Metoda inicjalizujaca, ladujaca dane uzytkownika, strone glowna oraz dodajaca handlery
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadUserDetails();
		loadMain();
		plansButton.setOnAction((event) -> {
			PlansController plansController = new PlansController();
			plansController.createStage(mainPage);
		});
		homeButton.setOnAction((event) -> {
			page.getChildren().remove(page.getChildren().size() - 1);
			mainPage.getChildren().clear();
			loadMain();
		});
		progressButton.setOnAction((event) -> {
			TrainingProgressController trainingProgressController = new TrainingProgressController();
			trainingProgressController.createStage(mainPage);
		});
		logsButton.setOnAction((event) -> {
			Logs logs = new Logs();
			try {
				logs.createStage(mainPage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		calculatorsButton.setOnAction((event) -> {
			CalculatorsController cc = new CalculatorsController();
			cc.createStage(mainPage);
		});
		refreshUserData.setOnMouseClicked((event) -> {// moze by dodac
			// podswietlnie? :)
			loadUserDetails();
		});
		editAvatar.setOnMouseClicked((event) -> {
			FileChooser chooseFile = new FileChooser();
			chooseFile.setTitle("Wybierz plik");
			chooseFile.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
			Stage stage = (Stage) root.getScene().getWindow();
			File file = chooseFile.showOpenDialog(stage);
			if (file != null) {
				Image image = new Image(file.toURI().toString());
				if (image.getWidth() / image.getHeight() > avatar.getFitWidth() / avatar.getFitHeight()) {
					image = new Image(file.toURI().toString(), avatar.getFitWidth(), avatar.getFitHeight(), false,
							false);
				} else {
					image = new Image(file.toURI().toString(), avatar.getFitWidth(), avatar.getFitHeight(), true,
							false);
				}
				double margin = (avatar.getFitWidth() - image.getWidth()) / 2;
				avatar.setX(margin);
				BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
				try{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(bi, "png", baos);
					Blob blFile = BlobProxy.generateProxy(baos.toByteArray());
					User user = User.readUser();
					user.setAvatar(blFile);
					user.saveUser();
					loadAvatar();
				} catch(IOException e){
					AlertUtility.fileSavingError(e);
				}
			}
		});
	}
	/**
	 * Metoda ladujaca dane uzytkownika
	 * @throws InvalidClassException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void loadUserDetails(){
		User user = User.readUser();
		userData.setText("Witaj " + user.getFirstName() + " " + user.getLastName() + "!\n" + "Waga: "
				+ user.getLogs().get(user.getLogs().size() - 1).getWeight() + " kg\n" + "Wiek: " + user.calculateAge()
				+ " lat");
	}
	/**
	 * Metoda laduajca avatar
	 */
	public void loadAvatar(){
		User user = User.readUser();
		InputStream avatarStream = null;
		try {
			avatarStream = user.getAvatar().getBinaryStream();
		} catch (SQLException e) {
			avatarStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/avatar.png");
		}
		Image avatarImage = new Image(avatarStream);
		double margin = (avatar.getFitWidth() - avatarImage.getWidth()) / 2;
		avatar.setImage(avatarImage);
		avatar.setX(margin);
	}
	/**
	 * Metoda ladujaca strone glowna
	 */
	public void loadMain() {
		ImageView home = new ImageView("/images/home.png");
		mainPage.getChildren().add(home);
		mainPage.setPadding(new Insets(10));
		mainPage.setPrefWidth(500);
		mainPage.setStyle("-fx-background-color:  #2e3539");
		mainPage.setSpacing(0);
		ScrollPane sp = new ScrollPane(mainPage);
		sp.setFitToHeight(true);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setFitToWidth(true);
		sp.setStyle("-fx-background-color:  #2e3539");
		page.getChildren().add(sp);
		Text text = new Text("Witaj w aplikacji goGym. Aplikacja umożliwia układanie własnych treningów oraz przeprowadzanie ich w czasie rzeczywistym. Wspieranie się goGym w codziennych treningach gwarantuje wykonanie zaplanowanej ilości ćwiczeń, serii oraz przerw.\n"
				+ "Ponadto aplikacja umożliwia obliczanie podstawowych wskaźników ciała człowieka oraz śledzenie postępów w perspektywie ćwiczeń, miesięcy oraz własnego ciała.\n\n Zapraszamy do ćwiczenia i życzymy wielu sukcesów!");
		text.setWrappingWidth(450);
		text.setFill(Color.WHITE);
		mainPage.getChildren().add(text);
		loadAvatar();
	}
}
