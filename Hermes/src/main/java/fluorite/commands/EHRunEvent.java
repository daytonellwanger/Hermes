package fluorite.commands;

public class EHRunEvent extends EHProgramExecutionEvent {
	public EHRunEvent() {
		
	}
	public EHRunEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}