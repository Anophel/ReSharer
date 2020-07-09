package cz.anophel.resharer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for modal window, where user specifies
 * information about a custom job.
 * 
 * @author Patrik Vesely
 *
 */
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

	/**
	 * URL of remote worker server.
	 */
	private String url;

	/**
	 * ClassPath of the job.
	 */
	private String cp;

	/**
	 * Main class of the job.
	 */
	private String mainClass;

	/**
	 * Name of the output file.
	 */
	private String output;

	/**
	 * Indicator, whether user clicked on Start.
	 */
	private boolean success = false;

	/**
	 * Sets all the values needed to start the job.
	 */
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
