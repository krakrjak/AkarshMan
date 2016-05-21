package data;

import java.io.File;
import java.io.Serializable;

/**
 * Application preferences
 * 
 * @author akars
 *
 */
public class AppPrefs implements Serializable { // save appPref with ser file...

	private static final long serialVersionUID = -1866793159015377959L;

	public boolean fullScreen;
	public String adminPassWord;

	public int loadSaveDataType;

	public File txtDataDir;

	public AppPrefs(String adminpass) {
		fullScreen = true;
		adminPassWord = adminpass;
	}

	public File getCurrentSaveDir() {
		return txtDataDir;
	}
}
