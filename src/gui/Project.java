package gui;

import io.IO;
import tal.Alphabet;
import tal.Narrative;
import tal.NarrativeManager;
import constraints.ConstraintsManager;

/**
 * A project contains a narrative, constraints and possibly a compiled on and a project file of it
 * So you might wonder now why this project is here, well that is because if you later add
 * reified narratives and prolog files, they can be put here as well.
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class Project 
{
	private String name;
	private NarrativeManager narrativeManager;
	private ConstraintsManager constraints;
	private String filename;
	
	public Project(String projectName) 
	{
		this.name = projectName;
		narrativeManager = new NarrativeManager();
		constraints = new ConstraintsManager();
		filename = null;
	}
	
	// GETTERS
	
	public ConstraintsManager getConstraintsManager() { return constraints; }
	public Alphabet getAlphabet() { return narrativeManager.getAlphabet(); }
	public Narrative getNarrative() { return narrativeManager.getNarrative(); }
	public NarrativeManager getNarrativeManager() { return narrativeManager; }
	public String getName() { return name; }
	public String getSaveFile() { return filename; }
	
	public String getData(String item) {
		String ret =  narrativeManager.getData(item);
		return ret;
	}
	
	public void setData(String item, String txt) {
		narrativeManager.setData(item, txt);
	}
	
	// SETTERS
	
	/**
	 * Once the project has been saved, we do not have to present a "Save As..." dialog
	 * to the user again, but we simply save it automatically in the given file name
	 * 
	 * @param fileName
	 */
	public void setSaveFile(String fileName) { this.filename = fileName; }
	
	/**
	 * Validate this project
	 */
	public void validate() { narrativeManager.validate(); }
}
