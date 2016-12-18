package gui;

import com.listen_picture.Main;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.concurrent.Worker.State;

import com.listen_picture.Main;
import javafx.stage.Stage;

public class LoginController {
    public WebView view;

    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String LOGIN_SUCCESS_PAGE = "blank.html#", LOGIN_FAILURE_PAGE = "blank.html#error";
    private Stage primaryStage;
    private String vkCode, clientSecret, url;
    private Integer appId;

    public void initialize(Stage primaryStage, Integer appId, String vkCode, String clientSecret) {
        this.primaryStage = primaryStage;
        this.appId = appId;
        this.vkCode = vkCode;
        this.clientSecret = clientSecret;
        this.url = "https://oauth.vk.com/authorize?client_id=" + Main.APP_ID + "&display=page&" +
                "redirect_uri=" + REDIRECT_URL + "&scope=audio&response_type=code&v=5.60";
    }

    public void start() {
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
                        this.vkCode = Main.vkCode;
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/main.fxml"));
                            Parent root = (Parent)fxmlLoader.load();
                            primaryStage.setScene(new Scene(root, 660, 380));
                            MainController controller = fxmlLoader.<MainController>getController();
                            controller.initialize(primaryStage, appId, vkCode, clientSecret);
                            controller.startInitialLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        );
    }
}
