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

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.bridgedb.IDMapperException;
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

	private Criterion myCriterion = new Criterion();
	private List<String> sampleNames;

	final static String IDENTIFIER = "CRITERION_PAGE";
	final static String ACTION_MIRNA_UP = "miRNAUPregulation";
	final static String ACTION_GENE_UP = "geneUpregulation";
	final static String ACTION_MIRNA_DOWN = "miRNADownregulation";
	final static String ACTION_GENE_DOWN = "geneDownregulation";
	final static String ACTION_NEG_INVERSE = "negInverse";
	final static String ACTION_POS_INVERSE = "posInverse";
	final static String ACTION_NEG_DIRECT = "negDirect";
	final static String ACTION_POS_DIRECT = "posDirect";
	final static String ACTION_ALL_REG = "allRegulation";

	final static String ACTION_INFO = "InfoButton";

	final static String ACTION_METHOD_DATASET = "methodDataset";
	final static String ACTION_METHOD_PATHWAY = "methodPathwayAndInteractions";
	final static String ACTION_METHOD_PATHWAY2 = "methodPathway";
	final static String ACTION_METHOD_ALL_GENES_MEASURED = "methodAllGenesMeasured";

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

	private JLabel miRNAUpGeneDown;
	private JCheckBox miRNAUpGeneDownCheck;

	private JLabel miRNADownGeneUp;
	private JCheckBox miRNADownGeneUpCheck;

	private JLabel miRNADownGeneDown;
	private JCheckBox miRNADownGeneDownCheck;

	private JLabel miRNAUpGeneUp;
	private JCheckBox miRNAUpGeneUpCheck;

	private JLabel allRegulations;
	private JCheckBox allRegulationsCheck;

	private JLabel methodDataset;
	private JCheckBox methodDatasetCheck;

	private JLabel methodInteractionPathway;
	private JCheckBox methodInteractionPathwayCheck;

	private JLabel methodPathway;
	private JCheckBox methodPathwayCheck;

	private JLabel methodAllGenesMeasured;
	private JCheckBox methodAllGenesMeasuredCheck;

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

		miRNAUpGeneDown = new JLabel(
				"Inverse: miRNA upregulated, Gene downregulated");
		miRNAUpGeneDownCheck = new JCheckBox();

		miRNADownGeneUp = new JLabel(
				"Inverse: miRNA downregulated, Gene upregulated");
		miRNADownGeneUpCheck = new JCheckBox();

		miRNADownGeneDown = new JLabel(
				"Direct: miRNA downregulated, Gene downregulated");
		miRNADownGeneDownCheck = new JCheckBox();

		miRNAUpGeneUp = new JLabel(
				"Direct: miRNA upregulated, Gene upregulated");
		miRNAUpGeneUpCheck = new JCheckBox();

		allRegulations = new JLabel("All of the above");
		allRegulationsCheck = new JCheckBox();

		methodDataset = new JLabel("Interaction Background");
		methodDatasetCheck = new JCheckBox();

		methodInteractionPathway = new JLabel(
				"Pathway with Interaction Background");
		methodInteractionPathwayCheck = new JCheckBox();

		methodPathway = new JLabel("Pathway Background");
		methodPathwayCheck = new JCheckBox();

		methodAllGenesMeasured = new JLabel("All genes measured Background");
		methodAllGenesMeasuredCheck = new JCheckBox();

		// pathway objects

		CellConstraints cc = new CellConstraints();

		FormLayout layout = new FormLayout(
				"pref,15dlu,pref,15dlu,pref,15dlu,pref,15dlu,pref,15dlu, pref, 15dlu, pref, default",
				"pref,10dlu, pref,8dlu,pref,20dlu,pref,20dlu,pref,10dlu,pref,8dlu,pref,8dlu,pref, 8dlu,pref, 8dlu,pref, 8dlu,pref, 8dlu,pref");
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

		builder.add(miRNAUpGeneDown, cc.xy(1, 15));
		builder.add(miRNAUpGeneDownCheck, cc.xy(3, 15));

		builder.add(miRNADownGeneUp, cc.xy(1, 17));
		builder.add(miRNADownGeneUpCheck, cc.xy(3, 17));

		builder.add(miRNAUpGeneUp, cc.xy(1, 19));
		builder.add(miRNAUpGeneUpCheck, cc.xy(3, 19));

		builder.add(miRNADownGeneDown, cc.xy(1, 21));
		builder.add(miRNADownGeneDownCheck, cc.xy(3, 21));

		builder.add(allRegulations, cc.xy(1, 23));
		builder.add(allRegulationsCheck, cc.xy(3, 23));

		builder.add(methodDataset, cc.xy(7, 15));
		builder.add(methodDatasetCheck, cc.xy(9, 15));

		builder.add(methodInteractionPathway, cc.xy(7, 17));
		builder.add(methodInteractionPathwayCheck, cc.xy(9, 17));

		builder.add(methodPathway, cc.xy(7, 19));
		builder.add(methodPathwayCheck, cc.xy(9, 19));

		builder.add(methodAllGenesMeasured, cc.xy(7, 21));
		builder.add(methodAllGenesMeasuredCheck, cc.xy(9, 21));

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

		miRNAUpGeneDownCheck.addActionListener(this);
		miRNAUpGeneDownCheck.setActionCommand(ACTION_NEG_INVERSE);

		miRNADownGeneUpCheck.addActionListener(this);
		miRNADownGeneUpCheck.setActionCommand(ACTION_POS_INVERSE);

		miRNADownGeneDownCheck.addActionListener(this);
		miRNADownGeneDownCheck.setActionCommand(ACTION_NEG_DIRECT);

		miRNAUpGeneUpCheck.addActionListener(this);
		miRNAUpGeneUpCheck.setActionCommand(ACTION_POS_DIRECT);

		allRegulationsCheck.addActionListener(this);
		allRegulationsCheck.setActionCommand(ACTION_ALL_REG);

		methodDatasetCheck.addActionListener(this);
		methodDatasetCheck.setActionCommand(ACTION_METHOD_DATASET);

		methodInteractionPathwayCheck.addActionListener(this);
		methodInteractionPathwayCheck.setActionCommand(ACTION_METHOD_PATHWAY);

		methodPathwayCheck.addActionListener(this);
		methodPathwayCheck.setActionCommand(ACTION_METHOD_PATHWAY2);

		methodAllGenesMeasuredCheck.addActionListener(this);
		methodAllGenesMeasuredCheck
				.setActionCommand(ACTION_METHOD_ALL_GENES_MEASURED);

		return builder.getPanel();
	}

	public void aboutToDisplayPanel() {
		getWizard().setPageTitle("Set Expression criteria");

		if (!DataHolding.isGeneFileLoaded()) {
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

		if (ACTION_MIRNA_UP.equals(action)) {
			ed.ExpressionDialog(miRNAUpExpr);
		}

		if (ACTION_GENE_UP.equals(action)) {
			ed.ExpressionDialog(geneUpExpr);
		}

		if (ACTION_MIRNA_DOWN.equals(action)) {
			ed.ExpressionDialog(miRNADownExpr);
		}

		if (ACTION_GENE_DOWN.equals(action)) {
			ed.ExpressionDialog(geneDownExpr);
		}

		if (ACTION_NEG_INVERSE.equals(action)) {
			if (miRNAUpGeneDownCheck.isSelected()) {
				DataHolding.setBolmiRNAUpGeneDown(true);

			}
			if (!miRNAUpGeneDownCheck.isSelected()) {
				DataHolding.setBolmiRNAUpGeneDown(false);
			}
		}
		if (ACTION_POS_INVERSE.equals(action)) {
			if (miRNADownGeneUpCheck.isSelected()) {
				DataHolding.setBolmiRNADownGeneUp(true);

			}
			if (!miRNADownGeneUpCheck.isSelected()) {
				DataHolding.setBolmiRNADownGeneUp(false);
			}

		}
		if (ACTION_NEG_DIRECT.equals(action)) {
			if (miRNADownGeneDownCheck.isSelected()) {
				DataHolding.setBolmiRNADownGeneDown(true);

			}
			if (!miRNADownGeneDownCheck.isSelected()) {
				DataHolding.setBolmiRNADownGeneDown(false);
			}

		}
		if (ACTION_POS_DIRECT.equals(action)) {
			if (miRNAUpGeneUpCheck.isSelected()) {
				DataHolding.setBolmiRNAUpGeneUp(true);

			}
			if (!miRNAUpGeneUpCheck.isSelected()) {
				DataHolding.setBolmiRNAUpGeneUp(false);
			}
		}
		if (ACTION_ALL_REG.equals(action)) {
			if (allRegulationsCheck.isSelected()) {
				DataHolding.setBolAllReg(true);
			}
			if (!allRegulationsCheck.isSelected()) {
				DataHolding.setBolAllReg(false);
			}

		}

		if (ACTION_METHOD_DATASET.equals(action)) {
			if (methodDatasetCheck.isSelected()) {
				DataHolding.setBolMethodDataset(true);
				methodInteractionPathwayCheck.setEnabled(false);
				methodPathwayCheck.setEnabled(false);
				methodAllGenesMeasuredCheck.setEnabled(false);
			}
			if (!methodDatasetCheck.isSelected()) {
				DataHolding.setBolMethodDataset(false);
				methodInteractionPathwayCheck.setEnabled(true);
				methodPathwayCheck.setEnabled(true);
				methodAllGenesMeasuredCheck.setEnabled(true);
			}
		}

		if (ACTION_METHOD_PATHWAY.equals(action)) {
			if (methodInteractionPathwayCheck.isSelected()) {
				DataHolding.setBolMethodPathway(true);
				methodDatasetCheck.setEnabled(false);
				methodPathwayCheck.setEnabled(false);
				methodAllGenesMeasuredCheck.setEnabled(false);
			}
			if (!methodInteractionPathwayCheck.isSelected()) {
				DataHolding.setBolMethodPathway(false);
				methodDatasetCheck.setEnabled(true);
				methodPathwayCheck.setEnabled(true);
				methodAllGenesMeasuredCheck.setEnabled(true);
			}
		}

		if (ACTION_METHOD_PATHWAY2.equals(action)) {
			if (methodPathwayCheck.isSelected()) {
				DataHolding.setBolMethodPathway2(true);
				methodInteractionPathwayCheck.setEnabled(false);
				methodDatasetCheck.setEnabled(false);
				methodAllGenesMeasuredCheck.setEnabled(false);

			}
			if (!methodPathwayCheck.isSelected()) {
				DataHolding.setBolMethodPathway2(false);
				methodInteractionPathwayCheck.setEnabled(true);
				methodDatasetCheck.setEnabled(true);
				methodAllGenesMeasuredCheck.setEnabled(true);
			}
		}

		if (ACTION_METHOD_ALL_GENES_MEASURED.equals(action)) {
			if (methodAllGenesMeasuredCheck.isSelected()) {
				DataHolding.setBolMethodAllGenesMeasured(true);
				methodInteractionPathwayCheck.setEnabled(false);
				methodDatasetCheck.setEnabled(false);
				methodPathwayCheck.setEnabled(false);
			}
			if (!methodAllGenesMeasuredCheck.isSelected()) {
				DataHolding.setBolMethodAllGenesMeasured(false);
				methodInteractionPathwayCheck.setEnabled(true);
				methodDatasetCheck.setEnabled(true);
				methodPathwayCheck.setEnabled(true);
			}
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

				if (expressionTextField.equals(miRNAUpExpr)
						&& miRNAUpExpr.getText() != null) {
					DataHolding.setMiRNAUpCrit(expressionTextField.getText());
					miRNAUpCrit.setExpression(expressionTextField.getText());

				}
				if (expressionTextField.equals(geneUpExpr)
						&& geneUpExpr.getText() != null) {
					DataHolding.setGeneUpCrit(expressionTextField.getText());
					geneUpCrit.setExpression(expressionTextField.getText());
				}
				if (expressionTextField.equals(miRNADownExpr)
						&& miRNADownExpr.getText() != null) {
					DataHolding.setMiRNADownCrit(expressionTextField.getText());
					miRNADownCrit.setExpression(expressionTextField.getText());
				}
				if (expressionTextField.equals(geneDownExpr)
						&& geneDownExpr.getText() != null) {
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

		if (miRNAUpExpr.getText().length() > 2) {
			DataHolding.miRNAUpCriterion.setExpression(miRNAUpExpr.getText());
			miRNAUpCrit.setExpression(miRNAUpExpr.getText());
			DataHolding.setMiRNAUpCritCheck(true);
		}

		if (miRNADownExpr.getText().length() > 2) {
			DataHolding.miRNADownCriterion.setExpression(miRNADownExpr
					.getText());
			miRNADownCrit.setExpression(miRNADownExpr.getText());
			DataHolding.setMiRNADownCritCheck(true);

		}

		if (DataHolding.isGeneFileLoaded()) {
			if (geneUpExpr.getText().length() > 2) {
				DataHolding.geneUpCriterion.setExpression(geneUpExpr.getText());
				geneUpCrit.setExpression(geneUpExpr.getText());
				DataHolding.setGeneUpCritCheck(true);
			}
			if (geneDownExpr.getText().length() > 2) {
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
		} catch (IDMapperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getWizard().getDialog().dispose();
	}

}
