package model.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
/**
 * Klasa do obsługi pomiarów
 * @author Paweł
 *
 */
@Entity
@Data
public class Log implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private Date mensurationDate = new Date();
	@Column
	private float weight;
	@Column
	private float neck;
	@Column
	private float chest;
	@Column
	private float biceps;
	@Column
	private float waist;
	@Column
	private float stomach;
	@Column
	private float hips;
	@Column
	private float thigh;
	@Column
	private float calf;
	@Column
	private static final long serialVersionUID = 1L;
	/**
	 * Pusty konstruktor
	 */
	public Log(){}
	/**
	 * Konstruktor tworzacy pomiar podajac kazdy element osobno
	 * @param weight
	 * @param neck
	 * @param chest
	 * @param biceps
	 * @param waist
	 * @param stomach
	 * @param hips
	 * @param thigh
	 * @param calf
	 */
	public Log(float weight, float neck, float chest, float biceps, float waist, float stomach, float hips, float thigh, float calf){
		this.setWeight(weight);
		this.setNeck(neck);
		this.setChest(chest);
		this.setBiceps(biceps);
		this.setWaist(waist);
		this.setStomach(stomach);
		this.setHips(hips);
		this.setThigh(thigh);
		this.setCalf(calf);
	}
	/**
	 * Konstruktor tworzacy pomiar podajac dane w tablicy
	 * @param log
	 */
	public Log(Float[] log){
		this.setWeight(log[0]);
		this.setNeck(log[1]);
		this.setChest(log[2]);
		this.setBiceps(log[3]);
		this.setWaist(log[4]);
		this.setStomach(log[5]);
		this.setHips(log[6]);
		this.setThigh(log[7]);
		this.setCalf(log[8]);
	}
}
