package dayton.ellwanger.helpbutton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fluorite.util.EHUtilities;

public class HelperViewController implements HelperListener{

	private final String getRequestURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/get-requests";
	private final String replyURL = "https://us-south.functions.appdomain.cloud/api/v1/web/ORG-UNC-dist-seed-james_dev/V2/provide-help";
	private HelperView view;
	private File helperFolder =  new File(System.getProperty("user.home")+File.separator + "helper-config" + File.separator +"Student Project");

	public HelperViewController(HelperView view) {
		this.view = view;
		view.setHelpListener(this);
	}

	public void reply(String replyText, String email, String password, String id) throws IOException {
		try {
			JSONObject reply = new JSONObject();
			JSONArray help = new JSONArray();
			reply.put("request-id", id);
			reply.put("help", help);
			reply.put("instructor", email);
			reply.put("password", password);
			String[] replies = replyText.split("\n");
			for (String string : replies) {
				help.put(string);
			}
			JSONObject response = HTTPRequest.post(reply, replyURL);
			if (response == null) {
				throw new IOException();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void pull(String term, String course, String assign, String problem, String password, String regex, String language) throws IOException {
		try {
			JSONObject pullRequest = new JSONObject();
			JSONObject filter = new JSONObject();
			pullRequest.put("course", course);
			pullRequest.put("term", term);
			pullRequest.put("password", password);
			pullRequest.put("filter", filter);
			if (language == null || language.equals("")) {
				language = "java";
			} 
			filter.put("language", language);
			filter.put("assign", assign);
			filter.put("problem", problem);
			filter.put("regex", regex);
			JSONObject response = HTTPRequest.post(pullRequest, getRequestURL);
			if (response == null) {
				throw new IOException("Connection Failed");
			}
//			System.out.println(response.toString(4));
			String message = response.getString("message");
			if (!message.equals("payload valid")) {
				throw new IOException(message);
			}
//			System.out.println(response.toString(4));
			view.populateRequestCombo(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void createProject(JSONObject request) {
//		JSONObject request = view.getSelectedRequest();
		try {
			if (request == null || request.getJSONObject("code") == null) {
				return;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (!helperFolder.exists()) {
						helperFolder.mkdir();
					}
//					String id = request.getString("request-id");
					File src = new File(helperFolder+File.separator+"src");
					if (src.exists()) {
						deleteFolder(src);
					}
					src.mkdir();
					File metricFolder = new File(helperFolder+File.separator+"Logs"+File.separator+"Metrics");
					if (!metricFolder.exists()) {
						metricFolder.mkdirs();
					}
					for (File metrics : metricFolder.listFiles()) {
						metrics.delete();
					}
					File logFolder = new File(helperFolder+File.separator+"Logs"+File.separator+"Eclipse");
					if (!logFolder.exists()) {
						logFolder.mkdirs();
					}
					for (File logs : logFolder.listFiles()) {
						logs.delete();
					}
					JSONObject code = request.getJSONObject("code");
					Iterator<String> keys = code.keys();
					while(keys.hasNext()) {
						String fileName = keys.next();
						if (fileName.equals("log")) {
							File log = new File(logFolder+File.separator+"log.xml");
							if (log.exists()) {
								log.delete();
							}
							log.createNewFile();
							FileOutputStream os = new FileOutputStream(log);
							JSONArray commands = code.getJSONArray("log");
							if (commands.length() == 0) {
								continue;
							}
							String startTimestamp = commands.getString(0);
							startTimestamp = startTimestamp.substring(startTimestamp.indexOf("starttimestamp=\"")+16);
							startTimestamp = startTimestamp.substring(0, startTimestamp.indexOf("\""));
							String logString = "<Events startTimestamp=\"" + startTimestamp + "\" logVersion=\"1.0.0.qualifier\">\r\n";
							for(int i = 0; i < commands.length(); i++) {
								logString += commands.getString(i);
							}
							logString += "</Events>";
							os.write(logString.getBytes());
							os.close();
							log.setLastModified(Long.parseLong(startTimestamp));
						} else {
							String text = code.getString(fileName);
							String pkg = text.substring(text.indexOf("package")+7, text.indexOf(";")).trim().replace(".", File.separator);
							File pkgFolder = new File(src.getPath()+File.separator+pkg);
							pkgFolder.mkdirs();
							File file = new File(pkgFolder.getPath()+File.separator+fileName);
							if (file.exists()) {
								file.delete();
							}
							file.createNewFile();
							FileOutputStream os = new FileOutputStream(file);
							os.write(text.getBytes());
							os.close();
						}
					}
					EHUtilities.createProjectFromLocation("Student Project", helperFolder.getPath(), "JavaSE-1.8");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}).start();
	}

	private JSONObject readJSON(File file) {
		try {
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			reader.close();
			return new JSONObject(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void deleteFolder(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				deleteFolder(file);
			} else {
				file.delete();
			}
		}
		folder.delete();
	}
}
