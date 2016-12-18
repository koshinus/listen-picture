package gui;

import com.listen_picture.Main;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import com.listen_picture.Main;

public class LoginController {
    public WebView view;

    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String LOGIN_SUCCESS_PAGE = "blank.html#", LOGIN_FAILURE_PAGE = "blank.html#error";

    // почему-то при вызове отсюда получаю ошибку от вк

    public void initialize() {
        String url = "https://oauth.vk.com/authorize?client_id=" + Main.APP_ID + "&display=page&" +
                "redirect_uri=" + REDIRECT_URL + "&scope=audio&response_type=code&v=5.60";

        final WebEngine engine = view.getEngine();
        engine.load(url);
        engine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        String Url = engine.getLocation();
                        if (Url.contains(LOGIN_FAILURE_PAGE)) {
                            System.out.println(url);
                        } else if (Url.contains(LOGIN_SUCCESS_PAGE)) {
                            Main.vkCode = Url.substring(Url.indexOf(LOGIN_SUCCESS_PAGE) + LOGIN_SUCCESS_PAGE.length() + 5);
                        }
                    }
                }
        );
    }
}
