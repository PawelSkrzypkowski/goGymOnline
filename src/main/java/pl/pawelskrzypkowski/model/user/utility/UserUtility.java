package pl.pawelskrzypkowski.model.user.utility;

import pl.pawelskrzypkowski.application.JPAHolder;
import pl.pawelskrzypkowski.model.user.GlobalUser;
import pl.pawelskrzypkowski.model.user.User;

import javax.persistence.EntityManager;

public class UserUtility {
    /**
     * Metoda odczytujaca uzytkownika
     * @return
     */
    public static User readUser(){
        EntityManager entityManager = JPAHolder.getEntityManager();
        User user = entityManager.find(User.class, GlobalUser.loggedUserId);
        user.getLogs();
        entityManager.close();
        return user;
    }
}
