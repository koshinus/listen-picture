package gui;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class MainController {
    public Label fuckIt;

    public void sayFuckIt(ActionEvent actionEvent) {
        fuckIt.setText("Hello, brave new world!");
    }
}
