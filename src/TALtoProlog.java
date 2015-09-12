import gui.Main;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Start the GUI from here, possibly interface for arguments in the future
 * 
 * @author Marc van Zee (marcvanzee@gmail.com) - Linköping University
 *
 */
public class TALtoProlog
{
	public static void main(String args[])
	{
		TALtoProlog app = new TALtoProlog();
		app.go();
	}
	
	public void go() {
		loadSwtJar();
		new Main();
	}
	
	private void loadSwtJar() {
		String swtFileName = "";
		
	    try {
	        String osName = System.getProperty("os.name").toLowerCase();
	        String osArch = System.getProperty("os.arch").toLowerCase();
	        URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
	        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
	        addUrlMethod.setAccessible(true);

	        String swtFileNameOsPart = 
	            osName.contains("win") ? "win32" :
	            osName.contains("mac") ? "macosx" :
	            osName.contains("linux") || osName.contains("nix") ? "linux_gtk" :
	            ""; // throw new RuntimeException("Unknown OS name: "+osName)

	        String swtFileNameArchPart = osArch.contains("64") ? "x64" : "x86";
	        swtFileName = "swt_"+swtFileNameOsPart+"_"+swtFileNameArchPart+".jar";
	        System.out.print("filename: " + swtFileName);
	        URL swtFileUrl = new URL("rsrc:"+swtFileName); // I am using Jar-in-Jar class loader which understands this URL; adjust accordingly if you don't
	        addUrlMethod.invoke(classLoader, swtFileUrl);
	    }
	    catch(Exception e) {
	        System.out.println("Unable to add the swt jar to the class path: "+swtFileName);
	        e.printStackTrace();
	    }
	}
}