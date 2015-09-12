package gui;

import java.io.File;

import io.FileIO;
import io.IO;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import exceptions.FileParseException;

public class GUIHelper 
{
	public static String question(Shell sh, String str, String val) {
		
		IInputValidator validator = new IInputValidator() {
			public String isValid(String newText) {
				if(newText.matches("[\\w_\\- ]+")) return null;
				else return "The answer contains illigal characters";
			}
		};
		
		InputDialog dialog = new InputDialog(sh, "Question", str, val, validator);
	    if(dialog.open() != Window.OK) return null;
	    
	    return dialog.getValue();
	}
	
	public static String newProject(Shell sh, final ProjectManager projects) 
	{
		IInputValidator validator = new IInputValidator() 
		{
			public String isValid(String newText) 
			{
				if(newText.matches("[\\w_\\- ]+") || projects.contains(newText)) return null;
				else return "The answer contains illigal characters";
			}
		};
		
		InputDialog dialog = new InputDialog(sh, "Question", "Please enter a project name", "Project " + projects.size(), validator);
	    if(dialog.open() != Window.OK) return null;
	    
	    return dialog.getValue();
	}
	
	public static boolean question(Shell sh, String question)
	{
		
		 return MessageDialog.openQuestion(sh, "Question", question);
	}
	
	public static Project openFile(Shell shell) {
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Open TAL Narrative");
		fd.setFilterPath(".");
		String[] filterExt = { "*.tal", "*.*" };
		fd.setFilterExtensions(filterExt);
		
		IO.print("euhhh");
		String file = fd.open();
		
		if (file == null) return null;
		
		Project project = null;
		
		try {
			project = FileIO.parse(file);
		} catch (FileParseException e) {
			// TODO Auto-generated catch block
			IO.guiErr("Error while parsing file " + file + "\n" + e.getMessage());
		}
		
		if (project != null) project.setSaveFile(file);
		
		return project;
	}
	
	public static String saveFile(Shell shell, Project project) {
		
		String filename = null;
		
		// if it has not been saved before, present a save as dialog to the user
		if ((filename = project.getSaveFile()) == null)
		{
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
			fd.setText("Save TAL Project");
			fd.setFilterPath(".");
			String[] filterExt = { "*.tal", "*.*" };
			fd.setFilterExtensions(filterExt);
			filename = saveFile(fd);
			project.setSaveFile(filename);
		}
		
		return filename;
	}
	
	public static String saveFile(FileDialog fd) {
		String fileName = null;

		// The user has finished when one of the following happens:
		// 1) The user dismisses the dialog by pressing Cancel
		// 2) The selected file name does not exist
		// 3) The user agrees to overwrite existing file
		boolean done = false;

		while (!done) {
			// Open the File Dialog
			fileName = fd.open();
			if (fileName == null) {
				// User has cancelled, so quit and return
				done = true;
			} else {
				// User has selected a file; see if it already exists
		        File file = new File(fileName);
		        if (file.exists()) {
		        	// The file already exists; asks for confirmation
		        	MessageBox mb = new MessageBox(fd.getParent(), SWT.ICON_WARNING
		        			| SWT.YES | SWT.NO);

		        	// We really should read this string from a resource bundle
		        	mb.setMessage(fileName + " already exists. Do you want to replace it?");

		        	// If they click Yes, we're done and we drop out. If
		        	// they click No, we redisplay the File Dialog
		        	done = mb.open() == SWT.YES;
		        } else
		        	// File does not exist, so drop out
		        	done = true;
			}
		}
		return fileName;
	}
}
