package pl.pawelskrzypkowski.controller.utility;

import javafx.scene.control.Alert;

public class AlertUtility {
    public static void noNumberValue(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Wprowadzona wartość nie jest liczbą");
        alert.showAndWait();
    }
    public static void errorFileReading(Exception e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Błąd odczytu pliku. Błąd: " + e.getMessage());
        alert.showAndWait();
    }
    public static void noRequiredData(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Nie podałeś wymaganych danych");
        alert.showAndWait();
    }
    public static void toShortCredentials(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Login i hasło muszą mieć przynajmniej 6 znaków");
        alert.showAndWait();
    }
    public static void loginExists(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Użytkownik o podanym loginie już istnieje!");
        alert.showAndWait();
    }
    public static void registered(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Zostaleś zarejestrowany!");
        alert.showAndWait();
    }
    public static void dateError(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Błąd: niepoprawna data");
        alert.showAndWait();
    }
    public static void hashAlgorithmError(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Błąd algorytmu hashującego");
        alert.showAndWait();
    }
    public static void basicAvatarSaveError(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Nie udany zapis podstawowego avatara");
        alert.showAndWait();
    }
    public static void logsAdded(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Dodałeś pomiary!");
        alert.showAndWait();
    }
    public static void error(Exception e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Błąd: " + e.getMessage());
        alert.showAndWait();
    }
    public static void badData(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Podales niepoprawne dane!");
        alert.showAndWait();
    }
    public static void onlyNumbersAndLetters(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Możesz korzystać tylko z liter, cyfr i znaku -");
        alert.showAndWait();
    }
    public static void nameBusy(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Podana nazwa juz jest zajęta");
        alert.showAndWait();
    }
    public static void brokenFile(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Uszkodzony lub brak pliku treningowego!");
        alert.showAndWait();
    }
    public static void emptyWorkout(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Wybrany trening jest pusty.");
        alert.showAndWait();
    }
    public static void fileSavingError(Exception e){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText("");
        alert.setContentText("Błąd: " + e.getMessage()
                + ". Zapis pliku nie powiódł się.");
        alert.showAndWait();
    }
}
