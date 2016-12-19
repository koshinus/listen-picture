package com.listen_picture;

import javafx.application.Application;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import gui.MainController;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.util.Collections;

public class Gui extends Application {

    public static void main(String[] args) throws Exception { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Listen Picture");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/main.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            MainController controller = fxmlLoader.<MainController>getController();
            controller.initialize(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            String[] urls = {
                    "http://www.hubharp.com/web_sound/BachGavotteShort.mp3",
                    "https://promakh.ru/www/yellow-submarine/02_Only_A_Northern_Song.mp3",
                    "https://promakh.ru/www/yellow-submarine/03_All_Together_Now.mp3",
                    "https://promakh.ru/www/yellow-submarine/04_Hey_Bulldog.mp3"
            };

            ArrayList<String> ar = new ArrayList<>(Arrays.asList(urls));
            Collections.shuffle(ar);

            controller.startInitialLoading(ar);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}