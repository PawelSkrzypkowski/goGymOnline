package pl.pawelskrzypkowski.model.diary;

import pl.pawelskrzypkowski.application.JPAHolder;
import lombok.Data;
import pl.pawelskrzypkowski.model.user.GlobalUser;
import pl.pawelskrzypkowski.model.user.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
/**
 * Klasa obsługująca wykonane treningi
 * @author Paweł
 *
 */
@Entity
@Data
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
}
