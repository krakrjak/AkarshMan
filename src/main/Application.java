package main;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import data.AppPrefs;
import data.Volunteer;
import ui.FGFrame;
import ui.view.SettingsView;
import ui.view.ViewCnfg;
import util.DataController;
import util.Util;

public final class Application {
	public static final Image appImage = Util.getImage("freegeek_logo.png").getImage();

	public static final String VOLEXTENSION = ".fgvol";
	public static final String PREFEXTENSION = ".fgpref";

	public static final File appPath = new File(System.getenv("appdata") + "/Free Geek Volunteering/");

	public static final File preferenceFile = new File(appPath.getAbsolutePath() + "\\preference" + PREFEXTENSION);

	public static final String newLine = System.getProperty("line.separator");

	public static final File errorLogFile = new File(appPath.getAbsolutePath() + "\\error_log.txt");
	public static final File logFile = new File(appPath.getAbsolutePath() + "\\time_log.txt");
	public static PrintStream logFileStream;

	public static final boolean firstRun;

	public static AppPrefs appPrefs;

	public static final Thread onCloseThread = new Thread(new Runnable() {
		@Override
		public void run() {
			for (Volunteer vol : Volunteer.volunteers) {
				if (vol.isSignedIn()) {
					vol.signout();
				}
			}
			try {
				Application.preferenceFile.createNewFile();
				DataController.saveAppPrefs();
			} catch (IOException ee) {
				ee.printStackTrace();
			}
			DataController.saveVolList();

			logFileStream.close();
			System.err.close();
		}
	});

	public static FGFrame frame;

	static {
		appPath.mkdirs();

		try {
			errorLogFile.createNewFile();
			PrintStream ps = new PrintStream(new FileOutputStream(errorLogFile, true), true);
			System.setErr(ps);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			logFile.createNewFile();
			logFileStream = new PrintStream(new FileOutputStream(logFile, true), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		appPrefs = DataController.loadAppPrefs();
		if (appPrefs == null) {
			firstRun = true;
			appPrefs = new AppPrefs("");
		} else if (!appPrefs.getCurrentSaveDir().exists()) {
			firstRun = true;
			appPrefs = new AppPrefs("");
		} else {
			firstRun = false;
		}

		try {
			preferenceFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(onCloseThread);

		setupUILAF();

	}

	private static void setupUILAF() {
		try {
			@SuppressWarnings("serial")
			LookAndFeel laf = new NimbusLookAndFeel() {
				@Override
				public UIDefaults getDefaults() {
					UIDefaults ret = super.getDefaults();
					ret.put("defaultFont", Util.normalFont);
					return ret;
				}
			};
			UIManager.setLookAndFeel(laf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final void start() {
		if (firstRun) {
			logFileStream
					.println(newLine + newLine + newLine + newLine + "*********************************************"
							+ newLine + "Starting Free Geek Volunteering for the first time - " + Util.getRightNow()
							+ newLine + "*********************************************");
			firstTimeStart();
		} else {
			logFileStream
					.println(newLine + newLine + newLine + newLine + "*********************************************"
							+ newLine + "Starting Free Geek Volunteering again - " + Util.getRightNow() + newLine
							+ "*********************************************");

			DataController.loadVolList();
			startFGFrame();
		}
	}

	public static final void firstTimeStart() {
		JFrame dialog = new JFrame("Setup Application");
		dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		dialog.setSize(1200, 900);
		dialog.setMinimumSize(new Dimension(800, 500));

		SettingsView sview = new SettingsView(null);

		ViewCnfg cnfg = new ViewCnfg();
		cnfg.admin = true;
		sview.cnfgFor(cnfg);

		final Runnable afterFirstTimeSetup = new Runnable() {
			@Override
			public void run() {
				try {
					dialog.dispose();
					startFGFrame();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "There was a problem starting the application",
							"Cannot Start Application", JOptionPane.ERROR_MESSAGE, null);
				}
			}

		};

		sview.setAfterApplyRunnable(afterFirstTimeSetup);

		dialog.getContentPane().add(sview);

		dialog.setIconImage(appImage);

		dialog.setVisible(true);

		JOptionPane.showMessageDialog(dialog,
				"<html><div style=\"text-align: center;\">It looks like it's this is the first time you're running Free Geek Volunteering!<br>We would like you to setup some stuff to get the application started</html>",
				"First Time Running", JOptionPane.INFORMATION_MESSAGE, null);
	}

	private static final void startFGFrame() {

		frame = new FGFrame();

		frame.setSize(new Dimension(1500, 900));

		frame.setupFrame(appPrefs);
		frame.setVisible(true);

		ViewCnfg cnfg = new ViewCnfg();
		Application.frame.showNextView(Application.frame.mview, cnfg);
	}
}
