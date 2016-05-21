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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data.Session;
import ui.FGFrame;
import util.DataController;
import util.Util;

@SuppressWarnings("serial")
public class TimeChartView extends View {

	private TimeTable timeTable;
	private final JButton volButton;

	private static final String blankString = "-";

	private class TimeTable extends JPanel {

		private final JScrollPane scrollPane;
		private final JPanel table;

		public final ArrayList<SessionRow> sessionRows;

		private JButton addButton;

		private JButton applyButton;

		private final GridBagConstraints ttgbc = new GridBagConstraints();

		private static final double weight_sessnum = 0.2;
		private static final double weight_signinout = 1.0;
		private static final double weight_time = 0.3;
		private static final double weight_delete = 0.0;

		private static final int picSize = 22;

		private final ImageIcon addIcon = Util.getScaledImage("add.png", picSize, picSize);
		private final ImageIcon deleteIcon = Util.getScaledImage("delete.png", picSize, picSize);

		private final DocumentListener changeListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!applyButton.isEnabled()) {
					applyButton.setEnabled(true);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!applyButton.isEnabled()) {
					applyButton.setEnabled(true);
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		};

		public class SessionRow {
			private JTextField sessionnum, signinField, signoutField, total;

			private JButton removeButton;

			private SessionRow(Session s, int y) {

				String signinstring = blankString;
				String signoutstring = blankString;
				if (s.getSigninTime() != null) {
					signinstring = Util.normalFormat.format(s.getSigninTime());
				}
				if (s.getSignoutTime() != null) {
					signoutstring = Util.normalFormat.format(s.getSignoutTime());
				}

				sessionnum = new JTextField("Session " + y + ": ");
				sessionnum.setFont(Util.normalFont);
				sessionnum.setEditable(false);
				sessionnum.setBackground(Util.copyColor(table.getBackground()));
				signinField = new JTextField(signinstring);
				signinField.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
						signinField.selectAll();
					}

					@Override
					public void focusLost(FocusEvent e) {
					}
				});
				signinField.setFont(Util.normalFont);
				signinField.setEditable(viewcnfg.admin);
				signinField.setBackground(viewcnfg.admin ? Util.getDarkerColor(table.getBackground(), -20)
						: Util.copyColor(table.getBackground()));
				signinField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (signinField.getText().length() > 18) {
							e.consume();
						}
					}
				});
				signinField.getDocument().addDocumentListener(changeListener);
				signinField.setToolTipText("Session " + y + " sign in time");
				signoutField = new JTextField(signoutstring);
				signoutField.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
						signoutField.selectAll();
					}

					@Override
					public void focusLost(FocusEvent e) {
					}
				});
				signoutField.setFont(Util.normalFont);
				signoutField.setEditable(viewcnfg.admin);
				signoutField.setBackground(viewcnfg.admin ? Util.getDarkerColor(table.getBackground(), -20)
						: Util.copyColor(table.getBackground()));
				signoutField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) {
						if (signoutField.getText().length() > 18) {
							e.consume();
						}
					}
				});
				signoutField.getDocument().addDocumentListener(changeListener);
				signoutField.setToolTipText("Session " + y + " sign out time");

				String totalstring = null;
				try {
					totalstring = Util.formatTime(s.getTotalTime());
				} catch (Exception e) {
					totalstring = blankString;
				}
				total = new JTextField(totalstring);
				total.setFont(Util.normalFont);
				total.setEditable(false);
				total.setBackground(Util.copyColor(table.getBackground()));
				total.setToolTipText("Session " + y + " total time");
				if (viewcnfg.admin) {
					removeButton = new JButton(deleteIcon);
					removeButton.setToolTipText("Remove session " + y + " from record");

					removeButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							SessionRow.this.remove();
							sessionRows.remove(SessionRow.this);
							updateEverything();
							centertopit();
							if (!applyButton.isEnabled()) {
								applyButton.setEnabled(true);
							}

						}
					});
				}
			}

			public void remove() {
				table.remove(sessionnum);
				table.remove(signinField);
				table.remove(signoutField);
				table.remove(total);
				table.remove(removeButton);
			}

		}

		public TimeTable() {
			super();

			scrollPane = new JScrollPane();
			table = new JPanel();
			scrollPane.setViewportView(table);

			if (viewcnfg.admin) {
				applyButton = new JButton("<html><div style=\"text-align: center;\">Save<br>Changes</html>");
				applyButton.setMargin(new Insets(5, 5, 5, 5));
				applyButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						boolean crash = applyAllChanges();
						applyButton.setEnabled(crash);
						if (crash)
							return;
						setupSessionRows();
						setupAll();
						updateEverything();
						centertopit();

						System.out.println(viewcnfg.vol.isSignedIn());
					}
				});
				applyButton.setEnabled(false);
				applyButton.setToolTipText("Save all changes made to the record");

				addButton = new JButton(addIcon);
				addButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sessionRows.add(new SessionRow(new Session(), sessionRows.size() + 1));
						setupAll();
						updateEverything();
						centertopit();

						if (!applyButton.isEnabled()) {
							applyButton.setEnabled(true);
						}
					}
				});
				addButton.setToolTipText("Add a new session manually");
			}

			sessionRows = new ArrayList<SessionRow>();
			setupSessionRows();

			setupAll();

			setLayout(new BorderLayout());
			addComponentListener(new ComponentListener() {
				boolean previouslybigger = true;

				@Override
				public void componentResized(ComponentEvent e) {
					boolean bigger = (scrollPane.getPreferredSize().getHeight() > TimeTable.this.getSize().getHeight());

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

			JScrollBar horbar = scrollPane.getHorizontalScrollBar();
			horbar.setPreferredSize(new Dimension(0, 30));
			horbar.setUnitIncrement(10);

			JScrollBar bar = scrollPane.getVerticalScrollBar();
			bar.setPreferredSize(new Dimension(30, 0));
			bar.setUnitIncrement(10);

			InputMap im = bar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			im.put(KeyStroke.getKeyStroke("down"), "positiveUnitIncrement");
			im.put(KeyStroke.getKeyStroke("up"), "negativeUnitIncrement");

			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

			setBorder(null);
			scrollPane.setBorder(null);
		}

		public void setupSessionRows() {
			sessionRows.clear();
			for (int i = 0; i < viewcnfg.vol.getSessions().size(); i++) {
				sessionRows.add(new SessionRow(viewcnfg.vol.getSessions().get(i), i + 1));
			}
			System.gc();
		}

		public void setupAll() {
			table.removeAll();

			table.setLayout(new GridBagLayout());
			ttgbc.fill = GridBagConstraints.HORIZONTAL;
			ttgbc.insets = new Insets(0, 0, 5, 2);

			ttgbc.gridx = 0;
			ttgbc.gridy = 0;
			initColumnHeader(ttgbc);

			for (int y = 0; y < sessionRows.size(); y++) {
				ttgbc.gridy++;
				initSessRow(sessionRows.get(y));
			}

			ttgbc.gridy++;

			if (viewcnfg.admin) {
				initNewButtonRow(ttgbc);
				ttgbc.gridy++;
			}

			initTotalHeader();

		}

		public void initSessRow(SessionRow row) {

			ttgbc.weightx = weight_sessnum;
			ttgbc.gridx = 0;
			table.add(row.sessionnum, ttgbc);

			ttgbc.weightx = weight_signinout;
			ttgbc.gridx = 1;
			table.add(row.signinField, ttgbc);

			ttgbc.weightx = weight_signinout;
			ttgbc.gridx = 2;
			table.add(row.signoutField, ttgbc);

			ttgbc.weightx = weight_time;
			ttgbc.gridx = 3;
			table.add(row.total, ttgbc);

			if (viewcnfg.admin) {
				ttgbc.weightx = weight_delete;
				ttgbc.gridx = 4;
				table.add(row.removeButton, ttgbc);
			}

		}

		public void initTotalHeader() {
			ttgbc.weightx = weight_sessnum;
			ttgbc.gridx = 0;
			JTextField totalLabel = new JTextField("Total: ");
			totalLabel.setFont(Util.standoutFont);
			totalLabel.setEditable(false);
			totalLabel.setBorder(null);
			totalLabel.setBackground(Util.copyColor(table.getBackground()));
			table.add(totalLabel, ttgbc);

			ttgbc.weightx = weight_signinout;
			ttgbc.gridx = 1;
			JComponent blank1 = new JComponent() {
			};
			blank1.setBorder(null);
			table.add(blank1, ttgbc);

			ttgbc.weightx = weight_signinout;
			ttgbc.gridx = 2;
			JComponent blank2 = new JComponent() {
			};
			blank2.setBorder(null);
			table.add(blank2, ttgbc);

			ttgbc.weightx = weight_time;
			ttgbc.gridx = 3;
			String totaltimestring = Util.formatTime(viewcnfg.vol.getTotalVolunteeringTime());
			JTextField totaltime = new JTextField(totaltimestring);
			totaltime.setFont(Util.standoutFont);
			totaltime.setEditable(false);
			totaltime.setToolTipText("Total career volunteering time");
			totaltime.setBorder(new EmptyBorder(new Insets(15, 0, 15, 0)));
			totaltime.setBackground(Util.copyColor(table.getBackground()));
			table.add(totaltime, ttgbc);

			if (viewcnfg.admin) {
				ttgbc.weightx = weight_delete;
				ttgbc.gridx = 4;
				applyButton.setFont(Util.standoutFont);
				table.add(applyButton, ttgbc);
			}

		}

		public void initNewButtonRow(GridBagConstraints c) {
			c.weightx = weight_sessnum;
			c.gridx = 0;
			table.add(addButton, c);

			c.weightx = weight_signinout;
			c.gridx = 1;
			JComponent blank1 = new JComponent() {
			};
			blank1.setBorder(null);
			table.add(blank1, c);

			c.weightx = weight_signinout;
			c.gridx = 2;
			JComponent blank2 = new JComponent() {
			};
			blank2.setBorder(null);
			table.add(blank2, c);

			c.weightx = weight_time;
			c.gridx = 3;
			JComponent blank3 = new JComponent() {
			};
			blank3.setBorder(null);
			table.add(blank3, c);

			if (viewcnfg.admin) {
				c.weightx = weight_delete;
				c.gridx = 4;
				JComponent blank4 = new JComponent() {
				};
				blank4.setBorder(null);
				table.add(blank4, c);
			}

		}

		public void initColumnHeader(GridBagConstraints c) {

			c.weightx = weight_sessnum;
			c.gridx = 0;
			JTextField sessnum = new JTextField("Session #");
			sessnum.setFont(Util.standoutFont);
			sessnum.setEditable(false);
			sessnum.setBackground(Util.copyColor(table.getBackground()));
			table.add(sessnum, c);

			c.weightx = weight_signinout;
			c.gridx = 1;
			JTextField signintime = new JTextField("Sign in Time");
			signintime.setFont(Util.standoutFont);
			signintime.setEditable(false);
			signintime.setBackground(Util.copyColor(table.getBackground()));
			table.add(signintime, c);

			c.weightx = weight_signinout;
			c.gridx = 2;
			JTextField signouttime = new JTextField("Sign out Time");
			signouttime.setFont(Util.standoutFont);
			signouttime.setEditable(false);
			signouttime.setBackground(Util.copyColor(table.getBackground()));
			table.add(signouttime, c);

			c.weightx = weight_time;
			c.gridx = 3;
			JTextField time = new JTextField("Time");
			time.setFont(Util.standoutFont);
			time.setEditable(false);
			time.setBackground(Util.copyColor(table.getBackground()));
			table.add(time, c);

			if (viewcnfg.admin) {
				c.weightx = weight_delete;
				c.gridx = 4;
				JTextField delete = new JTextField("Delete");
				delete.setFont(Util.standoutFont);
				delete.setEditable(false);
				delete.setBackground(Util.copyColor(table.getBackground()));
				table.add(delete, c);
			}
		}

		@Override
		public void revalidate() {
			if (sessionRows != null) {

				int i = 1;
				for (SessionRow sr : sessionRows) {
					sr.sessionnum.setText("Session " + i);
					i++;
				}
			}
			super.revalidate();
		}

		public void centertopit() {
			if (scrollPane.getPreferredSize().getHeight() > this.getSize().getHeight()) {
				this.add(scrollPane, BorderLayout.CENTER);
				return;
			}
			this.add(scrollPane, BorderLayout.NORTH);
		}

	}

	public TimeChartView(FGFrame parent) {
		super(parent);
		titleLabel.setText("Time Chart");

		volButton = new JButton();
		volButton.setFont(Util.subtitleFont);
		volButton.setHorizontalAlignment(SwingConstants.CENTER);

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

		subPanel.removeAll();
		subPanel.setLayout(new GridBagLayout());
		subPanel.add(volButton);
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		this.add(subPanel, gbc);
	}

	@Override
	public void cnfgFor(ViewCnfg cnfg) {
		super.cnfgFor(cnfg);

		timeTable = new TimeTable();

		volButton.setText(Util.formatName(viewcnfg.vol));
		volButton.setToolTipText(cnfg.admin ? "See volunteer information" : "See your account");

		Util.setActionListener(volButton, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (viewcnfg.admin) {
					parent.showNextView(parent.viview, cnfg);
				} else {
					parent.showNextView(parent.vview, cnfg);
				}
			}
		});

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		this.add(timeTable, gbc);
	}

	public void scrollDown() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JScrollBar bar = timeTable.scrollPane.getVerticalScrollBar();
				bar.setValue(bar.getMaximum());
			}
		});
	}

	public boolean applyAllChanges() {
		int crashAt = 1;
		String crashMessage = "";
		Exception crash = null;
		try {
			// check for crash
			for (TimeTable.SessionRow sr : timeTable.sessionRows) {
				Session.checkTimes(Util.normalFormat.parse(sr.signinField.getText()),
						Util.normalFormat.parse(sr.signoutField.getText()));
				crashAt++;
			}

			viewcnfg.vol.getSessions().clear();

			for (TimeTable.SessionRow sr : timeTable.sessionRows) {
				Date in = Util.normalFormat.parse(sr.signinField.getText());
				Date out = Util.normalFormat.parse(sr.signoutField.getText());

				viewcnfg.vol.getSessions().add(new Session(in, out));
			}

			viewcnfg.vol.sortSessions();
			DataController.saveVol(viewcnfg.vol);
			return false;
		} catch (ParseException e) {
			crashMessage = "<html><div style=\"text-align: center;\"><strong>Invalid time format at session " + crashAt
					+ "</strong><br><br>Time has to be in the format of <br>[2/25/2016 12:39 AM]</html>";
			crash = e;
		} catch (IllegalArgumentException e) {
			crashMessage = "<html><div style=\"text-align: center;\"><strong>Invalid times at session " + crashAt
					+ "</strong><br><br>Sign out time has to come after sign in time</html>";
			crash = e;
		}

		JOptionPane.showMessageDialog(this, crashMessage, crash.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE,
				null);
		return true;
	}

	@Override
	public void afterDisplay() {
		timeTable.centertopit();
		scrollDown();
	}

	@Override
	public void saveMem() {
		timeTable = null;
	}

	@Override
	public void beforeClose() {
		if (timeTable == null)
			return;
		if (!viewcnfg.admin || !timeTable.applyButton.isEnabled()) {
			this.remove(timeTable);
			return;
		}
		int result = JOptionPane.showConfirmDialog(this, "Would you like to save changes?", "Unsaved Changes",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);

		if (result == JOptionPane.YES_OPTION) {
			boolean crash = applyAllChanges();
			if (crash) {
				parent.cancelTransition();
				return;
			}
		} else if (result == JOptionPane.CANCEL_OPTION) {
			parent.cancelTransition();
			return;
		}
		this.remove(timeTable);
	}

	@Override
	public String getFrameTitleString() {
		return "Time Chart for " + Util.formatName(viewcnfg.vol);
	}

	@Override
	public String getBackString() {
		return "Time Chart";
	}
}
