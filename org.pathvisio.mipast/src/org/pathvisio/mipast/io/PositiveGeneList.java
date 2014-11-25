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

package org.pathvisio.mipast.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.mipast.util.MiPastZScoreCalculator;
import org.pathvisio.mipast.util.MiPastZScoreCalculator.RefInfo;
import org.pathvisio.mipast.util.WriteFiles;
import org.pathvisio.rip.Interaction;
import org.pathvisio.rip.RegIntPlugin;

/**
 * This class initiates the ZScorecalculator to create the XrefInfo for all the
 * criteria the user entered. With these criteria Xref lists of miRNA and genes
 * that fulfill the criteria will be created. If no criteria is given, no list
 * will be created for the specific regulation.
 * 
 * The regulations are:
 * 
 * miRNA upregulation miRNA downregulation gene upregulation gene downregulation
 * 
 * Finally, this class will see if the miRNA and or Genes are found in the
 * interaction file; if so they will be added to a positive geneList which will
 * be used in the statistics later on.
 */
public class PositiveGeneList {

	private PvDesktop desktop;
	private SwingEngine se;
	private RegIntPlugin plugin;

	public PositiveGeneList(PvDesktop desktop, SwingEngine se,
			RegIntPlugin plugin) {
		this.desktop = desktop;
		this.se = se;
		this.plugin = plugin;

	}

	private ImportInformation importInformation;
	private ProgressKeeper pk;

	private Map<Xref, List<Interaction>> interactions;
	private File pwDir;

	private Xref miRNAup;
	private Xref miRNADown;
	private Xref geneUp;
	private Xref geneDown;
	private RefInfo miRNAUpRef;
	private RefInfo geneUpRef;
	private RefInfo miRNADownRef;
	private RefInfo geneDownRef;

	private Set<String> miRNAUpPosIntGenes;
	private Set<String> geneUpPosIntGenes;
	private Set<String> miRNADownPosIntGenes;
	private Set<String> geneDownPosIntGenes;

	private File miRNAUpFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/miRNA_up_criteria.txt");
	private File miRNADownFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/miRNA_down_criteria.txt");
	private File geneUpFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/gene_up_criteria.txt");
	private File geneDownFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/gene_down_criteria.txt");

	private File miRNAUpIntFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/miRNA_up_has_interactions.txt");
	private File miRNADownIntFile = new File(PreferenceManager.getCurrent()
			.get(GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/miRNA_down_has_interactions.txt");
	private File geneUpIntFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/gene_up_has_interactions.txt");
	private File geneDownIntFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX)
			+ "/gene_down_has_interactions.txt");

	private WriteFiles wf = new WriteFiles();



	

	/**
	 * This method creates the Xref based upon the criteria given in the
	 * CriterionPage. It uses the method evaluateRef for this purpose. The
	 * outcome will be up to 4 XrefInfo objects, containing the positive genes
	 * and all genes measured. Furthermore there Xrefinfo will then be compared
	 * to the interaction files loaded in the regulatory interaction plugin. If
	 * the genes are in both lists, they will be added to a list containing all
	 * genes that fulfill the given criteria and have at least one interaction.
	 * 
	 * @throws IOException
	 */
	public void createXrefs(MiPastZScoreCalculator zcMiU,
			MiPastZScoreCalculator zcGu, MiPastZScoreCalculator zcMiD,
			MiPastZScoreCalculator zcGd) throws IOException {

		interactions = plugin.getInteractions();
		importInformation = DataHolding.getCombinedImportInformation();

		BufferedReader in = new BufferedReader(new FileReader(
				importInformation.getTxtFile()));
		String line = new String();
		for (int i = 0; i < importInformation.getDataRowsImported(); i++) {
			in.readLine();

			while ((line = in.readLine()) != null) {
				String[] str = line.split(importInformation.getDelimiter());

				for (int j = 0; j < importInformation.getDataRowsImported(); j++) {

					if (str[importInformation.getColNames().length - 1]
							.contains("miRNA")) {

						DataSource dsMiRNA = DataHolding
								.getMiRNAImportInformation().getDataSource();
						String miRNAString = str[0];
						miRNAup = new Xref(miRNAString, dsMiRNA);

						miRNADown = new Xref(miRNAString, dsMiRNA);
						if (miRNAup != null) {
							miRNAUpRef = new RefInfo(zcMiU.evaluateRef(miRNAup)
									.getProbesMeasured(), zcMiU.evaluateRef(
									miRNAup).getProbesPositive());
						}
						if (miRNADown != null) {
							miRNADownRef = new RefInfo(zcGu.evaluateRef(
									miRNADown).getProbesMeasured(), zcMiU
									.evaluateRef(miRNADown).getProbesPositive());
						}
					}
					if (str[importInformation.getColNames().length - 1]
							.contains("gene")) {

						DataSource dsGene = DataHolding
								.getGeneImportInformation().getDataSource();
						String geneString = str[0];
						geneUp = new Xref(geneString, dsGene);

						geneDown = new Xref(geneString, dsGene);
						if (geneUp != null) {
							geneUpRef = new RefInfo(zcGu.evaluateRef(geneUp)
									.getProbesMeasured(), zcMiU.evaluateRef(
									geneUp).getProbesPositive());
						}
						if (geneDown != null) {
							geneDownRef = new RefInfo(zcGu
									.evaluateRef(geneDown).getProbesMeasured(),
									zcMiU.evaluateRef(geneDown)
											.getProbesPositive());
						}
					}
				}

			}
		}

		if (miRNAUpRef.getProbesPositive()== null) {
			System.out.print("No positive probes" + "\n");
		}

		if (miRNAUpRef != null) {
			System.out.print("Positive probes" + "\n");
		}

		for (int i = 0; i < miRNAUpRef.getProbesPositive().size(); i++) {
			if (interactions.containsKey(miRNAUpRef.getProbesPositive()
					.toArray()[i]) && miRNAUpRef != null) {
				miRNAUpPosIntGenes.add((String) miRNAUpRef.getProbesPositive()
						.toArray()[i]);

			}
		}
		for (int i = 0; i < geneUpRef.getProbesPositive().size(); i++) {
			if (interactions.containsKey(geneUpRef.getProbesPositive()
					.toArray()[i]) && geneUpRef != null) {
				geneUpPosIntGenes.add((String) geneUpRef.getProbesPositive()
						.toArray()[i]);
			}
		}
		for (int i = 0; i < miRNADownRef.getProbesPositive().size(); i++) {
			if (interactions.containsKey(miRNADownRef.getProbesPositive()
					.toArray()[i]) && miRNADownRef != null) {
				miRNADownPosIntGenes.add((String) miRNADownRef
						.getProbesPositive().toArray()[i]);
			}
		}
		for (int i = 0; i < geneDownRef.getProbesPositive().size(); i++) {
			if (interactions.containsKey(geneDownRef.getProbesPositive()
					.toArray()[i]) && geneDownRef != null) {
				geneDownPosIntGenes.add((String) geneDownRef
						.getProbesPositive().toArray()[i]);
			}
		}

		DataHolding.setMiRNAUpList(miRNAUpPosIntGenes);
		DataHolding.setGeneUpList(geneUpPosIntGenes);
		DataHolding.setMiRNADownList(miRNADownPosIntGenes);
		DataHolding.setGeneDownList(geneDownPosIntGenes);

		// Write the different lists to a file to check what miRNA and genes
		// met the criteria and what met the criteria and had interactions.

		wf.writeListToFile(miRNADownRef.getProbesMeasured(), miRNADownFile);
		wf.writeListToFile(miRNAUpRef.getProbesMeasured(), miRNAUpFile);
		wf.writeListToFile(geneDownRef.getProbesMeasured(), geneDownFile);
		wf.writeListToFile(geneUpRef.getProbesMeasured(), geneUpFile);

	}

	/**
	 * 
	 * The retrieveFinalList method compares all Positive lists with each other
	 * and adds all genes to a final. Starting with the miRNAUpList, adding
	 * everything of the other lists that is not already in there. This Results
	 * into a final lists with genes that fulfill specified criteria and have an
	 * interaction.
	 * 
	 * @author ChrOertlin
	 */

	public void retrieveFinalList() {

		// do we actually need the miRNA list ??

		DataHolding.positiveGeneList = null;
		for (int i = 0; i < DataHolding.getMiRNAUpList().size(); i++) {
			DataHolding.positiveGeneList.add((String) DataHolding
					.getMiRNAUpList().toArray()[i]);
		}

		for (int i = 0; i < DataHolding.getMiRNADownList().size(); i++) {
			if (!DataHolding.positiveGeneList.contains(DataHolding
					.getMiRNAUpList().toArray()[i])) {
				DataHolding.positiveGeneList.add((String) DataHolding
						.getMiRNAUpList().toArray()[i]);
			}
		}
		for (int i = 0; i < DataHolding.getGeneDownList().size(); i++) {
			if (!DataHolding.positiveGeneList.contains(DataHolding
					.getGeneDownList().toArray()[i])) {
				DataHolding.positiveGeneList.add((String) DataHolding
						.getGeneDownList().toArray()[i]);
			}
		}
		for (int i = 0; i < DataHolding.getGeneUpList().size(); i++) {
			if (!DataHolding.positiveGeneList.contains(DataHolding
					.getGeneUpList().toArray()[i])) {
				DataHolding.positiveGeneList.add((String) DataHolding
						.getGeneUpList().toArray()[i]);
			}
		}

		DataHolding.setAllGenesList(geneUpRef.getProbesMeasured());
	}
	// class end token
}
