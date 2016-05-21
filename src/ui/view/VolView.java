package ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ui.FGFrame;
import ui.components.ScalableImage;
import util.Util;

@SuppressWarnings("serial")
public class VolView extends View implements ComponentListener {

	private final JLabel subLabel;

	private final JPanel signPanel;
	private final JButton signinButton;
	private final JButton signoutButton;

	private final JPanel imagePanel;
	private ScalableImage bannerImage;

	private final JButton totalTimeButton;
	private final JButton infoButton;

	public VolView(FGFrame parent) {
		super(parent);

		subLabel = new JLabel();
		subLabel.setFont(Util.subtitleFont);

		signPanel = new JPanel();
		signinButton = new JButton("Sign in");
		signinButton.setFont(Util.subtitleFont);
		Util.setActionListener(signinButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewcnfg.vol.signin();
				updateButtons();
			}
		});
		signinButton.setToolTipText("Sign in to Free Geek");
		signoutButton = new JButton("Sign out");
		signoutButton.setFont(Util.subtitleFont);
		Util.setActionListener(signoutButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewcnfg.vol.signout();
				updateButtons();
				totalTimeButton.setText("<html><div style=\"text-align: center;\">Total Time Volunteered<br>"
						+ Util.formatTime(viewcnfg.vol.getTotalVolunteeringTime()) + "</html>");
			}
		});
		signoutButton.setToolTipText("Sign out of Free Geek");

		imagePanel = new JPanel();

		totalTimeButton = new JButton();
		totalTimeButton.setFont(Util.standoutFont);
		Util.setActionListener(totalTimeButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewCnfg cnfg = new ViewCnfg();
				cnfg.admin = false;
				cnfg.vol = viewcnfg.vol;
				parent.showNextView(parent.tcview, cnfg);
			}
		});
		totalTimeButton.setToolTipText("See your time chart");

		infoButton = new JButton("See Information");
		infoButton.setFont(Util.standoutFont);
		Util.setActionListener(infoButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewCnfg cnfg = new ViewCnfg();
				cnfg.admin = false;
				cnfg.vol = viewcnfg.vol;
				parent.showNextView(parent.viview, cnfg);
			}
		});
		infoButton.setToolTipText("See your information");

		layoutView();
		addComponentListener(this);
	}

	@Override
	public void layoutView() {
		setLayout(new GridBagLayout());

		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		this.add(titleLabel, gbc);

		subPanel.add(subLabel);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		this.add(subPanel, gbc);

		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		this.add(imagePanel, gbc);

		signPanel.setLayout(new GridLayout(1, 2));
		signPanel.add(signinButton);
		signPanel.add(signoutButton);

		gbc.weightx = 1;
		gbc.weighty = .5;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.insets = FGFrame.sideButtonInsets;
		gbc.ipady = 100;
		this.add(signPanel, gbc);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.ipadx = 50;
		gbc.ipady = 50;
		this.add(totalTimeButton, gbc);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbc.ipadx = 50;
		gbc.ipady = 50;
		this.add(infoButton, gbc);

	}

	@Override
	public void cnfgFor(ViewCnfg cnfg) {
		super.cnfgFor(cnfg);

		if (viewcnfg.vol == null)
			throw new RuntimeException("Cannot open VolView with no volunteer");

		titleLabel.setText(viewcnfg.vol.getName());
		subLabel.setText(viewcnfg.vol.getID());

		totalTimeButton.setText("<html><div style=\"text-align: center;\">Total Time Volunteered<br>"
				+ Util.formatTime(viewcnfg.vol.getTotalVolunteeringTime()) + "</html>");

		updateButtons();

		Image image = Util.getImage("freegeek_banner.jpg").getImage();
		bannerImage = new ScalableImage(image, true);
		imagePanel.setLayout(new GridLayout(1, 1));
		imagePanel.add(bannerImage);

	}

	@Override
	public void saveMem() {
		bannerImage = null;
	}

	@Override
	public void beforeClose() {
		imagePanel.remove(bannerImage);
	}

	@Override
	public void afterDisplay() {

	}

	@Override
	public String getFrameTitleString() {
		return "Volunteer Account - " + Util.formatName(viewcnfg.vol);
	}

	@Override
	public String getBackString() {
		return "Volunteer Account";
	}

	public void updateButtons() {
		boolean signedin = viewcnfg.vol.isSignedIn();

		signinButton.setEnabled(!signedin);
		signoutButton.setEnabled(signedin);

	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (bannerImage == null)
			return;
		if (this.getSize().getHeight() < getPreferredSize().getHeight()) {
			bannerImage.setVisible(false);
		} else {
			bannerImage.setVisible(true);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

}
