package org.pathvisio.mipast.gui;



import java.awt.Component;

import javax.swing.JLabel;

import org.pathvisio.rip.dialog.FilePage;
import org.pathvisio.rip.dialog.ImportPage;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

class StatInfoPage extends WizardPanelDescriptor {
	
	static final String IDENTIFIER = "STAT_INFO_PAGE";

	public StatInfoPage() {
		super(IDENTIFIER);

	}

	public Object getNextPanelDescriptor() {
		return CriterionPage.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return ImportPage.IDENTIFIER;
	}

	@Override
	protected Component createContents() {
		FormLayout layout = new FormLayout("pref,pref:grow",
				"pref,40dlu,pref,40dlu,pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		JLabel statInfo = new JLabel(
				"Step 3: Set expression criteria and perform statistics");
		builder.addSeparator("", cc.xyw(1, 1, 2));
		builder.add(statInfo, cc.xy(1, 3));
		builder.addSeparator("", cc.xyw(1, 5, 2));
		return builder.getPanel();

	}

	public void aboutToDisplayPanel() {
		getWizard().setNextFinishButtonEnabled(true);
		getWizard().setBackButtonEnabled(true);

		getWizard().setPageTitle("Expression criteria and statistics");

	}

}

