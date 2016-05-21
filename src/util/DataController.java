package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Scanner;

import data.AppPrefs;
import data.Session;
import data.Volunteer;
import main.Application;

public class DataController {

	private static final IllegalArgumentException loadSaveFileException = new IllegalArgumentException(
			"Cannot send file to this method");

	public static void saveVolList() {
		saveVolListTxt(Application.appPrefs.txtDataDir);
	}

	public static void saveVolListTxt(File dir) {
		if (dir.isFile())
			throw loadSaveFileException;
		for (Volunteer vol : Volunteer.volunteers) {
			saveVolTxt(vol, dir);
		}
	}

	public static void saveVol(Volunteer vol) {
		saveVolTxt(vol, Application.appPrefs.txtDataDir);
	}

	public static void saveVolTxt(Volunteer vol, File dir) {
		if (dir.isFile())
			throw loadSaveFileException;
		vol.sortSessions();
		File file = new File(dir.getAbsolutePath() + "/" + vol.getID() + ".txt");
		try {
			file.createNewFile();
			PrintStream ps = new PrintStream(file);

			ps.println(vol.getName());
			ps.println(vol.getID());

			for (Session s : vol.getSessions()) {
				ps.println(s.getSigninTime().getTime() + " "
						+ (s.getSignoutTime() == null ? "-" : s.getSignoutTime().getTime()));
			}

			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void loadVolList() {
		loadVolListTxt(Application.appPrefs.txtDataDir);
	}

	/**
	 * Recommended that you clear Volunteer.volunteers before using this method
	 * 
	 * @param dir
	 */
	public static void loadVolListTxt(File dir) {
		if (dir.isFile())
			throw loadSaveFileException;
		File[] files = dir.listFiles();

		int vols = 0;
		for (File file : files) {
			if (!file.getName().endsWith(".txt")) {
				continue;
			}
			Volunteer.volunteers.add(decodeTxtFile(file));
			vols++;
		}
		Application.logFileStream
				.println("Successfully loaded " + vols + " volunteers from txt files at " + dir.getAbsolutePath());

	}

	private static Volunteer decodeTxtFile(File file) {
		Scanner filescan = null;
		try {
			filescan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Volunteer vol = new Volunteer(filescan.nextLine(), filescan.nextLine());
		while (filescan.hasNextLine()) {
			String ss = filescan.nextLine();
			String[] sstimes = ss.split(" ");
			long login = Long.parseLong(sstimes[0]);
			try {
				long logout = Long.parseLong(sstimes[1]);
				vol.getSessions().add(new Session(new Date(login), new Date(logout)));
			} catch (Exception e) {
				Session s = new Session();
				s.setSigninTime(new Date(login));
				vol.getSessions().add(s);
			}

		}
		filescan.close();
		return vol;
	}

	public static AppPrefs loadAppPrefs() {
		File prefFile = Application.preferenceFile;

		if (!prefFile.exists())
			return null;

		AppPrefs pref = null;

		try {
			FileInputStream fis = new FileInputStream(prefFile);
			ObjectInputStream ois = new ObjectInputStream(fis);

			pref = (AppPrefs) ois.readObject();

			ois.close();
			fis.close();
		} catch (Exception e) {
			return null;
		}

		return pref;
	}

	public static void saveAppPrefs() {
		File prefFile = Application.preferenceFile;

		try {
			FileOutputStream fis = new FileOutputStream(prefFile);
			ObjectOutputStream ois = new ObjectOutputStream(fis);

			ois.writeObject(Application.appPrefs);

			ois.close();
			fis.close();
			saveVolList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteVol(Volunteer vol) {
		File file = new File(Application.appPrefs.txtDataDir.getAbsolutePath() + "/" + vol.getID() + ".txt");
		if (file.exists()) {
			file.delete();
		}
	}

}
