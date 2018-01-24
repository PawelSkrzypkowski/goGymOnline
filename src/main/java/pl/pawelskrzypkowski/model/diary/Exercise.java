package pl.pawelskrzypkowski.model.diary;


import pl.pawelskrzypkowski.application.JPAHolder;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
/**
 * Klasa do obslugi cwiczen
 * @author Paweł
 *
 */
@Entity
@Data
public class Exercise implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name = "id_exercise")
	private Long id;

	@Column
	private String name;

	// private Obrazek obrazek;

	@Column
	@Type(type = "text")
	private String description;

	@ElementCollection
	@OrderColumn
	@LazyCollection(LazyCollectionOption.FALSE)
	private String[] workingMuscles;

	@Column
	private int record;

	@Column
	private int recordReps;

	@ManyToMany(mappedBy = "exercises")
	private List<Workout> workouts;
	/**
	 * Konstruktor tworzacy cwiczenie
	 * @param name
	 * @param description
	 * @param workingMuscles
	 */
	public Exercise(String name, String description, String[] workingMuscles) {
		this.setName(name);
		this.setDescription(description);
		this.setWorkingMuscles(workingMuscles);
		this.setRecord(0);
		this.setRecordReps(0);
	}
	/**
	 * Konstruktor tworzacy puste cwiczenie
	 */
	public Exercise() {
	}
	/**
	 * Metoda zapisujaca cwiczenie
	 * @throws IOException
	 */
	public void saveExercise(){
		EntityManager entityManager = JPAHolder.getEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(this);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public String toString() {
		return "Exercise{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", workingMuscles=" + Arrays.toString(workingMuscles) +
				", record=" + record +
				", recordReps=" + recordReps +
				'}';
	}
}
