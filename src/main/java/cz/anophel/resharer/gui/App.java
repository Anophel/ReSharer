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
 * JavaFX App
 */
public class App extends Application {

	private static Scene scene;
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

	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
		stage.sizeToScene();
	}

	static void showModal(String fxml) throws IOException {
		Stage dialog = new Stage();
		dialog.initOwner(stage);
		dialog.initModality(Modality.APPLICATION_MODAL); 
		dialog.setScene(new Scene(loadFXML(fxml)));
		dialog.showAndWait();
	}
	
	static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	static Image loadImage(String name) throws IOException {
		return new Image(App.class.getResource(name).toString());
	}

	static Image loadImage(String name, double requestedWidth, double requestedHeight, boolean preserveRatio,
			boolean smooth) throws IOException {
		return new Image(App.class.getResource(name).toString(), requestedWidth, requestedHeight, preserveRatio,
				smooth);
	}

	public static void main(String[] args) {
		launch();
	}

}