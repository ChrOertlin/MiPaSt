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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.data.DataException;
import org.pathvisio.data.DataInterface;
import org.pathvisio.data.IRow;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.gex.CachedData;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.desktop.visualization.Criterion.CriterionException;

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

	private static PvDesktop desktop;
	private SwingEngine se;
	private RegIntPlugin plugin;

	public PositiveGeneList(PvDesktop desktop, SwingEngine se,
			RegIntPlugin plugin) {
		this.desktop = desktop;
		this.se = se;
		this.plugin = plugin;

	}

	

	
	
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

	public void execute() throws DataException, IOException {
		
		
		
		
		Set<Xref> miRNABackgroundSet = new HashSet<Xref>();
		Set<Xref> geneBackgroundSet = new HashSet<Xref>();
		Set<String> miRNAFinal = new HashSet<String>();
		Set<String> geneFinal = new HashSet<String>();

		miRNABackgroundSet = backgroundList(DataHolding
				.getMiRNAImportInformation().getDataSource());
		geneBackgroundSet = backgroundList(DataHolding
				.getGeneImportInformation().getDataSource());

		miRNAUpPosIntGenes = criterionEvaluation(DataHolding.miRNAUpCriterion,
				miRNABackgroundSet);
		miRNADownPosIntGenes = criterionEvaluation(
				DataHolding.miRNADownCriterion, miRNABackgroundSet);
		geneUpPosIntGenes = criterionEvaluation(DataHolding.geneUpCriterion,
				geneBackgroundSet);
		geneDownPosIntGenes = criterionEvaluation(
				DataHolding.geneDownCriterion, geneBackgroundSet);
		
		miRNAFinal.addAll(createFinalList(miRNAUpPosIntGenes));
		miRNAFinal.addAll(createFinalList(miRNADownPosIntGenes));
		geneFinal.addAll(createFinalList(geneUpPosIntGenes));
		geneFinal.addAll(createFinalList(geneDownPosIntGenes));
		
		wf.writeListToFile(miRNADownPosIntGenes, miRNADownFile);
		wf.writeListToFile(miRNAUpPosIntGenes, miRNAUpFile);
		wf.writeListToFile(geneDownPosIntGenes, geneDownFile);
		wf.writeListToFile(geneUpPosIntGenes, geneUpFile);
		System.out.println("miRNA"+ miRNAFinal +"\n");
		System.out.println("gene" + geneFinal + "\n");

	}

	public Set<String> createFinalList(Set<String> set) {
		Map<Xref, List<Interaction>> interactions = plugin.getInteractions();
		Set<String> finalList = new HashSet<String>();
		String [] arr = set.toArray(new String[set.size()]);
		for (int i = 0;i<arr.length;i++) {
			System.out.print("arr" + arr[i] + "\n");
			
		
			if (set != null
					&& interactions
							.containsKey(arr[i])) {
				
				
				finalList.add(arr[i]);
			}
		}
		return finalList;
	}

	


	


	public Set<Xref> backgroundList(DataSource ds) {
		Set<String> cGeneTotal = new HashSet<String>();
		Set<Xref> set = new HashSet<Xref>();
		try {
			// get all xrefs in dataset
			// TODO: for some reason the iterator doesn't work but in this way
			// it works

			for (int i = 0; i < desktop.getGexManager().getCurrentGex()
					.getNrRow(); i++) {
				IRow row = desktop.getGexManager().getCurrentGex().getRow(i);
				if (row.getXref().getDataSource() == ds) {
					set.add(row.getXref());
				}
			}
			Collection<? extends IRow> rows = desktop.getGexManager()
					.getCurrentGex().getData(set);
			if (rows != null) {
				for (IRow row : rows) {
					// Use group (line number) to identify a measurement
					cGeneTotal.add(row.getGroup() + "");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return set;
	}

	public static Set<String> criterionEvaluation(Criterion crit, Set<Xref> set)
			throws DataException {

		Set<String> cGenePositive = new HashSet<String>();

		Collection<? extends IRow> rows = desktop.getGexManager()
				.getCurrentGex().getData(set);

		if (rows != null) {
			for (IRow row : rows) {
				// // Use group (line number) to identify a measurement
				// cGeneTotal.add(row.getGroup() + "");
				try {
					boolean eval = crit.evaluate(row.getByName());
					if (eval)
						cGenePositive.add(row.getGroup() + "");
				} catch (CriterionException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("posgenes" + cGenePositive +"\n");
		return cGenePositive;
	}
}
