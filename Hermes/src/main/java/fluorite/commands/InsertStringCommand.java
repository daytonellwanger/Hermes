package fluorite.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fluorite.model.EHEventRecorder;
import fluorite.util.EHUtilities;

public class InsertStringCommand 
//	extends InsertStringCommand 
	extends AbstractCommand
	implements EHICommand{
//
	public InsertStringCommand(String data) {
//		super(data);
		mData = data;
	}

	public static final String XML_Data_Tag = "data";

	private String mData;
	// private MyTextChangeListener mTextChangeListener=new
	// MyTextChangeListener();
	private MyExtendedModifyListener mExtendedModifyListener = new MyExtendedModifyListener();

	// for serialization only
	public InsertStringCommand() {

	}

	public void dump() {
		System.out.println(getName());
	}

	public boolean execute(final IEditorPart target) {
		if (mData != null) {
			StyledText widget = EHUtilities.getStyledText(target);
			if (widget != null) {
				// I've had to hack to prevent problems with auto-edits. The
				// problem with just inserting
				// into the styled text widget is that a string with more than
				// one character is treated
				// like a "paste", and in the Java editor, for instance, the
				// paste will adjust the
				// leading whitespace on the current line. Therefore the amount
				// of text actually inserted
				// will not match the original string and setting the caret
				// offset will be off. Also,
				// I might have compressed the string, so the result from
				// inserting individual chars
				// (which is what the user actually typed) wouldn't match the
				// results from a string
				// insertion. Also, there is a flag in the styled widget that
				// would keep the caret updated
				// but it's not publicly available in the api. The fix seems to
				// be to insert >1 length
				// strings directly into the styledTextContent, which bypasses
				// any auto
				// indent strategy that might be set on the viewer. I don't
				// think I want to always do
				// this because I *do* want to get autoindent behavior for \n
				// insertions.
				int caretPos = widget.getCaretOffset();
				int selSize = widget.getSelectionCount();
				StyledTextContent content = widget.getContent();
				if (content != null && (mData.length() > 1))// ||
															// (mData.length()==1
															// &&
															// mData.charAt(0)!='\r')))
				{
					try {
						// content.addTextChangeListener(mTextChangeListener);
						content.replaceTextRange(caretPos, selSize, mData);
						widget.setCaretOffset(caretPos + mData.length()
								- selSize);
						// widget.setCaretOffset(mTextChangeListener.getCaretOffset());
					} finally {
						// content.removeTextChangeListener(mTextChangeListener);
					}
				} else {
					// want to allow single characters through so that we get
					// good behavior from \r, '{', etc.
					try {
						widget.addExtendedModifyListener(mExtendedModifyListener);
						mExtendedModifyListener.clearCaret();
						String insertString = mData;
						if (mData.equals("\r") || mData.equals("\n"))
							insertString = widget.getLineDelimiter();
						widget.insert(insertString);
						// widget.setCaretOffset(caretPos+insertString.length()-selSize);
						if (mExtendedModifyListener.getCaretOffset() >= 0)
							widget.setCaretOffset(mExtendedModifyListener
									.getCaretOffset());
					} finally {
						widget.removeExtendedModifyListener(mExtendedModifyListener);
					}
				}
				return true;
			}
		}

		return false;
	}

	private static class MyExtendedModifyListener implements
			ExtendedModifyListener {
		private int mCaretPos;

		public void modifyText(ExtendedModifyEvent event) {
			mCaretPos = event.start + event.length;
		}

		public void clearCaret() {
			mCaretPos = (-1);
		}

		public int getCaretOffset() {
			return mCaretPos;
		}
	}

	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("timestamp2", Long.toString(getTimestamp2()));
		return attrMap;
	}

	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (mData != null) {
			dataMap.put(XML_Data_Tag, mData);
		}
		return dataMap;
	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		NodeList nodeList = null;
		
		if ((nodeList = commandElement.getElementsByTagName(XML_Data_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mData = textNode.getTextContent();
		}

	}

	public String getCommandType() {
		return "InsertStringCommand";
	}

	public String getDescription() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Insert the string into the document at the current caret location");
		if (mData != null) {
			buffer.append("\n");
			buffer.append(getName());
		}
		return buffer.toString();
	}

	public String getName() {
		String printableData = mData;
		if (printableData != null) {
			printableData = printableData.replace("\r", "<CR>");
			printableData = printableData.replace("\n", "<LF>");
			printableData = printableData.replace("\t", "<tab>");
		}
		return "Insert string: " + printableData;
	}

	public String getCategory() {
		return EHEventRecorder.MacroCommandCategory;
	}

	public String getCategoryID() {
		return EHEventRecorder.MacroCommandCategoryID;
	}

	public boolean combine(EHICommand anotherCommand) {
		if (!(anotherCommand instanceof InsertStringCommand)) {
			return false;
		}

		InsertStringCommand nextCommand = (InsertStringCommand) anotherCommand;

		if (mData.indexOf('\n') >= 0 || nextCommand.mData.indexOf('\n') >= 0
				|| mData.indexOf('\r') >= 0
				|| nextCommand.mData.indexOf('\r') >= 0)
			return false;

		// just concatenate the 2 strings
		mData += nextCommand.mData;
		return true;
	}
}
