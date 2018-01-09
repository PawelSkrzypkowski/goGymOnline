package model.diary;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * Klasa do tworzenia wykonanych serii
 * @author Paweł
 *
 */
@Entity(name = "set_table")
@Data
public class Set implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private Integer reps;

	@Column
	private Boolean dropSet;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ElementCollection
	private List<Double> weight;

	/**
	 * Konstruktor tworzacy obiekt z dropSetem
	 * @param reps
	 * @param dropSet
	 * @param weight
	 */
	public Set(Integer reps, Boolean dropSet, List<Double> weight){
		setReps(reps);
		setDropSet(dropSet);
		setWeight(weight);
	}
	/**
	 * Konstruktor tworzacy obiekt bez dropsetu
	 * @param reps
	 * @param weight
	 */
	public Set(Integer reps, Double weight){
		setReps(reps);
		setDropSet(false);
		List<Double> list = new LinkedList<Double>();
		for(int i=0;i<reps;i++)
			list.add(weight);
		setWeight(list);
	}
	public Set(){}
}
