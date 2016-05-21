package ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;

import data.AppPrefs;
import main.Application;
import ui.view.AdminView;
import ui.view.MainView;
import ui.view.SettingsView;
import ui.view.TimeChartView;
import ui.view.View;
import ui.view.ViewCnfg;
import ui.view.VolInfoView;
import ui.view.VolView;
import util.Util;

/**
 * This is the main JFrame for the entire program, accessed by FGFrame.frame
 * 
 * This application frame holds all the views (lightweight) in memory but
 * generates new dialogs (heavyweight) from the views when needed
 * 
 * @author akars
 *
 */
@SuppressWarnings("serial")
public class FGFrame extends JFrame implements WindowListener, MouseListener {

	public static final String defaultTitle = "Free Geek Volunteering";

	public static final Insets sideButtonInsets = new Insets(10, 10, 10, 10);
	// (5, 5, 146, 146); // 0,0,156,156

	private final Container pane;

	private final GridBagConstraints gbc = new GridBagConstraints();

	private ArrayList<Screen> screenChain = new ArrayList<Screen>();

	// private View currentView = null;

	public final BackButton backButton;

	public final TimeChartView tcview;
	public final VolInfoView viview;
	public final AdminView aview;
	public final MainView mview;
	public final VolView vview;
	public final SettingsView sview;

	private boolean actualtransition = true;

	static {
	}

	public class BackButton extends JButton implements ActionListener {
		// JButton runs the last ActionListener added first

		@Override
		public void actionPerformed(ActionEvent e) {
			actualtransition = true;
			currentView().beforeClose();
			if (!actualtransition) {
				actualtransition = true;
				return;
			}
			currentView().saveMem();
			System.gc();

			screenChain.remove(screenChain.size() - 1);
			showScreen(currentScreen());
		}

		public BackButton() {
			super(Util.getScaledImage("back.png", 100, 100));
			Util.setActionListener(this, this);

			setBorder(null);

		}

	}

	public FGFrame() {
		super(defaultTitle);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

		pane = getContentPane();

		backButton = new BackButton();

		tcview = new TimeChartView(this);
		viview = new VolInfoView(this);
		aview = new AdminView(this);
		mview = new MainView(this);
		vview = new VolView(this);
		sview = new SettingsView(this);

		init();

		setMinimumSize(new Dimension(800, 500));
	}

	private void init() {
		pane.setLayout(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = sideButtonInsets;
		gbc.ipadx = 40;
		gbc.ipady = 40;
		pane.add(backButton, gbc);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0, 0, 0, 0);

		setIconImage(Application.appImage);
	}

	/**
	 * Recommended that you use show next view to establish working of back
	 * button<br>
	 * This method shows the param view and sets it as the current view and
	 * makes sure everything works according to the view<br>
	 * VIEW ALREADY NEEDS TO BE CONFIGURED BEFORE THIS
	 * 
	 * @param view
	 */
	private void showScreen(Screen screen) {
		View view = screen.view;

		view.setVisible(false);

		view.cnfgFor(screen.viewCnfg);

		view.revalidate();
		for (Component cmpt : pane.getComponents())
			if (cmpt != backButton) {
				pane.remove(cmpt);
			}

		pane.add(view, gbc);

		if (getPrevScreen(screen) == null) {
			backButton.setVisible(false);
		} else {
			backButton.setVisible(true);
			backButton.setToolTipText("Go back to " + getPrevScreen(screen).getBackString());
		}

		String titleString = view.getFrameTitleString();
		setTitle(defaultTitle + (titleString == null ? "" : (" - " + titleString)));

		revalidate();
		view.setVisible(true);
		this.repaint();

		view.updateEverything();
		view.afterDisplay();
		view.updateEverything();

	}

	public void showNextView(View view, ViewCnfg cnfg) {
		if (currentView() != null) {
			actualtransition = true;
			currentView().beforeClose();
			if (!actualtransition) {
				actualtransition = true;
				return;
			}
			currentView().setVisible(false);
			currentView().saveMem();
			System.gc();
		}
		Screen screen = new Screen(view, cnfg);
		if (screenChain.contains(screen)) {
			for (int i = screenChain.size() - 1; i >= 0; i--) {
				if (!screenChain.get(i).equals(screen)) {
					screenChain.remove(i);
				} else {
					showScreen(screen);
					return;
				}
			}
		} else {
			screenChain.add(screen);
			showScreen(screen);
		}
	}

	public void replaceCurrentView(View view, ViewCnfg cnfg) {
		if (currentView() != null) {
			actualtransition = true;
			currentView().beforeClose();
			if (!actualtransition) {
				actualtransition = true;
				return;
			}
			currentView().setVisible(false);
			currentView().saveMem();
			System.gc();
		}
		Screen screen = new Screen(view, cnfg);
		screenChain.set(screenChain.size() - 1, screen);
		showScreen(screen);
	}

	/**
	 * Get current view
	 * 
	 * @return
	 */
	public View currentView() {
		Screen current = currentScreen();
		if (current == null)
			return null;
		return current.view;
	}

	public Screen currentScreen() {
		if (screenChain.isEmpty())
			return null;
		return screenChain.get(screenChain.size() - 1);
	}

	public Screen getPrevScreen(Screen screen) {
		if (screenChain.isEmpty() || screenChain.size() == 1)
			return null;
		return screenChain.get(screenChain.lastIndexOf(screen) - 1);
	}

	public void printScreenChain() {
		for (Screen s : screenChain) {
			System.out.println(s);
		}
		System.out.println("\n\n");
	}

	public void cancelTransition() {
		actualtransition = false;
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		JPanel lpfpanel = new JPanel();

		JLabel pfLabel = new JLabel("Enter the Administrator password to close application");
		JPasswordField pf = new JPasswordField();

		lpfpanel.setLayout(new GridLayout(2, 1));
		lpfpanel.add(pfLabel);
		lpfpanel.add(pf);

		int result = JOptionPane.showConfirmDialog(this, lpfpanel, "Admin Password", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null);

		if (result == JOptionPane.OK_OPTION) {
			String pass = new String(pf.getPassword());

			if (pass.isEmpty()) {
				Application.logFileStream
						.println("Failed close attempt - cannot close program without correct admin password - "
								+ Util.getRightNow());
				return;
			}
			if (!Application.appPrefs.adminPassWord.equals(pass)) {
				Application.logFileStream
						.println("Failed close attempt - cannot close program without correct admin password - "
								+ Util.getRightNow());
				JOptionPane.showMessageDialog(this, "Wrong admin password - cannot close application", "Wrong password",
						JOptionPane.ERROR_MESSAGE, null);
				return;
			}
		} else {
			Application.logFileStream
					.println("Failed close attempt - cannot close program without correct admin password - "
							+ Util.getRightNow());
			return;
		}

		Application.logFileStream.println("Successful close attempt - closing program - " + Util.getRightNow());

		while (true) {
			actualtransition = true;
			currentView().beforeClose();
			if (!actualtransition) {
				actualtransition = true;
				return;
			}
			currentView().saveMem();
			System.gc();
			screenChain.remove(screenChain.size() - 1);

			Screen screen = currentScreen();
			if (screen == null) {
				break;
			}
			showScreen(screen);
		}

		dispose();
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	public void setupFrame(AppPrefs prefs) {
		setVisible(false);
		dispose();
		if (prefs.fullScreen) {
			setExtendedState(Frame.MAXIMIZED_BOTH);
			setUndecorated(true);
		} else {
			setExtendedState(Frame.NORMAL);
			setUndecorated(false);
		}
		revalidate();
		setVisible(true);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public String toString() {
		return currentScreen().toString();
	}
}
