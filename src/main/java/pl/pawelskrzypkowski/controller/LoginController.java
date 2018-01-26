package pl.pawelskrzypkowski.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.FirstStart;
import pl.pawelskrzypkowski.application.JPAHolder;
import pl.pawelskrzypkowski.application.LocaleHolder;
import pl.pawelskrzypkowski.application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.pawelskrzypkowski.model.user.GlobalUser;
import pl.pawelskrzypkowski.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    static final Logger LOG = LoggerFactory.getLogger(Main.class);
    static final ObservableList languageList = FXCollections.observableArrayList("Polski", "English");
    static final ObservableList skinList = FXCollections.observableArrayList("Flatter", "Aqua", "Aero");
    @FXML
    TextField login;
    @FXML
    PasswordField password;
    @FXML
    Button log, register;
    @FXML
    Label error;
    @FXML
    ComboBox<String> language, skin;

    public void tryLogIn() throws NoSuchAlgorithmException, IOException {
        LOG.trace("Logging in...");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(password.getText().getBytes(StandardCharsets.UTF_8));
        String encode = Base64.getEncoder().encodeToString(hash);
        EntityManager entityManager = JPAHolder.getEntityManager();
        TypedQuery<User> findUser = entityManager.createQuery("select u from User u where u.login=:login and u.password=:pass", User.class);
        findUser.setParameter("login", login.getText());
        findUser.setParameter("pass", encode);
        try {
            LOG.trace("User found");
            User user = findUser.getSingleResult();
            GlobalUser.loggedUserId = user.getId();
            VBox root = FXMLLoader.load(getClass().getResource("/StartPage.fxml"), LocaleHolder.getDefaultInstance());
            Scene scene = new Scene(root,600,400);
            scene.getStylesheets().add(getClass().getResource(GlobalUser.skin).toExternalForm());
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(scene);
            secondaryStage.setResizable(false);
            secondaryStage.setTitle("goGym");
            secondaryStage.getIcons().add(new Image((getClass().getResource("/images/icon.png").toExternalForm())));
            secondaryStage.show();
            Main.stage.close();
            LOG.trace("Logged in. User ID: " + GlobalUser.loggedUserId);
        } catch (NoResultException e) {
            LOG.warn("No user with this credentials");
            password.clear();
            error.setVisible(true);
        }
    }

    public void register() throws IOException {
        LOG.trace("Go to register page");
        new FirstStart().newUser();
        Main.stage.close();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        language.setItems(languageList);
        skin.setItems(skinList);
        if(LocaleHolder.getDefaultInstance().getLocale().equals(Locale.US))
            language.getSelectionModel().selectLast();
        else
            language.getSelectionModel().selectFirst();
        if(GlobalUser.skin.equals("/application.css"))
            skin.getSelectionModel().select(0);
        else if(GlobalUser.skin.equals("/application2.css"))
            skin.getSelectionModel().select(1);
        else if(GlobalUser.skin.equals("/application3.css"))
            skin.getSelectionModel().select(2);
        language.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.trace("Changing locale");
            if(newValue.equals(languageList.get(0))){
                LocaleHolder.changeDeafultInstance(Locale.getDefault());
            } else {
                LocaleHolder.changeDeafultInstance(Locale.US);
            }
            new Main().start(Main.stage);
            LOG.trace("Locale changed");
        });
        skin.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.trace("Changing skin");
            if(newValue.equals(skinList.get(0))){
                GlobalUser.skin = "/application.css";
                GlobalUser.color = "#2e3539";
                GlobalUser.fontColor = Color.WHITE;
            } else if(newValue.equals(skinList.get(1))){
                GlobalUser.skin = "/application2.css";
                GlobalUser.color = "#EDEDED";
                GlobalUser.fontColor = Color.BLACK;
            } else if(newValue.equals(skinList.get(2))){
                GlobalUser.skin = "/application3.css";
                GlobalUser.color = "#F0F0F0";
                GlobalUser.fontColor = Color.BLACK;
            }
            new Main().start(Main.stage);
            LOG.trace("Skin changed");
        });
        log.setOnMouseClicked(event->{
            try {
                tryLogIn();
            } catch(NoSuchAlgorithmException e){
                error.setText(LocaleHolder.readMessage("login.error.algorithm"));
                error.setVisible(true);
            } catch (IOException e){
                error.setText(LocaleHolder.readMessage("login.error.scene"));
                e.printStackTrace();
                error.setVisible(true);
            }
        });
        register.setOnMouseClicked(event ->{
            try {
                register();
            } catch(IOException e){
                error.setText(LocaleHolder.readMessage("login.error.window"));
                error.setVisible(true);
                LOG.error("Can't start register window");
            }
        });
    }
}
