package model.diary.utility;

import application.JPAHolder;
import model.diary.Workout;
import model.user.GlobalUser;
import model.user.User;

import javax.persistence.EntityManager;

public class WorkoutUtility {
    /**
     * Metoda odczytująca trening
     * @param fileName
     * @return
     */
    public static Workout readWorkout(String fileName){
        EntityManager entityManager = JPAHolder.getEntityManager();
        User user = entityManager.find(User.class, GlobalUser.loggedUserId);
        Workout workout = null;
        for(Workout work : user.getWorkouts()) {
            if(work.getWorkoutName().equals(fileName)) {
                workout = work;
                break;
            }
        }
        entityManager.close();
        return workout;
    }
}
