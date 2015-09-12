package gui;

import gui.listeners.CloseFileListener;
import gui.listeners.InputListener;
import gui.listeners.OpenFileListener;
import gui.listeners.ShortcutListener;
import io.FileIO;
import io.IO;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;


/**
 * Genoeg werk aan de winkel vriend!
 * Er gaat wat dingen fout. De parser is nu gewoon best scheef geimplementeerd omdat je eerst
 * een hele andere opzet had. Ik denk dat je gewoon even rustig moet nadenken hoe het nu het
 * beste kan, en niet te gehaast werken.
 * 
 * Misschien is het dus een goed idee om de visitor even te negeren, en te kijken hoe je de FOL
 * parser snel kan aanroepen. Zo is het het makkelijkst (en verder moet je ook niet gaan):
 * 
 * STAP #1
 * - elke 0.5 seconde wordt de FOL parser aangeroepen op het project waar je nu mee bezig bent
 * - als er parse errors zijn dan print hij deze
 *
 * STAP #2
 * - indentation fixen in de text input
 * - syntax hightlighting, verschillende kleuren voor
 * 		> FOR/EXISTS
 * 		> AND/OR/NOT/IMPLIES/EQUIV
 * 		> VARIABLE
 * 		> NUMBER
 * 		> CHANGE CONTEXT ANNOTATION
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class Main 
{
	private static final boolean SMALL = false;
	
	private final Shell shlTaltoprolog = new Shell();
	CTabItem tabErr, tabOut;
	Display display = Display.getDefault();
	Text guiOut, guiErr;
	
	CTabFolder tabFiles, tabFolder;
	
	Tree tree;
	ProjectManager projectManager;
	
	HashMap<CTabItem, StyledText> tabToTxt = new HashMap<CTabItem, StyledText>();

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		
		shlTaltoprolog.setLocation(Positioning.getCenter(display, shlTaltoprolog.getSize()));
		
		IO.setTabFolder(tabFolder);
		IO.setGuiOut(tabOut, guiOut);
		IO.setGuiErr(tabErr, guiErr);
		
		shlTaltoprolog.open();
		shlTaltoprolog.layout();
	
		while (!shlTaltoprolog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlTaltoprolog.setText("TALtoProlog BETA v0.2");
		if (SMALL) 	shlTaltoprolog.setSize(707, 588);
		else		shlTaltoprolog.setSize(Positioning.getBounds(display));
		shlTaltoprolog.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ViewForm viewForm = new ViewForm(shlTaltoprolog, SWT.NONE); // toolbar and workbench
	
		ToolBar toolBar = new ToolBar(viewForm, SWT.BORDER | SWT.RIGHT);
		viewForm.setTopLeft(toolBar);
				
		ToolItem toolbarNew = new ToolItem(toolBar, SWT.FLAT);
		toolbarNew.setToolTipText("New Narrative (Ctrl + N)");
		toolbarNew.setImage(SWTResourceManager.getImage(Main.class, "/resources/new_new.png"));
		
		ToolItem toolbarSave = new ToolItem(toolBar, SWT.NONE);
		toolbarSave.setToolTipText("Save Narrative (Ctrl + S)");
		toolbarSave.setImage(SWTResourceManager.getImage(Main.class, "/resources/save_new.png"));
		
		ToolItem toolbarExecute = new ToolItem(toolBar, SWT.NONE);
		toolbarExecute.setToolTipText("Execute Narrative (Ctrl + E)");
		toolbarExecute.setImage(SWTResourceManager.getImage(Main.class, "/resources/compile_new.png"));
				
		SashForm sashForm = new SashForm(viewForm, SWT.VERTICAL); // main screen and console
		viewForm.setContent(sashForm);
		
		SashForm sashForm_1 = new SashForm(sashForm, SWT.NONE); // tree and editor
				
		tree = new Tree(sashForm_1, SWT.BORDER);
		projectManager = new ProjectManager(this, tree, display);
		  
		tabFiles = new CTabFolder(sashForm_1, SWT.BORDER);
		tabFiles.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFiles.setSimple(false);
		sashForm_1.setWeights(new int[] {202, 486});
		
		tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSimple(false);
		
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		tabOut = new CTabItem(tabFolder, SWT.CLOSE);
		tabOut.setShowClose(false);
		tabOut.setImage(SWTResourceManager.getImage(Main.class, "/resources/console_new.png"));
		tabOut.setText("Console");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		tabOut.setControl(composite);
		
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		
		guiOut = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		guiOut.setFont(SWTResourceManager.getFont("Courier New", 8, SWT.NORMAL));
		guiOut.setText("Welcome to TALtoProlog, my friend! \r\n---\r\nYou can start by creating a new file or opening an existing one.\r\n");
		guiOut.setBackground(white);
		
		tabErr = new CTabItem(tabFolder, SWT.CLOSE);
		tabErr.setShowClose(false);
		tabErr.setImage(SWTResourceManager.getImage(Main.class, "/resources/problems_new.png"));
		tabErr.setText("Problems");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		composite_1.setLayout(new FillLayout());
		
		tabErr.setControl(composite_1);
		
		tabFolder.setSelection(tabOut);
		
		guiErr = new Text(composite_1, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		guiErr.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		guiErr.setFont(SWTResourceManager.getFont("Courier New", 8, SWT.NORMAL));
		guiErr.setBackground(white);
		
		sashForm.setWeights(new int[] {254, 78});
				
		Menu menu = new Menu(shlTaltoprolog, SWT.BAR);
		shlTaltoprolog.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem menuNew = new MenuItem(menu_1, SWT.NONE);
		menuNew.setText("New Project\t(Ctrl + N)");
		
		MenuItem menuOpen = new MenuItem(menu_1, SWT.NONE);
		menuOpen.setText("Open File...\t(Ctrl + O)");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem menuSaveAs = new MenuItem(menu_1, SWT.NONE);
		menuSaveAs.setText("Save\t(Ctrl + S)");
		
		MenuItem mntmSaveAs = new MenuItem(menu_1, SWT.NONE);
		mntmSaveAs.setText("Save As...");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem menuExit = new MenuItem(menu_1, SWT.NONE);
		menuExit.setText("Exit");
		
		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("Edit");
		
		Menu menu_5 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_5);
		
		MenuItem menuConstraints = new MenuItem(menu_5, SWT.NONE);
		menuConstraints.setText("Constraints");
		
		MenuItem mntmRun = new MenuItem(menu, SWT.CASCADE);
		mntmRun.setText("Run");
		
		Menu menu_2 = new Menu(mntmRun);
		mntmRun.setMenu(menu_2);
		
		MenuItem menuCompile = new MenuItem(menu_2, SWT.NONE);
		menuCompile.setText("Compile Narrative\t(F2)");

		// menubar listeners
		menuNew.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { createProject(); } });
		menuOpen.addSelectionListener(new OpenFileListener(this, shlTaltoprolog, projectManager));
		
		// toolbar listeners
		toolbarNew.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { createProject(); } });
		toolbarExecute.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent event) { validateProject(); } });
		
		// other listeners				
		tabFiles.addCTabFolder2Listener(new CloseFileListener(shlTaltoprolog, projectManager));
		display.addFilter(SWT.KeyDown, new ShortcutListener(this, shlTaltoprolog, projectManager, tabFiles));
		
		
	}
	
	/**
	 * Create a new project from scratch. This is called by a listener for the menu item File>New
	 * The method will ask the user to pick a name for the project and send the name to the
	 * project manager who will make sure that the new tree will be visualized.
	 * 
	 */
	private void createProject()
	{
	    String name = GUIHelper.newProject(shlTaltoprolog, projectManager);
	    if (name == null) return;
	    
	    projectManager.add(name);
	    
		IO.gui("Created new project " + name + " with narrative specification files");
	}
	
	/**
	 * Validate the project that currently active, meaning the project that the item that it
	 * currently being displayed in the itemTabs.
	 * 
	 */
	private void validateProject()
	{
		Project project;
		if ((project = getCurrentProject()) == null) return;
		
		saveProject();
		projectManager.validate(project);
	}
	
	/**
	 * Open a project that is already parsed, this is called by OpenFileListener which has already
	 * loaded a complete Project. All we have to do here is send it to the project manager and 
	 * inform the user through the GUI
	 * 
	 * @param project
	 */
	public void openProject(Project project) 
	{		
		if (!projectManager.contains(project.getName())) {
			projectManager.add(project);
			IO.gui("Opened project " + project.getName());
		} else {
			IO.guiErr("A project with the name " + project.getName() + " is already openend.");
		}
	}
	
	/**
	 * Open an item in the editor. This is called by the TreeListener, when a user double-clicks on an item.
	 * If the item is a file then it will call this method. This method will create a new CTabItem containing
	 * a text field with the text belonging to that item.
	 * 
	 * @param project
	 * @param filename
	 */
	public void openItem(String project, final String filename) 
	{
		if (opened(project, filename)) return;
		
		final CTabItem tabItem = new CTabItem(tabFiles, SWT.CLOSE);
		tabItem.setText(filename);
		tabItem.setToolTipText(project + ":" + filename);
				
		Composite comp = new Composite(tabFiles, SWT.NONE);
		comp.setLayout(new FillLayout());
		tabItem.setControl(comp);
		tabItem.setImage(SWTResourceManager.getImage(Main.class, "/resources/file.png"));
		
		final StyledText styledText = new StyledText(comp, SWT.BORDER);
		styledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		
		String text = projectManager.getItemData(project, filename);
		
		tabFiles.setSelection(tabItem);
		styledText.setFocus();
		
		styledText.setText(text);		
		styledText.addModifyListener(new InputListener(styledText, tabItem));
		
		
		tabToTxt.put(tabItem, styledText);
	}
	
	/**
	 * This is called by the ShortcutListener, when the user presses ctrl+s
	 * 
	 * When a file is open that belongs to a project, save it and also save all other open
	 * files belonging to this project. If the user has no yet saved the file before, present
	 * a dialog (handled in GUIHelper.saveFile())
	 * 
	 */
	public void saveProject() {
		CTabItem selTab = tabFiles.getSelection();
		String itemname = selTab.getText();
		String tooltip = selTab.getToolTipText();
		
		if (GUIConstants.unsaved(itemname)) 
		{
			Project project = projectManager.getItemProject(tooltip);
			
			// locate the place where the project needs to be saved
			// we do this first because it will possibly present the user
			// with a save as dialog which he can skip out of, meaning
			// that we should cancel saving
			String filename = GUIHelper.saveFile(shlTaltoprolog, project);
						
			if (filename == null) return;
			
			// update the string representation of all open, 
			// unsaved items of this project. 
			LinkedList<CTabItem> items = new LinkedList<CTabItem>();
						
			// store all text in the unsaved tabs of this project in the narrative
			for (CTabItem item : tabFiles.getItems()) {
				
				if (GUIConstants.unsaved(item.getText()) && 
								projectManager.getItemProjectStr(item.getToolTipText()).equals(project.getName())) {
					
					// remove the "*" as prefix
					item.setText(item.getText().substring(1));	
					
					// update
					projectManager.update(project, item.getText(), tabToTxt.get(item).getText());
				}
			}
						
			// try saving the file
			try {
				FileIO.save(project, filename);
			} catch (IOException e) {
				IO.guiErr("Error saving data to file: " + e.getMessage());
				return;
			}
		}
	}
	
	private boolean opened(String project, String filename) {
		for (CTabItem item : tabFiles.getItems())
			if (item.getToolTipText().equals(project+":"+filename)) return true;
		return false;
	}
	
	private Project getCurrentProject() {
		CTabItem selTab = tabFiles.getSelection();
		if (selTab == null) return null;
		
		String tooltip = selTab.getToolTipText();
		
		return projectManager.getItemProject(tooltip);
	}
}
