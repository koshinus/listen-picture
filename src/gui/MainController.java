package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.util.List;

public class MainController {
    private Stage primaryStage;
    public Label statusLabel, labelName_1, labelName_2, labelName_3, labelName_4;
    public Button runButton;
    public ListView<String> peopleView;
    public ProgressBar progressBar_1, progressBar_2, progressBar_3, progressBar_4, progressBars[];

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.progressBars = new ProgressBar[]{progressBar_1, progressBar_2, progressBar_3, progressBar_4};
    }

    public void startInitialLoading(List<String> songs) {
        progressBar_1.prefWidthProperty().bind(peopleView.prefWidthProperty());
        progressBar_2.prefWidthProperty().bind(peopleView.prefWidthProperty());
        progressBar_3.prefWidthProperty().bind(peopleView.prefWidthProperty());
        progressBar_4.prefWidthProperty().bind(peopleView.prefWidthProperty());

        labelName_1.setText(songs.get(0));
        labelName_2.setText(songs.get(1));
        labelName_3.setText(songs.get(2));
        labelName_4.setText(songs.get(3));

        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                for (ProgressBar progressBar : progressBars) {
                    final Task task = new Task<ObservableList<String>>() {
                        @Override
                        protected ObservableList<String> call() throws InterruptedException {
                            updateMessage("Finding friends . . .");
                            for (int i = 0; i < 10; i++) {
                                Thread.sleep(200);
                                updateProgress(i + 1, 10);
                            }
                            updateMessage("Finished.");
                            return FXCollections.observableArrayList("John", "Jim", "Geoff", "Jill", "Suki");
                        }
                        //          @Override protected void done() {
                        //            super.done();
                        //            System.out.println("This is bad, do not do this, this thread " + Thread.currentThread() + " is not the FXApplication thread.");
                        //            runButton.setText("Voila!");
                        //          }
                    };

                    statusLabel.textProperty().bind(task.messageProperty());
                    runButton.disableProperty().bind(task.runningProperty());
                    peopleView.itemsProperty().bind(task.valueProperty());
                    progressBar.progressProperty().bind(task.progressProperty());
                    task.stateProperty().addListener(new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                            if (newState == Worker.State.SUCCEEDED) {
                                System.out.println("This is ok, this thread " + Thread.currentThread() + " is the JavaFX Application thread.");
                                runButton.setText("Voila!");
                            }
                        }
                    });

                    new Thread(task).start();
                }
            }
        });
    }
}
