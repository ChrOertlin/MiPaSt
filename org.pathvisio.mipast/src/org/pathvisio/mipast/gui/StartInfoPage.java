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

import javax.swing.JLabel;

import org.pathvisio.mipast.DataHolding;


import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

/**
 * 
 * The StartInfoPage is the very first page of the MiPaSt plugin. Given the user information about
 * the first step in the mipast plugin.
 * 
 * @author ChrOertlin
 *
 */

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
		DataHolding.setMipastActive(true);

	}

}
