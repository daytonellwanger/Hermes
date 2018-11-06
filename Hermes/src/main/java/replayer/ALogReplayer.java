package replayer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.util.EHUtilities;
import util.annotations.Visible;
import util.misc.Common;

public class ALogReplayer implements IExecutionListener {
	
	IProject lastManipulatedProject;
	IEditorPart lastEditor;
	ITextEditor lastTextEditor;
	IDocumentProvider lastDocumentProvider;
	IDocument lastDocument;
	StyledText lastStyledText;
	public static final String TEST_PROJECT_NAME = "DummyProj";
	public static final String TEST_PROJECT_LOCATION = "D:/TestReplay/DummyProject";
	public static final String TEST_FILE = "src/HelloWorld.java";
	String[] commands = new String[] {
			IWorkbenchCommandConstants.EDIT_COPY,
			IWorkbenchCommandConstants.EDIT_CONTENT_ASSIST,
			IWorkbenchCommandConstants.FILE_RENAME,
			IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE,
			IWorkbenchCommandConstants.EDIT_UNDO
			

	};
	
	String javaVersion = "JavaSE-1.8";

	public void listenToCommands() { 
		ICommandService commandService = EHUtilities.getCommandService();
		if (commandService == null) {
			System.out.println("No command service");
		}
		try {
			for (String aCommandName: commands) {				
			
//		Command aCommand = commandService.getCommand("com.foo.the.command").executeWithChecks(new ExecutionEvent());
				Command aCommand = commandService.getCommand(aCommandName);
				aCommand.addExecutionListener(this);
			}
		

		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Visible(false)
	public String getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	public void getOrCreateProject(String aProjectName, String aLocation) {
		lastManipulatedProject = EHUtilities.createProjectFromLocation(aProjectName, aLocation, javaVersion);
//		EHUtilities.createProjectFromFolder(aProjectName, aLocation);

	}
	public void getOrCreatePredefinedProject() {
		getOrCreateProject(TEST_PROJECT_NAME, TEST_PROJECT_LOCATION);
	}
	public void openEditorOfPredefinedFile() {
		openEditorFromSeparateThread(TEST_FILE);	
	}
	public static void printWorkingDirectory() {
		 System.out.println("Working Directory = " +
	              System.getProperty("user.dir"));
		 Path currentRelativePath = Paths.get("");
		 String s = currentRelativePath.toAbsolutePath().toString();
		 System.out.println("Current relative path is: " + s);
		 System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
		 
	}
	protected void addTextAndRefresh(String aFolderName, String aRelativeFileName, String aText) {
		String aFileName = aFolderName + "/" + aRelativeFileName;
		try {
			
		File aFile = new File(aFileName);
		if (!aFile.exists()) {
			return;
		}
		StringBuffer aPreviousText = Common.toStringBuffer(aFile);
//		aPreviousText.append(aText);
		Common.writeText(aFile, aPreviousText.toString() + aText);
		refreshFile(aRelativeFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void addTextAndRefreshPredefinedFile(String aText) {
		addTextAndRefresh (TEST_PROJECT_LOCATION, TEST_FILE, aText);
		
	}
//	public void openEditor(String aFileName) {
//		if (lastProject == null)
//			return;
//		lastEditor = EHUtilities.openEditor(lastProject, aFileName);
//		
//	}
	protected void openEditorInUIThread(String aFileName) {
		if (lastManipulatedProject == null)
			return;
		
	 EHUtilities.openEditorInUIThread(lastManipulatedProject, aFileName);
		
	}
	protected void openEditorFromSeparateThread(String aFileName) {
		if (lastManipulatedProject == null) {
			getOrCreatePredefinedProject();
//			return;
		}
		EHUtilities.openEditorFromSeparateThread(lastManipulatedProject, aFileName);
//		Runnable newRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				openEditorInUIThread(aFileName);
//			}
//			
//		};
//		Thread newThread = new Thread(newRunnable) ;
//		newThread.start();
		
	}
	public void refreshPredefinedFile() {
		refreshFile(TEST_FILE);
	}
	protected void refreshFile(String aFileName) {
		if (lastManipulatedProject == null) {
			this.getOrCreatePredefinedProject();
//			return;
		}
		EHUtilities.refreshFile(lastManipulatedProject, aFileName);	
//		Runnable aRunnable = new Runnable() {
//			@Override
//			public void run() {
//				EHUtilities.refreshFile(lastProject, aFileName);				
//			}			
//		};
//		(new Thread (aRunnable)).start();
		
	}
	
	public static OEFrame createUI() {
		return ObjectEditor.edit(new ALogReplayer());
	}
	public static void main (String[] args) {
		createUI();
	}
	@Visible(false)
	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// TODO Auto-generated method stub
		
	}
	@Visible(false)
	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
		System.out.println("Post execution failure:" + commandId);

		
	}
	@Visible(false)
	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
		System.out.println("Post execution success:" + commandId + " -->" + returnValue);
		
	}
	@Visible(false)
	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		System.out.println("Pre execution success:" + commandId + "-->" + event);
		
	}
	public void selectTextInCurrentEditor (int anOffset, int aLength) {
		setTextEditorDataStructures();
		if (lastTextEditor == null) {
			return;
		}
//		lastTextEditor.selectAndReveal(anOffset, aLength);
		EHUtilities.selectTextInSeparateThread(lastTextEditor, anOffset, aLength);
	}
	public void positionCursorInCurrentEditor (int anOffset) {
		setTextEditorDataStructures();
		if (lastStyledText == null) {
			return;
		}
//		lastTextEditor.selectAndReveal(anOffset, aLength);
		EHUtilities.positionCursorInSeparateThread(lastStyledText, anOffset);
	}
	public void saveTextInCurrentEditor() {
		setTextEditorDataStructures();
		if (lastTextEditor == null) {
			return;
		}
		EHUtilities.saveTextInSeparateThread(lastTextEditor);
//		lastTextEditor.doSave(new NullProgressMonitor());
	}
	protected void setTextEditorDataStructures() {
		lastEditor = EHUtilities.getCurrentEditorPart();
		if (lastEditor == null) {
			openEditorOfPredefinedFile();
			System.out.println("no open editor. try again");
//			lastEditor = EHUtilities.getActiveEditor();
			return;
		}
		if (!(lastEditor instanceof AbstractTextEditor))
			      return;
			   lastTextEditor = (ITextEditor) lastEditor;
		lastDocumentProvider = lastTextEditor.getDocumentProvider();
		
		   lastDocument = lastDocumentProvider.getDocument(lastTextEditor.getEditorInput());
		   lastStyledText = (StyledText)lastTextEditor.getAdapter(Control.class);
		   

		
	}
	public void replaceTextInCurrentEditor (int anOffset, int aLength, String aText) {
//		lastEditor = EHUtilities.getCurrentEditorPart();
//		if (lastEditor == null) {
//			openEditorOfPredefinedFile();
//			System.out.println("no open editor. try again");
////			lastEditor = EHUtilities.getActiveEditor();
//			return;
//		}
//		if (!(lastEditor instanceof AbstractTextEditor))
//			      return;
//			   lastTextEditor = (ITextEditor) lastEditor;
//		lastDocumentProvider = lastTextEditor.getDocumentProvider();
		setTextEditorDataStructures();
		if (lastDocument == null) {
			return;
		}
		
		   lastDocument = lastDocumentProvider.getDocument(lastTextEditor.getEditorInput());
		   try {
			lastDocument.replace(anOffset, aLength, aText);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
