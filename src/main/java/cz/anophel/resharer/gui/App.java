package cz.anophel.resharer.gui;

import java.io.IOException;

import cz.anophel.resharer.rmi.ResourceProviderFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Main JavaFx Application
 */
public class App extends Application {

	/**
	 * Scene of main stage.
	 */
	private static Scene scene;
	
	/**
	 * Main stage.
	 */
	private static Stage stage;

	@Override
	public void start(Stage stage) throws IOException {
		App.stage = stage;
		scene = new Scene(loadFXML("primary"));
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		System.out.println("Good bye!");
		ResourceProviderFactory.mischiefManaged();
		System.exit(0);
	}

	/**
	 * Loads new scene to the main stage
	 * 
	 * @param fxml
	 * @throws IOException
	 */
	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
		stage.sizeToScene();
	}

	/**
	 * Opens a modal window with scene specified in fxml.
	 * 
	 * @param fxml
	 * @throws IOException
	 */
	static void showModal(String fxml) throws IOException {
		Stage dialog = new Stage();
		dialog.initOwner(stage);
		dialog.initModality(Modality.APPLICATION_MODAL); 
		dialog.setScene(new Scene(loadFXML(fxml)));
		dialog.showAndWait();
	}
	
	/**
	 * Loads fxml from the resources.
	 * 
	 * @param fxml
	 * @return
	 * @throws IOException
	 */
	static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	/**
	 * Loads an image from the resources.
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	static Image loadImage(String name) throws IOException {
		return new Image(App.class.getResource(name).toString());
	}

	/**
	 * Loads an image from the resources with different parameters.
	 * 
	 * @param name
	 * @param requestedWidth
	 * @param requestedHeight
	 * @param preserveRatio
	 * @param smooth
	 * @return
	 * @throws IOException
	 */
	static Image loadImage(String name, double requestedWidth, double requestedHeight, boolean preserveRatio,
			boolean smooth) throws IOException {
		return new Image(App.class.getResource(name).toString(), requestedWidth, requestedHeight, preserveRatio,
				smooth);
	}

	public static void main(String[] args) {
		launch();
	}

}