package model.diary.utility;

import application.JPAHolder;
import model.diary.Exercise;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExerciseUtility {
    /**
     * Metoda odczytująca cwiczenie
     * @param fileName
     */
    public static Exercise readExercise(String fileName){
        EntityManager entityManager = JPAHolder.getEntityManager();
        TypedQuery<Exercise> query = entityManager.createQuery("SELECT e FROM Exercise e WHERE e.name=:name", Exercise.class);
        query.setParameter("name", fileName);
        Exercise exercise = query.getSingleResult();
        entityManager.close();
        return exercise;
    }

    /**
     * Metoda pobierajaca liste wszystkich cwiczen
     * @return
     */
    public static List<Exercise> downloadExercises(){
        EntityManager entityManager = JPAHolder.getEntityManager();
        Query query = entityManager.createQuery("SELECT e from Exercise e");
        List<Exercise> list = query.getResultList();
        entityManager.close();
        return list;
    }
}