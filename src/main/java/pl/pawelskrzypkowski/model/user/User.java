package pl.pawelskrzypkowski.model.user;

import pl.pawelskrzypkowski.application.JPAHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pawelskrzypkowski.model.diary.Diary;
import pl.pawelskrzypkowski.model.diary.Workout;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
/**
 * Klasa do obslugi uzytkownika i jego pomiarow
 * @author Paweł
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String login;
	@Column
	private String password;
	@Column
	private Date startDate = new Date();
	@Column
	private String firstName;
	@Column
	private String lastName;
	@Column
	private Date birthDate;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Log> logs;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Workout> workouts;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<Diary> diaryList;
	@Lob
	private Blob avatar;

	/**
	 * Metoda zapisujaca uzytkownika
	 * @throws IOException
	 */
	public void saveUser(){
		EntityManager entityManager = JPAHolder.getEntityManager();
		this.id=GlobalUser.loggedUserId;
		entityManager.getTransaction().begin();
		User user = entityManager.merge(this);
		GlobalUser.loggedUserId = user.getId();
		entityManager.getTransaction().commit();
		entityManager.close();
	}
	/**
	 * Metoda obliczajca wiek uzytkownika
	 * @return
	 */
	public int calculateAge() {
		Calendar today = Calendar.getInstance();
		Calendar birthDate = Calendar.getInstance();

		int age = 0;

		birthDate.setTime(this.getBirthDate());
		if (birthDate.after(today)) {
			throw new IllegalArgumentException("Can't be born in the future");
		}

		age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
		if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3)
				|| (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
			age--;
		} else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
				&& (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
			age--;
		}

		return age;
	}
	/**
	 * Metoda zwracjaca mape Data - Pomiar
	 * @param logName
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public TreeMap<Date, Float> getDateLogMap(String logName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		TreeMap<Date, Float> map = new TreeMap<Date, Float>();
		for(Log l : logs){
			Method method = l.getClass().getMethod("get" + logName);
			map.put(l.getMensurationDate(), (Float)method.invoke(l));
		}
		return map;
	}

	public void addLog(Log log) {
		logs.add(log);
	}

	public void removeLog(int index) {
		logs.remove(index);
	}
}
