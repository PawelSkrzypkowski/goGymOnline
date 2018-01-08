package controller;

import application.FirstStart;
import application.JPAHolder;
import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.user.GlobalUser;
import model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    TextField login;
    @FXML
    PasswordField password;
    @FXML
    Button log, register;
    @FXML
    Label error;

    public void tryLogIn() throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(password.getText().getBytes(StandardCharsets.UTF_8));
        String encode = Base64.getEncoder().encodeToString(hash);
        EntityManager entityManager = JPAHolder.getEntityManager();
        TypedQuery<User> findUser = entityManager.createQuery("select u from User u where u.login=:login and u.password=:pass", User.class);
        findUser.setParameter("login", login.getText());
        findUser.setParameter("pass", encode);
        try {
            User user = findUser.getSingleResult();
            GlobalUser.loggedUserId = user.getId();
            VBox root = FXMLLoader.load(getClass().getResource("/StartPage.fxml"));
            Scene scene = new Scene(root,400,600);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(scene);
            secondaryStage.setResizable(false);
            secondaryStage.setTitle("goGym");
            secondaryStage.getIcons().add(new Image((getClass().getResource("/images/icon.png").toExternalForm())));
            secondaryStage.show();
            Main.stage.close();
        } catch (NoResultException e) {
            password.clear();
            error.setVisible(true);
        }
    }

    public void register() throws IOException {
        new FirstStart().newUser();
        Main.stage.close();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        log.setOnMouseClicked(event->{
            try {
                tryLogIn();
            } catch(NoSuchAlgorithmException e){
                error.setText("Błąd algorytmu. Zgłoś to administratorowi.");
                error.setVisible(true);
            } catch (IOException e){
                error.setText("Utworzenie sceny nie powiodło się.");
                error.setVisible(true);
            }
        });
        register.setOnMouseClicked(event ->{
            try {
                register();
            } catch(IOException e){
                error.setText("Brak możliwości otworzenia okna rejestracji.");
                error.setVisible(true);
            }
        });
    }
}
