package org.pathvisio.mipast.gui;

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.mipast.MiPaStPlugin;
import org.pathvisio.mipast.gui.StartInfoPage;
import org.pathvisio.rip.RegIntPlugin;
import org.pathvisio.rip.dialog.FilePage;

import com.nexes.wizard.Wizard;

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
	FilePage fdp;
	
	
	
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
		
		// Regulatory interaction plugin wizard pages
		fdp= new FilePage(plugin);
		
		registerWizardPanel(sip);
		registerWizardPanel(flp);
		registerWizardPanel(mfip);
		registerWizardPanel(mcp);
		registerWizardPanel(gfip);
		registerWizardPanel(gcp);
		registerWizardPanel(fmp);
		registerWizardPanel(rip);
		registerWizardPanel(fdp);
		
		
		
		setCurrentPanel(StartInfoPage.IDENTIFIER);
		
	}
	
}

