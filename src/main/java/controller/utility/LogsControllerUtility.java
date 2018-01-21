package controller.utility;

import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsControllerUtility {
    public static Float[] textFieldArrayToFloatArray(TextField[] logTable) throws NumberFormatException {
        Float[] logInFloat = new Float[9];
        int i = 0;
        for (TextField field : logTable) {
            field.setText(field.getText().replace(',', '.'));
            if (field.getText().isEmpty())
                field.setText("0");
            logInFloat[i] = Float.parseFloat(field.getText());
            i++;
        }
        return logInFloat;
    }

    public static boolean checkFloatCorrectness(String number) {
        Pattern pattern = Pattern.compile("[^0-9,.]");
        Matcher matcher = pattern.matcher(number);
        if (matcher.find() == true)// jesli zostal odnaleziony znak spoza
            // zakresu 0-9 , .
            return false;
        return true;
    }
}
