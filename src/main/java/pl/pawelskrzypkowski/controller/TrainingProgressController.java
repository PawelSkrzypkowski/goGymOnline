package pl.pawelskrzypkowski.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.Main;
import pl.pawelskrzypkowski.controller.utility.AlertUtility;
import pl.pawelskrzypkowski.controller.utility.FirstStartControllerUtility;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pl.pawelskrzypkowski.model.diary.Diary;
import pl.pawelskrzypkowski.model.diary.Exercise;
import pl.pawelskrzypkowski.model.diary.utility.DiaryUtility;
import pl.pawelskrzypkowski.model.diary.utility.ExerciseUtility;
import pl.pawelskrzypkowski.model.user.utility.UserUtility;

/**
 * Klasa - kontroler obsługujący sekcję do przeglądania postępów treningowych
 * @author Paweł
 *
 */
public class TrainingProgressController {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private String[] logNames = new String[]{"Weight", "Neck", "Chest", "Biceps", "Waist", "Stomach", "Hips", "Thigh", "Calf"};
	private String[] logNamesPl = new String[]{"Waga", "Szyja", "Klatka piersiowa", "Biceps", "Talia", "Brzuch", "Biodra", "Udo", "Łydka"};
	/**
	 * Metoda pokazująca wykres wybranego ćwiczenia
	 * @param exercise
	 * @param mainPage
	 */
	public void showExerciseChart(Exercise exercise, VBox mainPage) {
		LOG.trace("Loading exercise chart. Exercise name: " + exercise.getName());
		mainPage.getChildren().clear();
		TreeMap<Date, Double> map = DiaryUtility.getMapDateRecord(exercise);
		Double max = 0.0;
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<String, Number> chart = new LineChart<String, Number>(xAxis, yAxis);
		chart.setTitle("Postępy w ćwiczeniu " + exercise.getName());
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		Set<Entry<Date, Double>> entrySet = map.entrySet();
		for (Entry<Date, Double> entry : entrySet) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			series.getData().add(new XYChart.Data<String, Number>(sdf.format(entry.getKey()), entry.getValue()));
			if (max < entry.getValue())
				max = entry.getValue();
		}
		series.setName("Maksymalne wyniki na poszczególnych treningach");
		chart.getData().add(series);
		Label record = new Label("Aktualny rekord: " + max.toString());
		mainPage.getChildren().addAll(chart, record);
		LOG.trace("Exercise chart loaded");
	}
	/**
	 * Metoda pokazująca wykres wybranego elementu pomiarów ciała
	 * @param i
	 * @param mainPage
	 */
	public void showLogChart(int i, VBox mainPage){
		LOG.trace("Loading log chart");
		mainPage.getChildren().clear();
		try {
			TreeMap<Date, Float> tm = UserUtility.readUser().getDateLogMap(logNames[i]);
			CategoryAxis xAxis = new CategoryAxis();
			NumberAxis yAxis = new NumberAxis();
			LineChart<String, Number> chart = new LineChart<String, Number>(xAxis, yAxis);
			XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
			Float max = (float) 0;
			Float min = (float) 999;
			for(Entry<Date, Float> entry : tm.entrySet()){
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				series.getData().add(new XYChart.Data<>(sdf.format(entry.getKey()), entry.getValue()));
				if(max < entry.getValue())
					max = entry.getValue();
				if(min > entry.getValue())
					min = entry.getValue();
			}
			series.setName("Zmiany partii ciała: " + logNamesPl[i]);
			chart.getData().add(series);
			Label recordMin = new Label("Najmniejsza wartość: " + min.toString()), recordMax = new Label("Największa wartość: " + max.toString());
			mainPage.getChildren().addAll(chart, recordMin, recordMax);
			LOG.trace("Log chart loaded");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			AlertUtility.error(e);
			LOG.trace("Can't load log chart. Error: " + e.getMessage());
		}
	}
	/**
	 * Metoda pokazująca wykres podnoszonego cięzaru w przeciągu roku
	 * @param mainPage
	 */
	public void showMonthlyRaisedWeightChart(VBox mainPage) {
		LOG.trace("Loading monthly raised weight chart");
		mainPage.getChildren().clear();
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<String, Number> chart = new LineChart<String, Number>(xAxis, yAxis);
		chart.setTitle("Raport miesięczny - podnoszony cięar");
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		Double max = 0.0;
		for (int i = 11; i >= 0; i--) {
			Double raised = DiaryUtility.getMonthlyRaisedWeight(i);
			String m = FirstStartControllerUtility.getMonthOptions().get(((Calendar.getInstance().get(Calendar.MONTH) - i) % 12 + 12) % 12);
			series.getData().add(new XYChart.Data<String, Number>(m, raised));
			if(raised > max)
				max = raised;
		}
		series.setName("Podnoszony cięar w przeciągu roku");
		chart.getData().add(series);
		Label record = new Label("Aktualny miesięczny rekord: " + max.toString());
		mainPage.getChildren().addAll(chart, record);
		LOG.trace("Monthly raised weight chart loaded");
	}
	/**
	 * Metoda pokazujaca podsumowanie wybranego treningu
	 * @param mainPage
	 * @param fileName
	 */
	public void showTrainingSummary(VBox mainPage, String fileName) {
		LOG.trace("Loading training summary");
		Diary diary = DiaryUtility.readDiary(fileName);
		mainPage.getChildren().clear();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Label summary = new Label("Podsumowanie treningu z dnia " + format.format(diary.getStartDate()));
		summary.setFont(new Font(20));
		mainPage.getChildren().add(summary);
		Integer restTime = diary.getRestTime() / 60;// min
		Integer exerciseTime = diary.showTrainingTime() - restTime;
		Label label1 = new Label("Całkowity czas treningu:"), label2 = new Label("Czas ćwiczeń:");
		label1.setPrefWidth(245);
		label2.setPrefWidth(245);
		Label label3 = new Label(diary.showTrainingTime().toString() + " min");
		label3.setFont(new Font(15));
		Label label4 = new Label(exerciseTime.toString() + " min");
		label4.setFont(new Font(15));
		Label label5 = new Label("Czas odpoczynku:"), label6 = new Label("Wykonane ćwiczenia:");
		Label label7 = new Label(restTime.toString() + " min");
		label7.setFont(new Font(15));
		Label label8 = new Label(new Integer(diary.getExercisesDone().size()).toString());
		label8.setFont(new Font(15));
		Label label9 = new Label("Podniesiony cięar:");
		Label label10 = new Label(new Double(diary.showRaisedWeight()).toString() + " kg");
		label10.setFont(new Font(17));
		GridPane gp = new GridPane();
		gp.setPrefWidth(493);
		gp.setPadding(new Insets(10, 10, 10, 10));
		gp.addRow(0, label1, label2);
		gp.addRow(1, label3, label4);
		gp.addRow(2, label5, label6);
		gp.addRow(3, label7, label8);
		gp.addRow(4, label9);
		gp.addRow(5, label10);
		mainPage.getChildren().add(gp);
		LOG.trace("Training summary loaded");
	}
	/**
	 * Metoda pokauzjąca podsumowanie wybranego miesiąca
	 * @param mainPage
	 * @param minusMonth
	 */
	public void showMonthSummary(VBox mainPage, int minusMonth) {
		LOG.trace("Loading month summary");
		mainPage.getChildren().clear();
		Label summary = new Label("Podsumowanie treningów z miesiąca " + FirstStartControllerUtility.getMonthOptions().get(((Calendar.getInstance().get(Calendar.MONTH) - minusMonth) % 12 + 12) % 12));
		summary.setFont(new Font(20));
		mainPage.getChildren().add(summary);
		Integer restTime = DiaryUtility.getMonthlyRestTime(minusMonth) / 60;// min
		Integer exerciseTime = DiaryUtility.getMonthlyExercisingTime(minusMonth);
		Label label1 = new Label("Całkowity czas treningu:"), label2 = new Label("Czas ćwiczeń:");
		label1.setPrefWidth(245);
		label2.setPrefWidth(245);
		Label label3 = new Label(DiaryUtility.getMonthlyTrainingTime(minusMonth) + " min");
		label3.setFont(new Font(15));
		Label label4 = new Label(exerciseTime.toString() + " min");
		label4.setFont(new Font(15));
		Label label5 = new Label("Czas odpoczynku:"), label6 = new Label("Wykonane ćwiczenia:");
		Label label7 = new Label(restTime.toString() + " min");
		label7.setFont(new Font(15));
		Label label8 = new Label(DiaryUtility.getMonthlyExercisesDone(minusMonth).toString());
		label8.setFont(new Font(15));
		Label label9 = new Label("Podniesiony cięar:");
		Label label10 = new Label(DiaryUtility.getMonthlyRaisedWeight(minusMonth).toString() + " kg");
		label10.setFont(new Font(17));
		GridPane gp = new GridPane();
		gp.setPrefWidth(493);
		gp.setPadding(new Insets(10, 10, 10, 10));
		gp.addRow(0, label1, label2);
		gp.addRow(1, label3, label4);
		gp.addRow(2, label5, label6);
		gp.addRow(3, label7, label8);
		gp.addRow(4, label9);
		gp.addRow(5, label10);
		mainPage.getChildren().add(gp);
		LOG.trace("Month summary loaded");
	}
	/**
	 * Metoda umożliwiająca wybranie wybranego podsumowania
	 * @param mainPage
	 */
	public void createStage(VBox mainPage) {
		LOG.trace("Loading main training progress window");
		mainPage.getChildren().clear();
		ImageView progress = new ImageView("/images/progress.png");
		mainPage.getChildren().add(progress);
		Button trainingsSummaries = new Button("Pokaż podsumowanie treningów"), showExercisesSummaries = new Button("Pokaż podsumowanie ćwiczeń"), showMonthByMonthSummaries = new Button("Pokaż podsumowanie miesięczne"),
				showMeansurmentsSummaries = new Button("Pokaż podsumowanie pomiarów ciała");
		mainPage.setSpacing(10);
		mainPage.getChildren().addAll(trainingsSummaries, showExercisesSummaries, showMonthByMonthSummaries, showMeansurmentsSummaries);
		trainingsSummaries.setOnAction((event) -> {
			mainPage.getChildren().clear();
			LinkedList<Diary> list = DiaryUtility.downloadDiaries();
			SortedSet<Date> set = new TreeSet<Date>(Collections.reverseOrder());
			for(Diary d : list)
				set.add(d.getStartDate());
			for(Date date : set){
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				Button button = new Button("Trening z dnia: " + sdf.format(date));
				mainPage.getChildren().add(button);
				button.setOnAction((event2) -> {
					SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
					showTrainingSummary(mainPage, sdf2.format(date));
				});
			}
		});
		showExercisesSummaries.setOnAction((event) -> {
			mainPage.getChildren().clear();
				List<Exercise> list = ExerciseUtility.downloadExercises();
				for(Exercise ex : list){
					Button button = new Button("Zobacz postępy w ćwiczeniu " + ex.getName());
					mainPage.getChildren().add(button);
					button.setOnAction((event2) -> {
						showExerciseChart(ex, mainPage);
					});
				}
		});
		showMonthByMonthSummaries.setOnAction((event) -> {
			showMonthlyRaisedWeightChart(mainPage);
			for (int i = 0; i <= 11; i++) {
				String m = FirstStartControllerUtility.getMonthOptions().get(((Calendar.getInstance().get(Calendar.MONTH) - i) % 12 + 12) % 12);
				Button monthSummary = new Button("Podsumowanie miesiąca " + m);
				mainPage.getChildren().add(monthSummary);
				final int i2 = i;
				monthSummary.setOnAction((event2) -> {
					showMonthSummary(mainPage, i2);
				});
			}
		});
		showMeansurmentsSummaries.setOnAction((event) -> {
			mainPage.getChildren().clear();
			int i=0;
			for(@SuppressWarnings("unused") String s : logNames){
				Button button = new Button("Postępy - " + logNamesPl[i]);
				mainPage.getChildren().add(button);
				final int in = i;
				button.setOnAction((event2) -> {
					showLogChart(in, mainPage);
				});
				i++;
			}
		});
		LOG.trace("Main training progress window loaded");
	}
}