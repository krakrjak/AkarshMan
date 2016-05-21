package ui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import data.Session;
import data.Volunteer;
import ui.FGFrame;
import util.DataController;
import util.Util;

@SuppressWarnings("serial")
public class AdminView extends View {

	private final JButton addVolButton;
	private final JButton settingsButton;

	private ContentPanel contentPanel;

	private final ActionListener addVolListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ViewCnfg cnfg = new ViewCnfg();
			cnfg.admin = true;
			parent.showNextView(parent.viview, cnfg);
		}
	};
	private final ActionListener settingsListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ViewCnfg cnfg = new ViewCnfg();
			cnfg.admin = true;
			parent.showNextView(parent.sview, cnfg);
		}
	};

	private class ContentPanel extends JPanel {

		private final ArrayList<VolLabel> volLabels = new ArrayList<VolLabel>();

		private final JScrollPane scrollPane;
		private final JPanel content;

		private static final int signiniconSize = 50;
		private static final int volButtonPicSize = 50;
		private final ImageIcon signedinicon = Util.getScaledImage("signedin.png", signiniconSize, signiniconSize);
		private final ImageIcon usericon = Util.getScaledImage("user.png", volButtonPicSize, volButtonPicSize);
		private final ImageIcon removeusericon = Util.getScaledImage("remove_user.png", volButtonPicSize,
				volButtonPicSize);

		private final GridBagConstraints cpgbc = new GridBagConstraints();

		private static final double weight_signin = 0;
		private static final double weight_name = 1;
		private static final double weight_inout = 1;
		private static final double weight_lastaff = 1;
		private static final double weight_info = .2;
		private static final double weight_remove = .2;

		private class VolLabel {
			private final Volunteer vol;

			private final JLabel signedinLabel;
			private final JLabel nameLabel;
			private final JPanel inoutPanel;

			private final JButton inButton;
			private final JButton outButton;

			private final JLabel lastAffLabel;

			private final JButton infoButton;
			private final JButton removeButton;

			private final ActionListener infoButtonListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ViewCnfg viewcnfg = new ViewCnfg();
					viewcnfg.admin = true;
					viewcnfg.editnow = false;
					viewcnfg.vol = vol;
					parent.showNextView(parent.viview, viewcnfg);
				}
			};
			private final ActionListener removeButtonListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(AdminView.this,
							"Are you sure you want to permanently delete " + Util.formatName(vol) + "?",
							"Permanently Delete Volunteer?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
							null);

					if (result == JOptionPane.NO_OPTION)
						return;
					else if (result == JOptionPane.YES_OPTION) {
						Volunteer.volunteers.remove(vol);
						DataController.deleteVol(vol);
						parent.replaceCurrentView(parent.aview, viewcnfg);
					}

				}
			};

			private VolLabel(Volunteer vol) {
				super();
				this.vol = vol;

				signedinLabel = new JLabel();
				signedinLabel.setHorizontalAlignment(SwingConstants.CENTER);

				nameLabel = new JLabel();
				nameLabel.setText(Util.formatName(vol));
				nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
				nameLabel.setFont(Util.standoutFont);

				inButton = new JButton("Sign in");
				inButton.setToolTipText("Manually sign " + Util.formatName(vol) + " in");
				outButton = new JButton("Sign out");
				outButton.setToolTipText("Manually sign " + Util.formatName(vol) + " out");
				inoutPanel = new JPanel();
				inoutPanel.setLayout(new GridBagLayout());
				GridBagConstraints inoutgbc = new GridBagConstraints();
				inoutgbc.fill = GridBagConstraints.VERTICAL;
				inoutgbc.weighty = 1;
				inoutgbc.gridx = 0;
				inoutPanel.add(inButton, inoutgbc);
				inoutgbc.gridx = 1;
				inoutPanel.add(outButton, inoutgbc);

				Util.setActionListener(inButton, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						vol.signin();
						updateText();
					}
				});
				Util.setActionListener(outButton, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						vol.signout();
						updateText();
					}
				});

				lastAffLabel = new JLabel();
				lastAffLabel.setHorizontalAlignment(SwingConstants.CENTER);

				infoButton = new JButton(usericon);
				infoButton.setToolTipText("See volunteer information for " + Util.formatName(vol));
				infoButton.setIcon(usericon);
				Util.setActionListener(infoButton, infoButtonListener);

				removeButton = new JButton();
				removeButton.setToolTipText("Permanently delete " + Util.formatName(vol) + " from the system");
				removeButton.setIcon(removeusericon);
				Util.setActionListener(removeButton, removeButtonListener);

				updateText();

			}

			private void updateText() {
				if (vol.isSignedIn()) {
					signedinLabel.setIcon(signedinicon);
					signedinLabel.setToolTipText("Currently signed in");

					inButton.setEnabled(false);
					outButton.setEnabled(true);

					lastAffLabel.setText("<html><div style=\"text-align: center;\">Last Signed in at:<br>"
							+ Util.normalFormat.format(vol.lastAffiliation()) + "</html>");

				} else {
					signedinLabel.setIcon(null);
					signedinLabel.setPreferredSize(new Dimension(signiniconSize, signiniconSize));
					signedinLabel.setToolTipText("Not signed in");

					inButton.setEnabled(true);
					outButton.setEnabled(false);

					Session lastsess = vol.getLastSession();

					lastAffLabel.setText(lastsess == null ? "None"
							: "<html><div style=\"text-align: center;\">Last Signed out at:<br>"
									+ Util.normalFormat.format(vol.lastAffiliation()) + "</html>");

				}
			}

			private void addToContentPanel() {
				cpgbc.gridx = 0;
				cpgbc.weightx = weight_signin;
				content.add(signedinLabel, cpgbc);

				cpgbc.gridx = 1;
				cpgbc.weightx = weight_name;
				content.add(nameLabel, cpgbc);

				cpgbc.gridx = 2;
				cpgbc.weightx = weight_inout;
				content.add(inoutPanel, cpgbc);

				cpgbc.gridx = 3;
				cpgbc.weightx = weight_lastaff;
				content.add(lastAffLabel, cpgbc);

				cpgbc.gridx = 4;
				cpgbc.weightx = weight_info;
				content.add(infoButton, cpgbc);

				cpgbc.gridx = 5;
				cpgbc.weightx = weight_remove;
				content.add(removeButton, cpgbc);
			}

		}

		private ContentPanel() {
			scrollPane = new JScrollPane();
			content = new JPanel();
			scrollPane.setViewportView(content);

			content.setLayout(new GridBagLayout());
			cpgbc.gridy = 0;
			cpgbc.fill = GridBagConstraints.HORIZONTAL;
			cpgbc.weighty = 1;
			cpgbc.insets = new Insets(0, 0, 10, 0);
			cpgbc.ipadx = 25;

			cpgbc.gridx = 0;
			JLabel signedinTitle = new JLabel("<html><div style=\"text-align: center;\">Signed<br>In</html>");
			signedinTitle.setFont(Util.standoutFont);
			signedinTitle.setHorizontalAlignment(SwingConstants.CENTER);
			content.add(signedinTitle, cpgbc);
			cpgbc.gridx++;

			JLabel volTitle = new JLabel("<html><div style=\"text-align: center;\">Volunteer Name - ID</html>");
			volTitle.setFont(Util.standoutFont);
			volTitle.setHorizontalAlignment(SwingConstants.CENTER);
			content.add(volTitle, cpgbc);
			cpgbc.gridx++;

			JLabel inoutTitle = new JLabel("<html><div style=\"text-align: center;\">Sign In /<br>Sign Out</html>");
			inoutTitle.setFont(Util.standoutFont);
			inoutTitle.setHorizontalAlignment(SwingConstants.CENTER);
			content.add(inoutTitle, cpgbc);
			cpgbc.gridx++;

			JLabel lastAffTitle = new JLabel("<html><div style=\"text-align: center;\">Last Contact</html>");
			lastAffTitle.setFont(Util.standoutFont);
			lastAffTitle.setHorizontalAlignment(SwingConstants.CENTER);
			content.add(lastAffTitle, cpgbc);
			cpgbc.gridx++;

			JLabel volinfoTitle = new JLabel("<html><div style=\"text-align: center;\">Volunteer<br>Info</html>");
			volinfoTitle.setFont(Util.standoutFont);
			volinfoTitle.setHorizontalAlignment(SwingConstants.CENTER);
			content.add(volinfoTitle, cpgbc);
			cpgbc.gridx++;

			JLabel deletevolTitle = new JLabel("<html><div style=\"text-align: center;\">Delete<br>Volunteer</html>");
			deletevolTitle.setFont(Util.standoutFont);
			deletevolTitle.setHorizontalAlignment(SwingConstants.CENTER);
			content.add(deletevolTitle, cpgbc);
			cpgbc.gridx++;
			cpgbc.gridy++;

			cpgbc.gridwidth = 6;
			cpgbc.gridx = 0;
			JSeparator titlesep = new JSeparator(SwingConstants.HORIZONTAL);
			content.add(titlesep, cpgbc);
			cpgbc.gridy++;

			for (Volunteer vol : Volunteer.volunteers) {
				cpgbc.gridwidth = 1;
				VolLabel vl = new VolLabel(vol);
				volLabels.add(vl);
				vl.addToContentPanel();
				cpgbc.gridy++;

				cpgbc.gridwidth = 6;
				cpgbc.gridx = 0;
				JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
				content.add(sep, cpgbc);

				cpgbc.gridy++;
			}

			JScrollBar horbar = scrollPane.getHorizontalScrollBar();
			horbar.setPreferredSize(new Dimension(0, 30));
			horbar.setUnitIncrement(10);

			JScrollBar bar = scrollPane.getVerticalScrollBar();
			bar.setPreferredSize(new Dimension(30, 0));
			bar.setUnitIncrement(10);

			setLayout(new BorderLayout());

			addComponentListener(new ComponentListener() {
				boolean previouslybigger = true;

				@Override
				public void componentResized(ComponentEvent e) {
					boolean bigger = (scrollPane.getPreferredSize().getHeight() > ContentPanel.this.getSize()
							.getHeight());

					if (previouslybigger != bigger) {
						centertopit();
					}

					previouslybigger = bigger;
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
			});

		}

		public void centertopit() {
			if (scrollPane.getPreferredSize().getHeight() > this.getSize().getHeight()) {
				this.add(scrollPane, BorderLayout.CENTER);
			} else {
				this.add(scrollPane, BorderLayout.NORTH);
			}
			revalidate();
			repaint();
		}
	}

	public AdminView(FGFrame parent) {
		super(parent);
		titleLabel.setText("Administrator");

		int bannerPicSize = 100;
		addVolButton = new JButton(Util.getScaledImage("add_user.png", bannerPicSize, bannerPicSize));
		settingsButton = new JButton(Util.getScaledImage("settings.png", bannerPicSize, bannerPicSize));

		subPanel.setLayout(new GridBagLayout());
		GridBagConstraints subgbc = new GridBagConstraints();
		subgbc.fill = GridBagConstraints.BOTH;
		subgbc.weightx = 1;
		subgbc.gridx = 0;
		subPanel.add(new JComponent() {
		}, subgbc);
		subgbc.weightx = 0;
		subgbc.gridx = 1;
		subgbc.insets = FGFrame.sideButtonInsets;
		subPanel.add(addVolButton, subgbc);
		subgbc.gridx = 2;
		subPanel.add(settingsButton, subgbc);

		Util.setActionListener(addVolButton, addVolListener);
		Util.setActionListener(settingsButton, settingsListener);

		layoutView();
	}

	@Override
	public void layoutView() {
		setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.BOTH;

		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(titleLabel, gbc);

		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		this.add(subPanel, gbc);
	}

	@Override
	public void cnfgFor(ViewCnfg cnfg) {
		super.cnfgFor(cnfg);
		// just in case, don't give volunteers and non admins permission to see
		if (!cnfg.admin || cnfg.vol != null)
			throw new RuntimeException("Volunteer or non admin trying to access admin view");

		contentPanel = new ContentPanel();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		this.add(contentPanel, gbc);

	}

	@Override
	public void saveMem() {
		contentPanel = null;
	}

	@Override
	public void beforeClose() {
		if (contentPanel != null) {
			this.remove(contentPanel);
		}

	}

	@Override
	public void afterDisplay() {
		contentPanel.centertopit();
	}

	@Override
	public String getFrameTitleString() {

		return "Administrator View";
	}

	@Override
	public String getBackString() {
		return "Adminstrator View";
	}

}
