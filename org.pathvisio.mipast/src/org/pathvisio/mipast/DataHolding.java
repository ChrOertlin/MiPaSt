package org.pathvisio.mipast;

import java.io.File;

import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.mipast.util.RipImportInformation;

/**
 * Class that holds all the data objects for the MiPaSt plugin.
 * For every data object there are getters and setters.
 * 
 * @author ChrOertlin
 *
 */

public class DataHolding {
	private static ImportInformation miRNAImportInformation = new ImportInformation();
	private static ImportInformation geneImportInformation = new ImportInformation();
	private static ImportInformation combinedImportInformation = new ImportInformation();
	private static File miRNAFile;
	private static File geneFile;
	private RipImportInformation importInformation = new RipImportInformation();
	
	public RipImportInformation getImportInformation() {
		return importInformation;
	}
	public void setImportInformation(RipImportInformation importInformation) {
		this.importInformation = importInformation;
	}
	public static ImportInformation getMiRNAImportInformation() {
		return miRNAImportInformation;
	}
	public void setMiRNAImportInformation(ImportInformation miRNAImportInformation) {
		this.miRNAImportInformation = miRNAImportInformation;
	}
	public static ImportInformation getGeneImportInformation() {
		return geneImportInformation;
	}
	public void setGeneImportInformation(ImportInformation geneImportInformation) {
		this.geneImportInformation = geneImportInformation;
	}
	public static ImportInformation getCombinedImportInformation() {
		return combinedImportInformation;
	}
	public void setCombinedImportInformation(
			ImportInformation combinedImportInformation) {
		this.combinedImportInformation = combinedImportInformation;
	}
	public static File getMiRNAFile() {
		return miRNAFile;
	}
	public static void setMiRNAFile(File miRNAFile) {
		DataHolding.miRNAFile = miRNAFile;
	}
	public static File getGeneFile() {
		return geneFile;
	}
	public static void setGeneFile(File geneFile) {
		DataHolding.geneFile = geneFile;
	}
}
