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

import javafx.scene.canvas.Canvas;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainController {
    private Stage primaryStage;
    public Label statusLabel, labelName_1, labelName_2, labelName_3, labelName_4;
    public Button runButton, button_1, button_2, button_3, button_4;
    public ProgressBar progressBar_1, progressBar_2, progressBar_3, progressBar_4, progressBars[];

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.progressBars = new ProgressBar[]{progressBar_1, progressBar_2, progressBar_3, progressBar_4};
    }

    public void startInitialLoading(List<String> songs) {
        labelName_1.setText(songs.get(0));
        labelName_2.setText(songs.get(1));
        labelName_3.setText(songs.get(2));
        labelName_4.setText(songs.get(3));

        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = 0;
                for (ProgressBar progressBar : progressBars) {
                    final String currentSong = songs.get(index);
                    final Task task = new Task<ObservableList<String>>() {
                        @Override
                        protected ObservableList<String> call() throws InterruptedException {
//                            updateMessage("Finding friends . . .");
                            BufferedInputStream in = null;
                            FileOutputStream out = null;
                            try {
                                URL url = new URL(currentSong);
                                URLConnection conn = url.openConnection();
                                int size = conn.getContentLength();

                                if (size < 0) {
                                    throw new RuntimeException("Size of file undefined");
                                }

                                in = new BufferedInputStream(url.openStream());
                                String[] bits = currentSong.split("/");
                                String lastOne = bits[bits.length-1];
                                out = new FileOutputStream(lastOne);
                                byte data[] = new byte[1024];
                                int count;
                                double sumCount = 0.0;

                                while ((count = in.read(data, 0, 1024)) != -1) {
                                    out.write(data, 0, count);
                                    sumCount += count;
                                    updateProgress((int)((sumCount / size) * 100), 100);
                                }
                            } catch (Exception e) {
                                System.out.println(e.fillInStackTrace());
                                throw new RuntimeException(e);
                            }
                            return FXCollections.observableArrayList(songs);
                        }
                        // todo тут писать когда файл музыкальный загрузился
                        //          @Override protected void done() {
                        //            super.done();
                        //            System.out.println("This is bad, do not do this, this thread " + Thread.currentThread() + " is not the FXApplication thread.");
                        //            runButton.setText("Voila!");
                        //          }
                    };

                    statusLabel.textProperty().bind(task.messageProperty());
                    runButton.disableProperty().bind(task.runningProperty());
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
                    index++;
                }
            }
        });


        //todo тут обработчики кнопок, которые прокидывают параметр на startDrawing()

        button_1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                startDrawing("-fx-background-color: red");
            }
        });

        button_2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                startDrawing("-fx-background-color: gray");
            }
        });

        button_3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                startDrawing("-fx-background-color: green");
            }
        });

        button_4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                startDrawing("-fx-background-color: yellow");
            }
        });
    }

    //todo тут рисовалка
    private void startDrawing(String someArgument) {
        //todo это не работает, не знаю почему
    }
}
