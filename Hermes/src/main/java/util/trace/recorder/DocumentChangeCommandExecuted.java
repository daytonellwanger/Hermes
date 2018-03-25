package util.trace.recorder;

import fluorite.commands.BaseDocumentChangeEvent;
import util.trace.Tracer;

public class DocumentChangeCommandExecuted extends ICommandInfo{
	public DocumentChangeCommandExecuted(String aMessage, BaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {
		 super(aMessage, aCommand, aStartTimeStamp, aFinder);
	}	
    
    public static DocumentChangeCommandExecuted newCase (String aMessage, BaseDocumentChangeEvent aCommand, long aStartTimeStamp,  Object aFinder) {

    	if (shouldInstantiate(DocumentChangeCommandExecuted.class)) {
    	DocumentChangeCommandExecuted retVal = new DocumentChangeCommandExecuted(aMessage, aCommand, aStartTimeStamp, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(DocumentChangeCommandExecuted.class, aMessage);


    	return null;
    }
    public static DocumentChangeCommandExecuted newCase (BaseDocumentChangeEvent aCommand, long aStartTimestamp,  Object aFinder) {
    	String aMessage = toString(aCommand, aStartTimestamp);
    	return newCase(aMessage, aCommand, aStartTimestamp, aFinder);

    }
}
