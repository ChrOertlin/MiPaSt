package org.pathvisio.mipast.gui;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.mipast.gui.StartInfoPage;
import org.pathvisio.rip.RegIntPlugin;
import org.pathvisio.rip.dialog.FilePage;
import org.pathvisio.rip.dialog.ColumnPage;
import org.pathvisio.rip.dialog.ImportPage;

import com.nexes.wizard.Wizard;

/**
 * 
 * MiPaSt Wizard, here all the pages for the MiPaSt plugin are created and registered
 * in the wizard. Also here is where the Regulatory interaction plugin gets called and registered.
 * At last the statistics plugin is called.
 * 
 * @author ChrOertlin
 *
 */


public class MiPaStWizard extends Wizard {
	
	// Needed objects
	private final PvDesktop standaloneEngine;
	private RegIntPlugin plugin;
	
	// initialize page objects
	StartInfoPage sip;
	FileLoaderPage flp;
	MiRNAFilesInformationPage mfip;
	MiRNAColumnPage mcp;
	GeneFilesInformationPage gfip;
	GeneColumnPage gcp;
	FileMergePage fmp;
	RipInfoPage rip;
	//FilePage fdp;
	RipFileLoaderPage rfdp;
	ColumnPage cpd;
	ImportPage ipd;
	CriterionPage scp;
	
	
	public MiPaStWizard(PvDesktop desktop, RegIntPlugin plugin){
		this.standaloneEngine= desktop;
		this.plugin = plugin;
		
		// MiPaSt wizard pages
		sip = new StartInfoPage();
		flp = new FileLoaderPage(desktop);
		mfip = new MiRNAFilesInformationPage();
		mcp= new MiRNAColumnPage();
		gfip = new GeneFilesInformationPage();
		gcp = new GeneColumnPage();
		fmp = new FileMergePage(desktop);
		rip = new RipInfoPage();
		scp= new CriterionPage();
		
		// Regulatory interaction plugin wizard pages
		
		rfdp = new RipFileLoaderPage(plugin);
		cpd = new ColumnPage(plugin);
		ipd = new ImportPage(plugin);
		
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
		registerWizardPanel(ipd);
		
		registerWizardPanel(scp);
		
		
		setCurrentPanel(CriterionPage.IDENTIFIER);
		
	}
	
}

