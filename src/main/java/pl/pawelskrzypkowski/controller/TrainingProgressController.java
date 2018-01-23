package pl.pawelskrzypkowski.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pawelskrzypkowski.application.LocaleHolder;
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
	private String[] logNamesDictionary = new String[]{LocaleHolder.readMessage("logName.weight"), LocaleHolder.readMessage("logName.neck"), LocaleHolder.readMessage("chest"), LocaleHolder.readMessage("logName.biceps"), LocaleHolder.readMessage("logName.waist"), LocaleHolder.readMessage("stomach"), LocaleHolder.readMessage("logName.hips"), LocaleHolder.readMessage("logName.thigh"), LocaleHolder.readMessage("logName.calf")};
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
		chart.setTitle(LocaleHolder.readMessage("trainingProgress.chart.1") + exercise.getName());
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		Set<Entry<Date, Double>> entrySet = map.entrySet();
		for (Entry<Date, Double> entry : entrySet) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			series.getData().add(new XYChart.Data<String, Number>(sdf.format(entry.getKey()), entry.getValue()));
			if (max < entry.getValue())
				max = entry.getValue();
		}
		series.setName(LocaleHolder.readMessage("trainingProgress.series.1"));
		chart.getData().add(series);
		Label record = new Label(LocaleHolder.readMessage("trainingProgress.label.1") + max.toString());
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
			series.setName(LocaleHolder.readMessage("trainingProgress.series.2") + logNamesDictionary[i]);
			chart.getData().add(series);
			Label recordMin = new Label(LocaleHolder.readMessage("trainingProgress.label.2") + min.toString()), recordMax = new Label(LocaleHolder.readMessage("trainingProgress.label.3") + max.toString());
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
		chart.setTitle(LocaleHolder.readMessage("trainingProgress.chart.2"));
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		Double max = 0.0;
		for (int i = 11; i >= 0; i--) {
			Double raised = DiaryUtility.getMonthlyRaisedWeight(i);
			String m = FirstStartControllerUtility.getMonthOptions().get(((Calendar.getInstance().get(Calendar.MONTH) - i) % 12 + 12) % 12);
			series.getData().add(new XYChart.Data<String, Number>(m, raised));
			if(raised > max)
				max = raised;
		}
		series.setName(LocaleHolder.readMessage(LocaleHolder.readMessage("trainingProgress.series.3")));
		chart.getData().add(series);
		Label record = new Label(LocaleHolder.readMessage("trainingProgress.label.4") + max.toString());
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
		Label summary = new Label(LocaleHolder.readMessage("trainingProgress.label.5") + format.format(diary.getStartDate()));
		summary.setFont(new Font(20));
		mainPage.getChildren().add(summary);
		Integer restTime = diary.getRestTime() / 60;// min
		Integer exerciseTime = diary.showTrainingTime() - restTime;
		Label label1 = new Label(LocaleHolder.readMessage("trainingProgress.label.6")), label2 = new Label(LocaleHolder.readMessage("trainingProgress.label.7"));
		label1.setPrefWidth(245);
		label2.setPrefWidth(245);
		Label label3 = new Label(diary.showTrainingTime().toString() + LocaleHolder.readMessage("trainingProgress.label.8"));
		label3.setFont(new Font(15));
		Label label4 = new Label(exerciseTime.toString() + LocaleHolder.readMessage("trainingProgress.label.8"));
		label4.setFont(new Font(15));
		Label label5 = new Label(LocaleHolder.readMessage("trainingProgress.label.9")), label6 = new Label(LocaleHolder.readMessage("trainingProgress.label.10"));
		Label label7 = new Label(restTime.toString() + LocaleHolder.readMessage("trainingProgress.label.8"));
		label7.setFont(new Font(15));
		Label label8 = new Label(new Integer(diary.getExercisesDone().size()).toString());
		label8.setFont(new Font(15));
		Label label9 = new Label(LocaleHolder.readMessage("trainingProgress.label.11"));
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
		Label summary = new Label(LocaleHolder.readMessage("trainingProgress.label.12") + FirstStartControllerUtility.getMonthOptions().get(((Calendar.getInstance().get(Calendar.MONTH) - minusMonth) % 12 + 12) % 12));
		summary.setFont(new Font(20));
		mainPage.getChildren().add(summary);
		Integer restTime = DiaryUtility.getMonthlyRestTime(minusMonth) / 60;// min
		Integer exerciseTime = DiaryUtility.getMonthlyExercisingTime(minusMonth);
		Label label1 = new Label(LocaleHolder.readMessage("trainingProgress.label.13")), label2 = new Label(LocaleHolder.readMessage("trainingProgress.label.7"));
		label1.setPrefWidth(245);
		label2.setPrefWidth(245);
		Label label3 = new Label(DiaryUtility.getMonthlyTrainingTime(minusMonth) + LocaleHolder.readMessage("trainingProgress.label.8"));
		label3.setFont(new Font(15));
		Label label4 = new Label(exerciseTime.toString() + LocaleHolder.readMessage("trainingProgress.label.8"));
		label4.setFont(new Font(15));
		Label label5 = new Label(LocaleHolder.readMessage("trainingProgress.label.9")), label6 = new Label(LocaleHolder.readMessage("trainingProgress.label.10"));
		Label label7 = new Label(restTime.toString() + " min");
		label7.setFont(new Font(15));
		Label label8 = new Label(DiaryUtility.getMonthlyExercisesDone(minusMonth).toString());
		label8.setFont(new Font(15));
		Label label9 = new Label(LocaleHolder.readMessage("trainingProgress.label.11"));
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
		Button trainingsSummaries = new Button(LocaleHolder.readMessage("trainingProgress.button.trainingSummary")), showExercisesSummaries = new Button(LocaleHolder.readMessage("trainingProgress.button.exerciseSummary")), showMonthByMonthSummaries = new Button(LocaleHolder.readMessage("trainingProgress.button.monthSummary")),
				showMeansurmentsSummaries = new Button(LocaleHolder.readMessage("trainingProgress.button.bodySummary"));
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
				Button button = new Button(LocaleHolder.readMessage("trainingProgress.button.trainingDay") + sdf.format(date));
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
					Button button = new Button(LocaleHolder.readMessage("trainingProgress.button.exerciseProgress") + ex.getName());
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
				Button monthSummary = new Button(LocaleHolder.readMessage("trainingProgress.button.monthProgress") + m);
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
				Button button = new Button(LocaleHolder.readMessage("trainingProgress.button.progress") + logNamesDictionary[i]);
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
