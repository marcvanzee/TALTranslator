package io;

import tal.TALConstants;

public class IOConstants 
{
	public static final String LDEL = "<";
	public static final String RDEL = ">";
	public static final String SLASH = "/";
	public static final String EOL = System.getProperty("line.separator", "\n");
	
	public static final String PROJECT = "PROJECT";
	public static final String GENERAL_CONSTR = "GENERAL_CONSTR";
	public static final String ALPHABET = "ALPHABET";
	public static final String OBSERVATIONS = "OBSERVATIONS";
	public static final String ACTION_OCC = "ACTION_OCC";
	public static final String ACTION_SPEC = "ACTION_SPEC";
	public static final String DOM_CONSTR = "DOM_CONSTR";
	public static final String PERSISTENCE = "PERSISTENCE";
	public static final String DEP_CONSTR = "DEP_CONSTR";
	
	public static final int[] types = { 
		TALConstants.PROJECT,
		TALConstants.GENERAL_CONSTR, 
		TALConstants.ALPHABET, 
		TALConstants.OBSERVATION,
		TALConstants.ACTION_OCC, 
		TALConstants.ACTION_SPEC, 
		TALConstants.DOMAIN_CONSTR, 
		TALConstants.PERSISTENCE, 
		TALConstants.DEP_CONSTR };
	
	public static String getTypeIdenifier(int type) {
		switch (type) {
		case TALConstants.PROJECT: return PROJECT;
		case TALConstants.GENERAL_CONSTR: return GENERAL_CONSTR;
		case TALConstants.OBSERVATION: return OBSERVATIONS;
		case TALConstants.ACTION_OCC: return ACTION_OCC;
		case TALConstants.ACTION_SPEC: return ACTION_SPEC;
		case TALConstants.DOMAIN_CONSTR: return DOM_CONSTR;
		case TALConstants.PERSISTENCE: return PERSISTENCE;
		case TALConstants.DEP_CONSTR: return DEP_CONSTR;
		case TALConstants.ALPHABET: return ALPHABET;
		}
		
		return "";
	}
}
