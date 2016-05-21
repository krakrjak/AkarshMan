package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import data.Volunteer;

public class Util {

	/**
	 * 2/19/2016 8:58 PM Fri
	 */
	public static final SimpleDateFormat normalFormat = new SimpleDateFormat("M/dd/yyyy h:mm a");
	/**
	 * Feb 19, 2016 8:58:17 PM
	 */
	public static final SimpleDateFormat logFormat = new SimpleDateFormat("E, MMM d, y h:mm:ss a");
	/**
	 * 2/19/2016
	 */
	public static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd/M/yyyy");

	public static final Font titleFont = new Font("OCR A Extended", Font.PLAIN, 110);
	// new Font("SegoeScript Bold",Font.ITALIC,110);
	public static final Font subtitleFont = new Font(Font.MONOSPACED, Font.BOLD, 30);
	public static final Font normalFont = new Font(Font.MONOSPACED/* "Times New Roman" */, Font.PLAIN, 20);
	public static final Font standoutFont = new Font(Font.MONOSPACED/* "Times New Roman" */, Font.BOLD, 24);

	public static String getRightNow() {
		return normalFormat.format(new Date());
	}

	public static double round(double value, int places) {
		double factor = Math.pow(10, places);
		value = value * factor;
		double tmp = Math.round(value);
		return tmp / factor;
	}

	public static boolean dateIsBetween(Date date, Date range1, Date range2) {
		return (date.compareTo(range2) < 0 && date.compareTo(range1) > 0);
	}

	public static boolean dateIsBetweenInc(Date date, Date range1, Date range2) {
		return (date.compareTo(range2) <= 0 && date.compareTo(range1) >= 0);
	}

	public static String formatTime(int minutes) {
		return (minutes + " mins (" + round((double) minutes / 60, 2) + " hrs)");
	}

	public static String formatName(Volunteer vol) {
		return vol.getName() + " - " + vol.getID();
	}

	public static void toFullScreen(JFrame frame) {
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
	}

	public static ImageIcon getImage(String imgname) {
		return new ImageIcon(Util.class.getResource("/images/" + imgname));
	}

	public static ImageIcon getScaledImage(String imgname, int x, int y) {
		ImageIcon icon = getImage(imgname);
		Image img = icon.getImage();
		img = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	public static ImageIcon scaleImageIcon(ImageIcon icon, int x, int y) {
		Image img = icon.getImage();
		img = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		return icon;
	}

	public static Color copyColor(Color col) {
		return new Color(col.getRed(), col.getGreen(), col.getBlue());
	}

	public static Color getDarkerColor(Color col, int num) {
		return new Color(col.getRed() - num, col.getGreen() - num, col.getBlue() - num);
	}

	public static boolean isValidEmail(String email) {
		if (email.contains("@") && email.contains("."))
			return true;
		return false;
	}

	public static String generateRandomID() {
		String result;
		do {
			result = "";
			for (int i = 0; i < 4; i++) {
				result += (int) (Math.random() * 10);
			}
		} while (idIsTaken(result));
		return result;
	}

	public static boolean idIsTaken(String id) {
		for (Volunteer vol : Volunteer.volunteers)
			if (vol.getID().equals(id))
				return true;
		return false;
	}

	public static Volunteer getVolunteer(String id) {
		if (!idIsTaken(id))
			throw new RuntimeException("Can't get volunteer, ID doesn't exist");
		for (Volunteer vol : Volunteer.volunteers)
			if (vol.getID().equals(id))
				return vol;
		return null;
	}

	public static String formatPhoneNumber(String pn) {
		String result = "";
		for (char c : pn.toCharArray()) {
			if (c >= '0' && c <= '9') {
				result += c;
			}
		}
		if (result.length() > 11)
			throw new RuntimeException("Invalid phone number");
		result = result.substring(result.length() - 10, result.length());

		String init = result.substring(0, 3);
		String mid = result.substring(3, 6);
		String last = result.substring(6, 10);

		return init + "-" + mid + "-" + last;
	}

	public static void setActionListener(JButton button, ActionListener al) {
		for (ActionListener a : button.getActionListeners()) {
			button.removeActionListener(a);
		}
		button.addActionListener(al);
	}

}
