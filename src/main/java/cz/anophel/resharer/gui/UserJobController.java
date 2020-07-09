package cz.anophel.resharer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserJobController {

	@FXML
	private VBox rootVBox;

	@FXML
	private TextField urlField;

	@FXML
	private TextField cpField;

	@FXML
	private TextField mainClassField;

	@FXML
	private TextField outputField;

	private String url;

	private String cp;

	private String mainClass;

	private String output;

	private boolean success = false;

	@FXML
	private void startJob() {
		url = urlField.getText();
		cp = cpField.getText();
		mainClass = mainClassField.getText();
		output = outputField.getText();
		success = true;
		((Stage) rootVBox.getScene().getWindow()).close();
	}

	@FXML
	private void close() {
		((Stage) rootVBox.getScene().getWindow()).close();
	}

	public String getUrl() {
		return url;
	}

	public String getCp() {
		return cp;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getOutput() {
		return output;
	}

	public boolean isSuccess() {
		return success;
	}

}
