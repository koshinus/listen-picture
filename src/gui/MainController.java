package gui;

import com.listen_picture.Converter;
import com.listen_picture.Main;
import com.sun.codemodel.internal.JLabel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.scene.canvas.Canvas;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainController {
    private Stage primaryStage;
    public Label labelName_1, labelName_2, labelName_3, labelName_4, labelNames[];
    public Button runButton;
    public ProgressBar progressBar_1, progressBar_2, progressBar_3, progressBar_4, progressBars[];

    static final int canvasSizeX = 1000, canvasSizeY = 500;

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.progressBars = new ProgressBar[]{progressBar_1, progressBar_2, progressBar_3, progressBar_4};
        this.labelNames = new Label[]{labelName_1, labelName_2, labelName_3, labelName_4};
    }

    public void startInitialLoading(List<String> songs) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double rightX = primaryScreenBounds.getWidth() - canvasSizeX;
        double rightY = primaryScreenBounds.getHeight() - canvasSizeY;
        double[][] bounds = {{0, 0}, {rightX, 0}, {0, rightY}, {rightX, rightY}};
        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                final ProgressWorker w = new ProgressWorker(progressBars, labelNames, bounds, songs);
            }
        });
    }

    static void openCanvasWindow(String filePath, double X, double Y) {
        Group root = new Group();
        Stage stage = new Stage();
        Canvas canvas = new Canvas(canvasSizeX, canvasSizeY);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        GraphicsAdapter ga = new GraphicsAdapter(gc);
        final Converter converter = new Converter();
        Main.MyThread t = converter.encode(filePath, ga);

        stage.setScene(new Scene(root));
        System.out.println(X);
        System.out.println(Y);
        stage.setX(X);
        stage.setY(Y);
        stage.show();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t1 -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Main.decode(t.image, t.length);
            t.runned = true;
        });
    }
}
