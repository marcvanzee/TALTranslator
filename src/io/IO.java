package io;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import parser.ParseException;
import parser.TALParser;
import parser.syntaxtree.Node;
import parser.visitor.TreeDumper;

/**
 * All the input and output is handled here. In general we can use three types of messages to inform the user:
 * - Gui:		calling gui(String) will send a message to the gui element that is set. 
 * - Printing: 	by calling the print(String) method, a message is sent to the debugging output.
 * - Error: 	by calling error(String), a message is sent to the error output stream. Usually this is simply the console.
 */
public class IO {			
	private static Text guiOut, guiErr;
	
	private static ByteArrayOutputStream baos;
	private static TreeDumper dumper;
	
	private static CTabItem tabOut, tabErr;
	
	private static CTabFolder tabFolder;
	
	/**
	 * Convenience method because System.out.println is a lot of typing
	 * 
	 * @param o The object to print
	 */
	public static void print(Object o) {	System.out.println(o);	}
	
	public static void print1(Object o) {	System.out.print(o);	}
	
	/**
	 * Print an error message to the console (usually displayed in red)
	 * 
	 * @param o The object to print
	 */
	public static void error(Object o) {	System.err.println(o); }
	
	/**
	 * Print a message to a Text instance in a GUI.
	 * This Text instance needs to be set first using setGuiOutput(Text)
	 * 
	 * @param o The object to print
	 */
	public static void guiErr(Object o)   {	guiErr1(o.toString() + "\n"); } 
	
	/**
	 * Print a message without a leading carriage return to a Text instance in a GUI.
	 * This Text instance needs to be set first using setGuiOutput(Text)
	 * 
	 * @param o The object to print
	 */
	public static void guiErr1(Object o)  {	if (guiErr == null) 	error("Error when printing to gui: No gui output set to print to!");
											else					{ guiErr.append(o.toString());	setFocus(tabErr); } 	}

	/**
	 * Print a message to a Text instance in a GUI.
	 * This Text instance needs to be set first using setGuiOutput(Text)
	 * 
	 * @param o The object to print
	 */
	public static void gui(Object o)   {	gui1(o.toString() + "\n"); } 
	
	/**
	 * Print a message without a leading carriage return to a Text instance in a GUI.
	 * This Text instance needs to be set first using setGuiOutput(Text)
	 * 
	 * @param o The object to print
	 */
	public static void gui1(Object o)  {	if (guiOut == null) 	error("Error when printing to gui: No gui output set to print to!");
											else					guiOut.append(o.toString()); setFocus(tabOut);	}
	
	
	public static void alert(Shell sh, String title, String errorMsg) { MessageDialog.openError(sh, title + " error", "Error: " + errorMsg); }
	
	/**
	 * Set the Text instance in a GUI that will be used to send message to using
	 * gui(Object) and gui1(Object)
	 * 
	 * @param txt the Text instance
	 */
	public static void setGuiOut(CTabItem tab, Text txt) {
		tabOut = tab;
		guiOut = txt;
	}
	
	/**
	 * Set the Text instance in a GUI that will be used to send error messages to using
	 * guiErr(Object) and guiErr1(Object)
	 * 
	 * @param txt the Text instance
	 */
	public static void setGuiErr(CTabItem tab, Text txt) {
		tabErr = tab;
		guiErr = txt;
	}
	
	/**
	 * Set the focus on the tab, mostly used when a string has been added to it so the user can read it
	 * 
	 * @param node
	 * @return
	 */
	public static void setFocus(CTabItem tab) { tabFolder.setSelection(tab); }
	
	/**
	 * Set the folder which contains tab items that can print messages
	 * This folder can control the focus (which tab is shown to the user)
	 * 
	 * @param node
	 * @return
	 */
	public static void setTabFolder(CTabFolder folder) { tabFolder = folder; } 
	
	
	public static String treeToStr(Node node)
	{
		baos = new ByteArrayOutputStream();
		dumper = new TreeDumper(baos);
		dumper.startAtNextToken();
		node.accept(dumper);
		
		return baos.toString();
		
	}
	
	public static String treeToStr(Node node, int i)
	{
		baos = new ByteArrayOutputStream();
		dumper = new TreeDumper(baos);
		dumper.skipTokens(i);
		dumper.startAtNextToken();
		node.accept(dumper);
		
		return baos.toString();
	}
	
	/**
	 * Returns the tree of a string. If the input are multiple formulas
	 * (separated by a ";"),  then multiple_formulas should be true.
	 * Otherwise, this string is interpreted as one line (a single formula).
	 * 
	 * @param str
	 * @param multiple_formulas
	 * @return
	 * @throws ParseException 
	 */
	public static Node strToTree(String str) throws ParseException
	{
		TALParser.ReInit(new StringReader(str));
		
		return TALParser.one_line();
	}
}
