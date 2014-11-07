package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pathvisio.data.DataException;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.util.TextFieldUtils;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.statistics.StatisticsPlugin;
import org.pathvisio.statistics.StatisticsPlugin.StatisticsDlg;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

public class CriterionPage extends WizardPanelDescriptor implements
		ActionListener {

	private PvDesktop desktop;
	private SwingEngine swingEngine;
	



	private Criterion myCriterion = new Criterion();
	private List<String> sampleNames;

	final static String IDENTIFIER = "CRITERION_PAGE";
	final static String ACTION_MIRNA_UP = "miRNAUPregulation";
	final static String ACTION_GENE_UP = "geneUpregulation";
	final static String ACTION_MIRNA_DOWN = "miRNADownregulation";
	final static String ACTION_GENE_DOWN = "geneDownregulation";

	final static String ACTION_INFO = "InfoButton";

	// MiRNA GUI objects
	private JLabel miRNALbl;

	private JLabel miRNAUp;
	private JTextField miRNAUpExpr;
	private JButton miRNAUpButton;

	private JLabel miRNADown;
	private JTextField miRNADownExpr;
	private JButton miRNADownButton;

	// gene GUI objects
	private JLabel geneLbl;

	private JLabel geneUp;
	private JTextField geneUpExpr;
	private JButton geneUpButton;

	private JLabel geneDown;
	private JTextField geneDownExpr;
	private JButton geneDownButton;

	private JButton infoBtn;

	public CriterionPage(PvDesktop desktop) {
		super(IDENTIFIER);
		this.desktop = desktop;
	}

	public Object getNextPanelDescriptor() {

		return null;
	}

	public Object getBackPanelDescriptor() {

		return null;

	}

	@Override
	protected Component createContents() {

		miRNALbl = new JLabel("miRNA");

		miRNAUp = new JLabel("Upregulated");
		miRNAUpExpr = new JTextField(40);
		miRNAUpButton = new JButton("Expr");

		miRNADown = new JLabel("Downregulated");
		miRNADownExpr = new JTextField(40);
		miRNADownButton = new JButton("Expr");

		geneLbl = new JLabel("Gene");

		geneUp = new JLabel("Upregulated");
		geneUpExpr = new JTextField(40);
		geneUpButton = new JButton("Expr");

		geneDown = new JLabel("Downregulated");
		geneDownExpr = new JTextField(40);
		geneDownButton = new JButton("Expr");

		// gene GUI objects
		geneLbl = new JLabel("Gene");
		geneUp = new JLabel("Upregulated");
		geneDown = new JLabel("Downregulated");

		infoBtn = new JButton("Info");

		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout(
				"pref,15dlu,pref,15dlu,pref,15dlu,pref,15dlu,pref,15dlu, pref, 15dlu, pref, default",
				"pref,10dlu, pref,8dlu,pref,20dlu,pref,20dlu,pref,10dlu,pref,8dlu,pref,8dlu,pref");
		PanelBuilder builder = new PanelBuilder(layout);

		builder.addSeparator("", cc.xyw(1, 2, 10));

		builder.add(miRNALbl, cc.xy(1, 3));
		builder.add(geneLbl, cc.xy(7, 3));

		builder.addSeparator("", cc.xyw(1, 5, 10));

		builder.add(miRNAUp, cc.xy(1, 6));
		builder.add(geneUp, cc.xy(7, 6));

		builder.add(miRNAUpExpr, cc.xy(1, 7));
		builder.add(geneUpExpr, cc.xy(7, 7));

		builder.add(miRNAUpButton, cc.xy(5, 7));
		builder.add(geneUpButton, cc.xy(9, 7));

		builder.addSeparator("", cc.xyw(1, 8, 10));

		builder.add(miRNADown, cc.xy(1, 9));
		builder.add(geneDown, cc.xy(7, 9));

		builder.add(miRNADownExpr, cc.xy(1, 11));
		builder.add(geneDownExpr, cc.xy(7, 11));

		builder.add(miRNADownButton, cc.xy(5, 11));
		builder.add(geneDownButton, cc.xy(9, 11));

		builder.add(infoBtn, cc.xy(5, 13));

		infoBtn.addActionListener(this);
		infoBtn.setActionCommand(ACTION_INFO);

		miRNAUpButton.addActionListener(this);
		miRNAUpButton.setActionCommand(ACTION_MIRNA_UP);

		geneUpButton.addActionListener(this);
		geneUpButton.setActionCommand(ACTION_GENE_UP);

		miRNADownButton.addActionListener(this);
		miRNADownButton.setActionCommand(ACTION_MIRNA_DOWN);

		geneDownButton.addActionListener(this);
		geneDownButton.setActionCommand(ACTION_GENE_DOWN);

		return builder.getPanel();
	}

	public void aboutToDisplayPanel() {
		getWizard().setPageTitle("Set Expression criteria");
		
	}

	public Criterion getCriterion() {
		return myCriterion;
	}

	private void updateCriterion(JTextField txtExpr, List<String> sampleNames,
			JLabel lblError) {
		String error = myCriterion
				.setExpression(txtExpr.getText(), sampleNames);
		if (error != null) {
			lblError.setText(error);
		} else {
			lblError.setText("OK");
		}
	}
	
	public  List<String> getSampleNames(){
		GexManager gm = desktop.getGexManager();
		try {
			sampleNames = gm.getCurrentGex().getSampleNames();
			} catch (DataException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		return sampleNames;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		if (ACTION_INFO.equals(action)) {
			try {
				helpURL();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		else if (ACTION_MIRNA_UP.equals(action)) {
			Frame exprFrame = desktop.getFrame();
			FormLayout layout = new FormLayout (
					"4dlu, min:grow, 4dlu, min:grow, 4dlu",
					"4dlu, pref, 4dlu, pref, 4dlu, [50dlu,min]:grow, 4dlu, pref, 4dlu");
			layout.setColumnGroups(new int[][]{{2,4}});
			
			JPanel critPanel = new JPanel(layout);
			CellConstraints cc = new CellConstraints();
			critPanel.add (new JLabel ("Expression: "), cc.xy(2,2));
			sampleNames= getSampleNames();
			miRNAUpExpr.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent e)
				{
					updateCriterion(miRNAUpExpr,sampleNames ,miRNALbl);
				}

				public void insertUpdate(DocumentEvent e)
				{
					updateCriterion(miRNAUpExpr,sampleNames ,miRNALbl);
				}

				public void removeUpdate(DocumentEvent e)
				{
					updateCriterion(miRNAUpExpr,sampleNames ,miRNALbl);
				}
			});

			

			final JList lstOperators = new JList(Criterion.TOKENS);
			critPanel.add (new JScrollPane (lstOperators), cc.xy (2,6));

			lstOperators.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent me)
				{
					int selectedIndex = lstOperators.getSelectedIndex();
					if (selectedIndex >= 0)
					{
						String toInsert = Criterion.TOKENS[selectedIndex];
						TextFieldUtils.insertAtCursorWithSpace(miRNAUpExpr, toInsert);
					}
					// after clicking on the list, move focus back to text field so
					// user can continue typing
					miRNAUpExpr.requestFocusInWindow();
					// on Mac L&F, requesting focus leads to selecting the whole field
					// move caret a bit to work around. Last char is a space anyway.
					miRNAUpExpr.setCaretPosition(miRNAUpExpr.getDocument().getLength() - 1);
				}
			} );

			final JList lstSamples = new JList(sampleNames.toArray());

			lstSamples.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent me)
				{
					int selectedIndex = lstSamples.getSelectedIndex();
					if (selectedIndex >= 0)
					{
						String toInsert = "[" + sampleNames.get(selectedIndex) + "]";
						TextFieldUtils.insertAtCursorWithSpace(miRNAUpExpr, toInsert);
					}
					// after clicking on the list, move focus back to text field so
					// user can continue typing
					miRNAUpExpr.requestFocusInWindow();
					// on Mac L&F, requesting focus leads to selecting the whole field
					// move caret a bit to work around. Last char is a space anyway.
					miRNAUpExpr.setCaretPosition(miRNAUpExpr.getDocument().getLength() - 1);
				}
			} );

			critPanel.add (new JScrollPane (lstSamples), cc.xy (4,6));
			
			
			miRNAUpExpr.requestFocus();
			
			exprFrame.add(critPanel);
			exprFrame.pack();
			exprFrame.setVisible(true);
			exprFrame.setSize(300,300);
			
		}
	

		else if (ACTION_GENE_UP.equals(action)) {

		}

		else if (ACTION_MIRNA_DOWN.equals(action)) {

		}

		else if (ACTION_GENE_DOWN.equals(action)) {

		}

	}

	public void helpURL() throws Exception {
		Desktop helpBrowse = Desktop.getDesktop();
		helpBrowse.browse(new URI("https://www.google.com"));
	}

}
