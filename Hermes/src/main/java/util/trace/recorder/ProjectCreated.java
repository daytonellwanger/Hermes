package util.trace.recorder;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class ProjectCreated extends TraceableInfo{
	String fileName;
	public ProjectCreated(String aMessage, String aFileName,  Object aFinder) {
		 super(aMessage, aFinder);
		 fileName = aFileName;
	}
	public String getFileName() {
		return fileName;
	}
	
	
    public static String toString(String aFileName) {
    	return("(" + 
    				aFileName + 
    				")");
    }
    public static ProjectCreated newCase (String aMessage, String aFileName,  Object aFinder) {
//    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(LogFileCreated.class))
//	    	  EventLoggerConsole.getConsole().getMessageConsoleStream().println("(" + Tracer.infoPrintBody(LogFileCreated.class) + ") " +aMessage);
    	if (shouldInstantiate(ProjectCreated.class)) {
    	ProjectCreated retVal = new ProjectCreated(aMessage, aFileName, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);

    	return null;
    }
    public static ProjectCreated newCase (String aFileName,  Object aFinder) {
    	String aMessage = toString(aFileName);
    	return newCase(aMessage, aFileName, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aFileName, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
