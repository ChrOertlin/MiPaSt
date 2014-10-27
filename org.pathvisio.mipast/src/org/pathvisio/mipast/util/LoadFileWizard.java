package org.pathvisio.mipast.util;

import java.util.ArrayList;
import java.util.List;


import org.pathvisio.mipast.util.RipImportInformation;

import com.nexes.wizard.Wizard;

/**
 * Modified version of the wizard dialog used for loading interaction files of the regulatory interaction plugin
 * @author Stefan
 */
public class LoadFileWizard extends Wizard {
	private List<RipImportInformation> importInformationList = new ArrayList<RipImportInformation>();
	


	
	private RipImportInformation currentFile = null;
	
	public LoadFileWizard ()
	{

	}

	public List<RipImportInformation> getImportInformationList() {
		return importInformationList;
	}

	public void setImportInformationList(List<RipImportInformation> importInformationList) {
		this.importInformationList = importInformationList;
	}

	public RipImportInformation getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(RipImportInformation currentFile) {
		this.currentFile = currentFile;
	}
	

}