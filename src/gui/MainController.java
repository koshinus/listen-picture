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

    public void sayFuckIt(ActionEvent actionEvent) {
        fuckIt.setText("Hello, brave new world!");
    }

    public void startInitialLoading() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);

        UserAuthResponse authResponse = null;
        try {
            authResponse = vk.oauth()
                    .userAuthorizationCodeFlow(appId, clientSecret, "https://oauth.vk.com/blank.html", vkCode)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());

        try {
            List audios = vk.audio().get(actor).count(10).execute().getItems();
            System.out.println();

//            java.util.List users = vk.users().get(actor)
//                    .fields(UserField.VERIFIED, UserField.SEX, UserField.SCREEN_NAME)
//                    .lang(Lang.RU)
//                    .execute();

        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }
}
