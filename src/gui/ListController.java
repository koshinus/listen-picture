package gui;

import com.listen_picture.Converter;
import com.listen_picture.Main;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ListController {
    private static final int canvasSizeX = 1000, canvasSizeY = 500;

    public GridPane gridPane;
    public ScrollPane scrollPane;

    void initialize(List<String> urls) {
        for (int index = 0; index < urls.size(); ++index) {
            String url = urls.get(index);
            String[] parts = url.split("/");
            String fileName = parts[parts.length - 1];

            Label label = new Label(fileName);
            Button button = new Button("Play");
            button.setOnAction(actionEvent -> {
                openCanvasWindowFromMp3(fileName, 100, 100);
            });
            gridPane.addRow(index, label, button);
        }
    }

    static void openCanvasWindowFromMp3(String filePath, double X, double Y) {
        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);

        BorderPane root = new BorderPane();
        stage.setScene(new Scene(root));

        ScrollPane scroll = new ScrollPane();
        scroll.setPrefSize(canvasSizeX, canvasSizeY);
        root.setCenter(scroll);

        final File file1 = new File(filePath);
        int width = (int)file1.length() / 1500 + 10;
        Canvas canvas = new Canvas(width, canvasSizeY + 30);
        scroll.setContent(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        GraphicsAdapter ga = new GraphicsAdapter(gc);
        final Converter converter = new Converter();
        Main.MyThread t = converter.encode(filePath, ga);

        MenuBar menuBar = new MenuBar();
        root.setTop(menuBar);

        final Menu menu = new Menu("File");
        menuBar.getMenus().add(menu);

        MenuItem menuItem = new MenuItem("Save");
        menuItem.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save as image");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("(.png)", "*.png"));
            File file = chooser.showSaveDialog(stage);

            if (file != null) {
                File outputFile = new File(file.getParentFile().getPath() +
                        "/" +
                        FilenameUtils.removeExtension(file.getName()) +
                        "." + t.length +
                        "." + FilenameUtils.getExtension(file.getName())
                );
                try {
                    ImageIO.write(t.image, "png", outputFile);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        menu.getItems().add(menuItem);

        stage.show();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t1 -> {
            if (t.playingThread != null) {
                t.playingThread.stop();
                t.playingThread = null;
                System.out.println("play stop");
            } else {
                t.playingThread = new Thread(() -> {
                    try {
                        t.join();
                        Platform.runLater(() -> {
                            canvas.setWidth(t.image.getWidth());
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Main.decode(t.image, t.length);
                });
                t.playingThread.start();
            }
        });

        stage.setOnCloseRequest(event -> {
            if (t.playingThread != null) t.playingThread.stop();
            System.out.println("canvas closed");
            stage.close();
        });
    }

    static void openCanvasWindowFromImage(String filePath, double X, double Y) {
        final File file = new File(filePath);
        String[] split = filePath.split("\\.", -1);
        int length = Integer.parseInt(split[split.length - 2]);

        try {
            final BufferedImage image = ImageIO.read(file);
            Stage stage = new Stage();

            BorderPane root = new BorderPane();

            ScrollPane scroll = new ScrollPane();
            scroll.setPrefSize(canvasSizeX, canvasSizeY + 30);
            root.setCenter(scroll);

            Canvas canvas = new Canvas(image.getWidth(), canvasSizeY);
            scroll.setContent(canvas);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(SwingFXUtils.toFXImage(image, null), 0,0);

            stage.setScene(new Scene(root));
            stage.setX(X);
            stage.setY(Y);
            stage.show();

            Main.MyThread t = new Main.MyThread(() -> {});
            canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t1 -> {
                if (t.playingThread != null) {
                    t.playingThread.stop();
                    t.playingThread = null;
                    System.out.println("play stop");
                } else {
                    t.playingThread = new Thread(() -> {
                        Main.decode(image, length);
                    });
                    t.playingThread.start();
                }
            });

            stage.setOnCloseRequest(event -> {
                if (t.playingThread != null) t.playingThread.stop();
                System.out.println("canvas closed");
                stage.close();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
