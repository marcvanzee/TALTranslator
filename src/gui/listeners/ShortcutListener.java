package gui.listeners;

import gui.GUIHelper;
import gui.Main;
import gui.Project;
import gui.ProjectManager;
import io.FileIO;
import io.IO;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Responses to the different shortcuts:
 * 
 * ctrl+s     saves the Project that belongs to the current open file
 * ctrl+o     opens the "open file" dialog
 * ctrl+w     closes current item
 *     
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class ShortcutListener implements Listener
{
	Main main;
	ProjectManager projectManager;
	CTabFolder folder;
	
	Shell shell;
	
	public ShortcutListener(Main main, Shell shell, ProjectManager projectManager, CTabFolder folder) { 
		this.shell = shell;
		this.main = main;
		this.projectManager = projectManager;
		this.folder = folder;
	}
	
	public void handleEvent(Event e) 
	{
		if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
			if (e.keyCode == 's')
				main.saveProject();
			if (e.keyCode == 'o') {
				Project project = GUIHelper.openFile(shell);
				if (project != null) main.openProject(project);
			}
			if (e.keyCode == 'w') {
				CTabItem item = folder.getSelection();
				if (item == null) return;
				
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
						} catch (IOException ex) {
							IO.guiErr("Error saving file: " + ex.getMessage());
							return;
						}			
					}
				}
				item.dispose();
			}
        }
	}
}
