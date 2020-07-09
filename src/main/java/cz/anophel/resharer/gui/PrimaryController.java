package cz.anophel.resharer.gui;

import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Controller of first page of application.
 * 
 * @author Patrik Vesely
 *
 */
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
