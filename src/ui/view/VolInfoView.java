package ui.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import data.Volunteer;
import ui.FGFrame;
import util.Util;

@SuppressWarnings("serial")
public class VolInfoView extends View {

	private final JLabel subLabel;

	private ContentPanel contentPanel;

	private static final Object[] ages;

	private class ContentPanel extends JScrollPane {
		private final GridBagConstraints cpgbc = new GridBagConstraints();

		private final ImageIcon editusericon;

		private final JPanel content;

		// 0 for creating new
		// 1 for admin editing vol
		// 2 for non editable vol show (admin or not)
		int type = -1;

		private final JLabel nameLabel = new JLabel();
		private JTextField nameField;

		private final JLabel idLabel = new JLabel();
		private JPanel idoptionpanel = new JPanel();
		private JRadioButton chooseIDrb = new JRadioButton("Custom ID");
		private JRadioButton generateIDrb = new JRadioButton("Generate random ID");
		private JRadioButton phonenumIDrb = new JRadioButton("Use last 4 digits of phone number");
		private JTextField idField;

		private JRadioButton lastIDrb = null;
		// focus listener to track if id is valid

		private final JLabel genderLabel = new JLabel();
		private JPanel genderoptionpanel = new JPanel();
		private JRadioButton malerb = new JRadioButton("Male");
		private JRadioButton femalerb = new JRadioButton("Female");
		private JLabel genderField;

		private final JLabel ageLabel = new JLabel();
		private JLabel ageFieldLabel;
		private JComboBox<Object> ageBox;

		private final JLabel phoneLabel = new JLabel();
		private JTextField phoneField;

		private final JLabel emailLabel = new JLabel();
		private JTextField emailField;

		private final JLabel addressLabel = new JLabel();
		private JTextField addressField;

		private final JButton createApplyButton = new JButton();

		private final JButton totalHoursButton = new JButton();

		private final ActionListener createVolListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!viewcnfg.admin)
					return;

				Volunteer newvol = createNewVolunteer();
				if (newvol == null)
					return;
				Volunteer.volunteers.add(newvol);

				viewcnfg.vol = newvol;
				createApplyButton.setEnabled(false);
				parent.replaceCurrentView(VolInfoView.this, viewcnfg);

			}
		};
		private final ActionListener saveChangesListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!viewcnfg.admin)
					return;
				boolean crash = saveAllChanges();
				if (!crash) {
					viewcnfg.editnow = false;
					createApplyButton.setEnabled(false);
					parent.replaceCurrentView(VolInfoView.this, viewcnfg);
				}
			}
		};
		private final ActionListener editVolListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!viewcnfg.admin)
					return;
				viewcnfg.editnow = true;
				parent.replaceCurrentView(VolInfoView.this, viewcnfg);
			}
		};

		public ContentPanel() {
			super();
			content = new JPanel();
			content.setLayout(new GridBagLayout());

			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new GridBagLayout());

			Insets padbtwnfields = new Insets(0, 0, 30, 0);
			Insets toppad = new Insets(15, 0, 0, 0);
			Insets noinsets = new Insets(0, 0, 0, 0);

			cpgbc.fill = GridBagConstraints.BOTH;
			cpgbc.weightx = 1.0;
			cpgbc.weighty = 1.0;
			cpgbc.gridx = 0;
			cpgbc.gridy = 0;
			cpgbc.ipady = 0;
			cpgbc.gridwidth = 2;

			if (viewcnfg.vol == null && viewcnfg.admin) {
				// creating new vol
				type = 0;
				nameField = new JTextField();
				idField = new JTextField();
				ageBox = new JComboBox<Object>(ages);
				phoneField = new JTextField();
				emailField = new JTextField();
				addressField = new JTextField();

			} else if (viewcnfg.vol != null && viewcnfg.admin && viewcnfg.editnow) {
				// admin editing
				type = 1;
				nameField = new JTextField();
				idField = new JTextField();
				ageBox = new JComboBox<Object>(ages);
				phoneField = new JTextField();
				emailField = new JTextField();
				addressField = new JTextField();

			} else if (viewcnfg.vol != null) {
				// showing volunteer stuff not editable
				type = 2;
				nameField = new JTextField();
				idField = new JTextField();
				ageFieldLabel = new JLabel("0");
				genderField = new JLabel();
				phoneField = new JTextField();
				emailField = new JTextField();
				addressField = new JTextField();
			}

			if (type == 2 && viewcnfg.admin) {
				editusericon = Util.getScaledImage("edit_user.png", 100, 100);
			} else {
				editusericon = null;
			}

			nameLabel.setFont(Util.standoutFont);
			nameLabel.setText("<html>&nbsp&nbsp Name" + (type < 2 ? " <font color=\"red\">*</font>" : "") + "</html>");

			idLabel.setFont(Util.standoutFont);
			idLabel.setText("<html>&nbsp&nbsp ID" + (type < 2 ? " <font color=\"red\">*</font>" : "") + "</html>");

			genderLabel.setFont(Util.standoutFont);
			genderLabel
					.setText("<html>&nbsp&nbsp Gender" + (type < 2 ? " <font color=\"red\">*</font>" : "") + "</html>");

			ageLabel.setFont(Util.standoutFont);
			ageLabel.setText("<html>&nbsp&nbsp Age" + (type < 2 ? " <font color=\"red\">*</font>" : "") + "</html>");

			phoneLabel.setFont(Util.standoutFont);
			phoneLabel.setText("<html>&nbsp&nbsp Phone number</html>");

			emailLabel.setFont(Util.standoutFont);
			emailLabel.setText("<html>&nbsp&nbsp Email</html>");

			addressLabel.setFont(Util.standoutFont);
			addressLabel.setText("<html>&nbsp&nbsp Address</html>");

			cpgbc.insets = toppad;
			centerPanel.add(nameLabel, cpgbc);
			cpgbc.insets = noinsets;
			cpgbc.gridy++;
			cpgbc.insets = padbtwnfields;
			centerPanel.add(nameField, cpgbc);
			cpgbc.gridy++;

			cpgbc.insets = noinsets;
			centerPanel.add(idLabel, cpgbc);
			cpgbc.gridy++;
			if (type != 2) {
				ButtonGroup idbg = new ButtonGroup();
				idbg.add(chooseIDrb);
				idbg.add(generateIDrb);
				idbg.add(phonenumIDrb);

				idField.addKeyListener(new KeyAdapter() {

					@Override
					public void keyTyped(KeyEvent e) {
						if (idField.getText().length() > 4) {
							e.consume();
						}
					}
				});
				generateIDrb.setVerticalAlignment(SwingConstants.BOTTOM);
				generateIDrb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						lastIDrb = generateIDrb;
						idField.setEnabled(false);
						idField.setText(Util.generateRandomID());
					}
				});
				phonenumIDrb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						lastIDrb = phonenumIDrb;
						idField.setEnabled(false);
						idField.setText("Last 4 of phone number");
					}
				});
				chooseIDrb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (lastIDrb == phonenumIDrb) {
							idField.setText("");
						}
						lastIDrb = chooseIDrb;
						idField.setEnabled(true);
					}
				});

				phonenumIDrb.doClick();

				idoptionpanel.setLayout(new GridLayout(3, 1));
				idoptionpanel.add(chooseIDrb);
				idoptionpanel.add(generateIDrb);
				idoptionpanel.add(phonenumIDrb);

				centerPanel.add(idoptionpanel, cpgbc);
				cpgbc.gridy++;
			}
			cpgbc.insets = padbtwnfields;
			centerPanel.add(idField, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = noinsets;

			centerPanel.add(genderLabel, cpgbc);
			cpgbc.gridy++;
			if (type == 2) {
				cpgbc.insets = padbtwnfields;
				centerPanel.add(genderField, cpgbc);
			} else {
				ButtonGroup genderbg = new ButtonGroup();
				genderbg.add(malerb);
				genderbg.add(femalerb);
				genderoptionpanel.setLayout(new GridLayout(2, 1));
				genderoptionpanel.add(malerb);
				genderoptionpanel.add(femalerb);

				cpgbc.insets = padbtwnfields;
				centerPanel.add(genderoptionpanel, cpgbc);
			}
			cpgbc.gridy++;
			cpgbc.insets = noinsets;

			centerPanel.add(ageLabel, cpgbc);
			cpgbc.gridy++;

			cpgbc.insets = padbtwnfields;
			centerPanel.add(type < 2 ? ageBox : ageFieldLabel, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = noinsets;

			centerPanel.add(phoneLabel, cpgbc);
			cpgbc.gridy++;
			phoneField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					try {
						phoneField.setText(Util.formatPhoneNumber(phoneField.getText()));
					} catch (RuntimeException ex) {
						// JOptionPane.showMessageDialog(VolInfoView.content,
						// "<html><div style=\"text-align:
						// center;\"><strong>Warning</strong><br>That is not a
						// valid phone number</html>",
						// "Not a valid phone number",
						// JOptionPane.WARNING_MESSAGE, null);
					}
				}
			});
			phoneField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if ((e.getKeyChar() < '0' || e.getKeyChar() > '9')) {
						e.consume();
					}
					if (phoneField.getText().length() > 9) {
						e.consume();
					}
				}
			});
			cpgbc.insets = padbtwnfields;
			centerPanel.add(phoneField, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = noinsets;

			centerPanel.add(emailLabel, cpgbc);
			cpgbc.gridy++;
			emailField.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (!Util.isValidEmail(emailField.getText())) {
						// JOptionPane.showMessageDialog(VolInfoView.content,
						// "<html><div style=\"text-align:
						// center;\"><strong>Warning</strong><br>That is not a
						// valid email</html>",
						// "Not a valid email", JOptionPane.WARNING_MESSAGE,
						// null);
					}
				}
			});
			cpgbc.insets = padbtwnfields;
			centerPanel.add(emailField, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = noinsets;

			centerPanel.add(addressLabel, cpgbc);
			cpgbc.gridy++;
			cpgbc.insets = padbtwnfields;
			centerPanel.add(addressField, cpgbc);
			cpgbc.gridy++;

			cpgbc.ipady = 100;
			cpgbc.weightx = 5;
			cpgbc.weighty = 0;
			cpgbc.gridwidth = viewcnfg.admin ? 1 : 2;
			if (type > 0) {
				totalHoursButton.setFont(Util.standoutFont);
				totalHoursButton.setText("<html><div style=\"text-align: center;\">Total Hours:<br>"
						+ Util.formatTime(viewcnfg.vol.getTotalVolunteeringTime()) + "</html>");
				totalHoursButton.setHorizontalAlignment(SwingConstants.CENTER);
				totalHoursButton.setToolTipText(
						(viewcnfg.admin) ? "See the time chart for content volunteer" : "See your time chart");

				JPanel totalHoursHolder = new JPanel();
				totalHoursHolder.setLayout(new GridBagLayout());
				if (type != 1) {
					totalHoursHolder.add(totalHoursButton);
				}

				centerPanel.add(totalHoursHolder, cpgbc);
			} else {
				centerPanel.add(new JComponent() {
				}, cpgbc);
			}
			if (viewcnfg.admin) {
				cpgbc.gridx = 1;
				cpgbc.weightx = 1;
				createApplyButton.setFont(Util.standoutFont);
				centerPanel.add(createApplyButton, cpgbc);
				cpgbc.gridy++;
			}

			JLabel blank1 = new JLabel();
			JLabel blank2 = new JLabel();

			GridBagConstraints maingbc = new GridBagConstraints();
			maingbc.fill = GridBagConstraints.BOTH;
			maingbc.weightx = 1.0;
			maingbc.weighty = 1.0;
			maingbc.gridx = 0;
			maingbc.gridy = 0;

			content.add(blank1, maingbc);
			maingbc.gridx++;
			maingbc.weightx = 0;
			maingbc.ipadx = 210;
			content.add(centerPanel, maingbc);
			maingbc.gridx++;
			maingbc.weightx = 1;
			maingbc.ipadx = 0;
			content.add(blank2, maingbc);
			maingbc.gridx++;

			createApplyButton.setText(
					(viewcnfg.vol == null) ? "Create New Volunteer" : "<html>Save Changes<br>to Volunteer</html>");
			createApplyButton.setToolTipText((viewcnfg.vol == null) ? "Enter the new volunteer to the database"
					: "<html>Save all changes made to the record</html>");

			if (type == 0) {
				createApplyButton.setText("Create New Volunteer");
				createApplyButton.setToolTipText("Enter the new volunteer to the database");
				Util.setActionListener(createApplyButton, createVolListener);
			} else if (type == 1) {
				createApplyButton.setText("<html>Save Changes<br>to Volunteer</html>");
				createApplyButton.setToolTipText("Save all changes made to the record");
				Util.setActionListener(createApplyButton, saveChangesListener);
			} else if (type == 2) {
				if (viewcnfg.admin) {// admin show
					createApplyButton.setText("");
					createApplyButton.setIcon(editusericon);
					createApplyButton.setToolTipText("<html>Edit volunteer record</html>");
					Util.setActionListener(createApplyButton, editVolListener);
				} else {// vol show
					createApplyButton.setText("THIS SHOULDNT SHOW UP");
				}
			}

			nameField.setEditable(type < 2);
			idField.setEditable(type < 2);
			phoneField.setEditable(type < 2);
			emailField.setEditable(type < 2);
			addressField.setEditable(type < 2);

			totalHoursButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showTimeChart();
				}
			});
			setViewportView(content);
		}

	}

	static {
		ages = new Object[121];
		ages[0] = "None given";
		// 1-120
		// 5-120 //116
		for (int i = 1; i < ages.length; i++) {
			ages[i] = i;
		}
	}

	public VolInfoView(FGFrame parent) {
		super(parent);

		subLabel = new JLabel();
		subLabel.setFont(Util.subtitleFont);
		subLabel.setHorizontalAlignment(SwingConstants.CENTER);

		layoutView();

	}

	@Override
	public void layoutView() {
		setLayout(new GridBagLayout());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0;
		this.add(titleLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		subPanel.removeAll();
		subPanel.add(subLabel);
		this.add(subPanel, gbc);

	}

	/**
	 * Configures the VolInfoView for either a new Volunteer(admin=true,
	 * vol=null) or an existing one(vol!=null)
	 */
	@Override
	public void cnfgFor(ViewCnfg cnfg) {
		super.cnfgFor(cnfg);

		contentPanel = new ContentPanel();

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 1;
		this.add(contentPanel, gbc);

		JScrollBar horbar = contentPanel.getHorizontalScrollBar();
		horbar.setPreferredSize(new Dimension(0, 30));
		horbar.setUnitIncrement(10);

		JScrollBar bar = contentPanel.getVerticalScrollBar();
		bar.setPreferredSize(new Dimension(30, 0));
		bar.setUnitIncrement(10);

		if (cnfg.vol == null) {
			titleLabel.setText("New Volunteer");
			subLabel.setText("<html>You must put an entry for all boxes labeled <font color=\"red\">*</font></html>");
		} else {
			if (cnfg.admin) {
				titleLabel.setText("Volunteer Information");
				subLabel.setText(Util.formatName(cnfg.vol));
			} else {
				titleLabel.setText(cnfg.vol.getName());
				subLabel.setText(cnfg.vol.getID());
			}
		}

		revalidate();
	}

	@Override
	public void beforeClose() {
		if (contentPanel.type == 2) {
			this.remove(contentPanel);
			return;
		} else if (contentPanel.type == 0) {
			// creating new vol
			if (!contentPanel.createApplyButton.isEnabled()) {
				this.remove(contentPanel);
				return;
			}
			int result = JOptionPane.showConfirmDialog(this, "Would you like to cancel creating the new Volunteer?",
					"Volunteer Creation Canceling", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if (result == JOptionPane.YES_OPTION) {
				this.remove(contentPanel);
				return;
			} else if (result == JOptionPane.NO_OPTION) {
				parent.cancelTransition();
				return;
			}
		} else if (contentPanel.type == 1) {
			// saving vol
			if (!contentPanel.createApplyButton.isEnabled()) {
				this.remove(contentPanel);
				return;
			}
			int result = JOptionPane.showConfirmDialog(this, "Would you like to save changes to the volunteer record?",
					"Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {
				boolean crash = saveAllChanges();
				if (crash) {
					parent.cancelTransition();
					return;
				}
				this.remove(contentPanel);
				return;
			} else if (result == JOptionPane.CANCEL_OPTION) {
				parent.cancelTransition();
				return;
			} else if (result == JOptionPane.NO_OPTION) {
				this.remove(contentPanel);
				return;
			}

		}
	}

	@Override
	public void afterDisplay() {
		// sets fields as necessary
		if (contentPanel.type == 0) {// creating new
			contentPanel.nameField.setText("");
			contentPanel.idField.setText("");

			contentPanel.ageBox.setSelectedIndex(0);

			contentPanel.phoneField.setText("");
			contentPanel.emailField.setText("");
			contentPanel.addressField.setText("");

			contentPanel.phonenumIDrb.doClick();

			contentPanel.createApplyButton.setEnabled(true);
		} else if (contentPanel.type == 1) {// admin edit
			contentPanel.nameField.setText(viewcnfg.vol.getName());

			contentPanel.chooseIDrb.doClick();
			contentPanel.idField.setText(viewcnfg.vol.getID());

			(viewcnfg.vol.getGender() == Volunteer.MALE ? contentPanel.malerb : contentPanel.femalerb).doClick();

			contentPanel.ageBox.setSelectedIndex(viewcnfg.vol.getAge());

			contentPanel.phoneField.setText(viewcnfg.vol.getPhoneNumber());
			contentPanel.emailField.setText(viewcnfg.vol.getEmail());
			contentPanel.addressField.setText(viewcnfg.vol.getAddress());

			contentPanel.createApplyButton.setEnabled(true);
		} else if (contentPanel.type == 2) {// no edit
			contentPanel.nameField.setText(viewcnfg.vol.getName());
			contentPanel.idField.setText(viewcnfg.vol.getID());
			contentPanel.genderField.setText(viewcnfg.vol.getGender() == Volunteer.MALE ? "Male" : "Female");
			contentPanel.ageFieldLabel.setText(viewcnfg.vol.getAge() + "");
			contentPanel.phoneField.setText(viewcnfg.vol.getPhoneNumber());
			contentPanel.emailField.setText(viewcnfg.vol.getEmail());
			contentPanel.addressField.setText(viewcnfg.vol.getAddress());

			contentPanel.createApplyButton.setEnabled(!viewcnfg.editnow);
		}
	}

	@Override
	public void saveMem() {
		contentPanel = null;
	}

	/**
	 * will return null if crash
	 * 
	 * @return
	 */
	private Volunteer createNewVolunteer() {

		Volunteer vol = new Volunteer("", "");

		// need to check if name is empty
		// id is alright (if custom id is select)
		// id is alright if last 4 of pn (if pn = null)
		// phone number is parsable(in case)

		if (contentPanel.nameField.getText().equals("")) {
			tellLeftBlank(this);
			return null;
		}
		String id = contentPanel.idField.getText();
		if (contentPanel.phonenumIDrb.isSelected()) {
			String pn = contentPanel.phoneField.getText();
			try {
				pn = pn.substring(pn.length() - 4);
				if (Util.idIsTaken(pn)) {
					tellIDIsTaken(this, pn);
					return null;
				}
				id = pn;
			} catch (IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(this, "Can't find the last 4 digits of phone number",
						"Need phone number for ID", JOptionPane.ERROR_MESSAGE, null);
				return null;
			}
		}

		if (!(contentPanel.malerb.isSelected() || contentPanel.femalerb.isSelected())) {
			tellLeftBlank(this);
			return null;
		}
		if (contentPanel.chooseIDrb.isSelected() && id.equals("")) {
			tellLeftBlank(this);
			return null;
		} else if (Util.idIsTaken(id)) {
			tellIDIsTaken(this, id);
			return null;
		}

		if (contentPanel.ageBox.getSelectedIndex() == 0) {
			tellLeftBlank(VolInfoView.this);
			return null;
		}

		vol.setName(contentPanel.nameField.getText());
		vol.setID(id);
		vol.setGender(contentPanel.malerb.isSelected() ? Volunteer.MALE : Volunteer.FEMALE);
		vol.setAge(contentPanel.ageBox.getSelectedIndex());
		String phone = contentPanel.phoneField.getText();
		vol.setPhoneNumber((phone.equals("") ? "Not given" : phone));
		String email = contentPanel.emailField.getText();
		vol.setEmail((email.equals("") ? "Not given" : email));
		String address = contentPanel.addressField.getText();
		vol.setAddress((address.equals("") ? "Not given" : address));
		return vol;
	}

	public boolean saveAllChanges() {
		// need to check if name is empty
		// id is alright (if custom id is select)
		// id is alright if last 4 of pn (if pn = null)
		// phone number is parsable(in case)

		if (contentPanel.nameField.getText().equals("")) {
			tellLeftBlank(this);
			return true;
		}
		String id = contentPanel.idField.getText();
		if (contentPanel.phonenumIDrb.isSelected()) {
			String pn = contentPanel.phoneField.getText();
			try {
				pn = pn.substring(pn.length() - 4);
				if (Util.idIsTaken(pn) && !id.equals(viewcnfg.vol.getID())) {
					tellIDIsTaken(this, pn);
					return true;
				}
				id = pn;
			} catch (IndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(this, "Can't find the last 4 digits of phone number",
						"Need phone number for ID", JOptionPane.ERROR_MESSAGE, null);
				return true;
			}
		}

		if (contentPanel.chooseIDrb.isSelected() && id.equals("")) {
			tellLeftBlank(this);
			return true;
		} else if (Util.idIsTaken(id) && !id.equals(viewcnfg.vol.getID())) {
			tellIDIsTaken(this, id);
			return true;
		}

		if (contentPanel.ageBox.getSelectedIndex() == 0) {
			tellLeftBlank(VolInfoView.this);
			return true;
		}

		viewcnfg.vol.setName(contentPanel.nameField.getText());
		viewcnfg.vol.setID(id);
		viewcnfg.vol.setGender(contentPanel.malerb.isSelected() ? Volunteer.MALE : Volunteer.FEMALE);
		viewcnfg.vol.setAge(contentPanel.ageBox.getSelectedIndex());
		String phone = contentPanel.phoneField.getText();
		viewcnfg.vol.setPhoneNumber(phone.equals("") ? "Not given" : phone);
		String email = contentPanel.emailField.getText();
		viewcnfg.vol.setEmail(email.equals("") ? "Not given" : email);
		String address = contentPanel.addressField.getText();
		viewcnfg.vol.setAddress(address.equals("") ? "Not given" : address);

		return false;
	}

	public void showTimeChart() {
		parent.showNextView(parent.tcview, viewcnfg);
	}

	private static void tellIDIsTaken(VolInfoView view, String id) {
		JOptionPane.showMessageDialog(view,
				"The ID " + id + " is already being used by someone. Please choose a different ID.",
				"ID is already taken", JOptionPane.ERROR_MESSAGE, null);
	}

	private static void tellLeftBlank(VolInfoView view) {
		JOptionPane.showMessageDialog(view, "There's no entry for some required fields. Please fill everything out.",
				"Required fields not filled out", JOptionPane.ERROR_MESSAGE, null);
	}

	@Override
	public String getFrameTitleString() {
		return (viewcnfg.vol == null ? "Creating New Volunteer"
				: "Volunteer Information for " + Util.formatName(viewcnfg.vol));
	}

	@Override
	public String getBackString() {
		return (viewcnfg.vol == null ? "Creating New Volunteer" : "Volunteer Information");
	}

}