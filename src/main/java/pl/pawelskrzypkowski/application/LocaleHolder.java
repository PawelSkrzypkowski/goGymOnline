package pl.pawelskrzypkowski.application;

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
}
