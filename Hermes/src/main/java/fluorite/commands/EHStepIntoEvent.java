package fluorite.commands;

public class EHStepIntoEvent extends EHProgramExecutionEvent {
	public EHStepIntoEvent() {
		super();
		
	}
	public EHStepIntoEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn);
	}

}
