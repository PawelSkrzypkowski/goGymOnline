package pl.pawelskrzypkowski.application;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Paweł Skrzypkowski
 * Wojskowa Akademia Techniczna im. Jarosława Dąbrowskiego, Warszawa 20187.
 */
public class LocaleHolder {
    private static ResourceBundle defaultLocale;
    private LocaleHolder(){}

    public static ResourceBundle getDefaultInstance(){
        if(defaultLocale == null)
            defaultLocale = ResourceBundle.getBundle("messages");
        return defaultLocale;
    }

    public static ResourceBundle changeDeafultInstance(Locale locale){
        defaultLocale = ResourceBundle.getBundle("messages", locale);
        return getDefaultInstance();
    }
    public static String readMessage(String key){
        try {
            return new String(getDefaultInstance().getString(key).getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
