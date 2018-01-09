package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import application.JPAHolder;
import controller.utility.AlertUtility;
import controller.utility.PlansControllerUtility;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.diary.Diary;
import model.diary.Exercise;
import model.diary.ExercisesDone;
import model.diary.Set;
import model.diary.Workout;
import model.diary.utility.ExerciseUtility;
import model.user.GlobalUser;
import model.user.User;

import javax.persistence.EntityManager;

/**
 * Klasa - kontroler do obslugi planow treningowych
 * @author Paweł
 *
 */
public class PlansController {
	private List<Workout> workoutList;
	/**
	 * Konstruktor tworzacy pusty obiekt
	 */
	public PlansController() {
		workoutList = new LinkedList<Workout>();
	}
	/**
	 * Metoda pobierajaca plany treningowe
	 */
	public void downloadPlans() {
		EntityManager entityManager = JPAHolder.getEntityManager();
		User user = entityManager.find(User.class, GlobalUser.loggedUserId);
		workoutList = user.getWorkouts();
		entityManager.close();
	}

	/**
	 * Metoda zapewniajaca wyglad do edycji cwiczenia
	 * @param exercise
	 * @param workout
	 * @param options
	 * @param i
	 * @param mainPage
	 */
	public void editExerciseSupport(Exercise exercise, Workout workout, ObservableList<String> options, int i,
									VBox mainPage) {
		ComboBox<String> cb = new ComboBox<String>(options);
		cb.setValue(exercise.getName());
		cb.setMaxWidth(150);
		TextField editSetsNumber = new TextField(workout.getSetsNumber().get(i).toString());
		editSetsNumber.setMaxWidth(50);
		TextField editRest = new TextField(workout.getRest().get(i).toString());
		editRest.setMaxWidth(50);
		Label setsNumber = new Label("Ilośc serii: "), rest = new Label("Odpoczynek: ");
		Button save = new Button("Zapisz"), delete = new Button("Usuń"), up = new Button("Do góry"),
				down = new Button("Do dołu");
		HBox hb = new HBox();
		hb.setSpacing(30);
		if (i == 0 && i == workout.getExercises().size() - 1)
			hb.getChildren().addAll(save, delete);
		else if (i == 0)
			hb.getChildren().addAll(save, delete, down);
		else if (i == workout.getExercises().size() - 1)
			hb.getChildren().addAll(save, delete, up);
		else
			hb.getChildren().addAll(save, delete, up, down);
		VBox vb = new VBox();
		vb.setPadding(new Insets(5));
		if (i % 2 == 1) {
			vb.setStyle("-fx-background-color: #bc5856; -fx-background-radius: 5 5 5 5; -fx-border-radius: 5 5 5 5;");
		} else {
			vb.setStyle("-fx-background-color: #0db5df; -fx-background-radius: 5 5 5 5; -fx-border-radius: 5 5 5 5;");
		}
		vb.getChildren().addAll(cb, setsNumber, editSetsNumber, rest, editRest, hb);
		mainPage.getChildren().add(vb);
		final int finalI = i;
		save.setOnAction((event) -> {
			saveEdittedExercise(workout, cb.getValue(), editSetsNumber.getText(), editRest.getText(), i);
			editWorkout(workout, mainPage);
		});
		delete.setOnAction((event) -> {
			workout.deleteItem(finalI);
			editWorkout(workout, mainPage);
		});
		up.setOnAction((edit) -> {
			workout.moveUpExercise(finalI);
			editWorkout(workout, mainPage);
		});
		down.setOnAction((edit) -> {
			workout.moveDownExercise(finalI);
			editWorkout(workout, mainPage);
		});
	}
	/**
	 * Metoda do zapisu edytowanego cwiczenia
	 * @param workout
	 * @param exerciseName
	 * @param setsNumber
	 * @param rest
	 * @param i
	 */
	public void saveEdittedExercise(Workout workout, String exerciseName, String setsNumber, String rest, int i) {
		Exercise ex;
		try {
			ex = ExerciseUtility.readExercise(exerciseName);
			workout.editExercise(i, ex, Integer.parseInt(setsNumber), Integer.parseInt(rest));
		} catch(NumberFormatException e) {
			AlertUtility.badData();
		}
	}
	/**
	 * Metoda do zapisu nowego cwiczenia
	 * @param workout
	 * @param exerciseName
	 * @param setsNumber
	 * @param rest
	 */
	public void saveNewExercise(Workout workout, String exerciseName, String setsNumber, String rest) {
		Exercise ex;
		ex = ExerciseUtility.readExercise(exerciseName);
		workout.addItemAtTheEnd(ex, Integer.parseInt(setsNumber), Integer.parseInt(rest));

	}
	/**
	 * Metoda zapewbiajaca wyglad przy dodawaniu nowego cwiczenia
	 * @param workout
	 * @param options
	 * @param mainPage
	 */
	public void addExerciseSupport(Workout workout, ObservableList<String> options, VBox mainPage) {
		Label add = new Label("Dodaj: ");
		add.setFont(new Font(15));
		Label setsNumber = new Label("Ilośc serii: "), rest = new Label("Odpoczynek: ");
		TextField editSetsNumber = new TextField("3");
		TextField editRest = new TextField();
		editRest.setPromptText("sekundy");
		ComboBox<String> cb = new ComboBox<String>(options);
		Button save = new Button("Zapisz");
		mainPage.getChildren().addAll(add, cb, setsNumber, editSetsNumber, rest, editRest, save);
		save.setOnAction((event) -> {
			saveNewExercise(workout, cb.getValue(), editSetsNumber.getText(), editRest.getText());
			editWorkout(workout, mainPage);
		});
	}
	/**
	 * Metoda do edycji elementow opisowych treningu
	 * @param workout
	 * @param mainPage
	 */
	public void editWorkoutPropertiesSupport(Workout workout, VBox mainPage) {
		Label name = new Label("Nazwa:"), description = new Label("Opis:"), type = new Label("Typ treningu:"),
				level = new Label("Poziom treningu:"), header = new Label("Ćwiczenia:");
		header.setFont(new Font(15));
		TextField editName = new TextField(workout.getWorkoutName()),
				editType = new TextField(workout.getWorkoutType()),
				editLevel = new TextField(workout.getDifficultyLevel());
		TextArea editDescription = new TextArea(workout.getWorkoutDescription());
		Button save = new Button("Zapisz");
		mainPage.getChildren().addAll(name, editName, description, editDescription, type, editType, level, editLevel,
				save, header);
		save.setOnAction((event) -> {
			if (!PlansControllerUtility.checkStringCorrectness(editName.getText())
					|| !PlansControllerUtility.checkStringCorrectness(editDescription.getText())
					|| !PlansControllerUtility.checkStringCorrectness(editType.getText())
					|| !PlansControllerUtility.checkStringCorrectness(editLevel.getText())) {
				AlertUtility.onlyNumbersAndLetters();
			} else if (workout.checkIfWorkoutExist(editName.getText())) {
				AlertUtility.nameBusy();
			} else {
				workout.changeWorkoutProperties(editName.getText(), editDescription.getText(), editType.getText(),
						editLevel.getText());
				editWorkout(workout, mainPage);
			}
		});
	}
	/**
	 * Metoda do edycji treningu
	 * @param workout
	 * @param mainPage
	 */
	public void editWorkout(Workout workout, VBox mainPage) {
		mainPage.getChildren().clear();
		editWorkoutPropertiesSupport(workout, mainPage);
		List<Exercise> exerciseList;
		exerciseList = ExerciseUtility.downloadExercises();
		ObservableList<String> options = FXCollections.observableArrayList();
		for (Exercise exercise : exerciseList)
			options.add(exercise.getName());
		int i = 0;
		for (Exercise exercise : workout.getExercises()) {
			mainPage.getChildren().add(new Label());
			editExerciseSupport(exercise, workout, options, i, mainPage);
			i++;
		}
		addExerciseSupport(workout, options, mainPage);
	}
	/**
	 * Metoda dodajaca trening
	 * @param mainPage
	 */
	public void addWorkout(VBox mainPage) {
		mainPage.getChildren().clear();
		Workout workout = new Workout("", "", "", "");
		String name = "Nowy";
		int i = 1;
		while (workout.checkIfWorkoutExist(name)) {
			i++;
			name = "Nowy " + i;
		}
		workout.setWorkoutName(name);
		workout.saveWorkout();
		editWorkoutPropertiesSupport(workout, mainPage);
		List<Exercise> exerciseList = ExerciseUtility.downloadExercises();
		ObservableList<String> options = FXCollections.observableArrayList();
		for (Exercise exercise : exerciseList)
			options.add(exercise.getName());
		addExerciseSupport(workout, options, mainPage);
	}
	/**
	 * Metoda do odbywania przerwy
	 * @param workout
	 * @param diary
	 * @param exerciseNumber
	 * @param setNumber
	 * @param mainPage
	 */
	public void doRest(Workout workout, Diary diary, int exerciseNumber, int setNumber, VBox mainPage) {
		int restTime = workout.getRest().get(exerciseNumber - 1);
		mainPage.getChildren().clear();
		final int START = restTime;
		IntegerProperty startTime = new SimpleIntegerProperty(START);
		Label seconds = new Label();
		seconds.textProperty().bind(startTime.asString());
		Button skip = new Button("Pomiń");
		mainPage.getChildren().addAll(seconds, skip);
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(START + 1), new KeyValue(startTime, 0)));
		timeline.playFromStart();
		timeline.setOnFinished((event) -> {
			diary.increaseRestTime(START);
			if (setNumber == workout.getSetsNumber().get(exerciseNumber - 1)) // jesli ostatnia seria
				doSet(workout, diary, exerciseNumber + 1, 1, mainPage);
			else// jesli w srodku serii
				doSet(workout, diary, exerciseNumber, setNumber + 1, mainPage);
		});
		skip.setOnAction((event) -> {
			diary.increaseRestTime(START - startTime.intValue());
			timeline.stop();
			if (setNumber == workout.getSetsNumber().get(exerciseNumber - 1))//jesli ostatnia seria
				doSet(workout, diary, exerciseNumber + 1, 1, mainPage);
			else// jesli w srodku cwiczenia
				doSet(workout, diary, exerciseNumber, setNumber + 1, mainPage);
		});
	}
	/**
	 * Metoda do wykonywania wybranej serii treningu
	 * @param workout
	 * @param diary
	 * @param exerciseNumber
	 * @param setNumber
	 * @param mainPage
	 */
	public void doSet(Workout workout, Diary diary, int exerciseNumber, int setNumber, VBox mainPage) {
		mainPage.getChildren().clear();
		Label header = new Label(workout.getWorkoutName());
		header.setFont(new Font(15));
		mainPage.getChildren().add(header);
		Exercise exercise = workout.getExercises().get(exerciseNumber - 1);
		Label exerciseName = new Label(exercise.getName());
		TextField reps = new TextField(), weight = new TextField();
		reps.setPromptText("Ilość powtórzeń");
		weight.setPromptText("Ciężar");
		HBox set = new HBox(15);
		set.getChildren().addAll(reps, new Label("x"), weight);
		Button save = new Button("Zapisz"), skipSet = new Button("Pomiń serię"),
				skipExercise = new Button("Pomiń ćwiczenie");
		mainPage.getChildren().addAll(exerciseName, set, save, skipSet, skipExercise);
		skipExercise.setOnAction((event) -> {
			if (workout.getExercises().size() == exerciseNumber){// jesli koniec treningu
				diary.setFinishDate(new Date());
				diary.saveDiary();
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
				TrainingProgressController tpc = new TrainingProgressController();
				tpc.showTrainingSummary(mainPage, format.format(diary.getStartDate()));
				return;
			}
			else
				doSet(workout, diary, exerciseNumber + 1, 1, mainPage);
		});
		skipSet.setOnAction((event) -> {
			if(setNumber == 1){//jesli pierwsza seria
				ExercisesDone ed = new ExercisesDone(workout.getExercises().get(exerciseNumber-1));
				diary.getExercisesDone().add(ed);//dodaje tylko cwiczenie bez wykonanych serii
			}
			if (setNumber == workout.getSetsNumber().get(exerciseNumber - 1)) {// jesli ostatnia seria
				if (workout.getExercises().size() == exerciseNumber){// jesli koniec treningu
					diary.setFinishDate(new Date());
					diary.saveDiary();
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
					TrainingProgressController tpc = new TrainingProgressController();
					tpc.showTrainingSummary(mainPage, format.format(diary.getStartDate()));
					return;
				}
				else
					doSet(workout, diary, exerciseNumber + 1, 1, mainPage);
			} else// jesli w srodku cwiczenia
				doSet(workout, diary, exerciseNumber, setNumber + 1, mainPage);
		});
		save.setOnAction((event) -> {
			weight.setText(weight.getText().replace(',', '.'));
			try{
				Double w = Double.parseDouble(weight.getText());
				Integer r = Integer.parseInt(reps.getText());
				if (setNumber == 1) {// jesli 1 seria nowego cwiczenia
					ExercisesDone ed = new ExercisesDone(workout.getExercises().get(exerciseNumber - 1));
					ed.getSets().add(new Set(r, w));
					diary.getExercisesDone().add(ed);
				} else {
					diary.getExercisesDone().get(diary.getExercisesDone().size() - 1).getSets().add(new Set(r, w));
				}
				if(setNumber == workout.getSetsNumber().get(exerciseNumber - 1) && workout.getExercises().size() == exerciseNumber){//jesli koniec treningu
					diary.setFinishDate(new Date());
					diary.saveDiary();
					SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
					TrainingProgressController tpc = new TrainingProgressController();
					tpc.showTrainingSummary(mainPage, format.format(diary.getStartDate()));
					return;
				}
				else
					doRest(workout, diary, exerciseNumber, setNumber, mainPage);
			}
			catch(NumberFormatException e){
				AlertUtility.noNumberValue();
			}
		});
	}
	/**
	 * Metoda do obslugi wybranego treningu
	 * @param workout
	 * @param mainPage
	 * @param i
	 */
	public void showWorkoutSupport(Workout workout, VBox mainPage, int i) {
		HBox hb = new HBox(5);
		Label name = new Label(workout.getWorkoutName());
		Button start = new Button("Rozpocznij");
		Button edit = new Button("Edytuj");
		Button delete = new Button("Usuń");
		hb.getChildren().addAll(name, start, edit, delete);
		Label description = new Label(workout.getWorkoutDescription());
		description.setMaxHeight(30);
		description.setMaxWidth(450);
		Label type = new Label("Typ: " + workout.getWorkoutType());
		type.setMaxHeight(15);
		type.setMaxWidth(450);
		Label level = new Label("Poziom: " + workout.getDifficultyLevel());
		level.setMaxHeight(15);
		level.setMaxWidth(450);
		VBox vb = new VBox();
		vb.setMaxWidth(475);
		vb.setPadding(new Insets(5));
		vb.getChildren().addAll(hb, description, type, level);
		if (i % 2 == 1)
			vb.setStyle("-fx-background-color: #0db5df; -fx-background-radius: 5 5 5 5; -fx-border-radius: 5 5 5 5;");
		else
			vb.setStyle("-fx-background-color: #bc5856; -fx-background-radius: 5 5 5 5; -fx-border-radius: 5 5 5 5;");
		mainPage.getChildren().add(vb);
		delete.setOnAction((event) -> {
			workout.deleteWorkout();
			workoutList.clear();
			createStage(mainPage);
		});
		edit.setOnAction((event) -> {
			editWorkout(workout, mainPage);
		});
		start.setOnAction((event) -> {
			Diary diary = new Diary();
			if(workout.getExercises().isEmpty()){
				AlertUtility.emptyWorkout();
			} else {
				doSet(workout, diary, 1, 1, mainPage);
			}
		});
	}
	/**
	 * Metoda umozliwiajaca obsluge wszystkich opcji obslugi treningow
	 * @param mainPage
	 */
	public void createStage(VBox mainPage) {
		downloadPlans();
		mainPage.getChildren().clear();
		mainPage.setSpacing(0);
		ImageView plans = new ImageView("/images/plans.png");
		mainPage.getChildren().add(plans);
		Button addNewWorkout = new Button("Dodaj nowy plan");
		mainPage.getChildren().add(addNewWorkout);
		int i = 0;
		for (Workout workout : workoutList) {
			mainPage.getChildren().add(new Label());
			showWorkoutSupport(workout, mainPage, i);
			i++;
		}
		addNewWorkout.setOnAction((event) -> {
			addWorkout(mainPage);
		});
	}
}