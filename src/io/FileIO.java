package io;

import exceptions.FileParseException;
import gui.Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import tal.Narrative;
import tal.NarrativeManager;
import tal.TALConstants;

public class FileIO {
	public static void save(Project project, String filePath) throws IOException {
		HashMap<Integer, Boolean> constrMap = project.getConstraintsManager().getMap();
		
		// put the whole project in one string
		String str = start(IOConstants.PROJECT) + project.getName() + IOConstants.EOL + stop(IOConstants.PROJECT);
		
		// the general constraints
		str += start(IOConstants.GENERAL_CONSTR);
		int c = 0;
		for (Integer constr : constrMap.keySet()) {
			if (constrMap.get(constr)) { str += alloc(constr,"true"); c++; }				
		}
		str += stop(IOConstants.GENERAL_CONSTR);
		
		if (c == 0) str = "";
		
		// the alphabet
		str += start(IOConstants.ALPHABET) + project.getAlphabet().getStr() + IOConstants.EOL + stop(IOConstants.ALPHABET);
		
		// the narrative
		str += narrativeToStr(project.getNarrative());
		
		writeToFile(filePath, str);
	}
	
	
	/**
	 * Opens a file and puts it in a new Project which is returned
	 * If it fails, an exception is thrown
	 * 
	 * @param narrative
	 * @param filePath
	 * @return
	 */
	public static Project parse(String filePath) throws FileParseException {
		if (filePath == null) throw new FileParseException("Filename empty");
		
		String str = filetoStr(filePath);
		if (str == null) throw new FileParseException("Reading from file " + filePath + " failed");
				
		String file[] = str.split(IOConstants.EOL);
		
		int curType = -1;
		
		Project project = null;
		NarrativeManager narrativeManager = null;
		
		for (String line : file) {
			line = line.trim();
			if (line.length() <= 0) continue;
			
			if (startBlock(line)) {
				if (curType == -1) 
				{
					if ((curType = getType(line)) == -1)
						throw new FileParseException("unknown type delimiter: " + line);
					continue;
				} else
					throw new FileParseException("found start of block while in another block: " + line);
			} else if (stopBlock(line)) {
				if (curType != -1)
				{
					int stopType = getType(line);
					if (stopType == -1 || stopType != curType)
						throw new FileParseException("found incorrect ending of block: " + line);
					curType = -1;
					continue;
				} else
					throw new FileParseException("unknown type delimiter: " + line);
			}
			
			if (TALConstants.isProject(curType))
			{
				project = new Project(line);
				narrativeManager = project.getNarrativeManager();
			} else if (project == null || narrativeManager == null)
			{
				throw new FileParseException("Undefined project: the project should be defined at the beginning of the file");
			} else if (TALConstants.isAlphabet(curType) || TALConstants.isNarrative(curType)) 
			{
				narrativeManager.concatData(curType, line);
			} else if (curType == TALConstants.GENERAL_CONSTR) 
			{
				if (!line.contains("=") || (!line.contains("true") && !line.contains("false")))
					throw new FileParseException("when parsing general constraints: " + line);
				
				String alloc[] = line.split("=");
				if (alloc.length != 2 || !alloc[0].trim().matches("\\d+"))
					throw new FileParseException("when parsing general constraints: " + line);
				
				int constr = Integer.parseInt(alloc[0].trim());
				boolean val = alloc[1].trim().equals("true");
				
				narrativeManager.getConstraintsManager().put(constr, val);
			}
			
		}
		
		return project;		
	}
	
	private static boolean startBlock(String str) { 
		return !str.contains(IOConstants.SLASH) && str.startsWith(IOConstants.LDEL) && str.endsWith(IOConstants.RDEL);
	}
	
	private static boolean stopBlock(String str) { 
		return str.startsWith(IOConstants.LDEL + IOConstants.SLASH) && str.endsWith(IOConstants.RDEL);
	}
	
	private static int getType(String line) {
		line = line.substring(startBlock(line)?1:2, line.length()-1);
		for (int type : IOConstants.types) {
			String typeStr = IOConstants.getTypeIdenifier(type);
			if (line.equals(typeStr)) return type;
		}
		
		return -1;
	}
	
	private static String filetoStr(String filePath) {
		String ret = "";
		BufferedReader br = null;
		 
		try {
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(filePath));
 
			while ((sCurrentLine = br.readLine()) != null) {
				ret += sCurrentLine + IOConstants.EOL;
			}
 
		} catch (IOException e) {
			return e.getMessage();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				return null;
			}
		}
		
		return ret;
	}
		
	private static String start(String str) {
		return IOConstants.LDEL + str + IOConstants.RDEL + IOConstants.EOL;
	}
	
	private static String stop(String str) {
		return IOConstants.LDEL + IOConstants.SLASH + str + IOConstants.RDEL + IOConstants.EOL;
	}
	
	private static String alloc(int var, String val) { return (var + "=" + val + IOConstants.EOL); }
	
	private static String line(String str) { return str + IOConstants.EOL; }
	
	private static String narrativeToStr(Narrative narrative) {		
		return  narrativeType(narrative, TALConstants.OBSERVATION) +
				narrativeType(narrative, TALConstants.ACTION_OCC) +
				narrativeType(narrative, TALConstants.ACTION_SPEC) +
				narrativeType(narrative, TALConstants.DOMAIN_CONSTR) +
				narrativeType(narrative, TALConstants.PERSISTENCE);
	}
	
	private static String narrativeType(Narrative narrative, int type) {
		String descr = IOConstants.getTypeIdenifier(type);
		String txt = narrative.getStr(type);
		
		String ret = start(descr) + line(txt) + stop(descr);
		
		return (txt.length()==0 ? "" : ret);
	}
	
	private static void writeToFile(String filePath, String str) throws IOException {
			// Create file
			FileWriter fstream = new FileWriter(filePath);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(str);
			//Close the output stream
			out.close();
	}
}
