package org.pathvisio.mipast.gui;

import java.awt.Component;

import javax.swing.JLabel;

import org.pathvisio.rip.dialog.FilePage;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

class StartInfoPage extends WizardPanelDescriptor {
	
	static final String IDENTIFIER = "START_INFO_PAGE";

	public StartInfoPage() {
		super(IDENTIFIER);

	}

	public Object getNextPanelDescriptor() {
		return FileLoaderPage.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return null;
	}

	@Override
	protected Component createContents() {
		FormLayout layout = new FormLayout("pref,pref:grow",
				"pref,40dlu,pref,40dlu,pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		JLabel ripInfo = new JLabel(
				"Step 1: Load interaction miRNA and Transcriptomics file(s); press next for the import wizard");
		builder.addSeparator("", cc.xyw(1, 1, 2));
		builder.add(ripInfo, cc.xy(1, 3));
		builder.addSeparator("", cc.xyw(1, 5, 2));
		return builder.getPanel();

	}

	public void aboutToDisplayPanel() {
		getWizard().setNextFinishButtonEnabled(true);
		getWizard().setBackButtonEnabled(true);

		getWizard().setPageTitle("MiPaSt plugin");

	}

}
