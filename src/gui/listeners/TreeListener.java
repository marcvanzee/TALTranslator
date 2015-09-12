package gui.listeners;

import gui.Main;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import tal.TALConstants;

public class TreeListener implements Listener {
	final String projectName;
	final Main main;
	final Tree tree;
	
	public TreeListener(Main main, Tree tree, String projectName) {
		this.projectName = projectName;
		this.main = main;
		this.tree = tree;
	}
	
	public void handleEvent(Event event)
	{
		Point point = new Point(event.x, event.y);
		TreeItem item = tree.getItem(point);
		if (item != null && TALConstants.getNarrativeDescriptions().contains(item.getText())) 
			// we have a file that we can edit, send the identifier back to main so that it will
			// be opened there
			main.openItem(projectName, item.getText());
	}
}
