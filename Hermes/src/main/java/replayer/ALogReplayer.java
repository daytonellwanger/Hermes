package replayer;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.ITextViewerExtension6;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import fluorite.commands.FindCommand;
import fluorite.util.EHUtilities;
import util.annotations.Visible;
import util.misc.Common;


public class ALogReplayer implements IExecutionListener {
	
	protected IProject lastManipulatedProject;
	protected IEditorPart lastEditor;
	protected ITextEditor lastTextEditor;
	protected IDocumentProvider lastDocumentProvider;
	protected IDocument lastDocument;
	protected StyledText lastStyledText;
	protected IEditorInput lastEditorInput;
	protected ILaunchConfiguration lastConfiguration;
	
	protected ISourceViewer lastSourceViewer;
	protected TextViewer lastTextViewer;	
	protected IJavaElement lastJavaElement;
	protected ICompilationUnit lastCompilationUnit;

	protected IType lastSourceType;
	protected IFile lastFile;
	protected IFindReplaceTargetExtension lastFindReplaceTargetExt;
	protected IFindReplaceTargetExtension3 lastFindReplaceTargetExt3;
	protected ITextViewerExtension6 lastTextViewerExtension6;

	protected IUndoManager lastUndoManager;
	public static final String TEST_PROJECT_NAME = "DummyProj";
	public static final String TEST_PROJECT_LOCATION = "D:/TestReplay/DummyProject";
	public static final String TEST_FILE = "src/HelloWorld.java";
	public static final String TEST_MAIN_CLASS = "HelloWorld";
	public static final String TEST_CONFIGURATION_NAME = "HelloWorld";
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
//		Button button = (org.eclipse.swt.widgets.Button)Button.

	    try 
	    {                           
//	        Class<?>buttonClass = button.getClass().getSuperclass();

	        Method method = Button.class.getDeclaredMethod("click");
	        method.setAccessible(true);
//	        method.invoke(button);          
	    }
	    catch (SecurityException         e) { e.printStackTrace(); }
	    catch (NoSuchMethodException     e) { e.printStackTrace(); }
	    catch (IllegalArgumentException  e) { e.printStackTrace(); }
//	    catch (IllegalAccessException    e) { e.printStackTrace(); }
//	    catch (InvocationTargetException e) { e.printStackTrace(); }    
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
	public void createLaunchConfiguration() {
		
		lastConfiguration = EHUtilities.createLaunchConfiguration(TEST_CONFIGURATION_NAME, TEST_PROJECT_NAME, TEST_MAIN_CLASS);
		
	}
	ILaunchConfiguration lastConfiguration() {
		if (lastConfiguration == null) {
			createLaunchConfiguration();
		}
		return lastConfiguration;
	}
	public void debugConfiguration() {
		try {
			// do not need to this in separate thread
			EHUtilities.launchInSeparateThread(lastConfiguration(), ILaunchManager.DEBUG_MODE);			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void runConfiguration() {
		try {
			EHUtilities.launchInSeparateThread(lastConfiguration(), ILaunchManager.RUN_MODE);			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void setTextEditorDataStructures() {
		lastEditor = EHUtilities.getCurrentEditorPart();
		if (lastEditor == null) {
			openEditorOfPredefinedFile();
			System.out.println("no open editor. try again");
//			lastEditor = EHUtilities.getActiveEditor();
			return;
		}
		 lastSourceViewer = EHUtilities.getSourceViewer(lastEditor);
		 		 
		if (!(lastEditor instanceof AbstractTextEditor))
			      return;
			   lastTextEditor = (ITextEditor) lastEditor;
		lastDocumentProvider = lastTextEditor.getDocumentProvider();
		
		   lastDocument = lastDocumentProvider.getDocument(lastTextEditor.getEditorInput());
		   lastStyledText = (StyledText)lastTextEditor.getAdapter(Control.class);
		   if (!(lastSourceViewer instanceof TextViewer)) {
			   return;
		   }
		   lastTextViewer = (TextViewer) lastSourceViewer;	
		   lastFindReplaceTargetExt = (IFindReplaceTargetExtension) lastTextViewer.getFindReplaceTarget();
		   lastFindReplaceTargetExt3 = (IFindReplaceTargetExtension3) lastFindReplaceTargetExt;
//		   IUndoManager undoManager = null;
			if (lastSourceViewer instanceof ITextViewerExtension6) {
				lastTextViewerExtension6 = ((ITextViewerExtension6) lastSourceViewer);
				lastUndoManager = lastTextViewerExtension6.getUndoManager();
			}
			lastEditorInput = lastTextEditor.getEditorInput();
			lastFile = ((FileEditorInput) lastEditorInput).getFile();
			lastJavaElement =JavaCore.create(lastFile);
//			lastCompilationUnit = JavaCore.createCompilationUnitFrom(lastFile);
			lastCompilationUnit = (ICompilationUnit) lastJavaElement;
			
			try {
				lastSourceType =  lastCompilationUnit.getTypes()[0];
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			IPath path = file.getFullPath();
//			lastJavaElement = JavaCore.create(last);
	}
	public void insertLineAtCaret() {
		insertStringAtCaret("\n");
		
	}
	@Visible(false)
	public void replaceSelection (
			String aReplacementString,  boolean aRegExSearch) {
		setTextEditorDataStructures();
		EHUtilities.replaceSelectionInSeparateThread(lastStyledText, lastFindReplaceTargetExt3, aReplacementString, aRegExSearch);
	}
	public void replaceSelection (
			String aReplacementString) {
		replaceSelection(aReplacementString, false);
	}
	@Visible(false)
	public void findAndSelectTextAfterCursor (
			String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord, boolean aRegExSearch) {
		setTextEditorDataStructures();
		EHUtilities.findAndSelectTextAfterCursorInSeparateThread(lastStyledText, lastFindReplaceTargetExt3, aFindString, aSearchForward, aCaseSensitive, aWholeWord, aRegExSearch);
	}
	protected FindCommand lastFindCommand;
	public void interactiveFindAndSelectTextAfterCursor(String aFindStrng) {
		setTextEditorDataStructures();
		lastFindCommand = new FindCommand(aFindStrng);
		EHUtilities.invokeFindInSeparateThread(lastFindCommand);
	}
	public void interactiveCloseOfFindDialog () {
		EHUtilities.invokeCloseInSeparateThread(lastFindCommand);
	}
	public void findAndSelectTextAfterCursor (
			String aFindString) {
		findAndSelectTextAfterCursor(aFindString, true, false, false, false);
	}
//	public void findStringFromCaret(String aString) {
//		setTextEditorDataStructures();
//		
//		 TextViewer.this.findAndSelectInRange(0, "foo", true, true, true, 0, 0, true);
//		 //.this.findAndSelectInRange(modelOffset, findString, searchForward, caseSensitive, wholeWord, range.getOffset(), range.getLength(), regExSearch);
//		
//	}
	public void insertStringAtCaret(String aString) {
		setTextEditorDataStructures();
		try {
//			int anOffset = lastDocument.search(0, "for", true, true, true);
//			int anOffset = lastStyledText.getCaretOffset();
			EHUtilities.insertTextAfterCursorInSeparateThread(lastStyledText, aString);
//			lastStyledText.replaceTextRange(anOffset, 0, "for {\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	@Visible(false)
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
	public void executeCommand(String aCommandName) {
		EHUtilities.executeCommandInSeparateThread(aCommandName, EHUtilities.EMPTY_MAP);
		
	}
	public void invokeLineDownAction() {
		
		invokeAction(ST.LINE_DOWN);
	}
	public void invokeCutAction() {
		
		invokeAction(ST.CUT);
	}
	protected void invokeAction(int anAction) {
		setTextEditorDataStructures();
		EHUtilities.invokeActionInSeparateThread(lastStyledText, anAction);
	}
	public void invokeUndo() {
		setTextEditorDataStructures();
		EHUtilities.invokeUndoInSeparateThread(lastUndoManager);
	}
	public void invokeRedo() {
		setTextEditorDataStructures();
		EHUtilities.invokeRedoInSeparateThread(lastUndoManager);
	}
	public void showEditMenu() {
//		IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchWindow window = EHUtilities.getActiveWorkbenchWindow();

			if(! (window instanceof WorkbenchWindow)) {
				return;
			}
			 IMenuManager menuManager = ((WorkbenchWindow)window).getMenuManager();
			 IMenuManager editMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
			 
			 EHUtilities.showContributionItemInSeparateThread(editMenu);
//			 editMenu.addMenuListener(new IMenuListener() {
//
//				@Override
//				public void menuAboutToShow(IMenuManager manager) {
//					manager.setVisible(true);
//					
//				}
//		            
//		           
//		});
//		Event event = new Event();
//		event.type = SWT.Show;    
//		event.button = 1;
//		editMenu.notifyListeners(event);
	}
	public void showUndoMenuItem() {
//		IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchWindow window = EHUtilities.getActiveWorkbenchWindow();

			if(! (window instanceof WorkbenchWindow)) {
				return;
			}
			 IMenuManager menuManager = ((WorkbenchWindow)window).getMenuManager();
			 IMenuManager editMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
			 
			 String anUndoItem = org.eclipse.ui.actions.ActionFactory.UNDO.getId();
			 IContributionItem anItem = editMenu.findUsingPath(anUndoItem);
			 EHUtilities.showContributionItemInSeparateThread(anItem);

//			 editMenu.addMenuListener(new IMenuListener() {
//
//				@Override
//				public void menuAboutToShow(IMenuManager manager) {
//					manager.setVisible(true);
//					
//				}
//		            
//		           
//		});
//		Event event = new Event();
//		event.type = SWT.Show;    
//		event.button = 1;
//		editMenu.notifyListeners(event);
	}
	public void lineBreakpointToggle(int aLineNumber) {
		setTextEditorDataStructures();
		EHUtilities.lineBreakpointToggleInSeparateThread(lastSourceType, lastTextEditor, aLineNumber);
	}
	@Visible(false)
	public void menuProcessing() {
//		IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchWindow window = EHUtilities.getActiveWorkbenchWindow();

			if(! (window instanceof WorkbenchWindow)) {
				return;
			}
			 IMenuManager menuManager = ((WorkbenchWindow)window).getMenuManager();
			 IMenuManager editMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
			 
			 String anUndoItem = org.eclipse.ui.actions.ActionFactory.UNDO.getId();
			 IContributionItem anItem = editMenu.findUsingPath(anUndoItem);
			 
			 
			
//			menuManager.get
//
//			    //TODO you may need to remove items from the coolbar as well
//			    ICoolBarManager coolBarManager = null;
//
//			    if(((WorkbenchWindow) window).getCoolBarVisible()) {
//			        coolBarManager = ((WorkbenchWindow)window).getCoolBarManager2();
//			    }
//
//			    Menu menu = menuManager.getMenu();
//
//			    //you'll need to find the id for the item
//			    String itemId = "menuId";
//			    IContributionItem item = menuManager.find(itemId);
//
//			    // remember position, TODO this is protected
//			    int controlIdx = menu.indexOf(mySaveAction.getId());
			}
	
	
}
