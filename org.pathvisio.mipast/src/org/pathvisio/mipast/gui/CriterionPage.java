//Copyright 2014 BiGCaT
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package org.pathvisio.mipast.gui;

import java.awt.Component;
import java.awt.Desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.data.DataException;

import org.pathvisio.desktop.PvDesktop;

import org.pathvisio.desktop.util.TextFieldUtils;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.mipast.io.PositiveGeneList;
import org.pathvisio.mipast.util.MiPastZScoreCalculator;
import org.pathvisio.rip.RegIntPlugin;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

/**
 * 
 * The CriterionPage is used for the input of criteria that specify the up
 * and/or downregulation of either or both the genes and miRNA.
 * 
 * @author ChrOertlin
 * 
 */

public class CriterionPage extends WizardPanelDescriptor implements
		ActionListener {

	private ExpressionDialog ed = new ExpressionDialog();
	private PvDesktop desktop;
	private SwingEngine swingEngine;
	private RegIntPlugin plugin;

	private static Criterion miRNAUpCrit = new Criterion();
	private static Criterion geneUpCrit = new Criterion();
	private static Criterion miRNADownCrit = new Criterion();
	private static Criterion geneDownCrit = new Criterion();

	private MiPastZScoreCalculator zcMiU;
	private MiPastZScoreCalculator zcGu;
	private MiPastZScoreCalculator zcMiD;
	private MiPastZScoreCalculator zcGd;

	private ProgressKeeper pk;
	private File pwDir = new File("home/bigcat/Desktop/pathways/");

	private boolean geneDataAvailable;

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
	private JLabel lblError;

	private JTextField setExpr;
	private JButton exprOk;

	private JLabel pathwayLabel;
	private JButton browsePathways;
	private JTextField pathwayTextField;

	public CriterionPage(PvDesktop desktop, SwingEngine se, RegIntPlugin plugin) {
		super(IDENTIFIER);
		this.desktop = desktop;
		this.swingEngine = se;
		this.plugin = plugin;

	}

	public Object getNextPanelDescriptor() {

		return StatisticsPage.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {

		return StatInfoPage.IDENTIFIER;

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

		// pathway objects
		pathwayLabel = new JLabel("Pathway directory:");
		pathwayTextField = new JTextField(40);
		browsePathways = new JButton("Browse");

		CellConstraints cc = new CellConstraints();

		FormLayout layout = new FormLayout(
				"pref,15dlu,pref,15dlu,pref,15dlu,pref,15dlu,pref,15dlu, pref, 15dlu, pref, default",
				"pref,10dlu, pref,8dlu,pref,20dlu,pref,20dlu,pref,10dlu,pref,8dlu,pref,8dlu,pref, 8dlu,pref");
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
		
		if(!DataHolding.isGeneFileLoaded()){
			geneLbl.setVisible(false);
			geneUp.setVisible(false);
			geneUpExpr.setVisible(false);
			geneUpButton.setVisible(false);
			geneDown.setVisible(false);
			geneDownExpr.setVisible(false);
			geneDownButton.setVisible(false);

		}
	}

	public Criterion getCriterion() {
		return myCriterion;
	}

	private void updateCriterion(JTextField txtExpr, List<String> sampleNames,
			JLabel errorLbl) {
		String error = myCriterion
				.setExpression(txtExpr.getText(), sampleNames);
		if (error != null) {
			lblError.setText(error);
		} else {
			lblError.setText("OK");
		}
	}

	public List<String> getSampleNames() {

		try {
			sampleNames = desktop.getGexManager().getCurrentGex()
					.getSampleNames();
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
			ed.ExpressionDialog(miRNAUpExpr);
		}

		else if (ACTION_GENE_UP.equals(action)) {
			ed.ExpressionDialog(geneUpExpr);
		}

		else if (ACTION_MIRNA_DOWN.equals(action)) {
			ed.ExpressionDialog(miRNADownExpr);
		}

		else if (ACTION_GENE_DOWN.equals(action)) {
			ed.ExpressionDialog(geneDownExpr);
		}

	}

	public void helpURL() throws Exception {
		Desktop helpBrowse = Desktop.getDesktop();
		helpBrowse.browse(new URI("https://www.google.com"));
	}

	private class ExpressionDialog implements ActionListener {
		final static String ACTION_OK = "expressionOk";
		private JTextField expressionTextField;
		JDialog exprFrame;

		public void ExpressionDialog(JTextField expressionTextField) {
			this.expressionTextField = expressionTextField;
			exprFrame = new JDialog(desktop.getFrame(), true);
			exprFrame.setTitle("Expression");
			setExpr = new JTextField(40);
			lblError = new JLabel();
			exprOk = new JButton("Ok");

			FormLayout layout = new FormLayout(
					"4dlu, min:grow, 4dlu, min:grow, 4dlu",
					"4dlu,pref,4dlu, pref, 4dlu, pref, 4dlu, [50dlu,min]:grow, 4dlu, pref, 4dlu,pref, 8dlu,pref,8dlu ");
			layout.setColumnGroups(new int[][] { { 2, 4 } });

			JPanel critPanel = new JPanel(layout);
			CellConstraints cc = new CellConstraints();
			critPanel.add(new JLabel("Expression"), cc.xy(2, 2));
			critPanel.add(setExpr, cc.xy(2, 4));
			critPanel.add(lblError, cc.xy(2, 12));
			critPanel.add(exprOk, cc.xy(2, 14));
			sampleNames = getSampleNames();

			setExpr.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					updateCriterion(setExpr, sampleNames, lblError);
				}

				public void insertUpdate(DocumentEvent e) {
					updateCriterion(setExpr, sampleNames, lblError);
				}

				public void removeUpdate(DocumentEvent e) {
					updateCriterion(setExpr, sampleNames, lblError);
				}
			});

			final JList lstOperators = new JList(Criterion.TOKENS);
			critPanel.add(new JScrollPane(lstOperators), cc.xy(2, 6));

			lstOperators.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent me) {
					int selectedIndex = lstOperators.getSelectedIndex();
					if (selectedIndex >= 0) {
						String toInsert = Criterion.TOKENS[selectedIndex];
						TextFieldUtils.insertAtCursorWithSpace(setExpr,
								toInsert);
					}
					// after clicking on the list, move focus back to text field
					// so
					// user can continue typing
					setExpr.requestFocusInWindow();
					// on Mac L&F, requesting focus leads to selecting the whole
					// field
					// move caret a bit to work around. Last char is a space
					// anyway.
					setExpr.setCaretPosition(setExpr.getDocument().getLength() - 1);
				}
			});

			final JList lstSamples = new JList(sampleNames.toArray());

			lstSamples.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent me) {
					int selectedIndex = lstSamples.getSelectedIndex();
					if (selectedIndex >= 0) {
						String toInsert = "[" + sampleNames.get(selectedIndex)
								+ "]";
						TextFieldUtils.insertAtCursorWithSpace(setExpr,
								toInsert);
					}
					// after clicking on the list, move focus back to text field
					// so
					// user can continue typing
					setExpr.requestFocusInWindow();
					// on Mac L&F, requesting focus leads to selecting the whole
					// field
					// move caret a bit to work around. Last char is a space
					// anyway.
					setExpr.setCaretPosition(setExpr.getDocument().getLength() - 1);
				}
			});

			critPanel.add(new JScrollPane(lstSamples), cc.xy(4, 6));

			setExpr.requestFocus();

			exprOk.addActionListener(this);
			exprOk.setActionCommand(ACTION_OK);

			exprFrame.add(critPanel);
			exprFrame.pack();
			exprFrame.getComponentListeners();
			exprFrame.setSize(500, 500);
			exprFrame.requestFocus();
			exprFrame.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();

			if (ACTION_OK.equals(action)) {
				expressionTextField.setText(getExpression());

				if (expressionTextField.equals(miRNAUpExpr)&& miRNAUpExpr.getText()!= null) {
					DataHolding.setMiRNAUpCrit(expressionTextField.getText());
					miRNAUpCrit.setExpression(expressionTextField.getText());

				}
				if (expressionTextField.equals(geneUpExpr)&& geneUpExpr.getText()!= null) {
					DataHolding.setGeneUpCrit(expressionTextField.getText());
					geneUpCrit.setExpression(expressionTextField.getText());
				}
				if (expressionTextField.equals(miRNADownExpr)&& miRNADownExpr.getText()!= null) {
					DataHolding.setMiRNADownCrit(expressionTextField.getText());
					miRNADownCrit.setExpression(expressionTextField.getText());
				}
				if (expressionTextField.equals(geneDownExpr)&& geneDownExpr.getText()!= null) {
					DataHolding.setGeneDownCrit(expressionTextField.getText());
					geneDownCrit.setExpression(expressionTextField.getText());
				}

				exprFrame.dispose();
			}

		}

		public String getExpression() {
			return setExpr.getText();
		}

	}

	public void aboutToHidePanel() {

		if (miRNAUpExpr.getText().length()> 2) {
			DataHolding.miRNAUpCriterion.setExpression(miRNAUpExpr.getText());
			miRNAUpCrit.setExpression(miRNAUpExpr.getText());
			DataHolding.setMiRNAUpCritCheck(true);
		}

		if (miRNADownExpr.getText().length()> 2) {
			DataHolding.miRNADownCriterion.setExpression(miRNADownExpr
					.getText());
			miRNADownCrit.setExpression(miRNADownExpr.getText());
			DataHolding.setMiRNADownCritCheck(true);
			System.out.print("expr:  " + miRNADownExpr.getText()+ "\n");
			
		}

		if (DataHolding.isGeneFileLoaded()) {
			if (geneUpExpr.getText().length() > 2) {
				DataHolding.geneUpCriterion.setExpression(geneUpExpr.getText());
				geneUpCrit.setExpression(geneUpExpr.getText());
				DataHolding.setGeneUpCritCheck(true);
			}
			if (geneDownExpr.getText().length()>2) {
				DataHolding.geneDownCriterion.setExpression(geneDownExpr
						.getText());
				geneDownCrit.setExpression(geneDownExpr.getText());
				DataHolding.setGeneDownCritCheck(true);
			}

		}

		PositiveGeneList posList = new PositiveGeneList(desktop, swingEngine,
				plugin);
		try {
			posList.execute();
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
