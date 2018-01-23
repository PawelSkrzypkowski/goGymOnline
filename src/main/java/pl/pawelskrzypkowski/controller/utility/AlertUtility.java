package pl.pawelskrzypkowski.controller.utility;

import javafx.scene.control.Alert;
import pl.pawelskrzypkowski.application.LocaleHolder;

public class AlertUtility {
    public static void noNumberValue(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.0"));
        alert.showAndWait();
    }
    public static void errorFileReading(Exception e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.1") + e.getMessage());
        alert.showAndWait();
    }
    public static void noRequiredData(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.2"));
        alert.showAndWait();
    }
    public static void toShortCredentials(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.3"));
        alert.showAndWait();
    }
    public static void loginExists(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.4"));
        alert.showAndWait();
    }
    public static void registered(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.5"));
        alert.showAndWait();
    }
    public static void dateError(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.6"));
        alert.showAndWait();
    }
    public static void hashAlgorithmError(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.7"));
        alert.showAndWait();
    }
    public static void basicAvatarSaveError(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.8"));
        alert.showAndWait();
    }
    public static void logsAdded(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.9"));
        alert.showAndWait();
    }
    public static void error(Exception e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.10") + e.getMessage());
        alert.showAndWait();
    }
    public static void badData(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.11"));
        alert.showAndWait();
    }
    public static void onlyNumbersAndLetters(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.12"));
        alert.showAndWait();
    }
    public static void nameBusy(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.13"));
        alert.showAndWait();
    }
    public static void brokenFile(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.14"));
        alert.showAndWait();
    }
    public static void emptyWorkout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.15"));
        alert.showAndWait();
    }
    public static void fileSavingError(Exception e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LocaleHolder.readMessage("alert.information"));
        alert.setHeaderText("");
        alert.setContentText(LocaleHolder.readMessage("alert.10") + e.getMessage()
                + LocaleHolder.readMessage("alert.16"));
        alert.showAndWait();
    }
}
