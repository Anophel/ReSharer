package cz.anophel.resharer.gui;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Stack;

import cz.anophel.resharer.fs.DescriptorTypes;
import cz.anophel.resharer.fs.DirectoryDescriptor;
import cz.anophel.resharer.fs.FileSystem;
import cz.anophel.resharer.fs.IDescriptor;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class ServerPrimaryController {

	@FXML
	private VBox rootVBox;

	@FXML
	private Label currPathLabel;

	@FXML
	private TableView<IDescriptor> filesList;

	@FXML
	private TableColumn<IDescriptor, String> nameColumn;

	@FXML
	private TableColumn<IDescriptor, String> lastModifColumn;

	@FXML
	private TableColumn<IDescriptor, Long> uidColumn;
	
	/**
	 * System GUI for selecting files and directories.
	 */
	private FileChooser fileChooser;
	private DirectoryChooser dirChooser;
	
	/**
	 * UID of previous selected item for double click.
	 */
	private long previousSelectedUid = -2;

	/**
	 * Virtual file system for sharing files.
	 */
	private FileSystem fs;

	/**
	 * Current working directory.
	 */
	private DirectoryDescriptor workingDir;

	/**
	 * Stack of parent directories.
	 */
	private Stack<DirectoryDescriptor> parentDirs;

	@FXML
	public void initialize() {
		// Initialize virtual filesystem
		fs = new FileSystem();
		parentDirs = new Stack<>();
		workingDir = fs.getRoot();

		// Initialize table component
		nameColumn.setCellValueFactory(desc -> new ReadOnlyObjectWrapper<String>(desc.getValue().getName()));
		lastModifColumn.setCellValueFactory(desc -> {
			if (desc.getValue().getUid() == -1)
				return new ReadOnlyObjectWrapper<String>("");
			return new ReadOnlyObjectWrapper<String>(
					DateTimeFormatter.ISO_DATE_TIME.format(desc.getValue().getLastModif()));
		});
		uidColumn.setCellValueFactory(desc -> new ReadOnlyObjectWrapper<Long>(desc.getValue().getUid()));

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

	private void removeSelectedDesc() {
		IDescriptor desc = filesList.getSelectionModel().getSelectedItem();
		
		if (desc == null || desc.getUid() == -1)
			return;
		
		Alert alert = new Alert(AlertType.CONFIRMATION, 
				desc.getName() + " is going to be deleted from virtual filesystem, but stays on physical filesystem. Do you want to proceed?", 
				ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
		    fs.removeDescriptor(workingDir, desc);
		}
		
		ls();
	}
	
	/**
	 * List files in current directory.
	 */
	private void ls() {
		filesList.getItems().clear();
		if (parentDirs.size() > 0)
			filesList.getItems().add(new DirectoryDescriptor(-1, ".."));

		for (var desc : workingDir.ls())
			filesList.getItems().add(desc);
		
		currPathLabel.setText(parentDirs.stream().map(e -> e.getName()).reduce("/", (acc, obj) -> acc + obj + "/") + workingDir.getName() + "/");
	}

	/**
	 * Change current working directory.
	 * 
	 * @param newDir
	 */
	private void cd(DirectoryDescriptor newDir) {
		if (newDir.getUid() == -1) {
			workingDir = parentDirs.pop();
		} else {
			parentDirs.push(workingDir);
			workingDir = newDir;
		}
		
		ls();
	}

	@FXML
	private void descSelected(MouseEvent e) {
		if (e.isPrimaryButtonDown()) {
			IDescriptor desc = filesList.getSelectionModel().getSelectedItem();
			if (desc.getType() == DescriptorTypes.DIRECTORY && previousSelectedUid == desc.getUid()) {
				cd((DirectoryDescriptor) desc);
			}
			previousSelectedUid = desc.getUid();
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
				fs.addDescriptor(workingDir, f, 0);
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
			fs.addDescriptor(workingDir, f, 10);
		}
		ls();
	}

	@FXML
	private void goBack() throws IOException {
		App.setRoot("primary");
	}

}
