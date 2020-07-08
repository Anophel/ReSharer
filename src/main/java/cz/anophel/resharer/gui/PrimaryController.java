package cz.anophel.resharer.gui;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToUser() throws IOException {
        App.setRoot("userPrimary");
    }

    @FXML
    private void switchToServer() throws IOException {
        App.setRoot("serverPrimary");
    }
}
