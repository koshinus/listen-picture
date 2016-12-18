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
    public Label statusLabel;
    public Button runButton;
    public ListView<String> peopleView;
    public ProgressBar progressBar;

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void startInitialLoading(List<String> songs) {
//        final Label statusLabel = new Label("Status");
//        final Button runButton = new Button("Run");
//        final ListView<String> peopleView = new ListView<String>();
//        peopleView.setPrefSize(220, 162);
//        final ProgressBar progressBar = new ProgressBar();
        progressBar.prefWidthProperty().bind(peopleView.prefWidthProperty());

        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                final Task task = new Task<ObservableList<String>>() {
                    @Override protected ObservableList<String> call() throws InterruptedException {
                        updateMessage("Finding friends . . .");
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(200);
                            updateProgress(i+1, 10);
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
                    @Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            System.out.println("This is ok, this thread " + Thread.currentThread() + " is the JavaFX Application thread.");
                            runButton.setText("Voila!");
                        }
                    }
                });

                new Thread(task).start();
            }
        });
    }
}
