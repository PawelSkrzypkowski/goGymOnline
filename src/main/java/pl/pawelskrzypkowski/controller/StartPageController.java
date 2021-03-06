package pl.pawelskrzypkowski.controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.LocaleHolder;
import pl.pawelskrzypkowski.application.Logs;
import pl.pawelskrzypkowski.application.Main;
import pl.pawelskrzypkowski.controller.utility.AlertUtility;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import pl.pawelskrzypkowski.model.user.GlobalUser;
import pl.pawelskrzypkowski.model.user.User;
import pl.pawelskrzypkowski.model.user.utility.UserUtility;
import org.hibernate.engine.jdbc.BlobProxy;

/**
 * Klasa  - kontroler do obslugi okna Strony Głównej aplikacji
 * @author Paweł
 *
 */
public class StartPageController implements Initializable {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	@FXML
	private Button homeButton, plansButton, calculatorsButton, progressButton, logsButton;
	@FXML
	private ImageView refreshUserData, editAvatar, avatar;
	@FXML
	private Label userData, weather;
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
		LOG.trace("Loading main app screen");
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
				e.getMessage();
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
			LOG.trace("Editing avatar");
			FileChooser chooseFile = new FileChooser();
			chooseFile.setTitle(LocaleHolder.readMessage("startPage.avatarChoose"));
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
					User user = UserUtility.readUser();
					user.setAvatar(blFile);
					user.saveUser();
					loadAvatar();
					LOG.trace("Avatar changed");
				} catch(IOException e){
					AlertUtility.fileSavingError(e);
					LOG.error("Can't save avatar. Error: " + e.getMessage());
				}
			}
		});
		LOG.trace("Main app window loaded");
	}
	/**
	 * Metoda ladujaca dane uzytkownika
	 * @throws InvalidClassException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void loadUserDetails(){
		User user = UserUtility.readUser();
		userData.setText(LocaleHolder.readMessage("startPage.userDetails.1") + user.getFirstName() + " " + user.getLastName() + "!\n" + LocaleHolder.readMessage("startPage.userDetails.2")
				+ user.getLogs().get(user.getLogs().size() - 1).getWeight() + " kg\n" + LocaleHolder.readMessage("startPage.userDetails.3") + user.calculateAge()
				+ LocaleHolder.readMessage("startPage.userDetails.4"));
		LOG.trace("User details loaded");
	}
	/**
	 * Metoda laduajca avatar
	 */
	public void loadAvatar(){
		User user = UserUtility.readUser();
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
		LOG.trace("Avatar loaded");
	}
	public void loadWeather() throws IOException {
		String requestURL = "http://api.wunderground.com/api/7533263ef31f677a/conditions/q/poland/Warsaw.json";
		URL wikiRequest = new URL(requestURL);
		JSONTokener tokener = new JSONTokener(wikiRequest.openStream());
		JSONObject root = new JSONObject(tokener);
		JSONObject observation = root.getJSONObject("current_observation");
		weather.setText(LocaleHolder.readMessage("startPage.weather") + observation.get("temp_c") + "\u00b0" + "C");
	}
	/**
	 * Metoda ladujaca strone glowna
	 */
	public void loadMain() {
		ImageView home = new ImageView( );
		mainPage.getChildren().add(home);
		mainPage.setPadding(new Insets(10));
		mainPage.setPrefWidth(500);
		mainPage.setStyle("-fx-background-color: " + GlobalUser.color);
		mainPage.setSpacing(0);
		ScrollPane sp = new ScrollPane(mainPage);
		sp.setFitToHeight(true);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setFitToWidth(true);
		sp.setStyle("-fx-background-color: " +  GlobalUser.color);
		page.getChildren().add(sp);
		Text text = new Text(LocaleHolder.readMessage("startPage.hello.1") + "\n"
				+ LocaleHolder.readMessage("startPage.hello.2") + "\n\n" + LocaleHolder.readMessage("startPage.hello.3"));
		text.setWrappingWidth(450);
		text.setFill(GlobalUser.fontColor);
		mainPage.getChildren().add(text);
		loadAvatar();
		try {
			loadWeather();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
