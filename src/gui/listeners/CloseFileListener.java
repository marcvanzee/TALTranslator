package gui.listeners;

import gui.GUIHelper;
import gui.Project;
import gui.ProjectManager;
import io.FileIO;
import io.IO;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class CloseFileListener extends CTabFolder2Adapter 
{
	Shell shell;
	ProjectManager projectManager;
	
	public CloseFileListener(final Shell shell, final ProjectManager projectManager) {
		this.shell = shell;
		this.projectManager = projectManager;
	}
	
	public void close(CTabFolderEvent event) {
		CTabItem item = (CTabItem) event.item;
		String itemName = item.getText();
				
		if (itemName.charAt(0) == '*') {
			boolean save = GUIHelper.question(shell, itemName.substring(1) + " has been modified. Save changes?");
			
			if (save) {
				// retrieve the project we are saving
				Project project = projectManager.getItemProject(item.getToolTipText());
				
				// and the file to save to
				String filename = GUIHelper.saveFile(shell, project);

				try {
					FileIO.save(project, filename);
				} catch (IOException e) {
					IO.guiErr("Error saving file: " + e.getMessage());
					event.doit = false;
				}			
			}
		}
	}	
}
