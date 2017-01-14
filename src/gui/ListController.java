package gui;

import com.listen_picture.Converter;
import com.listen_picture.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.List;

public class ListController {
    private Stage primaryStage;
    private static final int canvasSizeX = 1000, canvasSizeY = 500;

    public GridPane gridPane;

    void initialize(Stage primaryStage, List<String> urls) {
        this.primaryStage = primaryStage;

        for (int index = 0; index < urls.size(); ++index) {
            String url = urls.get(index);
            String[] parts = url.split("/");
            String fileName = parts[parts.length - 1];

            Label label = new Label(fileName);
            Button button = new Button("Play");
            button.setOnAction(actionEvent -> {
                openCanvasWindow(fileName, 100, 100);
            });
            gridPane.addRow(index, label, button);
        }
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

        MenuItem menuItem = new MenuItem("Open");
        menuItem.setOnAction(e -> System.out.println("Opening Database Connection..."));
        final Menu menu = new Menu("File");
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);
        root.getChildren().add(menuBar);

        stage.setScene(new Scene(root));
        stage.setX(X);
        stage.setY(Y);
        stage.show();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t1 -> {
            if (t.playingThread != null) {
                t.playingThread.stop();
                t.playingThread = null;
            } else {
                t.playingThread = new Thread(() -> {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Main.decode(t.image, t.length);
                });
                t.playingThread.start();
            }
        });
    }
}
