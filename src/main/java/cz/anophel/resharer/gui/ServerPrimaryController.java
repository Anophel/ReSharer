package cz.anophel.resharer.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import cz.anophel.resharer.fs.FileSystem;
import cz.anophel.resharer.fs.FileSystemLoader;
import cz.anophel.resharer.fs.IDescriptor;
import cz.anophel.resharer.rmi.DirectoryDescriptorView;
import cz.anophel.resharer.rmi.FileDescriptorView;
import cz.anophel.resharer.rmi.ResourceProviderFactory;
import cz.anophel.resharer.utils.Ref;
import cz.anophel.resharer.utils.ResharerException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Primary server controller for managing file system and sharing.
 * 
 * @author Patrik Vesely
 *
 */
public class ServerPrimaryController extends AbstractFileController {

	@FXML
	private Label isSharingLabel;

	/**
	 * System GUI for selecting files and directories.
	 */
	private FileChooser fileChooser;
	private DirectoryChooser dirChooser;

	/**
	 * Virtual file system for sharing files.
	 */
	private Ref<FileSystem> fs;

	/**
	 * Indicator, if current server is sharing.
	 */
	private boolean sharing = false;

	@FXML
	public void initialize() {
		super.initialize();

		// Initialize virtual filesystem
		fs = new Ref<FileSystem>(new FileSystem());
		workingDir = fs.get().getRootView();

		// Initialize sharing system
		try {
			ResourceProviderFactory.instance().prepareStub(fs);
		} catch (ResharerException e2) {
			showSimpleModal(AlertType.ERROR, e2.getMessage(), ButtonType.OK);
			e2.printStackTrace();
		}

		// Initialize context menu
		ContextMenu descContextMenu = new ContextMenu();
		MenuItem deleteItem = new MenuItem("Delete");

		deleteItem.setOnAction(e -> removeSelectedDesc());

		descContextMenu.getItems().add(deleteItem);
		filesList.setContextMenu(descContextMenu);

		// Initialize file and directory chooser
		fileChooser = new FileChooser();
		fileChooser.setTitle("Add files to virtual file system");

		dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Add directory to virtual file system");

		// Show current directory
		ls();
	}

	/**
	 * Toggles sharing of virtual filesystem.
	 */
	@FXML
	private void toggleSharing() {
		try {
			if (!sharing) {
				ResourceProviderFactory.instance().bindStub();
			} else {
				ResourceProviderFactory.instance().unbindStub();
			}
			sharing = !sharing;
			isSharingLabel.setText(Boolean.toString(sharing));
		} catch (ResharerException e) {
			showSimpleModal(AlertType.ERROR, "Failed to start/stop sharing. " + e.getMessage(), ButtonType.OK);
			e.printStackTrace();
		}
	}

	/**
	 * Removes selected descriptors from virtual filesystem.
	 */
	private void removeSelectedDesc() {
		var descs = filesList.getSelectionModel().getSelectedItems();

		if (descs == null)
			return;

		Alert alert;
		if (descs.size() == 1) {
			alert = showSimpleModal(AlertType.CONFIRMATION,
					"File " + descs.get(0).getName() + " is going to be deleted from virtual filesystem,"
							+ " but stays on physical filesystem. Do you want to proceed?",
					ButtonType.YES, ButtonType.NO);
		} else {
			alert = showSimpleModal(AlertType.CONFIRMATION,
					descs.size() + " files are going to be deleted from virtual filesystem,"
							+ " but stay on physical filesystem. Do you want to proceed?",
					ButtonType.YES, ButtonType.NO);
		}

		if (alert.getResult() == ButtonType.YES) {
			for (var desc : descs)
				fs.get().removeDescriptor(workingDir, desc);
		}

		ls();
	}

	/**
	 * Opens a file directly with associated program.
	 */
	@Override
	void tryOpenFile(FileDescriptorView desc) {
		try {
			Desktop.getDesktop().open(fs.get().getFile(desc));
		} catch (IOException e1) {
			showSimpleModal(AlertType.ERROR, "File " + desc.getName() + " couldn't be opened!", ButtonType.OK);
		}
	}

	/**
	 * Opens dialog window for adding new file to virtual filesystem.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void addFile() throws IOException {
		List<File> list = fileChooser.showOpenMultipleDialog(rootVBox.getScene().getWindow());
		if (list != null) {
			for (var f : list)
				fs.get().addDescriptor(workingDir, f, 0);
		}
		ls();
	}

	/**
	 * Opens dialog window for adding new directory to virtual filesystem.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void addDir() throws IOException {
		File f = dirChooser.showDialog(rootVBox.getScene().getWindow());
		if (f != null) {
			fs.get().addDescriptor(workingDir, f, 10);
		}
		ls();
	}

	/**
	 * Saves virtual file system to a file.
	 */
	@FXML
	private void saveFileSystem() {
		File f = fileChooser.showSaveDialog(rootVBox.getScene().getWindow());
		if (f != null) {
			try {
				FileSystemLoader.save(fs.get(), f);
			} catch (ResharerException e) {
				showSimpleModal(AlertType.ERROR, "Could not save the virtual filesystem. " + e.getMessage(),
						ButtonType.OK);
			}
		}
	}

	/**
	 * Loads virtual file system from a file.
	 */
	@FXML
	private void loadFileSystem() {
		File f = fileChooser.showOpenDialog(rootVBox.getScene().getWindow());
		if (f != null) {
			try {
				fs.set(FileSystemLoader.load(f));
				workingDir = fs.get().getRootView();
				parentDirs.clear();
				ls();
			} catch (ResharerException e) {
				showSimpleModal(AlertType.ERROR, "Could not load the virtual filesystem. " + e.getMessage(),
						ButtonType.OK);
			}
		}
	}

	/**
	 * Creates virtual directory in virtual file system.
	 */
	@FXML
	private void createVirtDir() {
		TextInputDialog dialog = new TextInputDialog("New directory");
		dialog.setTitle("Create virtual directory");
		dialog.setHeaderText("Insert name of new virtual directory");
		dialog.setContentText("Name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			fs.get().addVirtualDir(workingDir, result.get());
			ls();
		}
	}

	/**
	 * Lists descriptors in directory desc from virtual file system.
	 */
	@Override
	List<IDescriptor> ls(DirectoryDescriptorView desc) {
		return fs.get().ls(desc);
	}

}
