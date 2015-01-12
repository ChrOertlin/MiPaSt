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
package org.pathvisio.mipast;

import java.io.File;
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.mipast.util.RipImportInformation;

/**
 * Class that holds all the data objects for the MiPaSt plugin. For every data
 * object there are getters and setters.
 * 
 * @author ChrOertlin
 * 
 */

public class DataHolding {

	private PvDesktop desktop;

	public DataHolding(PvDesktop desktop) {
		this.desktop = desktop;
	}

	private static String geneSysCode;
	private static String miRNASysCode;
	

	private static ImportInformation miRNAImportInformation = new ImportInformation();
	private static ImportInformation geneImportInformation = new ImportInformation();
	private static ImportInformation combinedImportInformation = new ImportInformation();
	private static File miRNAFile;
	private static File geneFile;
	private RipImportInformation importInformation = new RipImportInformation();

	private static boolean geneFileLoaded = false;
	private static boolean miRNAUpCritCheck = false;
	private static boolean miRNADownCritCheck = false;
	private static boolean geneUpCritCheck = false;
	private static boolean geneDownCritCheck = false;

	private static boolean mipastActive = false;

	private static String miRNAUpCrit;
	private static String geneUpCrit;
	private static String miRNADownCrit;
	private static String geneDownCrit;

	public static Criterion miRNAUpCriterion = new Criterion();
	public static Criterion geneUpCriterion = new Criterion();

	public static Criterion miRNADownCriterion = new Criterion();
	public static Criterion geneDownCriterion = new Criterion();

	private static Set<String> miRNAUpList;
	private static Set<String> geneUpList;
	private static Set<String> miRNADownList;
	private static Set<String> geneDownList;

	public static Set<String> positiveGeneList;
	public static Set<String> backgroundSet;
	
	public static Set<Xref>	geneFinal;
	public static Set<Xref> geneTotal;
	

	public static Set<Xref> getGeneFinal() {
		return geneFinal;
	}

	public static void setGeneFinal(Set<Xref> geneFinal) {
		DataHolding.geneFinal = geneFinal;
	}

	public static Set<Xref> getGeneTotal() {
		return geneTotal;
	}

	public static void setGeneTotal(Set<Xref> geneTotal) {
		DataHolding.geneTotal = geneTotal;
	}

	public static Set<String> getBackgroundSet() {
		return backgroundSet;
	}

	public static void setBackgroundSet(Set<String> backgroundSet) {
		DataHolding.backgroundSet = backgroundSet;
	}

	public static Set<String> allGenesList;

	public static Set<String> getAllGenesList() {
		return allGenesList;
	}

	public static void setAllGenesList(Set<String> allGenesList) {
		DataHolding.allGenesList = allGenesList;
	}

	public static Set<String> getPositiveGeneList() {
		return positiveGeneList;
	}

	public static void setPositiveGeneList(Set<String> positiveGeneList2) {
		DataHolding.positiveGeneList = positiveGeneList2;
	}

	public static Set<String> getMiRNAUpList() {
		return miRNAUpList;
	}

	public static void setMiRNAUpList(Set<String> miRNAUpList) {
		DataHolding.miRNAUpList = miRNAUpList;
	}

	public static Set<String> getGeneUpList() {
		return geneUpList;
	}

	public static void setGeneUpList(Set<String> geneUpList) {
		DataHolding.geneUpList = geneUpList;
	}

	public static Set<String> getMiRNADownList() {
		return miRNADownList;
	}

	public static void setMiRNADownList(Set<String> miRNADownList) {
		DataHolding.miRNADownList = miRNADownList;
	}

	public static Set<String> getGeneDownList() {
		return geneDownList;
	}

	public static void setGeneDownList(Set<String> geneDownList) {
		DataHolding.geneDownList = geneDownList;
	}

	public static String getMiRNAUpCrit() {
		System.out.print(miRNAUpCrit);
		return miRNAUpCrit;
	}

	public static void setMiRNAUpCrit(String miRNAUpCrit) {
		DataHolding.miRNAUpCrit = miRNAUpCrit;
	}

	public static String getGeneUpCrit() {
		return geneUpCrit;
	}

	public static void setGeneUpCrit(String geneUpCrit) {
		DataHolding.geneUpCrit = geneUpCrit;
	}

	public static String getMiRNADownCrit() {
		return miRNADownCrit;
	}

	public static void setMiRNADownCrit(String miRNADownCrit) {
		DataHolding.miRNADownCrit = miRNADownCrit;
	}

	public static String getGeneDownCrit() {
		return geneDownCrit;
	}

	public static void setGeneDownCrit(String geneDownCrit) {
		DataHolding.geneDownCrit = geneDownCrit;
	}

	public boolean isMipastActive() {
		return mipastActive;
	}

	public static void setMipastActive(boolean mipastActive) {
		DataHolding.mipastActive = mipastActive;
	}

	public RipImportInformation getImportInformation() {
		return importInformation;
	}

	public void setImportInformation(RipImportInformation importInformation) {
		this.importInformation = importInformation;
	}

	public static ImportInformation getMiRNAImportInformation() {
		return miRNAImportInformation;
	}

	public void setMiRNAImportInformation(
			ImportInformation miRNAImportInformation) {
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

	public static Criterion getGeneUpCriterion() {
		return geneUpCriterion;
	}

	public static void setGeneUpCriterion(Criterion geneUpCriterion) {
		DataHolding.geneUpCriterion = geneUpCriterion;
	}

	public static Criterion getGeneDownCriterion() {
		return geneDownCriterion;
	}

	public static void setGeneDownCriterion(Criterion geneDownCriterion) {
		DataHolding.geneDownCriterion = geneDownCriterion;
	}

	public static Criterion getMiRNAUpCriterion() {
		return miRNAUpCriterion;
	}

	public static void setMiRNAUpCriterion(Criterion miRNAUpCriterion) {
		DataHolding.miRNAUpCriterion = miRNAUpCriterion;
	}

	public static Criterion getMiRNADownCriterion() {
		return miRNADownCriterion;
	}

	public static void setMiRNADownCriterion(Criterion miRNADownCriterion) {
		DataHolding.miRNADownCriterion = miRNADownCriterion;
	}

	public static boolean isMiRNAUpCritCheck() {
		return miRNAUpCritCheck;
	}

	public static void setMiRNAUpCritCheck(boolean miRNAUpCritCheck) {
		DataHolding.miRNAUpCritCheck = miRNAUpCritCheck;
	}

	public static boolean isMiRNADownCritCheck() {
		return miRNADownCritCheck;
	}

	public static void setMiRNADownCritCheck(boolean miRNADownCritCheck) {
		DataHolding.miRNADownCritCheck = miRNADownCritCheck;
	}

	public static boolean isGeneUpCritCheck() {
		return geneUpCritCheck;
	}

	public static void setGeneUpCritCheck(boolean geneUpCritCheck) {
		DataHolding.geneUpCritCheck = geneUpCritCheck;
	}

	public static boolean isGeneDownCritCheck() {
		return geneDownCritCheck;
	}

	public static void setGeneDownCritCheck(boolean geneDownCritCheck) {
		DataHolding.geneDownCritCheck = geneDownCritCheck;
	}

	public static boolean isGeneFileLoaded() {
		return geneFileLoaded;
	}

	public static void setGeneFileLoaded(boolean geneFileLoaded) {
		DataHolding.geneFileLoaded = geneFileLoaded;
	}


	public static String getGeneSysCode() {
		return geneSysCode;
	}

	public static void setGeneSysCode(String geneSysCode) {
		DataHolding.geneSysCode = geneSysCode;
	}

	public static String getMiRNASysCode() {
		return miRNASysCode;
	}

	public static void setMiRNASysCode(String miRNASysCode) {
		DataHolding.miRNASysCode = miRNASysCode;
	}
}
