package ui.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import main.Application;
import ui.FGFrame;
import ui.components.HintTextField;
import ui.components.ScalableImage;
import util.Util;

@SuppressWarnings("serial")
public class MainView extends View implements MouseListener {

	private ScalableImage backgroundImage;

	private final JPanel foregroundPanel;

	private final JPanel loginPanel;
	private final GridBagConstraints lpgbc = new GridBagConstraints();
	private final JLabel loginLabel;
	private final HintTextField loginField;
	private final JButton loginButton;

	private final JButton adminButton;

	private final ActionListener adminButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			JPanel lpfpanel = new JPanel();

			JLabel pfLabel = new JLabel("Enter the Administrator password to go admin view");
			JPasswordField pf = new JPasswordField();

			lpfpanel.setLayout(new GridLayout(2, 1));
			lpfpanel.add(pfLabel);
			lpfpanel.add(pf);

			int result = JOptionPane.showConfirmDialog(MainView.this, lpfpanel, "Admin Password",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if (result == JOptionPane.CANCEL_OPTION) {
				Application.logFileStream.println(
						"Failed admin login - cannot access admin page without without correct admin password - "
								+ Util.getRightNow());
				return;
			} else {
				String passAttempt = new String(pf.getPassword());
				if (passAttempt.isEmpty()) {
					Application.logFileStream.println(
							"Failed admin login - cannot access admin page without without correct admin password - "
									+ Util.getRightNow());
					return;
				}
				if (Application.appPrefs.adminPassWord.equals(passAttempt)) {
					Application.logFileStream.println("Successful admin login - " + Util.getRightNow());
					ViewCnfg cnfg = new ViewCnfg();
					cnfg.admin = true;
					cnfg.vol = null;
					parent.showNextView(parent.aview, cnfg);
				} else {
					Application.logFileStream.println(
							"Failed admin login - cannot access admin page without without correct admin password - "
									+ Util.getRightNow());
					JOptionPane.showMessageDialog(MainView.this, "Wrong admin password - cannot go to admin view",
							"Wrong password", JOptionPane.ERROR_MESSAGE, null);
				}
			}

		}
	};
	private final ActionListener enterButtonListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String idattempt = loginField.getText();
			if (idattempt.isEmpty())
				return;
			else if (!Util.idIsTaken(idattempt)) {
				JOptionPane.showMessageDialog(MainView.this, "That ID is not in the system", "Volunteer does not Exist",
						JOptionPane.ERROR_MESSAGE, null);
				return;
			}
			ViewCnfg cnfg = new ViewCnfg();
			cnfg.admin = false;
			cnfg.vol = Util.getVolunteer(idattempt);
			parent.showNextView(parent.vview, cnfg);

		}
	};
	private static final double[] colweights = { 2, .5, .05 };
	private static final double[] rowweights = { 1, .5, 0 };

	public MainView(FGFrame parent) {
		super(parent);

		setOpaque(false);

		foregroundPanel = new JPanel();
		loginPanel = new JPanel();

		loginLabel = new JLabel("<html>&nbsp&nbsp Login to Volunteer Account</html>");

		loginField = new HintTextField("Enter ID Here...");
		loginField.setToolTipText("Enter volunteer ID");
		loginField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loginButton.doClick();
			}
		});

		loginButton = new JButton("Log in");
		loginButton.setToolTipText("Log in to volunteer account");
		loginButton.setFont(Util.standoutFont);
		Util.setActionListener(loginButton, enterButtonListener);

		adminButton = new JButton("Admin");
		adminButton.setFont(Util.standoutFont);
		adminButton.setPreferredSize(new Dimension(200, 200));
		adminButton.setToolTipText("Click here if you're an admin");
		Util.setActionListener(adminButton, adminButtonListener);

		layoutView();

		addMouseListener(this);

		revalidate();
	}

	@Override
	public void layoutView() {
		setLayout(new GridBagLayout());

		foregroundPanel.setLayout(new GridBagLayout());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = rowweights[0];
		gbc.weightx = colweights[0];
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		foregroundPanel.add(new JComponent() {
		}, gbc);

		layoutLoginPanel();

		gbc.gridx = 1;
		gbc.weighty = rowweights[1];
		gbc.weightx = colweights[1];
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		foregroundPanel.add(loginPanel, gbc);

		gbc.gridx = 2;
		gbc.weighty = rowweights[2];
		gbc.weightx = colweights[2];
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		// gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbc.insets = FGFrame.sideButtonInsets;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		foregroundPanel.add(adminButton, gbc);

		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;

		foregroundPanel.setOpaque(false);
		this.add(foregroundPanel, gbc);

	}

	private void layoutLoginPanel() {
		loginPanel.setLayout(new GridBagLayout());

		lpgbc.weightx = 1;
		lpgbc.weighty = 0;
		lpgbc.gridx = 0;
		lpgbc.fill = GridBagConstraints.HORIZONTAL;
		lpgbc.insets = new Insets(8, 8, 8, 8);

		loginLabel.setFont(Util.subtitleFont);

		lpgbc.gridy = 0;
		loginPanel.add(loginLabel, lpgbc);

		loginField.setFont(Util.standoutFont);

		lpgbc.gridy = 1;
		loginPanel.add(loginField, lpgbc);

		lpgbc.gridy = 2;
		lpgbc.fill = GridBagConstraints.NONE;
		lpgbc.anchor = GridBagConstraints.EAST;
		lpgbc.ipady = 20;
		lpgbc.ipadx = 20;
		loginPanel.add(loginButton, lpgbc);

		loginPanel.setBorder(
				BorderFactory.createLineBorder(Util.getDarkerColor(loginPanel.getBackground(), 100), 3, true));
		loginPanel.setOpaque(true);
	}

	@Override
	public void cnfgFor(ViewCnfg cnfg) {
		super.cnfgFor(cnfg);

		Image img = Util.getImage("freegeek_frontdesk.jpg").getImage();
		backgroundImage = new ScalableImage(img, false);
		backgroundImage.setOpaque(true);
		this.add(backgroundImage, gbc);

		loginField.clearText();

	}

	@Override
	public void saveMem() {
		backgroundImage = null;
	}

	@Override
	public void beforeClose() {
		this.remove(backgroundImage);
	}

	@Override
	public void afterDisplay() {
	}

	@Override
	public String getFrameTitleString() {
		return null;
	}

	@Override
	public String getBackString() {
		return "Main View";
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		MainView.this.grabFocus();
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
}