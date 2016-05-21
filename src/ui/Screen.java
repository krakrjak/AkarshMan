package ui;

import ui.view.View;
import ui.view.ViewCnfg;
import util.Util;

public class Screen {
	public View view;
	public ViewCnfg viewCnfg;

	public Screen(View view, ViewCnfg viewcnfg) {
		this.view = view;
		viewCnfg = viewcnfg == null ? null : new ViewCnfg(viewcnfg);
	}

	public Screen(View view) {
		this(view, view.viewcnfg);
	}

	public String getBackString() {
		String result = view.getBackString();
		if (viewCnfg != null) {
			if (viewCnfg.vol != null) {
				result += " for " + Util.formatName(viewCnfg.vol);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return (view == null ? "null view" : view.getClass().getSimpleName()) + "  "
				+ (viewCnfg == null ? "null" : (viewCnfg.admin + "   " + viewCnfg.vol));
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() != Screen.class)
			throw new RuntimeException("Trying to compare screen with something else");
		Screen an = (Screen) o;
		return (view == an.view && viewCnfg.equals(viewCnfg));
	}
}
