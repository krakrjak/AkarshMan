package ui.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import data.AppPrefs;
import main.Application;
import ui.FGFrame;
import util.Util;

@SuppressWarnings("serial")
public class SettingsView extends View {

	private ContentPanel contentPanel;

	private class ContentPanel extends JScrollPane {

		private final JPanel content;

		private final GridBagConstraints cpgbc;

		private final JLabel adminPassLabel;
		private final JTextField adminPassField;

		private final JLabel fullScreenLabel;
		private final JCheckBox fullScreenBox;

		private final JLabel loadSaveTypeLabel;

		private final JTextField txtLocaleField;

		private final JButton applyButton;

		private Runnable afterApply = null;

		private final ActionListener trySaveAppPrefs = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					applyChanges();
					if (afterApply != null) {
						afterApply.run();
					}
					if (parent != null) {
						parent.setupFrame(Application.appPrefs);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(SettingsView.this, e.getMessage(), "Can't Save AppPrefs",
							JOptionPane.ERROR_MESSAGE, null);
				}
			}
		};

		public ContentPanel() {
			super();

			content = new JPanel();

			cpgbc = new GridBagConstraints();

			adminPassLabel = new JLabel("<html>&nbsp&nbsp Admin Password</html>");
			adminPassLabel.setFont(Util.standoutFont);

			adminPassField = new JTextField();

			fullScreenLabel = new JLabel("<html>&nbsp&nbsp Full Screen</html>");
			fullScreenLabel.setFont(Util.standoutFont);
			fullScreenBox = new JCheckBox("Full screen");

			loadSaveTypeLabel = new JLabel("<html>&nbsp&nbsp Load/Save Data Directory</html>");
			loadSaveTypeLabel.setFont(Util.standoutFont);

			txtLocaleField = new JTextField();
			txtLocaleField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					txtLocaleField.selectAll();
				}

				@Override
				public void focusLost(FocusEvent e) {
				}
			});

			applyButton = new JButton("Save Settings");
			applyButton.setEnabled(true);
			applyButton.setFont(Util.standoutFont);

			layoutPanel();

			setViewportView(content);

			Util.setActionListener(applyButton, trySaveAppPrefs);

		}

		public void layoutPanel() {
			content.setLayout(new GridBagLayout());

			JComponent blank1 = new JComponent() {
			};
			JComponent blank2 = new JComponent() {
			};

			cpgbc.gridx = 0;
			cpgbc.gridy = 0;
			cpgbc.weightx = 1;
			cpgbc.weighty = 0;
			cpgbc.fill = GridBagConstraints.HORIZONTAL;

			content.add(blank1, cpgbc);
			cpgbc.gridx = 2;
			content.add(blank2, cpgbc);
			cpgbc.gridx = 1;
			cpgbc.weightx = .2;

			Insets padbtwnfields = new Insets(0, 0, 30, 0);
			Insets toppad = new Insets(30, 0, 0, 0);
			Insets noinsets = new Insets(0, 0, 0, 0);

			cpgbc.insets = toppad;
			content.add(adminPassLabel, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = padbtwnfields;
			content.add(adminPassField, cpgbc);
			cpgbc.gridy++;

			cpgbc.insets = noinsets;
			content.add(fullScreenLabel, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = padbtwnfields;
			content.add(fullScreenBox, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = noinsets;
			content.add(loadSaveTypeLabel, cpgbc);
			cpgbc.gridy++;
			content.add(txtLocaleField, cpgbc);
			cpgbc.gridy++;

			cpgbc.insets = FGFrame.sideButtonInsets;
			cpgbc.ipady = 200;
			cpgbc.ipadx = 200;
			cpgbc.fill = GridBagConstraints.NONE;

			content.add(applyButton, cpgbc);
			cpgbc.gridy++;
		}

		private void setupText(AppPrefs prefs) {
			if (prefs == null) {
				adminPassField.setText("");
				fullScreenBox.setSelected(true);
				txtLocaleField.setText(Application.appPath.getAbsolutePath() + "\\Volunteer Data");

			} else {
				adminPassField.setText(prefs.adminPassWord);
				fullScreenBox.setSelected(prefs.fullScreen);
				txtLocaleField.setText(prefs.txtDataDir.getAbsolutePath());

			}
			txtLocaleField.setEnabled(true);
		}
	}

	public SettingsView(FGFrame parent) {
		super(parent);

		titleLabel.setText("Settings");

		layoutView();
	}

	@Override
	public void layoutView() {
		setLayout(new GridBagLayout());

		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		this.add(titleLabel, gbc);

	}

	@Override
	public void cnfgFor(ViewCnfg cnfg) {
		super.cnfgFor(cnfg);

		if (!cnfg.admin || cnfg.vol != null)
			throw new RuntimeException("Can't cnfg settingsview for non admins or volunteers");

		contentPanel = new ContentPanel();

		JScrollBar horbar = contentPanel.getHorizontalScrollBar();
		horbar.setPreferredSize(new Dimension(0, 30));
		horbar.setUnitIncrement(10);

		JScrollBar bar = contentPanel.getVerticalScrollBar();
		bar.setPreferredSize(new Dimension(30, 0));
		bar.setUnitIncrement(10);

		contentPanel.setupText(Application.firstRun ? null : Application.appPrefs);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(contentPanel, gbc);

		revalidate();
	}

	public void applyChanges() throws Exception {
		if (contentPanel.adminPassField.getText().isEmpty())
			throw new Exception("No password given");
		String filedir = contentPanel.txtLocaleField.getText();
		File dir = new File(filedir);

		if (!dir.isDirectory())
			throw new Exception("Save directory not specified");
		try {
			dir.mkdirs();
		} catch (Exception e) {
			throw new Exception(
					"<html>Unable to create directories<br>You may have to create the directory manually and then run the program</html>");
		}
		AppPrefs ap = new AppPrefs(contentPanel.adminPassField.getText());
		ap.fullScreen = contentPanel.fullScreenBox.isSelected();
		ap.txtDataDir = new File(contentPanel.txtLocaleField.getText());

		Application.appPrefs = ap;
	}

	public void setAfterApplyRunnable(Runnable target) {
		contentPanel.afterApply = target;
	}

	@Override
	public void saveMem() {
		contentPanel = null;
	}

	@Override
	public void beforeClose() {
		this.remove(contentPanel);
	}

	@Override
	public void afterDisplay() {
	}

	@Override
	public String getFrameTitleString() {
		return "Settings";
	}

	@Override
	public String getBackString() {
		return "Settings";
	}

}
