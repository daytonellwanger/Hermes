package fluorite.recorders;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.EHDebugHitBreakpointBeforeDebugRunEvent;
import fluorite.commands.EHDebugHitBreakpointEvent;
import fluorite.commands.EHDebugRunEvent;
import fluorite.commands.EHDebugTerminateEvent;
import fluorite.commands.EHHitBreakpointBeforeDebugRunEvent;
import fluorite.commands.EHHitBreakpointBeforeRunEvent;
import fluorite.commands.EHHitBreakpointAfterRunEvent;
import fluorite.commands.EHProgramExecutionEvent;
import fluorite.commands.EHRunEvent;
import fluorite.commands.EHStepEndCommand;
import fluorite.commands.EHStepEndEvent;
import fluorite.commands.EHStepIntoEvent;
import fluorite.commands.EHStepReturnEvent;
import fluorite.commands.EHTerminateEvent;

public class EHDebugEventSetRecorder extends EHBaseRecorder implements IDebugEventSetListener {

	private static EHDebugEventSetRecorder instance = null;

	public static EHDebugEventSetRecorder getInstance() {
		if (instance == null) {
			instance = new EHDebugEventSetRecorder();
		}

		return instance;
	}

	private EHDebugEventSetRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		// Do nothing.
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		// Do nothing.
	}

	protected void printEvent(DebugEvent aDebugEvent) {
		System.out.println("Printing debug event");
		System.out.println("Kind" + aDebugEvent.getKind() + "Detail:" + aDebugEvent.getDetail() + "Source "
				+ aDebugEvent.getSource() + "isStepStart " + aDebugEvent.isEvaluation());

	}

	protected IProcess lastCreatedProcess = null;
	protected Object lastCreatedJDIThread = null;
	protected boolean lastProcessHasTerminated = false;
	protected boolean lastJDIThreadHasTerminated = false;
	protected int numRunProcesses = 0;
	protected int numCreatedJDIThreads = 0;
	protected int numTerminatedJDIThreads = 0;

	protected int numStartBreakPoints = 0;
	protected int numStartDebugBreakPoints = 0;
	protected int numPostRunBreakPoints = 0;
	protected int numEndDebugBreakPoints = 0;
	

	
	
	protected void lastProcessTerminated() {
		lastCreatedProcess = null;
		lastProcessHasTerminated = true;
		numStartBreakPoints = 0;
	}
	
	protected void lastJDIThreadTerminated() {
		lastCreatedJDIThread = null;
		lastJDIThreadHasTerminated = true;
		numStartDebugBreakPoints = 0;		
	}
// debug thread keeps changing!
	protected boolean testAndSetLastJDIThread(Object aDebugThread, String aProjectName) {

		if (lastCreatedJDIThread != null && !lastJDIThreadHasTerminated) {
			numCreatedJDIThreads++;


//		if (aDebugThread == lastCreatedJDIThread && !lastJDIThreadHasTerminated) {
			return false;
		}
		if (numStartDebugBreakPoints > 0) {
			getRecorder().recordCommand(
					// new EHProgramExecutionCommand(false,
					// terminate, projectName, 0, false, false,
					// false, false));
					new EHDebugHitBreakpointBeforeDebugRunEvent(false, false, aProjectName, 0, false, false, false, false, numStartDebugBreakPoints));
			numStartDebugBreakPoints = 0;
		}
		if (numPostRunBreakPoints > 0) {
			getRecorder().recordCommand(
					// new EHProgramExecutionCommand(false,
					// terminate, projectName, 0, false, false,
					// false, false));
					new EHHitBreakpointBeforeDebugRunEvent(false, false, aProjectName, 0, false, false, false, false, numPostRunBreakPoints));
			numPostRunBreakPoints = 0;
		}
		lastCreatedJDIThread = aDebugThread;
		lastJDIThreadHasTerminated = false;
		numCreatedJDIThreads = 1;
		numTerminatedJDIThreads = 0;
		numEndDebugBreakPoints = 0;
		return true;
	}
	protected boolean testAndSetLastProcess(IProcess aProcess, String aProjectName) {
		if (aProcess == lastCreatedProcess && !lastProcessHasTerminated) {
			numRunProcesses++;

			return false;
		}
		if (numStartBreakPoints > 0) {
			getRecorder().recordCommand(
					// new EHProgramExecutionCommand(false,
					// terminate, projectName, 0, false, false,
					// false, false));
					new EHHitBreakpointBeforeRunEvent(false, false, aProjectName, 0, false, false, false, false, numStartBreakPoints));
		}
		numStartBreakPoints = 0; // set also in lastProcessTerminated, so we really do not need this
		numRunProcesses = 1;
		lastCreatedProcess = aProcess;
		lastProcessHasTerminated = false;
		numPostRunBreakPoints = 0;
		return true;
	}
	
	protected boolean processIsActive() {
		return lastCreatedProcess != null;
	}
	
	protected boolean jdiThreadIsActive() {
		return lastCreatedJDIThread != null;
	}

	public void handleDebugEvents(DebugEvent[] debugEvents) {
		for (DebugEvent event : debugEvents) {
			// printEvent(event);
			Object source = event.getSource();
			IProcess process = null;
			ILaunchConfiguration config = null;
			// boolean terminate = event.getKind() == DebugEvent.TERMINATE;
			if (source instanceof IProcess) {
				// IProcess process = (IProcess) source;
				process = (IProcess) source;
				// if (!testAndSetLastProcess(process)) {
				// return;
				// }

				// ILaunchConfiguration config =
				// process.getLaunch().getLaunchConfiguration();
				config = process.getLaunch().getLaunchConfiguration();

				if (config == null) {
					return;
				}
			}

			if (event.getKind() == DebugEvent.CREATE)
			// event.getKind() == DebugEvent.TERMINATE) // should handle
			// terminate separately
			{

				// Object source = event.getSource();
				boolean terminate = event.getKind() == DebugEvent.TERMINATE;

				if (source instanceof IProcess) {
					// IProcess process = (IProcess) source;
					// ILaunchConfiguration config = process.getLaunch()
					// .getLaunchConfiguration();
					//
					// if (config == null) {
					// return;
					// }
					
					@SuppressWarnings("rawtypes")
					Map attributes = null;
					try {
						attributes = config.getAttributes();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Retrieve the corresponding project name
					String projectName = (String) (attributes.get("org.eclipse.jdt.launching.PROJECT_ATTR"));
					// run command,no ddebug
					// if (terminate) {
					//
					// }
					if (!testAndSetLastProcess(process, projectName)) {
						return;
					}
					getRecorder().recordCommand(
							// new EHProgramExecutionCommand(false,
							// terminate, projectName, 0, false, false,
							// false, false));
							new EHRunEvent(false, terminate, projectName, 0, false, false, false, false));

//				} else if (source instanceof IDebugTarget) {
				} else  {
					if (!testAndSetLastJDIThread(source, null)) {
//						return; // let us record all threads
					}
//					getRecorder()
//							.recordCommand(new EHDebugRunEvent(true, terminate, null, 0, false, false, false, false));
				}
			} else if (event.getKind() == DebugEvent.TERMINATE)
			// event.getKind() == DebugEvent.TERMINATE) // should handle
			// terminate separately
			{
				// Object source = event.getSource();
				boolean terminate = event.getKind() == DebugEvent.TERMINATE;

				if (source instanceof IProcess) {
					// IProcess process = (IProcess) source;
					// ILaunchConfiguration config = process.getLaunch()
					// .getLaunchConfiguration();
					//
					// if (config == null) {
					// return;
					// }

					@SuppressWarnings("rawtypes")
					Map attributes = null;
					try {
						attributes = config.getAttributes();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Retrieve the corresponding project name
					String projectName = (String) (attributes.get("org.eclipse.jdt.launching.PROJECT_ATTR"));
					// run command,no ddebug
					// if (terminate) {
					//
					// }
					// this should happen before the terminate process event
					if (numCreatedJDIThreads > 0) {
						getRecorder().recordCommand(
								new EHDebugRunEvent(true, terminate, null, 0, false, false, false, false, numCreatedJDIThreads ));
//						lastJDIThreadTerminated();
					}
					if (numPostRunBreakPoints > 0) {
					getRecorder().recordCommand(
							// new EHProgramExecutionEvent(false, false,
							// projectName, 0, true, false, false, false));
							new EHHitBreakpointAfterRunEvent(false, false, projectName, 0, true, false, false, false, numPostRunBreakPoints));// this
					}
					if (numTerminatedJDIThreads > 0) {
						getRecorder().recordCommand(
								new EHDebugTerminateEvent(true, terminate, null, 0, false, false, false, false, numTerminatedJDIThreads ));
					}
					
					lastJDIThreadTerminated();

					getRecorder().recordCommand(
							// new EHProgramExecutionCommand(false,
							// terminate, projectName, 0, false, false,
							// false, false));
							new EHTerminateEvent(false, terminate, projectName, 0, false, false, false, false)); // occurs after terminate run event
					lastProcessTerminated();
					

//				} else if (source instanceof IDebugTarget) {// should be JDIThread
				} else  {// should be JDIThread
					if (lastJDIThreadHasTerminated) {
						numTerminatedJDIThreads++; // why is should be numEndThreads
//						return; // let us record all teminations
					}
					numTerminatedJDIThreads++; 
//					getRecorder().recordCommand(
//							new EHDebugTerminateEvent(true, terminate, null, 0, false, false, false, false));
//					lastJDIThreadTerminated();
				}
			}

			else if (event.getKind() == DebugEvent.BREAKPOINT) // this seems
																// to be hit
																// for run
			{
				// Object source = event.getSource();

				if (source instanceof IProcess) {
					if (!processIsActive()) {
						numStartBreakPoints++;
						return;
					}
					// IProcess process = (IProcess) source;
					// ILaunchConfiguration config = process.getLaunch()
					// .getLaunchConfiguration();
					//
					// if (config == null) {
					// return;
					// }

					@SuppressWarnings("rawtypes")
					Map attributes = null;
					try {
						attributes = config.getAttributes();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Retrieve the corresponding project name
					String projectName = (String) (attributes.get("org.eclipse.jdt.launching.PROJECT_ATTR"));
					numPostRunBreakPoints++; //incremented in debug mode before first jdt thread created, also incremented after run, so perhaps should check that
					// run command (f11), no debug, actually debug also
					// comes here
//					getRecorder().recordCommand(
//							// new EHProgramExecutionEvent(false, false,
//							// projectName, 0, true, false, false, false));
//							new EHHitBreakpointAfterRunEvent(false, false, projectName, 0, true, false, false, false, numPostRunBreakPoints));// this
																												// seems
																												// to
																												// get
																												// hit
																												// before
																												// run

//				} else if (source instanceof IDebugTarget) {
				} else  {
					if (!jdiThreadIsActive()) {
						numStartDebugBreakPoints++;
						return;
					}
					if (lastProcessHasTerminated) {
					numEndDebugBreakPoints++;
					}
					// getRecorder().recordCommand(
					// new EHProgramExecutionEvent(false, false, null,0,
					// true, false, false, false));
					getRecorder().recordCommand(
							new EHDebugHitBreakpointEvent(false, false, null, 0, true, false, false, false));

				}
			} else if (event.getKind() == DebugEvent.STEP_END) {
				// only debug I guess
				// getRecorder().recordCommand(
				// new EHProgramExecutionEvent(false, false, null, 0, false,
				// true, false, false));
				getRecorder().recordCommand(new EHStepEndEvent(false, false, null, 0, false, true, false, false));

			} else if (event.getKind() == DebugEvent.STEP_INTO) {
				// only debug I guess
				getRecorder().recordCommand(
						// new EHProgramExecutionEvent(false, false, null,
						// 0, false, false, true, false));
						new EHStepIntoEvent(false, false, null, 0, false, false, true, false));

			}

			else if (event.getKind() == DebugEvent.STEP_RETURN)// STEP_RETURN
																// is the
																// same as
																// START!
			{
				// only debug I guess
				getRecorder().recordCommand(
						// new EHProgramExecutionEvent(false, false, null,
						// 0, false, false, false, true));
						new EHStepReturnEvent(false, false, null, 0, false, false, false, true));

			}

		}
		// public void handleDebugEvents(DebugEvent[] debugEvents) {
		// for (DebugEvent event : debugEvents)
		// {
		// printEvent(event);
		// if (event.getKind() == DebugEvent.CREATE
		// || event.getKind() == DebugEvent.TERMINATE)
		// {
		// Object source = event.getSource();
		// boolean terminate = event.getKind() == DebugEvent.TERMINATE;
		//
		// if (source instanceof IProcess) {
		// IProcess process = (IProcess) source;
		// ILaunchConfiguration config = process.getLaunch()
		// .getLaunchConfiguration();
		//
		// if (config == null) {
		// return;
		// }
		//
		// @SuppressWarnings("rawtypes")
		// Map attributes = null;
		// try {
		// attributes = config.getAttributes();
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // Retrieve the corresponding project name
		// String projectName = (String) (attributes
		// .get("org.eclipse.jdt.launching.PROJECT_ATTR"));
		//// run command,no ddebug
		// if (terminate) {
		//
		// }
		// getRecorder().recordCommand(
		//// new EHProgramExecutionCommand(false, terminate, projectName, 0,
		// false, false, false, false));
		// new EHRunEvent(false, terminate, projectName, 0, false, false, false,
		// false));
		//
		//
		// } else if (source instanceof IDebugTarget) {
		// getRecorder().recordCommand(
		// new EHProgramExecutionEvent(true, terminate, null, 0,false, false,
		// false, false));
		// }
		// }
		//
		// if (event.getKind() ==DebugEvent.BREAKPOINT)
		// {
		// Object source = event.getSource();
		//
		// if (source instanceof IProcess) {
		// IProcess process = (IProcess) source;
		// ILaunchConfiguration config = process.getLaunch()
		// .getLaunchConfiguration();
		//
		// if (config == null) {
		// return;
		// }
		//
		// @SuppressWarnings("rawtypes")
		// Map attributes = null;
		// try {
		// attributes = config.getAttributes();
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // Retrieve the corresponding project name
		// String projectName = (String) (attributes
		// .get("org.eclipse.jdt.launching.PROJECT_ATTR"));
		// // run command (f11), no debug
		// getRecorder().recordCommand(
		// new EHProgramExecutionEvent(false, false, projectName, 0, true,
		// false, false, false));
		// }
		// else if (source instanceof IDebugTarget) {
		// getRecorder().recordCommand(
		// new EHProgramExecutionEvent(false, false, null,0, true, false, false,
		// false));
		//
		// }
		// }
		// if (event.getKind() ==DebugEvent.STEP_END)
		// {
		// getRecorder().recordCommand(
		// new EHProgramExecutionEvent(false, false, null, 0, false, true,
		// false, false));
		//
		// }
		// if (event.getKind() ==DebugEvent.STEP_INTO)
		// {
		// getRecorder().recordCommand(
		// new EHProgramExecutionEvent(false, false, null, 0, false, false,
		// true, false));
		//
		// }
		//
		// if (event.getKind() ==DebugEvent.STEP_RETURN)
		// {
		// getRecorder().recordCommand(
		// new EHProgramExecutionEvent(false, false, null, 0, false, false,
		// false, true));
		//
		// }
		//
		// }
	}

}
