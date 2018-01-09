package model.diary;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * Klasa do obslugi wykonanych cwiczen
 * @author Paweł
 *
 */
@Entity
@Data
public class ExercisesDone implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	private Exercise exercise;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Set> sets;
	/**
	 * Konstruktor tworzacy pusty obiekt
	 */
	public ExercisesDone(){
		setSets(new LinkedList<Set>());
	}
	/**
	 * Konstruktor tworzacy obiekt z wybranego cwiczenia
	 * @param exercise
	 */
	public ExercisesDone(Exercise exercise){
		setSets(new LinkedList<Set>());
		setExercise(exercise);
	}
}
