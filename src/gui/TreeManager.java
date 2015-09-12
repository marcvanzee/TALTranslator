package gui;

import gui.listeners.TreeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import tal.TALConstants;

public class TreeManager 
{
	public static final int FOLDER_OUTER = 0;
	public static final int FOLDER_INNER = 1;
	public static final int FILE = 2;
	
	Tree tree;
	Display display;
	Main main;
	
	public TreeManager(Main main, Tree tree, Display display) {
		this.tree = tree;
		this.display = display;
		this.main = main;
	}
	
	public void add(String projectName)
	{
		TreeItem project =      addItem(tree,      display, tree.getItemCount(), projectName, FOLDER_INNER);
		TreeItem narrative = 	addItem(project,   display, 0, "Narrative Specification", FOLDER_OUTER);
		addItem(narrative, display, 0, TALConstants.getNarrativeDescription(TALConstants.ALPHABET), FILE);		
		addItem(narrative, display, 1, TALConstants.getNarrativeDescription(TALConstants.OBSERVATION), FILE);
		addItem(narrative, display, 2, TALConstants.getNarrativeDescription(TALConstants.ACTION_OCC), FILE);
		addItem(narrative, display, 3, TALConstants.getNarrativeDescription(TALConstants.ACTION_SPEC), FILE);
		addItem(narrative, display, 4, TALConstants.getNarrativeDescription(TALConstants.PERSISTENCE), FILE);
		addItem(narrative, display, 5, TALConstants.getNarrativeDescription(TALConstants.DOMAIN_CONSTR), FILE);

		tree.addListener(SWT.MouseDoubleClick, new TreeListener(main, tree, projectName));
				
		project.setExpanded(true);
		narrative.setExpanded(true);
	}
	
	public TreeItem addItem(Tree parent, Display display, int index, String name, int type) {
		TreeItem item = new TreeItem(parent, SWT.NONE, index);
		return addItem2(item, display, name, type);
	}
	
	public TreeItem addItem(TreeItem parent, Display display, int index, String name, int type) {
		TreeItem item = new TreeItem(parent, SWT.NONE, index);
		return addItem2(item, display, name, type);
	}
	
	public TreeItem addItem2(TreeItem item, Display display, String name, int type) {
		item.setText(name);
		item.setImage(new Image(display, getImage(type)));	
		
		return item;
	}
	
	public String getImage(int type) {
		switch (type) { 
		case FOLDER_OUTER: return "resources/narrative.png";
		case FOLDER_INNER: return "resources/folder.png";
		case FILE: return "resources/file.png";
		}
		
		return null;
	}
}
