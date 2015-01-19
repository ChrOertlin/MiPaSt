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

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.rip.RegIntPlugin;
import org.pathvisio.rip.dialog.ColumnPage;
import org.pathvisio.rip.dialog.ImportPage;

import com.nexes.wizard.Wizard;

/**
 * 
 * MiPaSt Wizard, here all the pages for the MiPaSt plugin are created and
 * registered in the wizard. Also here is where the Regulatory interaction
 * plugin gets called and registered. At last the statistics plugin is called.
 * 
 * @author ChrOertlin
 * 
 */

public class MiPaStWizard extends Wizard {

	// Needed objects
	private final PvDesktop desktop;
	private RegIntPlugin plugin;
	private SwingEngine se;

	// initialize page objects
	StartInfoPage sip;
	FileLoaderPage flp;
	MiRNAFilesInformationPage mfip;
	MiRNAColumnPage mcp;
	GeneFilesInformationPage gfip;
	GeneColumnPage gcp;
	FileMergePage fmp;
	RipInfoPage rip;
	// FilePage fdp;
	RipFileLoaderPage rfdp;
	ColumnPage cpd;
	//ImportPage ipd;
	ImportPage ip;
	CriterionPage scp;
	StatInfoPage sti;
	StatisticsPage sp;

	public MiPaStWizard(PvDesktop desktop, RegIntPlugin plugin) {
		this.desktop = desktop;
		this.plugin = plugin;

		
		
		
		// MiPaSt wizard pages
		sip = new StartInfoPage();
		flp = new FileLoaderPage(desktop);
		mfip = new MiRNAFilesInformationPage();
		mcp = new MiRNAColumnPage();
		gfip = new GeneFilesInformationPage();
		gcp = new GeneColumnPage();
		fmp = new FileMergePage(desktop);
		rip = new RipInfoPage();
		scp = new CriterionPage(desktop, se,plugin);
		sp = new StatisticsPage(desktop,plugin);
		sti = new StatInfoPage();
		// Regulatory interaction plugin wizard pages

		rfdp = new RipFileLoaderPage(plugin);
		cpd = new ColumnPage(plugin);
		ip = new ImportPage(plugin, StatInfoPage.IDENTIFIER);

		registerWizardPanel(sip);
		registerWizardPanel(flp);
		registerWizardPanel(mfip);
		registerWizardPanel(mcp);
		registerWizardPanel(gfip);
		registerWizardPanel(gcp);
		registerWizardPanel(fmp);
		registerWizardPanel(rip);

		registerWizardPanel(rfdp);
		registerWizardPanel(cpd);
		registerWizardPanel(ip);

		registerWizardPanel (sti);
		registerWizardPanel(scp);
		registerWizardPanel(sp);

		setCurrentPanel(StartInfoPage.IDENTIFIER);

	}

}
