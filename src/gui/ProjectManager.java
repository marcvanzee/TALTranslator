package gui;

import io.IO;

import java.util.LinkedList;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

/**
 * TODO: keep track of which project is currently open so that we save the correct ones
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class ProjectManager
{
	private final TreeManager tManager;
	private LinkedList<Project> projects = new LinkedList<Project>();
	
	Main main;
	Display display;
	
	public ProjectManager(Main main, Tree tree, Display display) 
	{
		this.main = main;
		tManager = new TreeManager(main, tree, display);
		this.display = display;
	}
	
	public void add(String projectName) 
	{
		tManager.add(projectName);
		projects.add(new Project(projectName));		
	}
	
	/**
	 * Add a project that is already parsed
	 * 
	 * @param project
	 */
	public void add(Project project) 
	{
		tManager.add(project.getName());
		projects.add(project);		
	}
	
	/**
	 * We use the tooltip text to identify a project, because it is always of the
	 * form "Project:Filename", so we can retrieve the project by taking the first half
	 * 
	 * @param toolTip
	 * @return
	 */
	public Project getItemProject(String toolTip) {
		String project = getItemProjectStr(toolTip);
		for (Project p : projects) {
			if (p.getName().equals(project))
				return p;
		}
		
		return null;
	}
	
	public String getItemProjectStr(String toolTip) { return toolTip.split(":")[0]; }
	
	public String getItemData(String project, String item) {
		return getProject(project).getData(item);
	}
	
	public int size() { 
		return projects.size(); 
	}
	
	public boolean contains(String projectName) { 
		for (Project p : projects)
			if (p.getName().equals(projectName)) return true;
		return false;
	}
	
	/**
	 * Update the text of an item in the project.
	 * We don't want a Project object as first argument because we want to compare it
	 * with 
	 * @param project
	 * @param itemName
	 * @param text
	 */
	public void update(Project project, String itemName, String text) 
	{
		project.setData(itemName, text);
	}
	
	public Project getProject(String project) {
		for (Project p : projects) {
			if (p.getName().equals(project))
					return p;
		}
		
		return null;
	}
	
	/**
	 * Validate the current project, error messages are handled during
	 * the validation process
	 * 
	 * @param project
	 */
	public void validate(Project project) {
		project.validate();
	}
}
