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
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.nexes.wizard.WizardPanelDescriptor;

/**
 * 
 * The RipInfoPage lets the user know that he is about to continue with the Regulatory Interaction Plugin
 * and needs to load interaction files to establish the interactions between miRNA and Genes.
 * 
 * @author ChrOertlin
 *
 */

class RipInfoPage extends WizardPanelDescriptor {
	static final String IDENTIFIER = "RIP_INFO_PAGE";

	public RipInfoPage() {
		super(IDENTIFIER);
	}

	public Object getNextPanelDescriptor() {
		return RipFileLoaderPage.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return FileMergePage.IDENTIFIER;
	}

	@Override
	protected Component createContents() {
		FormLayout layout = new FormLayout("pref,pref:grow",
				"pref,40dlu,pref,40dlu,pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		JLabel ripInfo = new JLabel(
				"Step 2: Load interaction file(s); press Next");
		builder.addSeparator("", cc.xyw(1, 1, 2));
		builder.add(ripInfo, cc.xy(1, 3));
		builder.addSeparator("", cc.xyw(1, 5, 2));
		return builder.getPanel();
	}

	public void aboutToDisplayPanel() {

		getWizard().setNextFinishButtonEnabled(true);
		getWizard().setBackButtonEnabled(true);
	}

}