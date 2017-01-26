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
            Parent root = fxmlLoader.load();
            MainController controller = fxmlLoader.getController();
            controller.initialize(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

//            String[] urls = {
//                    "http://mp3.promakh.ru/www/yellow-submarine/01_Yellow_Submarine.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/02_Only_A_Northern_Song.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/03_All_Together_Now.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/04_Hey_Bulldog.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/05 - It's All To Much.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/06 - All You Need Is Love.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/07 - Pepperland.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/08 - Sea of Time.mp3",
//                    "http://mp3.promakh.ru/www/yellow-submarine/09 - Sea of Holes.mp3",
//                    "http://www.hubharp.com/web_sound/BachGavotteShort.mp3",
//            "https://api.soundcloud.com/tracks/304582531/stream?client_id=dzKpRvB2UoL21eGOR2zbjwpmjwskebGR",
//            "https://api.soundcloud.com/tracks/304582529/stream?client_id=dzKpRvB2UoL21eGOR2zbjwpmjwskebGR",
//            "https://api.soundcloud.com/tracks/304582528/stream?client_id=dzKpRvB2UoL21eGOR2zbjwpmjwskebGR",
//            "https://api.soundcloud.com/tracks/304582527/stream?client_id=dzKpRvB2UoL21eGOR2zbjwpmjwskebGR",
//            };

            ArrayList<String> ar = SoundCloudLoader.getTracks();
            //ArrayList<String> ar = new ArrayList<>(Arrays.asList(urls));
            Collections.shuffle(ar);

            controller.startInitialLoading(ar);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}