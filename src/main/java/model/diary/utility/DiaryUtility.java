package model.diary.utility;

import application.JPAHolder;
import model.diary.Diary;
import model.diary.Exercise;
import model.diary.ExercisesDone;
import model.diary.Set;
import model.user.GlobalUser;
import model.user.User;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;

public class DiaryUtility {
    /**
     * Metoda pobierająca wykonane treningi
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static LinkedList<Diary> downloadDiaries(){
        EntityManager entityManager = JPAHolder.getEntityManager();
        User user = entityManager.find(User.class, GlobalUser.loggedUserId);
        LinkedList<Diary> list = new LinkedList<>(user.getDiaryList());
        entityManager.close();
        return list;
    }
    /**
     * Metoda obliczająca podniesiony ciezar w ciagu wybranego miesiaca
     * @param minusMonth
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Double getMonthlyRaisedWeight(int minusMonth){//dzisiejszy miesiac - minusMonth
        LinkedList<Diary> diary = getDiariesFromMonth(minusMonth);
        Double weight = 0.0;
        for(Diary d : diary){
            weight += d.showRaisedWeight();
        }
        return weight;
    }
    /**
     * Metoda pobierająca treningi w ciagu wybranego miesiaca
     * @param minusMonth
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static LinkedList<Diary> getDiariesFromMonth(int minusMonth){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        int yearNumber1, yearNumber2;
        if(cal1.get(Calendar.MONTH) - minusMonth < 0)
            yearNumber1 = cal1.get(Calendar.YEAR) - 1;
        else
            yearNumber1 = cal1.get(Calendar.YEAR);
        if(cal1.get(Calendar.MONTH) - minusMonth + 1 < 0)
            yearNumber2 = cal1.get(Calendar.YEAR) - 1;
        else
            yearNumber2 = cal1.get(Calendar.YEAR);
        int monthNumber1 = ((cal1.get(Calendar.MONTH) - minusMonth) % 12 + 12) % 12;
        int monthNumber2 = (cal1.get(Calendar.MONTH) - minusMonth + 1) % 12;
        cal1.set(yearNumber1, monthNumber1, 1, 0, 0, 0);
        cal2.set(yearNumber2, monthNumber2, 1, 0, 0, 0);
        cal1.set(Calendar.YEAR, yearNumber1);
        cal2.set(Calendar.YEAR, yearNumber2);
        LinkedList<Diary> diary = downloadDiaries();
        LinkedList<Diary> monthDiaries = new LinkedList<Diary>();
        for(Diary d : diary){
            if(d.getStartDate().before(cal2.getTime()) && d.getStartDate().after(cal1.getTime()))
                monthDiaries.add(d);
        }
        return monthDiaries;
    }
    /**
     * Metoda pobierajaca czas treningow w ciagu miesiaca
     * @param minusMonth
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Integer getMonthlyTrainingTime(int minusMonth){//dzisiejszy miesiac - minusMonth
        LinkedList<Diary> diaries = getDiariesFromMonth(minusMonth);
        Integer trainingTime = 0;
        for(Diary d : diaries){
            trainingTime += d.showTrainingTime();
        }
        return trainingTime;
    }
    /**
     * Metoda pobierajaca czas cwiczen w ciagu miesiaca
     * @param minusMonth
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Integer getMonthlyExercisingTime(int minusMonth){//dzisiejszy miesiac - minusMonth
        LinkedList<Diary> diaries = getDiariesFromMonth(minusMonth);
        Integer exercisingTime = 0;
        for(Diary d : diaries){
            exercisingTime += d.showTrainingTime() - d.getRestTime()/60;
        }
        return exercisingTime;
    }
    /**
     * Metoda obliczajaca ilość przerw w treningach w ciagu miesiaca
     * @param minusMonth
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Integer getMonthlyRestTime(int minusMonth){//dzisiejszy miesiac - minusMonth
        LinkedList<Diary> diaries = getDiariesFromMonth(minusMonth);
        Integer restTime = 0;
        for(Diary d : diaries){
            restTime += d.getRestTime();
        }
        return restTime;
    }
    /**
     * Metoda obliczajca ilsoc wykonanych cwiczen w ciagu miesiaca
     * @param minusMonth
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Integer getMonthlyExercisesDone(int minusMonth){//dzisiejszy miesiac - minusMonth
        LinkedList<Diary> diaries = getDiariesFromMonth(minusMonth);
        Integer exercisesDone = 0;
        for(Diary d : diaries){
            exercisesDone += d.getExercisesDone().size();
        }
        return exercisesDone;
    }
    /**
     * Metoda zwracajaca mapę Data - rekord dla wybranego cwiczenia
     * @param exercise
     * @return
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static TreeMap<Date, Double> getMapDateRecord(Exercise exercise){
        LinkedList<Diary> diaries = downloadDiaries();
        TreeMap<Date, Double> hm = new TreeMap<Date, Double>();
        for(Diary diary : diaries){
            Double max = 0.0;
            for(ExercisesDone ed : diary.getExercisesDone()){
                if(ed.getExercise().equals(exercise)){
                    for(Set set : ed.getSets()){
                        for(Double weight : set.getWeight()){
                            if(max < weight)
                                max = weight;
                        }
                    }
                }
            }
            if(!max.equals(0.0)){
                hm.put(diary.getStartDate(), max);
            }
        }
        return hm;
    }

    /**
     * Metoda odczytująca wybrany trening
     * @param fileName
     */
    public static Diary readDiary(String fileName){
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        EntityManager entityManager = JPAHolder.getEntityManager();
        User user = entityManager.find(User.class, GlobalUser.loggedUserId);
        Diary diary = null;
        for(Diary d : user.getDiaryList()) {
            if(sdf2.format(d.getStartDate()).equals(fileName)) {
                diary = d;
                break;
            }
        }
        entityManager.close();
        return diary;
    }
}
