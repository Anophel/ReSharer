package cz.anophel.resharer.gui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Stack;

import cz.anophel.resharer.fs.DescriptorTypes;
import cz.anophel.resharer.fs.IDescriptor;
import cz.anophel.resharer.rmi.DirectoryDescriptorView;
import cz.anophel.resharer.rmi.FileDescriptorView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Abstract GUI controller for basic handling events
 * and descriptor views.
 * 
 * @author Patrik Vesely
 *
 */
public abstract class AbstractFileController {

	@FXML
	VBox rootVBox;

	@FXML
	Label currPathLabel;

	/**
	 * Table of files.
	 */
	@FXML
	TableView<IDescriptor> filesList;

	@FXML
	TableColumn<IDescriptor, String> nameColumn;

	@FXML
	TableColumn<IDescriptor, String> lastModifColumn;

	@FXML
	TableColumn<IDescriptor, Long> uidColumn;

	@FXML
	TableColumn<IDescriptor, ImageView> statusColumn;

	@FXML
	TableColumn<IDescriptor, ImageView> typeColumn;

	int iconSize = 16;
	/**
	 * Basic icons.
	 */
	Image presentIcon;
	Image missingIcon;
	Image directoryIcon;
	Image fileIcon;

	/**
	 * UID of previous selected item for double click.
	 */
	long previousSelectedUid = -2;

	/**
	 * Current working directory.
	 */
	DirectoryDescriptorView workingDir;

	/**
	 * Stack of parent directories.
	 */
	Stack<DirectoryDescriptorView> parentDirs;

	public AbstractFileController() {
		parentDirs = new Stack<>();

		// Prepare icons
		try {
			presentIcon = App.loadImage("checkmark.png", iconSize, iconSize, true, true);
			missingIcon = App.loadImage("cancel.png", iconSize, iconSize, true, true);
			directoryIcon = App.loadImage("folder.png", iconSize, iconSize, true, true);
			fileIcon = App.loadImage("file.png", iconSize, iconSize, true, true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		// Initialize table component
		nameColumn.setCellValueFactory(desc -> new ReadOnlyObjectWrapper<String>(desc.getValue().getName()));
		lastModifColumn.setCellValueFactory(desc -> {
			if (desc.getValue().getUid() == -1)
				return new ReadOnlyObjectWrapper<String>("");
			return new ReadOnlyObjectWrapper<String>(desc.getValue().getLastModif() != null
					? DateTimeFormatter.ISO_DATE_TIME.format(desc.getValue().getLastModif())
					: "");
		});
		uidColumn.setCellValueFactory(desc -> new ReadOnlyObjectWrapper<Long>(desc.getValue().getUid()));
		statusColumn.setCellValueFactory(desc -> {
			if (desc.getValue().getType() == DescriptorTypes.FILE && !((FileDescriptorView) desc.getValue()).exists())
				return new ReadOnlyObjectWrapper<>(new ImageView(missingIcon));
			return new ReadOnlyObjectWrapper<>(new ImageView(presentIcon));
		});
		typeColumn.setCellValueFactory(desc -> {
			if (desc.getValue().getType() == DescriptorTypes.FILE)
				return new ReadOnlyObjectWrapper<>(new ImageView(fileIcon));
			return new ReadOnlyObjectWrapper<>(new ImageView(directoryIcon));
		});
		filesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	/**
	 * List files in current directory.
	 */
	@FXML
	void ls() {
		// Clear view
		filesList.getItems().clear();

		// Skip not loaded dir
		if (workingDir == null)
			return;

		// Add return folder if we are not in root
		if (parentDirs.size() > 0)
			filesList.getItems().add(new DirectoryDescriptorView(-1, ".."));

		// Show descriptors
		var files = ls(workingDir);
		if (files != null)
			for (var desc : ls(workingDir))
				filesList.getItems().add(desc);

		// Set path
		currPathLabel.setText(parentDirs.stream().map(e -> e.getName()).reduce("/", (acc, obj) -> acc + obj + "/")
				+ workingDir.getName() + "/");
	}

	/**
	 * Change current working directory.
	 * 
	 * @param newDir
	 */
	void cd(DirectoryDescriptorView newDir) {
		if (newDir.getUid() == -1) {
			workingDir = parentDirs.pop();
		} else {
			parentDirs.push(workingDir);
			workingDir = newDir;
		}

		ls();
	}

	/**
	 * Handle mouse events on list of files and directories.
	 * 
	 * @param e
	 */
	@FXML
	void descSelected(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			IDescriptor desc = filesList.getSelectionModel().getSelectedItem();

			if (desc != null) {
				if (desc.getType() == DescriptorTypes.DIRECTORY && previousSelectedUid == desc.getUid()) {
					cd((DirectoryDescriptorView) desc);
					previousSelectedUid = -2;
				} else if (desc.getType() == DescriptorTypes.FILE && previousSelectedUid == desc.getUid()) {
					tryOpenFile((FileDescriptorView) desc);
					previousSelectedUid = -2;
				}
				previousSelectedUid = desc.getUid();
			} else {
				previousSelectedUid = -2;
			}
		}
	}

	/**
	 * Tries to open a file.
	 * 
	 * @param desc
	 */
	abstract void tryOpenFile(FileDescriptorView desc);

	/**
	 * Show descriptors inside directory desc.
	 * 
	 * @param desc
	 * @return
	 */
	abstract List<IDescriptor> ls(DirectoryDescriptorView desc);
}
