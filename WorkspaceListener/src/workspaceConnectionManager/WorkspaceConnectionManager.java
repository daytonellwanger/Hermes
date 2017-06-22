package workspaceConnectionManager;

import java.util.HashMap;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.editors.text.EditorsUI;
import org.json.JSONArray;
import org.json.JSONObject;

import workspacelistener.Delta;
import workspacelistener.NewFileContents;
import workspacelistener.WorkspaceFileListener;
import workspacelistener.WorkspaceListener;
import workspacelistener.ui.PrivacyView;
import dayton.ellwanger.hermes.xmpp.ConnectionManager;
import util.trace.hermes.workspacelistener.FileForwardedToConnectionManager;
//import util.trace.messagebus.clients.JSONObjectForwardedToConnectionManager;
import util.trace.hermes.workspacelistener.WorkspaceListenerTraceUtility;


public class WorkspaceConnectionManager implements WorkspaceFileListener {
    static String activeDocumentName = "";

	
	private String workspaceString;
	private WorkspaceListener workspaceListener;
	private HashMap<String, ContentSender> pendingChanges;
	
	public WorkspaceConnectionManager() {
		EditorsUI.getPreferenceStore().setDefault(PrivacyView.PRIVACY_PREFERENCE, 1);
		pendingChanges = new HashMap<String, ContentSender>();
		workspaceString = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString().replace("/",".");
		workspaceListener = new WorkspaceListener();
		workspaceListener.addWorkspaceFileListener(this);	
//		WorkspaceListenerTraceUtility.setTracing();
	}

	@Override
	public void newFileContents(NewFileContents newFileContents) {
		int privacySetting = EditorsUI.getPreferenceStore().getInt(PrivacyView.PRIVACY_PREFERENCE);
		if(privacySetting == 0) {
			return;
		}
		if(ConnectionManager.getInstance() != null) {
			JSONObject messageData = new JSONObject();
			try {
				messageData.put("type", "editorContents");
				messageData.put("filename", workspaceString + newFileContents.getFilePath());
				messageData.put("contents", newFileContents.getContents().replace('\r', ' '));
				messageData.put("isPublic", (privacySetting == 2));
				JSONArray tags = new JSONArray();
				tags.put("EDITOR_CONTENTS");
				messageData.put("tags", tags);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			FileForwardedToConnectionManager.newCase(this, messageData.toString());
//			JSONObjectForwardedToConnectionManager.newCase(this, messageData.toString());
			
			ConnectionManager.getInstance().sendMessage(messageData);
		}
	}

	@Override
	public void fileDelta(Delta fileDelta) {
		String aFileName = workspaceString + fileDelta.getFilePath();
		setActiveDocumentName(aFileName);
		if(ConnectionManager.getInstance() != null) {
			ContentSender cs = pendingChanges.get(fileDelta.getFilePath());
			if(cs == null) {
				cs = new ContentSender(fileDelta);
				pendingChanges.put(fileDelta.getFilePath(), cs);
				(new Thread(cs)).start();
			} else {
				cs.send = false;
				cs.fileDelta = fileDelta;
			}
		}
	}
	
	class ContentSender implements Runnable {
		
		boolean send;
		int sleepDelay = 500;
		Delta fileDelta;
		
		public ContentSender(Delta fileDelta) {
			send = false;
			this.fileDelta = fileDelta;
		}
		
		public void run() {
			while(!send) {
				send = true;
				try {
					Thread.sleep(sleepDelay);
				} catch (Exception ex) {ex.printStackTrace();}
			}
			pendingChanges.remove(fileDelta.getFilePath());
			newFileContents(new NewFileContents(fileDelta.getIFilePath(), fileDelta.getChanges().getDocument().get()));
		}
		
	}
	public static String getActiveDocumentName() {
		return activeDocumentName;
	}

	public static void setActiveDocumentName(String activeDocumentName) {
		WorkspaceConnectionManager.activeDocumentName = activeDocumentName;
	}

	
}