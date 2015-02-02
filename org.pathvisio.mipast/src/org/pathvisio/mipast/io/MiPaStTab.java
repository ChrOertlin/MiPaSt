package org.pathvisio.mipast.io;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gui.SwingEngine;

public class MiPaStTab extends JSplitPane {
	
	private PvDesktop desktop;
	private JPanel pathwayPanel = new JPanel();
	private JPanel backpagePanel = new JPanel();
	private SwingEngine swingEngine;

	public MiPaStTab(PvDesktop desktop){
		super(JSplitPane.VERTICAL_SPLIT);
		this.desktop = desktop;
		pathwayPanel.setLayout(new BorderLayout());
		backpagePanel.setLayout(new BoxLayout(backpagePanel, BoxLayout.PAGE_AXIS));
		swingEngine = desktop.getSwingEngine();
		JScrollPane pathwayScroll = new JScrollPane(pathwayPanel);
		pathwayScroll.getVerticalScrollBar().setUnitIncrement(20);
		JScrollPane backpageScroll = new JScrollPane(backpagePanel);
		backpageScroll.getVerticalScrollBar().setUnitIncrement(20);
		setTopComponent(pathwayScroll);
		setBottomComponent(backpageScroll);
		setOneTouchExpandable(true);
		setDividerLocation(400);
	}
	
	
}
