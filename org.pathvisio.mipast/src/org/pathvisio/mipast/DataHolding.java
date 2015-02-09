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
	public static Set<Xref> geneBackgroundSet;
	
	public static Set<Xref> miRNAFinal;
	
	
	public static Set<Xref> allGenesList;
	public static Set<Xref> allmiRNAList;
	
	public static Set<Xref> pathwayGenes;
	
	public static boolean bolmiRNAUpGeneDown;
	public static boolean bolmiRNADownGeneUp;
	public static boolean bolmiRNADownGeneDown;
	public static boolean bolmiRNAUpGeneUp;
	
	public static boolean isBolmiRNAUpGeneDown() {
		return bolmiRNAUpGeneDown;
	}

	public static void setBolmiRNAUpGeneDown(boolean bolmiRNAUpGeneDown) {
		DataHolding.bolmiRNAUpGeneDown = bolmiRNAUpGeneDown;
	}

	public static boolean isBolmiRNADownGeneUp() {
		return bolmiRNADownGeneUp;
	}

	public static void setBolmiRNADownGeneUp(boolean bolmiRNADownGeneUp) {
		DataHolding.bolmiRNADownGeneUp = bolmiRNADownGeneUp;
	}

	public static boolean isBolmiRNADownGeneDown() {
		return bolmiRNADownGeneDown;
	}

	public static void setBolmiRNADownGeneDown(boolean bolmiRNADownGeneDown) {
		DataHolding.bolmiRNADownGeneDown = bolmiRNADownGeneDown;
	}

	public static boolean isBolmiRNAUpGeneUp() {
		return bolmiRNAUpGeneUp;
	}

	public static void setBolmiRNAUpGeneUp(boolean bolmiRNAUpGeneUp) {
		DataHolding.bolmiRNAUpGeneUp = bolmiRNAUpGeneUp;
	}

	public static boolean bolAllReg;
	
	public static boolean bolMethodDataset;
	public static boolean bolMethodPathway;
	
	public static boolean bolMethodPathway2;
	public static boolean bolMethodAllGenesMeasured;
	


	public static boolean isBolMethodPathway2() {
		return bolMethodPathway2;
	}

	public static void setBolMethodPathway2(boolean bolMethodPathway2) {
		DataHolding.bolMethodPathway2 = bolMethodPathway2;
	}

	public static boolean isBolMethodAllGenesMeasured() {
		return bolMethodAllGenesMeasured;
	}

	public static void setBolMethodAllGenesMeasured(
			boolean bolMethodAllGenesMeasured) {
		DataHolding.bolMethodAllGenesMeasured = bolMethodAllGenesMeasured;
	}

	public static boolean isBolMethodDataset() {
		return bolMethodDataset;
	}

	public static void setBolMethodDataset(boolean bolMethodDataset) {
		DataHolding.bolMethodDataset = bolMethodDataset;
	}

	public static boolean isBolMethodPathway() {
		return bolMethodPathway;
	}

	public static void setBolMethodPathway(boolean bolMethodPathway) {
		DataHolding.bolMethodPathway = bolMethodPathway;
	}

	

	public static boolean isBolAllReg() {
		return bolAllReg;
	}

	public static void setBolAllReg(boolean bolAllReg) {
		DataHolding.bolAllReg = bolAllReg;
	}

	public static Set<Xref> getPathwayGenes() {
		return pathwayGenes;
	}

	public static void setPathwayGenes(Set<Xref> pathwayGenes) {
		DataHolding.pathwayGenes = pathwayGenes;
	}

	public static Set<Xref> getAllmiRNAList() {
		return allmiRNAList;
	}

	public static void setAllmiRNAList(Set<Xref> allmiRNAList) {
		DataHolding.allmiRNAList = allmiRNAList;
	}

	public static Set<Xref> getGeneBackgroundSet() {
		return geneBackgroundSet;
	}

	public static void setGeneBackgroundSet(Set<Xref> geneBackgroundSet) {
		DataHolding.geneBackgroundSet = geneBackgroundSet;
	}

	public static Set<Xref> getMiRNAFinal() {
		return miRNAFinal;
	}

	public static void setMiRNAFinal(Set<Xref> miRNAFinal) {
		DataHolding.miRNAFinal = miRNAFinal;
	}

	public static Set<Xref> getGeneFinal() {
		return geneFinal;
	}

	public static void setGeneFinal(Set<Xref> geneFinal) {
		DataHolding.geneFinal = geneFinal;
	}

	public static Set<Xref> getGeneTotal() {
		return geneBackgroundSet;
	}

	public static void setGeneTotal(Set<Xref> geneTotal) {
		DataHolding.geneBackgroundSet = geneTotal;
	}

	public static Set<String> getBackgroundSet() {
		return backgroundSet;
	}

	public static void setBackgroundSet(Set<String> backgroundSet) {
		DataHolding.backgroundSet = backgroundSet;
	}



	public static Set<Xref> getAllGenesList() {
		return allGenesList;
	}

	public static void setAllGenesList(Set<Xref> allGenesList) {
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
