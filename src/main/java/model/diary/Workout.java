package model.diary;

import model.diary.Exercise;
import model.user.GlobalUser;
import model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
/**
 * Klasa do tworzenia treningów
 * @author Paweł
 *
 */
@Entity
public class Workout implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String workoutName;
	@OneToMany
	private List<Exercise> exercises;
	@ElementCollection
	@OrderColumn
	private List<Integer> setsNumber;
	@ElementCollection
	@OrderColumn
	private List<Integer> rest;
	@Column
	@Type(type = "text")
	private String workoutDescription;
	@Column
	private String workoutType;
	@Column
	private String difficultyLevel;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((difficultyLevel == null) ? 0 : difficultyLevel.hashCode());
		result = prime * result + ((exercises == null) ? 0 : exercises.hashCode());
		result = prime * result + ((rest == null) ? 0 : rest.hashCode());
		result = prime * result + ((setsNumber == null) ? 0 : setsNumber.hashCode());
		result = prime * result + ((workoutDescription == null) ? 0 : workoutDescription.hashCode());
		result = prime * result + ((workoutName == null) ? 0 : workoutName.hashCode());
		result = prime * result + ((workoutType == null) ? 0 : workoutType.hashCode());
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
		Workout other = (Workout) obj;
		if (difficultyLevel == null) {
			if (other.difficultyLevel != null)
				return false;
		} else if (!difficultyLevel.equals(other.difficultyLevel))
			return false;
		if (exercises == null) {
			if (other.exercises != null)
				return false;
		} else if (!exercises.equals(other.exercises))
			return false;
		if (rest == null) {
			if (other.rest != null)
				return false;
		} else if (!rest.equals(other.rest))
			return false;
		if (setsNumber == null) {
			if (other.setsNumber != null)
				return false;
		} else if (!setsNumber.equals(other.setsNumber))
			return false;
		if (workoutDescription == null) {
			if (other.workoutDescription != null)
				return false;
		} else if (!workoutDescription.equals(other.workoutDescription))
			return false;
		if (workoutName == null) {
			if (other.workoutName != null)
				return false;
		} else if (!workoutName.equals(other.workoutName))
			return false;
		if (workoutType == null) {
			if (other.workoutType != null)
				return false;
		} else if (!workoutType.equals(other.workoutType))
			return false;
		return true;
	}
	/**
	 * Metoda zapisujaca trening
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveWorkout(){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		User user = entityManager.find(User.class, GlobalUser.loggedUserId);
		entityManager.getTransaction().begin();
		user.getWorkouts().add(this);
		entityManager.merge(user);
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	/**
	 * Metoda odczytująca trening
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidClassException
	 */
	public static Workout readWorkout(String fileName){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		User user = entityManager.find(User.class, GlobalUser.loggedUserId);
		Workout workout = null;
		for(Workout work : user.getWorkouts()) {
			if(work.getWorkoutName().equals(fileName)) {
				workout = work;
				break;
			}
		}
		entityManager.close();
		entityManagerFactory.close();
		return workout;
	}
	/**
	 * Metoda zmieniająca elementy opisowe treningu
	 * @param name
	 * @param description
	 * @param type
	 * @param level
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void changeWorkoutProperties(String name, String description, String type, String level){
		if(!getWorkoutDescription().equals(description))
			setWorkoutDescription(description);
		if(!getWorkoutType().equals(type))
			setWorkoutType(type);
		if(!getDifficultyLevel().equals(level))
			setDifficultyLevel(level);
		if(!getWorkoutName().equals(name)){
			deleteWorkout();
			setWorkoutName(name);
		}
		saveWorkout();
	}
	/**
	 * Metoda sprawdzajaca czy istnieje juz trening i czy nie zostanie nadpisany
	 * @param name
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public boolean checkIfWorkoutExist(String name) throws ClassNotFoundException, IOException{//sprawdza czy po zmianie nazwy nie zostanie nadpisany inny plan
		if(name.equals(getWorkoutName()))//jesli nazwa nie zostala zmieniona
			return false;
		if(Workout.readWorkout(name) == null)
			return false;
		return true;
	}
	/**
	 * Metoda usuwająca trening
	 * @throws IOException
	 */
	public void deleteWorkout(){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.remove(this);
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	/**
	 * Metoda zmieniajaca wybrane cwiczenie
	 * @param index
	 * @param exercise
	 * @param setsNumber
	 * @param rest
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void editExercise(int index, Exercise exercise, int setsNumber, int rest){
		getExercises().set(index, exercise);
		getSetsNumber().set(index, setsNumber);
		getRest().set(index, rest);
		saveWorkout();
	}
	/**
	 * Metoda zamieniajaca cwiczenie miejscami z cwiczeniem wyzej
	 * @param index
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void moveUpExercise(int index){
		Exercise tempExercise = getExercises().get(index);
		Integer tempSetsNumber = getSetsNumber().get(index);
		Integer tempRest = getRest().get(index);
		editExercise(index, getExercises().get(index - 1), getSetsNumber().get(index - 1), getRest().get(index - 1));
		editExercise(index - 1, tempExercise, tempSetsNumber, tempRest);
	}
	/**
	 * Metoda zamieniajaca cwiczenie miejscami z cwiczeniem nizej
	 * @param index
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void moveDownExercise(int index){
		Exercise tempExercise = getExercises().get(index);
		Integer tempSetsNumber = getSetsNumber().get(index);
		Integer tempRest = getRest().get(index);
		editExercise(index, getExercises().get(index + 1), getSetsNumber().get(index + 1), getRest().get(index + 1));
		editExercise(index + 1, tempExercise, tempSetsNumber, tempRest);
	}
	/**
	 * Konstruktor tworzacy pusty trening
	 */
	public Workout() {
		setExercises(new LinkedList<Exercise>());
		setSetsNumber(new LinkedList<Integer>());
		setRest(new LinkedList<Integer>());
	}
	/**
	 * Konstruktor tworzacy trening z elementami opisowymi
	 * @param name
	 * @param workoutDescription
	 * @param workoutType
	 * @param difficultyLevel
	 */
	public Workout(String name, String workoutDescription, String workoutType, String difficultyLevel) {
		setExercises(new LinkedList<Exercise>());
		setSetsNumber(new LinkedList<Integer>());
		setRest(new LinkedList<Integer>());
		this.setWorkoutName(name);
		this.setWorkoutDescription(workoutDescription);
		this.setWorkoutType(workoutType);
		this.setDifficultyLevel(difficultyLevel);
	}
	/**
	 * Metoda dodajaca cwiczenie na koncu
	 * @param exercise
	 * @param setsNumber
	 * @param rest
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void addItemAtTheEnd(Exercise exercise, Integer setsNumber, Integer rest) {
		this.exercises.add(exercise);
		this.setsNumber.add(setsNumber);
		this.rest.add(rest);
		saveWorkout();
	}
	/**
	 * Metoda dodajaca cwiczenie na poczatku
	 * @param exercise
	 * @param setsNumber
	 * @param rest
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void addItemAtTheBeginning(Exercise exercise, Integer setsNumber, Integer rest) {
		this.exercises.add(0, exercise);
		this.setsNumber.add(0, setsNumber);
		this.rest.add(0, rest);
		saveWorkout();
	}
	/**
	 * Metoda dodajaca cwiczenie po wybranym cwiczeniu
	 * @param exercise
	 * @param setsNumber
	 * @param rest
	 * @param index
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void addItemAfter(Exercise exercise, Integer setsNumber, Integer rest, int index) {
		this.exercises.add(index, exercise);
		this.setsNumber.add(index, setsNumber);
		this.rest.add(index, rest);
		saveWorkout();
	}
	/**
	 * Metoda usuwajaca wybrane cwiczenie
	 * @param index
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteItem(int index) {
		this.exercises.remove(index);
		this.setsNumber.remove(index);
		this.rest.remove(index);
		saveWorkout();
	}

	public String getWorkoutName() {
		return workoutName;
	}

	public void setWorkoutName(String workoutName) {
		this.workoutName = workoutName;
	}

	public List<Exercise> getExercises() {
		return exercises;
	}

	public void setExercises(List<Exercise> exercises) {
		this.exercises = exercises;
	}

	public List<Integer> getSetsNumber() {
		return setsNumber;
	}

	public void setSetsNumber(List<Integer> setsNumber) {
		this.setsNumber = setsNumber;
	}

	public List<Integer> getRest() {
		return rest;
	}

	public void setRest(List<Integer> rest) {
		this.rest = rest;
	}

	public String getWorkoutDescription() {
		return workoutDescription;
	}

	public void setWorkoutDescription(String workoutDescription) {
		this.workoutDescription = workoutDescription;
	}

	public String getWorkoutType() {
		return workoutType;
	}

	public void setWorkoutType(String workoutType) {
		this.workoutType = workoutType;
	}

	public String getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(String difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	@Override
	public String toString() {
		return "Workout [workoutName=" + workoutName + ", exercises=" + exercises + ", setsNumber=" + setsNumber
				+ ", rest=" + rest + ", workoutDescription=" + workoutDescription + ", workoutType=" + workoutType
				+ ", difficultyLevel=" + difficultyLevel + "]";
	}


}
