package fluorite.commands;

public class DebugTerminateEvent extends ProgramExecutionEvent {
	public DebugTerminateEvent() {
		
	}
	public DebugTerminateEvent(boolean debug, boolean terminate, String projectName, int exitValue, boolean hitBreakPoint, boolean stepEnd, 
			boolean stepInto, boolean stepReturn, int aNumEvents) {
		super(debug, terminate, projectName, exitValue, hitBreakPoint, stepEnd, stepInto, stepReturn, aNumEvents);
	}

}
