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
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.pathvisio.mipast.util.BackgroundsetMethods;
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

	private Set<Xref> allXref = new HashSet<Xref>();
	private Set<Xref> geneTotalList = new HashSet<Xref>();
	private Set<Xref> miRNATotalList = new HashSet<Xref>();
	private Set<String> miRNAUpPosIntGenes;
	private Set<String> geneUpPosIntGenes;
	private Set<String> miRNADownPosIntGenes;
	private Set<String> geneDownPosIntGenes;

	Set<Xref> xrefSet = new HashSet<Xref>();

	public void execute() throws DataException, IOException {

		Set<Xref> allMiRNAInDataset = new HashSet<Xref>();
		Set<Xref> allGenesInDataset = new HashSet<Xref>();

		Set<Xref> miRNAFinalUp = new HashSet<Xref>();
		Set<Xref> geneFinalUp = new HashSet<Xref>();
		Set<Xref> miRNAFinalDown = new HashSet<Xref>();
		Set<Xref> geneFinalDown = new HashSet<Xref>();

		Set<Xref> miRNAFinal = new HashSet<Xref>();
		Set<Xref> geneFinal = new HashSet<Xref>();

		allMiRNAInDataset = getallXrefs(DataHolding.getMiRNAImportInformation()
				.getDataSource(), "miRNA");

		if (DataHolding.getGeneImportInformation() != null) {
			allGenesInDataset = getallXrefs(DataHolding
					.getGeneImportInformation().getDataSource(), "gene");
		}

		if (DataHolding.isMiRNAUpCritCheck() & DataHolding.isBolNegInverse()
				|| DataHolding.isMiRNAUpCritCheck()
				& DataHolding.isBolPosDirect()
				|| DataHolding.isMiRNAUpCritCheck() & DataHolding.isBolAllReg()) {
			miRNAUpPosIntGenes = criterionEvaluation(
					DataHolding.getMiRNAUpCriterion(), allMiRNAInDataset);
			miRNAFinalUp.addAll(createMetcriteriaHaveInteractionList(miRNAUpPosIntGenes, allXref,
					DataHolding.getMiRNASysCode()));
			// wf.writeListToFile(miRNADownPosIntGenes, miRNADownFile);

			System.out.print("miRNAUpRegulated");
		}

		if (DataHolding.isMiRNAUpCritCheck() & DataHolding.isBolPosInverse()
				|| DataHolding.isMiRNAUpCritCheck()
				& DataHolding.isBolNegDirect()
				|| DataHolding.isMiRNAUpCritCheck() & DataHolding.isBolAllReg()) {
			miRNADownPosIntGenes = criterionEvaluation(
					DataHolding.getMiRNADownCriterion(), allMiRNAInDataset);
			miRNAFinalDown.addAll(createMetcriteriaHaveInteractionList(miRNADownPosIntGenes,
					allXref, DataHolding.getMiRNASysCode()));
			
			System.out.print("miRNADownRegulated");

		}

		if (DataHolding.isGeneFileLoaded() & DataHolding.isGeneUpCritCheck()
				& DataHolding.isBolAllReg() || DataHolding.isGeneFileLoaded()
				& DataHolding.isGeneUpCritCheck()
				& DataHolding.isBolPosInverse()
				|| DataHolding.isGeneFileLoaded()
				& DataHolding.isGeneUpCritCheck()
				& DataHolding.isBolPosDirect()) {
			geneUpPosIntGenes = criterionEvaluation(
					DataHolding.geneUpCriterion, allGenesInDataset);

			geneFinalUp.addAll(createMetcriteriaHaveInteractionList(geneUpPosIntGenes, allXref,
					DataHolding.getGeneSysCode()));
			
			System.out.print("GeneUpRegulated");
			
		}

		if (DataHolding.isGeneFileLoaded() & DataHolding.isGeneUpCritCheck()
				& DataHolding.isBolAllReg() || DataHolding.isGeneFileLoaded()
				& DataHolding.isGeneUpCritCheck()
				& DataHolding.isBolNegInverse()
				|| DataHolding.isGeneFileLoaded()
				& DataHolding.isGeneUpCritCheck()
				& DataHolding.isBolNegDirect()) {
			geneDownPosIntGenes = criterionEvaluation(
					DataHolding.geneDownCriterion, allGenesInDataset);
			geneFinalDown.addAll(createMetcriteriaHaveInteractionList(geneDownPosIntGenes, allXref,
					DataHolding.getGeneSysCode()));
			
			System.out.print("GeneDownRegulated");

		}

		// if (DataHolding.isGeneFileLoaded()) {
		// for (Xref x : geneFinal) {
		// positiveGeneList.add(x.toString());
		// }
		// for (Xref y : geneTotalList) {
		// backgroundSet.add(y.toString());
		// }

		// }
		// else {
		// for (Xref x : miRNAFinal) {
		// positiveGeneList.add(x.toString());
		// }
		// for (Xref y : miRNABackgroundSet) {
		// backgroundSet.add(y.toString());
		// }
		// }

		

		DataHolding.setAllGenesList(allGenesInDataset);
		DataHolding.setAllmiRNAList(allMiRNAInDataset);

		geneFinal = addLists(geneFinalDown,geneFinalUp);
		miRNAFinal = addLists(miRNAFinalDown,miRNAFinalUp);
		
		System.out.print("genefinal: " + geneFinal+ "\n");
		System.out.print("miRNAfinal: " + miRNAFinal+ "\n");
		
		DataHolding.setGeneFinal(geneFinal);
		DataHolding.setMiRNAFinal(miRNAFinal);

		

	}
	
	
	public Set<Xref> addLists(Set<Xref> listDown, Set<Xref> listUp){
		Set<Xref> finalList = new HashSet<Xref>();
		for(Xref x:listDown){
			finalList.add(x);
		}
		for(Xref y:listUp){
			finalList.add(y);
		}
		return finalList;
	}
	

	public Set<Xref> createMetcriteriaHaveInteractionList(Set<String> set, Set<Xref> xrefSet,
			String ds) {

		Map<Xref, List<Interaction>> interactions = plugin.getInteractions();
		Object[] refArr = xrefSet.toArray();
		Set<Xref> positiveList = new HashSet<Xref>();
		String[] arr = set.toArray(new String[set.size()]);

		for (int i = 0; i < arr.length; i++) {

			int k = Integer.parseInt(arr[i]);
			if (k == refArr.length) {
				k = refArr.length - 1;
			}
			if (set != null && interactions.containsKey(refArr[k])
					&& refArr[k].toString().startsWith(ds)) {

				positiveList.add((Xref) refArr[k]);
			}

		}
		return positiveList;
	}

	/**
	 * This methods goes through all rows in the dataset and gets the Xrefs from
	 * the data. And adds their Xref to the xrefSet list. Creates two lists one
	 * for the miRNA Xrefs and one for the Gene Xrefs.
	 * 
	 * @param ds
	 * @param type
	 * @return
	 */

	public Set<Xref> getallXrefs(DataSource ds, String type) {
		Set<String> cGeneTotal = new HashSet<String>();

		if (ds == null && type == "miRNA") {
			ds = DataSource.getBySystemCode(DataHolding.getMiRNASysCode());

		}

		if (ds == null && type == "gene") {
			ds = DataSource.getBySystemCode(DataHolding.getGeneSysCode());
		}

		try {
			// get all xrefs in dataset
			// TODO: for some reason the iterator doesn't work but in this way
			// it works

			for (int i = 0; i < desktop.getGexManager().getCurrentGex()
					.getNrRow(); i++) {
				IRow row = desktop.getGexManager().getCurrentGex().getRow(i);

				if (row.getXref().getDataSource() == ds) {
					allXref.add(row.getXref());
				}
				if (row.getXref().getDataSource() == ds && type != "gene") {
					miRNATotalList.add(row.getXref());

				}
				if (row.getXref().getDataSource() == ds && type != "miRNA") {
					geneTotalList.add(row.getXref());

				}
			}
			Collection<? extends IRow> rows = desktop.getGexManager()
					.getCurrentGex().getData(allXref);
			if (rows != null) {
				for (IRow row : rows) {
					// Use group (line number) to identify a measurement
					cGeneTotal.add(row.getGroup() + "");

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (type == "gene") {
			xrefSet = geneTotalList;
		}
		if (type == "miRNA") {
			xrefSet = miRNATotalList;
		}
		return xrefSet;
	}

	/**
	 * This methods goes through the Xrefset and looks for genes that fulfull
	 * the criteria set by the user.
	 * 
	 * @param crit
	 * @param set
	 * @return
	 * @throws DataException
	 */
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

		return cGenePositive;
	}
}
