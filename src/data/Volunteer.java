package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import main.Application;
import util.Sorting;
import util.Util;

public class Volunteer implements Serializable, Comparable<Volunteer> {

	private static final long serialVersionUID = -2908227046965466540L;

	public static final ArrayList<Volunteer> volunteers;

	// add email, phone number, address, etc.

	private String name;
	private String id;
	private int age;

	public static final boolean MALE = false;
	public static final boolean FEMALE = true;
	private boolean gender;

	private String email;
	private String phoneNumber;
	private String address;

	private String password;

	private ArrayList<Session> sessions;

	static {
		volunteers = new ArrayList<Volunteer>();
	}

	public Volunteer(String name, String id) {
		this.name = name;
		this.id = id;
		sessions = new ArrayList<Session>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public ArrayList<Session> getSessions() {
		return sessions;
	}

	public int getTotalVolunteeringTime() {
		int totalTime = 0;
		for (Session s : sessions) {
			totalTime += s.getTotalTime();
		}
		return totalTime;
	}

	public boolean isSignedIn() {
		if (sessions.size() == 0)
			return false;
		Session current = sessions.get(sessions.size() - 1);
		if (current.getSigninTime() != null && current.getSignoutTime() == null)
			return true;
		return false;
	}

	public void signin() {
		Session newsession = new Session();
		newsession.signin();
		sessions.add(newsession);

		if (Application.logFileStream != null) {
			Application.logFileStream.println(
					Util.formatName(this) + ": Signed In at " + Util.normalFormat.format(newsession.getSigninTime()));
		}
	}

	public void signout() {
		if (sessions.get(sessions.size() - 1).isComplete())
			return;
		sessions.get(sessions.size() - 1).signout();

		if (Application.logFileStream != null) {
			Application.logFileStream.println(Util.formatName(this) + ": Signed Out at "
					+ Util.normalFormat.format(sessions.get(sessions.size() - 1).getSignoutTime()));
		}

		if (sessions.get(sessions.size() - 1).getTotalTime() < 1) {
			sessions.remove(sessions.size() - 1);
			if (Application.logFileStream != null) {
				Application.logFileStream.println(
						"* Last signout did was not recorded because the session was under a minute in length *");
			}
		}

	}

	public boolean wasVolBetween(Date a, Date b) {
		for (Session sess : sessions) {
			if (Util.dateIsBetween(sess.getSigninTime(), a, b) || Util.dateIsBetween(sess.getSignoutTime(), a, b)
					|| (sess.getSignoutTime().getTime() > b.getTime() && sess.getSigninTime().getTime() < a.getTime()))
				return true;
		}
		return false;
	}

	public boolean wasVolOnDay(Date date) {
		String dayString = Util.dayFormat.format(date);
		for (Session sess : sessions) {
			Date signin = sess.getSigninTime(), signout = sess.getSignoutTime();
			if (Util.dayFormat.format(signin).equals(dayString) || Util.dayFormat.format(signout).equals(dayString))
				return true;
		}
		return false;
	}

	public void sortSessions() {
		long[] intimes = new long[sessions.size()];
		for (int i = 0; i < sessions.size(); i++) {
			intimes[i] = sessions.get(i).getSigninTime().getTime();
		}

		long[] newintimes = Sorting.mergeSort(intimes, Sorting.INCREASING);

		ArrayList<Session> newTimeChart = new ArrayList<Session>();
		for (long newintime : newintimes) {
			for (int j = 0; j < sessions.size(); j++) {
				if (sessions.get(j).getSigninTime().getTime() == newintime) {
					newTimeChart.add(sessions.get(j));
					sessions.remove(j);
					break;
				}
			}
		}
		sessions = newTimeChart;

	}

	public Date lastAffiliation() {
		if (sessions.isEmpty())
			return null;
		Session last = getLastSession();
		if (last.isComplete())
			return last.getSignoutTime();
		else
			return last.getSigninTime();
	}

	public Session getLastSession() {
		if (sessions.isEmpty())
			return null;
		return sessions.get(sessions.size() - 1);
	}

	@Override
	public int compareTo(Volunteer vol) {
		return name.compareTo(vol.name);
	}

	@Override
	public String toString() {
		return name + " " + id + " " + sessions.size();
	}

}
