package ui.view;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ui.FGFrame;
import util.Util;

@SuppressWarnings("serial")
public abstract class View extends JPanel {

	public final FGFrame parent;

	// protected View prevView = null;

	/**
	 * The current view configuration
	 */
	public ViewCnfg viewcnfg = null;

	/**
	 * pre-made constraints if you want to use it to layout components
	 */
	protected final GridBagConstraints gbc;
	protected final JLabel titleLabel;
	protected final JPanel subPanel;

	protected View(FGFrame parent) {
		super();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		titleLabel = new JLabel();
		subPanel = new JPanel();

		// {
		// @Override
		// public void setText(String txt) {
		// super.setText("<html><div style=\"text-align: center;\">" + txt +
		// "</html>");
		// }
		// };
		titleLabel.setFont(Util.titleFont);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		titleLabel.setOpaque(false);
		super.setOpaque(false);
		this.parent = parent;
	}

	public abstract void layoutView();

	// public void setPrevView(View view) {
	// prevView = view;
	// }
	//
	// public View getPrevView() {
	// return prevView;
	// }

	/**
	 * simply calls the view's revalidate() and repaint() methods
	 */
	public void updateEverything() {
		revalidate();
		this.repaint();
	}

	/**
	 * SHOULD OVERRIDE Since the FGFrame.frame has one instance of each view,
	 * the view has a method for changing its crap and this is it. Ex. the
	 * TimeChartView.cnfgFor(cnfg) configures based on the volunteer of cnfg and
	 * if cnfg is admin.
	 * 
	 * @param cnfg
	 */
	public void cnfgFor(ViewCnfg cnfg) {
		viewcnfg = cnfg;
	}

	/**
	 * Gets rid of the current view configuration which probably won't be used
	 * again and saves memory
	 */
	public abstract void saveMem();

	/**
	 * This gives the view an opportunity to close up shop before going back to
	 * the previous view. example. asking user to save, etc. This also lets the
	 * view cancel the back operation by calling
	 * FGFrame.frame.backButton.cancelBack();
	 */
	public abstract void beforeClose();

	/**
	 * This method will be called by FGFrame after it has displayed the view in
	 * order to let the view do some final updates to straighten things up such
	 * as scrolling down, etc<br>
	 * <br>
	 * NOTE FGFrame will automatically call view.revalidate and view.repaint
	 * afterwards
	 */
	public abstract void afterDisplay();

	/**
	 * @return the String the view would like FGFrame to have the title of when
	 *         the view is active if this method returns null, the frame will
	 *         resort to the default title
	 */
	public abstract String getFrameTitleString();

	/**
	 * Example, return "Time Chart" Button would look like
	 * "Go back to Time Chart" from the next view (previous view is time chart
	 * view)
	 * 
	 * @return
	 */
	public abstract String getBackString();

}
