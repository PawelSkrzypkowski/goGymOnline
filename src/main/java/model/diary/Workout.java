package model.diary;

import application.JPAHolder;
import lombok.Data;
import model.diary.utility.WorkoutUtility;
import model.user.GlobalUser;
import model.user.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * Klasa do tworzenia treningów
 * @author Paweł
 *
 */
@Entity
@Data
public class Workout implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name = "id_workout")
	private Long id;

	@Column
	private String workoutName;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ManyToMany
	@JoinTable(name = "workout_exercise",
			joinColumns = {@JoinColumn(name="workout_id", referencedColumnName="id_workout")},
			inverseJoinColumns = {@JoinColumn(name="exercise_id", referencedColumnName="id_exercise")}
	)
	private List<Exercise> exercises;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	private List<Integer> setsNumber;

	@ElementCollection
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Integer> rest;

	@Column
	@Type(type = "text")
	private String workoutDescription;

	@Column
	private String workoutType;

	@Column
	private String difficultyLevel;

	/**
	 * Metoda zapisujaca trening
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveWorkout(){
		EntityManager entityManager = JPAHolder.getEntityManager();
		entityManager.getTransaction().begin();
		if(this.id == null) {
			User user = entityManager.find(User.class, GlobalUser.loggedUserId);
			user.getWorkouts().add(this);
			entityManager.merge(user);
		} else{
			List<Integer> rest = this.rest;
			List<Integer> sets = this.setsNumber;
			this.rest = new ArrayList<>();
			this.setsNumber = new ArrayList<>();
			entityManager.merge(this);
			this.rest = rest;
			this.setsNumber = sets;
			System.out.println("Rest - tabela: " + rest);
			System.out.println(sets);
			entityManager.merge(this);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
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
	 */
	public boolean checkIfWorkoutExist(String name){//sprawdza czy po zmianie nazwy nie zostanie nadpisany inny plan
		if(name.equals(getWorkoutName()))//jesli nazwa nie zostala zmieniona
			return false;
		if(WorkoutUtility.readWorkout(name) == null)
			return false;
		return true;
	}
	/**
	 * Metoda usuwająca trening
	 * @throws IOException
	 */
	public void deleteWorkout(){
		EntityManager entityManager = JPAHolder.getEntityManager();
		entityManager.getTransaction().begin();
		entityManager.remove(this);
		entityManager.getTransaction().commit();
		entityManager.close();
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
}
