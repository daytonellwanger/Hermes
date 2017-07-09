package fluorite.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.cmu.scs.fluorite.commands.ICommand;
import fluorite.model.EHEventRecorder;

public class EHCompilationCommand extends EHAbstractCommand {

	public EHCompilationCommand() {

	}
	/*
	 * Wonder why message  id has been converted to string or why the sourve strings etc
	 * have been converted. I suppose for xml conversion.
	 */
	public EHCompilationCommand(boolean isWarning, String errorMessage,
			String messageId, String sourceCodeLineNumber, String sourceStart,
			String sourceEnd, String fileName) {
		mIsWarning = isWarning;
		mErrorMessage = errorMessage;
		mMessageId = messageId;
		mSourceCodeLineNumber = sourceCodeLineNumber;
		mSourceStart = sourceStart;
		mSourceEnd = sourceEnd;
		mFileName = fileName;
		initialTimestamp = System.currentTimeMillis();
		initialCommandNumber = EHEventRecorder.getInstance().getNumNotifiedCommands();
		initialDataEventNumber = EHEventRecorder.getInstance().getNumDataEvents();
		initialFinalizedDataEventNumber = EHEventRecorder.getInstance().getNumFinalizedDataEvents();
//		workingContext = aWorkingContext;
	}

	private String mErrorMessage;
	private String mMessageId;
	private String mSourceCodeLineNumber;
	private String mFileName;
	private String mSourceStart;
	private String mSourceEnd;
	private boolean mIsWarning;
	protected String workingContext;
//	protected List<Long> timeStamps= new ArrayList();
//	protected List<Integer> commandNumbers = new ArrayList();
	protected long physicalDuration;
	protected long commandDuration;
	protected long dataEventDuration;
	protected long finalizedDataEventDuration;
	protected long initialTimestamp;
	protected int initialCommandNumber;
	protected int initialDataEventNumber;
	protected int initialFinalizedDataEventNumber;
	protected boolean disappeared;
	
	protected static final int MIN_PHYSICAL_DURATION = 10000; // 10 seconds
	protected static final int MIN_COMMAND_DURATION = 3; //
	protected static final int MIN_FINALIZED_DATA_EVENTS = 5;
	protected static final int MIN_DATA_EVENT_DURATION = 5;

	public String toString() {
		return getFileName() + "-" + getErrorMessage();
	}
	public String getErrorMessage() {
		return mErrorMessage;
	}

	public String getMessageId() {
		return mMessageId;
	}

	public String getSourceCodeLineNumber() {
		return mSourceCodeLineNumber;
	}

	public String getFileName() {
		return mFileName;
	}

	public String getSourceStart() {
		return mSourceStart;
	}
	
	public String getSourceEnd() {
		return mSourceEnd;
	}
	
	public boolean getIsWarning() {
		return mIsWarning;
	}
	public void setDisappeared(boolean newVal) {
		disappeared = newVal;
	}
	public boolean isDisappeared() {
		return disappeared;
	}
	public void increaseRepeatCount() {
		super.increaseRepeatCount();
		long aTimestamp = System.currentTimeMillis();
		int aCommandNumber = EHEventRecorder.getInstance().getNumNotifiedCommands();
		int aFinalizedDataEventNumber = EHEventRecorder.getInstance().getNumFinalizedDataEvents();
		int aDataEventEventNumber = EHEventRecorder.getInstance().getNumDataEvents();
		physicalDuration = aTimestamp - initialTimestamp;
		commandDuration = aCommandNumber - initialCommandNumber;
		dataEventDuration = aDataEventEventNumber - initialDataEventNumber;
		finalizedDataEventDuration = aFinalizedDataEventNumber - initialFinalizedDataEventNumber;
	}
	
	public boolean isPersistent() {
		return finalizedDataEventDuration >= MIN_FINALIZED_DATA_EVENTS &&
				physicalDuration >= MIN_PHYSICAL_DURATION &&
				commandDuration >= MIN_COMMAND_DURATION &&
				dataEventDuration >= MIN_DATA_EVENT_DURATION;

	}

	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);

		Attr attr = null;
		NodeList nodeList = null;

		if ((attr = commandElement.getAttributeNode("type")) != null) {
			mIsWarning = Boolean.parseBoolean(attr.getValue());
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_ErrorMessage_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mErrorMessage = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_MessageId_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mMessageId = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_SourceCodeLineNumber_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mSourceCodeLineNumber = textNode.getTextContent();
		}

		if ((nodeList = commandElement
				.getElementsByTagName(XML_SourceStart_Tag)).getLength() > 0) {
			Node textNode = nodeList.item(0);
			mSourceStart = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_SourceEnd_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mSourceEnd = textNode.getTextContent();
		}

		if ((nodeList = commandElement.getElementsByTagName(XML_FileName_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			mFileName = textNode.getTextContent();
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_Physical_Duration_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			physicalDuration = Long.parseLong(textNode.getTextContent());
		}
		if ((nodeList = commandElement.getElementsByTagName(XML_Logical_Duration_Tag))
				.getLength() > 0) {
			Node textNode = nodeList.item(0);
			commandDuration = Integer.parseInt(textNode.getTextContent());
		}
	}

	@Override
	public boolean execute(IEditorPart target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub

	}

	public static final String XML_ErrorMessage_Tag = "errorMessage";
	public static final String XML_MessageId_Tag = "messageId";
	public static final String XML_SourceCodeLineNumber_Tag = "sourceCodeLineNumber";
	public static final String XML_SourceStart_Tag = "sourceStart";
	public static final String XML_SourceEnd_Tag = "sourceEnd";
	public static final String XML_FileName_Tag = "filename";
	public static final String XML_Physical_Duration_Tag = "physicalDuration";
	public static final String XML_Logical_Duration_Tag = "logicalDuration";
	

	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", mIsWarning ? "Warning" : "Error");
		return attrMap;
	}

	@Override
	public Map<String, String> getDataMap() {
		Map<String, String> dataMap = new HashMap<String, String>();
		if (mErrorMessage != null)
			dataMap.put(XML_ErrorMessage_Tag, mErrorMessage);
		if (mMessageId != null)
			dataMap.put(XML_MessageId_Tag, mMessageId);
		if (mSourceCodeLineNumber != null)
			dataMap.put(XML_SourceCodeLineNumber_Tag, mSourceCodeLineNumber);
		if (mSourceStart != null)
			dataMap.put(XML_SourceStart_Tag, mSourceStart);
		if (mSourceEnd != null)
			dataMap.put(XML_SourceEnd_Tag, mSourceEnd);
		if (mFileName != null)
			dataMap.put(XML_FileName_Tag, mFileName);
		dataMap.put(XML_Physical_Duration_Tag, String.valueOf(physicalDuration));
		dataMap.put(XML_Logical_Duration_Tag, String.valueOf(commandDuration));

		return dataMap;
	}

	@Override
	public String getCommandType() {
		return "CompilationCommand";
	}

	@Override
	public String getName() {
		String name;
		if (mIsWarning)
			name = "Warning";
		else
			name = "Error";
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategory;
	}

	@Override
	public String getCategoryID() {
		// TODO Auto-generated method stub
		return EHEventRecorder.MacroCommandCategoryID;
	}
	
	public boolean equals(EHCompilationCommand anOther) {
		return 
				getMessageId().equals(anOther.getMessageId()) &&
				getErrorMessage().equals(anOther.getMessageId()) &&
				getSourceStart().equals(anOther.getSourceStart());
				
	}

	@Override
	public boolean combine(ICommand anotherCommand) {
		// TODO Auto-generated method stub
		return false;
	}

}
