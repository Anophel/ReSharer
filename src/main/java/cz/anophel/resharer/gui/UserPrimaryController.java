package cz.anophel.resharer.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import cz.anophel.resharer.fs.IDescriptor;
import cz.anophel.resharer.rmi.DirectoryDescriptorView;
import cz.anophel.resharer.rmi.FileDescriptorView;
import cz.anophel.resharer.rmi.IResourceProvider;
import cz.anophel.resharer.rmi.ResourceProviderFactory;
import cz.anophel.resharer.utils.ResharerException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Primary client side controller. Handles user's actions and manages
 * communication with server.
 * 
 * @author Patrik Vesely
 *
 */
public class UserPrimaryController extends AbstractFileController {

	@FXML
	private TextField ipTextField;

	/**
	 * Filechooser for selecting paths, where files should be downloaded.
	 */
	private FileChooser fileChooser;

	/**
	 * IResourceProvider from server
	 */
	private IResourceProvider server;

	public UserPrimaryController() {
		fileChooser = new FileChooser();
	}

	@Override
	@FXML
	public void initialize() {
		super.initialize();
		ls();
	}

	/**
	 * Uses ResourceProviderFactory to establish communication with server. The
	 * server is identified by address in ipTextField.
	 */
	@FXML
	private void connect() {
		try {
			server = ResourceProviderFactory.instance().getProviderFor(ipTextField.getText());
			workingDir = server.getRoot();
			parentDirs.clear();
		} catch (RemoteException e) {
			showSimpleModal(AlertType.ERROR, "Could not get the root of server", ButtonType.OK);
			e.printStackTrace();
		} catch (ResharerException e) {
			showSimpleModal(AlertType.ERROR, e.getMessage(), ButtonType.OK);
			e.printStackTrace();
		}
		ls();
	}

	/**
	 * Tries to download a file from server to given physical directory.
	 */
	@Override
	void tryOpenFile(FileDescriptorView desc) {
		if (server != null) {
			File f = fileChooser.showSaveDialog(rootVBox.getScene().getWindow());
			try (FileOutputStream fos = new FileOutputStream(f)) {
				byte[] buffer = server.getFile(desc);
				fos.write(buffer);
			} catch (RemoteException e) {
				showSimpleModal(AlertType.ERROR, "Error occured during downloading the file.", ButtonType.OK);
				e.printStackTrace();
			} catch (IOException e1) {
				showSimpleModal(AlertType.ERROR, "Error occured during saving the file.", ButtonType.OK);
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Loads directory's content from the server.
	 */
	@Override
	List<IDescriptor> ls(DirectoryDescriptorView desc) {
		try {
			if (server != null)
				return server.ls(desc);
		} catch (RemoteException e) {
			showSimpleModal(AlertType.ERROR, "Error occured loading files from directory " + desc.getName() + ".",
					ButtonType.OK);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Opens dialog window for additional informations about the job.
	 */
	@FXML
	private void runJob() {
		try {
			Stage dialog = new Stage();
			dialog.initOwner((Stage) rootVBox.getScene().getWindow());
			dialog.initModality(Modality.APPLICATION_MODAL);
			FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("userJob.fxml"));
			dialog.setScene(new Scene(fxmlLoader.load()));

			UserJobController ctrl = (UserJobController) fxmlLoader.getController();

			dialog.showAndWait();

			if (ctrl.isSuccess()) {
				try {
					server.startRemoteJob(ctrl.getCp(), ctrl.getUrl(), ctrl.getMainClass(), ctrl.getOutput());
				} catch (RemoteException e) {
					showSimpleModal(AlertType.ERROR, "Error while starting job.\nmsg: " + e.getMessage(), ButtonType.OK);
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			showSimpleModal(AlertType.ERROR, "Error while loading window.", ButtonType.OK);
			e.printStackTrace();
		}
	}

}
