package gui;

import com.listen_picture.Main;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.List;

public class MainController {
    public Label fuckIt;

    private Stage primaryStage;
    private String vkCode, clientSecret;
    private Integer appId;

    public void initialize(Stage primaryStage, Integer appId, String vkCode, String clientSecret) {
        this.primaryStage = primaryStage;
        this.appId = appId;
        this.vkCode = vkCode;
        this.clientSecret = clientSecret;
    }

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void startInitialLoading(List<String> songs) {

    }
}
