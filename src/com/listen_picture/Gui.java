package com.listen_picture;

import gui.LoginController;
import gui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.concurrent.Worker.State;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String LOGIN_SUCCESS_PAGE = "blank.html#", LOGIN_FAILURE_PAGE = "blank.html#error";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Listen Picture");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/login.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            LoginController controller = fxmlLoader.<LoginController>getController();
            controller.initialize(primaryStage, Main.APP_ID, null, Config.CLIENT_SECRET);
            controller.start();
            primaryStage.setScene(new Scene(root, 660, 380));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
