package com.listen_picture;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.concurrent.Worker.State;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String LOGIN_SUCCESS_PAGE = "blank.html#", LOGIN_FAILURE_PAGE = "blank.html#error";

//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Listen Picture");

//        String url = "https://oauth.vk.com/authorize?client_id=" + Main.APP_ID + "&display=page&" +
//                "redirect_uri=" + REDIRECT_URL + "&scope=audio&response_type=code&v=5.60";

        Parent root = FXMLLoader.load(getClass().getResource("/gui/login.fxml"));
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/gui/main.fxml"));

//        StackPane root = new StackPane();
//        root.getChildren().add(view);
        primaryStage.setScene(new Scene(root, 660, 380));
        primaryStage.show();
    }
}
