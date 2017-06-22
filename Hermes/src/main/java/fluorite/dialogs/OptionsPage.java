package fluorite.dialogs;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import fluorite.model.EHEventRecorder;
import fluorite.plugin.EHActivator;
//import fluorite.plugin.Activator;
import fluorite.preferences.Initializer;
/*
 * We will not relaly be using this page, using the default preferences.
 * If we do allow preferences to be changed, then the activator should be the real one
 * I think.
 */
public class OptionsPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button mEnableEventLogger;
	private Button mShowConsole;
	private Button mWriteToConsole;

	private Button mCombineCommands;
	private Text mCombineThreshold;

	private Button mLogInsertedText;
	private Button mLogDeletedText;

	private Button mLogTopBottomLines;
	private Button mLogMouseWheel;

	public OptionsPage() {
		// TODO Auto-generated constructor stub
	}

	public OptionsPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public OptionsPage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(getShell(), EHActivator.PLUGIN_ID + ".overallHelp");
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		mEnableEventLogger = new Button(comp, SWT.CHECK);
		mEnableEventLogger
				.setText("Enable EventLogger plugin (restart needed)");
		mEnableEventLogger.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getBoolean(Initializer.Pref_EnableEventLogger));

		mShowConsole = new Button(comp, SWT.CHECK);
		mShowConsole.setText("Show console");
		mShowConsole
				.setToolTipText("If set, make the console visible during command log.");
		mShowConsole.setSelection(EHActivator.getDefault().getPreferenceStore()
				.getBoolean(Initializer.Pref_ShowConsole));

		mWriteToConsole = new Button(comp, SWT.CHECK);
		mWriteToConsole.setText("Write to console");
		mWriteToConsole
				.setToolTipText("If set, write normal execution trace statements to the console during command log.");
		mWriteToConsole.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getBoolean(Initializer.Pref_WriteToConsole));

		mCombineCommands = new Button(comp, SWT.CHECK);
		mCombineCommands.setText("Combine multiple commands of same type");
		mCombineCommands.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getBoolean(Initializer.Pref_CombineCommands));

		Composite compCombine = new Composite(comp, SWT.NONE);
		compCombine.setLayout(new GridLayout(2, false));
		compCombine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(compCombine, SWT.NONE);
		label.setText("Command combine time threshold (in milliseconds)");

		mCombineThreshold = new Text(compCombine, SWT.BORDER);
		mCombineThreshold.setText(Integer.toString(EHActivator.getDefault()
				.getPreferenceStore()
				.getInt(Initializer.Pref_CombineTimeThreshold)));

		mLogInsertedText = new Button(comp, SWT.CHECK);
		mLogInsertedText.setText("Log Inserted Text");
		mLogInsertedText.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getBoolean(Initializer.Pref_LogInsertedText));

		mLogDeletedText = new Button(comp, SWT.CHECK);
		mLogDeletedText.setText("Log Deleted Text");
		mLogDeletedText.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getBoolean(Initializer.Pref_LogDeletedText));

		mLogTopBottomLines = new Button(comp, SWT.CHECK);
		mLogTopBottomLines
				.setText("Log top / bottom line numbers shown on the screen");
		mLogTopBottomLines.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getBoolean(Initializer.Pref_LogTopBottomLines));

		mLogMouseWheel = new Button(comp, SWT.CHECK);
		mLogMouseWheel.setText("Log mouse wheels");
		mLogMouseWheel.setSelection(EHActivator.getDefault().getPreferenceStore()
				.getBoolean(Initializer.Pref_LogMouseWheel));

		return comp;
	}

	public void init(IWorkbench workbench) {
		// nothing to do
	}

	@Override
	public boolean performOk() {
		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_EnableEventLogger,
						mEnableEventLogger.getSelection());
		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_ShowConsole,
						mShowConsole.getSelection());
		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_WriteToConsole,
						mWriteToConsole.getSelection());

		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_CombineCommands,
						mCombineCommands.getSelection());
		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_CombineTimeThreshold,
						Integer.parseInt(mCombineThreshold.getText()));
		EHEventRecorder.getInstance().setCombineCommands(
				mCombineCommands.getSelection());
		EHEventRecorder.getInstance().setCombineTimeThreshold(
				Integer.parseInt(mCombineThreshold.getText()));

		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_LogInsertedText,
						mLogInsertedText.getSelection());
		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_LogDeletedText,
						mLogDeletedText.getSelection());

		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_LogTopBottomLines,
						mLogTopBottomLines.getSelection());

		EHActivator
				.getDefault()
				.getPreferenceStore()
				.setValue(Initializer.Pref_LogMouseWheel,
						mLogMouseWheel.getSelection());

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		mEnableEventLogger.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_EnableEventLogger));
		mShowConsole.setSelection(EHActivator.getDefault().getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_ShowConsole));
		mWriteToConsole.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_WriteToConsole));

		mCombineCommands.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_CombineCommands));
		mCombineThreshold.setText(Integer.toString(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultInt(Initializer.Pref_CombineTimeThreshold)));

		mLogInsertedText.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_LogInsertedText));
		mLogDeletedText.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_LogDeletedText));

		mLogTopBottomLines.setSelection(EHActivator.getDefault()
				.getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_LogTopBottomLines));
		mLogMouseWheel.setSelection(EHActivator.getDefault().getPreferenceStore()
				.getDefaultBoolean(Initializer.Pref_LogMouseWheel));

		super.performDefaults();
	}

}
