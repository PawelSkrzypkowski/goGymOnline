package pl.pawelskrzypkowski.controller.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlansControllerUtility {
    public static boolean checkIntegerCorrectness(String number) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(number);
        if (matcher.find() == true || number.length() == 0)// jesli zostal
            // odnaleziony znak
            // spoza
            // zakresu 0-9
            return false;
        return true;
    }

    public static boolean checkStringCorrectness(String name) {
        Pattern pattern = Pattern.compile("[^A-Za-z0-9żźćńółęąśŻŹĆĄŚĘŁÓŃs\\s\\-,]");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {// jesli zostal
            // odnaleziony znak
            // spoza
            // zakresu a-z i A-Z -, czyli niepoprawny lub pusty
            System.out.println(name);
            return false;
        }
        return true;
    }
}
