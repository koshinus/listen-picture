package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.Label;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

/**
 * Created by iddqd on 19/12/2016.
 */
public class ProgressTask implements Runnable {
    ProgressBar progressBar;
    Label label;
    double canvasX, canvasY;
    String url;
    Callable<Integer> onDone;

    public ProgressTask(ProgressBar progressBar, Label label, double canvasX, double canvasY, String url, Callable<Integer> onDone) {
        this.progressBar = progressBar;
        this.label = label;
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        this.url = url;
        this.onDone = onDone;
    }

    public void run() {
        String[] bits = url.split("/");
        String currentSongName = bits[bits.length - 1];
        Platform.runLater(() -> {
            label.setText(currentSongName);
        });

        final String fullName = url;
        final Task task = new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws InterruptedException {
                BufferedInputStream in = null;
                FileOutputStream out = null;
                try {
                    URL url = new URL(fullName);
                    URLConnection conn = url.openConnection();
                    int size = conn.getContentLength();

                    if (size < 0) {
                        throw new RuntimeException("Size of file undefined");
                    }

                    in = new BufferedInputStream(url.openStream());
                    out = new FileOutputStream(currentSongName);
                    byte data[] = new byte[1024];
                    int count;
                    double sumCount = 0.0;

                    while ((count = in.read(data, 0, 1024)) != -1) {
                        out.write(data, 0, count);
                        sumCount += count;
                        updateProgress((int) ((sumCount / size) * 100), 100);
                    }
                } catch (Exception e) {
                    System.out.println(e.fillInStackTrace());
                    throw new RuntimeException(e);
                }
                return FXCollections.observableArrayList();
            }


            @Override
            protected void done() {
                super.done();
                try {
                    onDone.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//                Platform.runLater(() -> {
//                    MainController.openCanvasWindowFromMp3(currentSongName, canvasX, canvasY);
//                });
            }
        };

        Platform.runLater(() -> {
            progressBar.progressProperty().bind(task.progressProperty());
        });

        new Thread(task).start();
    }
}
