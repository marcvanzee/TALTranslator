package gui.listeners;

import gui.GUIHelper;
import gui.Main;
import gui.Project;
import gui.ProjectManager;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

public class OpenFileListener implements SelectionListener 
{
	Main main;
	Shell shell;
	ProjectManager projectManager;
	
	public OpenFileListener(final Main main, final Shell shell, final ProjectManager projectManager) {
		this.main = main;
		this.shell = shell;
		this.projectManager = projectManager;
	}
	
	public void widgetSelected(SelectionEvent event) 
	{
		Project project = GUIHelper.openFile(shell);
		if (project != null) main.openProject(project);
		
	}

	public void widgetDefaultSelected(SelectionEvent event) {}
}