package model.diary;

import application.JPAHolder;
import model.user.GlobalUser;
import model.user.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Klasa obsługująca wykonane treningi
 * @author Paweł
 *
 */
@Entity
public class Diary implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private Date startDate;

	@Column
	private Date finishDate;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<ExercisesDone> exercisesDone;

	@Column
	private Integer restTime;//seconds
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
	 * Metoda obliczajca czas treningu
	 * @return
	 */
	public Integer showTrainingTime() {
		double time = (finishDate.getTime() - startDate.getTime()) / 60000.0;
		return (int) time;
	}
	/**
	 * Metoda zwiekszjaca ilosc przerw
	 * @param rest
	 */
	public void increaseRestTime(int rest){
		setRestTime(getRestTime() + rest);
	}
	/**
	 * Konstruktor tworzący nowy Wykonany trening
	 */
	public Diary(){
		startDate = new Date();
		exercisesDone = new LinkedList<ExercisesDone>();
		restTime = 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exercisesDone == null) ? 0 : exercisesDone.hashCode());
		result = prime * result + ((finishDate == null) ? 0 : finishDate.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Diary other = (Diary) obj;
		if (exercisesDone == null) {
			if (other.exercisesDone != null)
				return false;
		} else if (!exercisesDone.equals(other.exercisesDone))
			return false;
		if (finishDate == null) {
			if (other.finishDate != null)
				return false;
		} else if (!finishDate.equals(other.finishDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	/**
	 * Metoda zwracajaca podniesiony ciezar
	 * @return podniesiony ciezar
	 */
	public double showRaisedWeight() {
		double weight = 0;
		for (int i = 0; i < exercisesDone.size(); i++)
			for (int j = 0; j < exercisesDone.get(i).getSets().size(); j++)
				for (int k = 0; k < exercisesDone.get(i).getSets().get(j).getWeight().size(); k++)
					weight += exercisesDone.get(i).getSets().get(j).getWeight().get(k);
		return weight;
	}
	/**
	 * Metoda zapisujaca dziennik
	 * @throws IOException
	 */
	public void saveDiary(){
		EntityManager entityManager = JPAHolder.getEntityManager();
		User user = entityManager.find(User.class, GlobalUser.loggedUserId);
		entityManager.getTransaction().begin();
		if(this.id == null) {
			user.getDiaryList().add(this);
			entityManager.merge(user);
		} else{
			entityManager.merge(this);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	/**
	 * Metoda odczytująca wybrany trening
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidClassException
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
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public List<ExercisesDone> getExercisesDone() {
		return exercisesDone;
	}

	public void setExercisesDone(List<ExercisesDone> exercisesDone) {
		this.exercisesDone = exercisesDone;
	}
	
	public Integer getRestTime() {
		return restTime;
	}

	public void setRestTime(Integer restTime) {
		this.restTime = restTime;
	}
	@Override
	public String toString() {
		return "Diary [startDate=" + startDate + ", finishDate=" + finishDate + ", exercisesDone=" + exercisesDone
				+ ", restTime=" + restTime + "]";
	}
	
}
