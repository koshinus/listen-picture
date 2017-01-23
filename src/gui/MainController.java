package gui;

import com.listen_picture.Gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {
    private Stage primaryStage;
    public Label labelName_1, labelName_2, labelName_3, labelName_4, labelNames[];
    public Button runButton;
    public ProgressBar progressBar_1, progressBar_2, progressBar_3, progressBar_4, progressBars[];
    public BorderPane borderPane;

    static final int canvasSizeX = 1000, canvasSizeY = 500;

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.progressBars = new ProgressBar[]{progressBar_1, progressBar_2, progressBar_3, progressBar_4};
        this.labelNames = new Label[]{labelName_1, labelName_2, labelName_3, labelName_4};

        MenuBar menuBar = new MenuBar();
        borderPane.setTop(menuBar);

        Menu menu = new Menu("File");
        menuBar.getMenus().add(menu);

        MenuItem menuItem = new MenuItem("Open");
        menuItem.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("(.png)", "*.png"));
            File file = chooser.showOpenDialog(borderPane.getScene().getWindow());

            if (file != null) {
                ListController.openCanvasWindowFromImage(file.getPath(), 100, 100);
            }
        });
        menu.getItems().add(menuItem);
    }

    public void startInitialLoading(List<String> songs) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        double rightX = primaryScreenBounds.getWidth() - canvasSizeX;
        double rightY = primaryScreenBounds.getHeight() - canvasSizeY;
        double[][] bounds = {{0, 0}, {rightX, 0}, {0, rightY}, {rightX, rightY}};
        runButton.setOnAction(actionEvent -> {
            new ProgressWorker(progressBars, labelNames, bounds, songs, () -> {
                Platform.runLater(() -> {
                    openListController(songs);
                });
                return null;
            });
        });
    }

    void openListController(List<String> songs){
        FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource("/gui/list.fxml"));

        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListController controller = fxmlLoader.getController();
        controller.initialize(songs);
        borderPane.setCenter(controller.scrollPane);
    }
}
