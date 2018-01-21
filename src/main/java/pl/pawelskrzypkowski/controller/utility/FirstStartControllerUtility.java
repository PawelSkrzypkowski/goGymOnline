package pl.pawelskrzypkowski.controller.utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstStartControllerUtility {
    public static boolean checkNameCorrectness(String name) {
        Pattern pattern = Pattern.compile("[^A-Za-z0-9żźćńółęąśŻŹĆĄŚĘŁÓŃ -]");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find() == true)// jesli zostal odnaleziony znak spoza
            // zakresu a-z i A-Z -, czyli niepoprawny
            return false;
        return true;
    }

    public static boolean checkFloatCorrectness(String number) {
        Pattern pattern = Pattern.compile("[^0-9,.]");
        Matcher matcher = pattern.matcher(number);
        if (matcher.find() == true)// jesli zostal odnaleziony znak spoza
            // zakresu 0-9 , .
            return false;
        return true;
    }

    private static ObservableList<Integer> dayOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31);
    private static ObservableList<String> monthOptions = FXCollections.observableArrayList("Styczeń", "Luty", "Marzec", "Kwiecień",
            "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień");

    public static ObservableList<String> getMonthOptions() {
        return monthOptions;
    }

    public static ObservableList<Integer> getDayOptions() {
        return dayOptions;
    }

    /**
     * Metoda zmieniająca polską datę na datę w formacie Javowym
     * @param day
     * @param month
     * @param year
     * @return
     * @throws ParseException
     */
    public static Date changeToDateType(Integer day, String month, Integer year) throws ParseException {
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
}
