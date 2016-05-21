package data;

import java.io.Serializable;
import java.util.Date;

import util.Util;

public class Session implements Serializable {

	private static final long serialVersionUID = 6701763963508430160L;

	public static final IllegalArgumentException illegalTimes = new IllegalArgumentException("Illegal session times");

	private Date signinTime;
	private Date signoutTime;

	public Session() {
		signinTime = null;
		signoutTime = null;
	}

	public Session(Date signinTime, Date signoutTime) {
		checkTimes(signinTime, signoutTime);
		this.signinTime = new Date(signinTime.getTime());
		this.signoutTime = new Date(signoutTime.getTime());
	}

	public Session(Session another) {
		setTimes(another.signinTime, another.signoutTime);
	}

	public void setSigninTime(Date signin) {
		signinTime = signin;
	}

	public Date getSigninTime() {
		return signinTime;
	}

	public void setSignoutTime(Date signout) {
		signoutTime = signout;
	}

	public Date getSignoutTime() {
		return signoutTime;
	}

	public static void checkTimes(Date signin, Date signout) {
		if (signout.compareTo(signin) <= 0)
			throw illegalTimes;
	}

	public void setTimes(Date signinTime, Date signoutTime) {
		checkTimes(signinTime, signoutTime);
		this.signinTime.setTime(signinTime.getTime());
		this.signoutTime.setTime(signoutTime.getTime());
	}

	/**
	 * @return the time between the signout and signin times as a integer IN
	 *         MINUTES. If the signout time is null, then it will return the
	 *         time between the current time and signin time (because volunteer
	 *         is still working)
	 */
	public int getTotalTime() {
		if (signinTime == null)
			throw new NullPointerException("No starting time to calculate total time for the session");
		if (signoutTime == null)
			return ((int) (new Date().getTime() - signinTime.getTime()) / 60000);
		return ((int) (signoutTime.getTime() - signinTime.getTime()) / 60000);
	}

	/**
	 * NOTE- this will not override previous signin time, it will only signin if
	 * the current signin time is null
	 */
	public void signin() {
		if (signinTime == null) {
			signinTime = new Date();
		}
	}

	/**
	 * NOTE- this will not override previous signout time, it will only signout
	 * if the current signout time is null
	 */
	public void signout() {
		if (signoutTime == null) {
			signoutTime = new Date();
		}
	}

	public boolean isComplete() {
		if (signinTime == null || signoutTime == null)
			return false;
		return true;
	}

	public boolean intersects(Session another) {
		long in1 = signinTime.getTime();
		long out1 = signoutTime.getTime();
		long in2 = another.signinTime.getTime();
		long out2 = another.signoutTime.getTime();

		if (out2 < out1 && in2 > in1)
			return true;
		if (out1 < out2 && in1 > in2)
			return true;

		if (out2 > out1 && in2 > in1 && in2 < out1)
			return true;
		if (out1 > out2 && in1 > in2 && in1 < out2)
			return true;

		return false;
	}

	@Override
	public String toString() {
		return "Session: \n" + Util.normalFormat.format(signinTime) + "\n" + Util.normalFormat.format(signoutTime);
	}
}
