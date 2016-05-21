package ui.components;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class HintTextField extends JTextField implements FocusListener {

	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		showingHint = true;
		setForeground(Color.gray);
		super.addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (getText().isEmpty()) {
			super.setText("");
			showingHint = false;
			setForeground(Color.black);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (getText().isEmpty()) {
			super.setText(hint);
			showingHint = true;
			setForeground(Color.gray);
		}
	}

	public void clearText() {
		super.setText(hint);
		showingHint = true;
		setForeground(Color.gray);
	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}

}
