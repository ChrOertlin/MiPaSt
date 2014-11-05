package org.pathvisio.mipast.gui;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

public class CriterionPage extends WizardPanelDescriptor{
	
	
	final static String IDENTIFIER = "CRITERION_PAGE";
	
	
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
	
	
	public CriterionPage(){
		super(IDENTIFIER);
	}

	@Override
	protected Component createContents() {
		
		
		miRNALbl = new JLabel("miRNA");
		
		miRNAUp = new JLabel("Upregulated");
		miRNAUpExpr = new JTextField();
		miRNAUpButton = new JButton("Expr");
		
		miRNADown = new JLabel("Downregulated");
		miRNADownExpr = new JTextField();
		miRNADownButton = new JButton("Expr");
		
		geneLbl = new JLabel("Gene");
		
		geneUp= new JLabel("Upregulated");
		geneUpExpr= new JTextField();
		geneUpButton = new JButton("Expr");
		
		geneDown = new JLabel("Downregulated");
		geneDownExpr = new JTextField();
		geneDownButton = new JButton("Expr");
		
		
	// gene GUI objects	
		geneLbl= new JLabel("Gene");
		geneUp= new JLabel("Upregulated");
		geneDown= new JLabel("Downregulated");
		
		
		infoBtn= new JButton("Info");
		
		CellConstraints cc = new CellConstraints();
		FormLayout layout = new FormLayout(
				"pref,15dlu,pref,15dlu,15dlu,pref,pref,15dlu, pref, 15dlu, pref, default",
				"pref,8dlu, pref,15dlu,pref,15dlu,pref,4dlu,pref,4dlu,pref,15dlu,pref,4dlu,pref, 4dlu,pref");
		PanelBuilder builder = new PanelBuilder(layout);

		
		
		
		
		builder.addSeparator("", cc.xyw(1, 2, 8));
		builder.addSeparator("",cc.xywh(6, 1, 1, 6));
		
		builder.add(miRNALbl, cc.xy(1,3));
		builder.add(geneLbl, cc.xy(7,3));
		builder.addSeparator("",cc.xyw(1, 5, 8));
		
		builder.add(miRNAUp, cc.xy(1,6));
		builder.add(geneUp, cc.xy(7, 6));
		
		builder.add(miRNAUpExpr, cc.xy(1, 7));
		builder.add(geneUpExpr, cc.xy(7, 7));
		
		builder.add(miRNAUpButton, cc.xy(5,7));
		builder.add(geneUpButton, cc.xy(9, 7));
		
		builder.addSeparator("",cc.xyw(1, 8, 8));
		
		builder.add(miRNADown, cc.xy(1,9));
		builder.add(geneDown, cc.xy(7,9));
		
		builder.add(miRNADownExpr, cc.xy(1,11));
		builder.add(geneDownExpr,cc.xy(7,11));
		
		builder.add(miRNADownButton,cc.xy(5,11));
		builder.add(geneDownButton, cc.xy(9,11));
		
		builder.add(infoBtn, cc.xy(5,13));
		
		
		return builder.getPanel();
	}

	
	public void aboutToDisplayPanel(){
		getWizard().setPageTitle("Set Expression criteria");
		
	}
}
