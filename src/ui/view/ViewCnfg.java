package ui.view;

import data.Volunteer;

public class ViewCnfg {

	public Volunteer vol;
	public boolean admin;

	public boolean editnow;

	public ViewCnfg() {
		vol = null;
		admin = false;
		editnow = false;
	}

	public ViewCnfg(ViewCnfg another) {
		vol = another.vol;
		admin = another.admin;
		editnow = another.editnow;
	}

	@Override
	public boolean equals(Object another) {
		if (another.getClass() != ViewCnfg.class)
			throw new RuntimeException("Tried to compare view cnfg with something else");
		ViewCnfg an = (ViewCnfg) another;
		return (vol == an.vol && admin == an.admin && editnow == an.editnow);
	}

}
