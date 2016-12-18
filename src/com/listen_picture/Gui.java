package com.listen_picture;

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

import netscape.javascript.JSObject;

public class Gui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String LOGIN_SUCCESS_PAGE = "blank.html#", LOGIN_FAILURE_PAGE = "blank.html#error";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Listen Picture");

        String url = "https://oauth.vk.com/authorize?client_id=" + Main.APP_ID + "&display=page&" +
                "redirect_uri=" + REDIRECT_URL + "&scope=audio&response_type=code&v=5.60";

        final WebView view = new WebView();
        final WebEngine engine = view.getEngine();
        engine.load(url);
        engine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == State.SUCCEEDED) {
                        String Url = engine.getLocation();
                        if (Url.contains(LOGIN_FAILURE_PAGE)) {
                            System.out.println(url);
                        } else if (Url.contains(LOGIN_SUCCESS_PAGE)) {
                            Main.vkCode = Url.substring(Url.indexOf(LOGIN_SUCCESS_PAGE) + LOGIN_SUCCESS_PAGE.length() + 5);
                            System.out.println(Main.vkCode);
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/main.fxml"));
                                Parent root = (Parent)fxmlLoader.load();
                                primaryStage.setScene(new Scene(root, 660, 380));
                                MainController controller = fxmlLoader.<MainController>getController();
                                controller.startInitialLoading(Main.APP_ID, Main.vkCode, Config.CLIENT_SECRET);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
        );

        StackPane root = new StackPane();
        root.getChildren().add(view);
        primaryStage.setScene(new Scene(root, 660, 380));
        primaryStage.show();
    }
}
