package dayton.ellwanger.hermes.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;


public class Preferences {

	protected static final String STORE_NODE = "hermes";
	
	public static final String USERNAME = "username";
	public static final String DOMAIN = "domain";
	public static final String PASSWORD = "password";
	public static final String HOST = "host";
	public static final String INSTRUCTOR = "instructor";
	public static final String SECURITY = "security";
	public static final String CREATE = "create";
	public static final String AUTOLOGIN = "autologin";
	
	private static Preferences instance;
	private List<PreferencesListener> listeners;
	
	private Preferences() {
		listeners = new LinkedList<PreferencesListener>();
	}
	
	public static Preferences getInstance() {
		if(instance == null) {
			instance = new Preferences();
		}
		return instance;
	}
	
	public void addListener(PreferencesListener l) {
		listeners.add(l);
	}
	
	public void removeListener(PreferencesListener l) {
		listeners.remove(l);
	}
	
	public void preferencesUpdated() {
		for(PreferencesListener l : listeners) {
			l.preferencesUpdated();
		}
	}
	
	public static String getPreference(String preferenceName) {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		if(prefs.nodeExists(Preferences.STORE_NODE)) {
			ISecurePreferences node = prefs.node(Preferences.STORE_NODE);
			try {
				return node.get(preferenceName, "");
			} catch (StorageException ex) {
				ex.printStackTrace();
			}
		}
		return "";
	}
	
	public static void storePreference(String preferenceName, String value) {
		ISecurePreferences prefs = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = prefs.node(Preferences.STORE_NODE);
		try {
			node.put(preferenceName, value, true);
		} catch (StorageException ex) {}
	}
	
}
