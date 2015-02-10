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

import java.io.IOException;

import java.util.Collection;
import java.util.HashSet;

import java.util.List;
import java.util.Map;

import java.util.Set;

import org.bridgedb.DataSource;

import org.bridgedb.Xref;

import org.pathvisio.data.DataException;

import org.pathvisio.data.IRow;
import org.pathvisio.desktop.PvDesktop;

import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.desktop.visualization.Criterion.CriterionException;

import org.pathvisio.gui.SwingEngine;
import org.pathvisio.mipast.DataHolding;

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
	private RegIntPlugin plugin;

	public PositiveGeneList(PvDesktop desktop, SwingEngine se,
			RegIntPlugin plugin) {
		this.desktop = desktop;
		this.plugin = plugin;

	}

	private Set<Xref> allXref = new HashSet<Xref>();
	private Set<Xref> geneTotalList = new HashSet<Xref>();
	private Set<Xref> miRNATotalList = new HashSet<Xref>();
	private Set<Xref> miRNAUp;
	private Set<Xref> geneUp;
	private Set<Xref> miRNADown;
	private Set<Xref> geneDown;

	Set<Xref> xrefSet = new HashSet<Xref>();

	public void execute() throws DataException, IOException {

		Set<Xref> allMiRNAInDataset = new HashSet<Xref>();
		Set<Xref> allGenesInDataset = new HashSet<Xref>();

		Set<Xref> miRNAFinal = new HashSet<Xref>();
		Set<Xref> geneFinal = new HashSet<Xref>();

		Set<Xref> allReg = new HashSet<Xref>();
		Set<Xref> directUp = new HashSet<Xref>();
		Set<Xref> directDown = new HashSet<Xref>();
		Set<Xref> miRNAUpGeneDown = new HashSet<Xref>();
		Set<Xref> miRNADownGeneUp = new HashSet<Xref>();

		allMiRNAInDataset = getallXrefs(DataHolding.getMiRNAImportInformation()
				.getDataSource(), "miRNA");

		if (DataHolding.getGeneImportInformation() != null) {
			allGenesInDataset = getallXrefs(DataHolding
					.getGeneImportInformation().getDataSource(), "gene");
		}

		if (DataHolding.isMiRNAUpCritCheck()
		// & DataHolding.isBolmiRNAUpGeneDown()
		// || DataHolding.isMiRNAUpCritCheck()
		// & DataHolding.isBolmiRNAUpGeneUp()
		// || DataHolding.isMiRNAUpCritCheck() & DataHolding.isBolAllReg()
		) {
			miRNAUp = criterionEvaluation(DataHolding.getMiRNAUpCriterion(),
					allMiRNAInDataset);
			// miRNAFinalUp.addAll(createMetcriteriaHaveInteractionList(miRNAUpPosIntGenes,
			// allXref,
			// DataHolding.getMiRNASysCode()));
			//
		}

		if (DataHolding.isMiRNADownCritCheck()
		// & DataHolding.isBolmiRNADownGeneUp()
		// || DataHolding.isMiRNADownCritCheck()
		// & DataHolding.isBolmiRNADownGeneDown()
		// || DataHolding.isMiRNADownCritCheck() & DataHolding.isBolAllReg()
		) {

			miRNADown = criterionEvaluation(
					DataHolding.getMiRNADownCriterion(), allMiRNAInDataset);
			// miRNAFinalDown.addAll(createMetcriteriaHaveInteractionList(miRNADownPosIntGenes,
			// allXref, DataHolding.getMiRNASysCode()));
		}

		if (DataHolding.isGeneFileLoaded() & DataHolding.isGeneUpCritCheck()
		// & DataHolding.isBolAllReg() || DataHolding.isGeneFileLoaded()
		// & DataHolding.isGeneUpCritCheck()
		// & DataHolding.isBolmiRNADownGeneUp()
		// || DataHolding.isGeneFileLoaded()
		// & DataHolding.isGeneUpCritCheck()
		// & DataHolding.isBolmiRNAUpGeneUp()
		) {
			geneUp = criterionEvaluation(DataHolding.geneUpCriterion,
					allGenesInDataset);

			// geneFinalUp.addAll(createMetcriteriaHaveInteractionList(geneUpPosIntGenes,
			// allXref,
			// DataHolding.getGeneSysCode()));

		}

		if (DataHolding.isGeneFileLoaded() & DataHolding.isGeneDownCritCheck()
		// & DataHolding.isBolAllReg() || DataHolding.isGeneFileLoaded()
		// & DataHolding.isGeneDownCritCheck()
		// & DataHolding.isBolmiRNAUpGeneDown()
		// || DataHolding.isGeneFileLoaded()
		// & DataHolding.isGeneDownCritCheck()
		// & DataHolding.isBolmiRNADownGeneDown()
		) {
			geneDown = criterionEvaluation(DataHolding.geneDownCriterion,
					allGenesInDataset);
			// geneFinalDown.addAll(createMetcriteriaHaveInteractionList(geneDownPosIntGenes,
			// allXref,
			// DataHolding.getGeneSysCode()));
		}

		if (DataHolding.isBolAllReg()) {

			Set<Xref> empty1 = new HashSet<Xref>();
			Set<Xref> empty2 = new HashSet<Xref>();
			Set<Xref> empty3 = new HashSet<Xref>();

			Set<Xref> allGenesMetCriteria = new HashSet<Xref>();
			Set<Xref> allMiRNAMetCriteria = new HashSet<Xref>();

			allGenesMetCriteria = addLists(geneDown, geneUp, empty1, empty2,
					empty3);
			allMiRNAMetCriteria = addLists(miRNADown, miRNAUp, empty1, empty2,
					empty3);
			allReg = createMetcriteriaHaveInteractionList(allGenesMetCriteria,
					allMiRNAMetCriteria, DataHolding.getGeneSysCode());
		}

		if (DataHolding.isBolmiRNAUpGeneUp()) {
			directUp = createMetcriteriaHaveInteractionList(geneUp, miRNAUp,
					DataHolding.getGeneSysCode());
		}
		if (DataHolding.isBolmiRNADownGeneDown()) {
			directDown = createMetcriteriaHaveInteractionList(geneDown,
					miRNADown, DataHolding.getGeneSysCode());
		}
		if (DataHolding.isBolmiRNAUpGeneDown()) {
			miRNAUpGeneDown = createMetcriteriaHaveInteractionList(geneDown,
					miRNAUp, DataHolding.getGeneSysCode());
		}
		if (DataHolding.isBolmiRNADownGeneUp()) {
			miRNADownGeneUp = createMetcriteriaHaveInteractionList(geneUp,
					miRNADown, DataHolding.getGeneSysCode());
		}

		DataHolding.setAllGenesList(allGenesInDataset);
		DataHolding.setAllmiRNAList(allMiRNAInDataset);

		// geneFinal = addLists(geneFinalDown,geneFinalUp);
		// miRNAFinal = addLists(miRNAFinalDown,miRNAFinalUp);

		geneFinal = addLists(allReg, directUp, miRNADownGeneUp,
				miRNAUpGeneDown, directDown);

		DataHolding.setGeneFinal(geneFinal);
		DataHolding.setMiRNAFinal(miRNAFinal);

		System.out.print("allreg " + allReg + "\n");
		System.out.print("directUp " + directUp + "\n");
		System.out.print("directDown " + directDown + "\n");
		System.out.print("miRNADownGeneUp " + miRNADownGeneUp + "\n");
		System.out.print("miRNAUpGeneDown" + miRNAUpGeneDown + "\n");
		System.out.print("geneFinal " + geneFinal + "\n");
	}

	public Set<Xref> addLists(Set<Xref> allReg, Set<Xref> miRNAUpGeneUp,
			Set<Xref> miRNAdownGeneUp, Set<Xref> miRNAUpGeneDown,
			Set<Xref> miRNADownGeneDown) {
		Set<Xref> finalList = new HashSet<Xref>();
		for (Xref x : allReg) {
			finalList.add(x);
		}
		for (Xref y : miRNAUpGeneUp) {
			finalList.add(y);
		}
		for (Xref t : miRNAdownGeneUp) {
			finalList.add(t);
		}
		for (Xref i : miRNAUpGeneDown) {
			finalList.add(i);
		}
		for (Xref l : miRNADownGeneDown) {
			finalList.add(l);
		}
		return finalList;
	}

	public Set<Xref> createMetcriteriaHaveInteractionList(Set<Xref> genes,
			Set<Xref> miRNA, String ds) {

		Map<Xref, List<Interaction>> interactions = plugin.getInteractions();
		Set<Xref> positiveList = new HashSet<Xref>();
		boolean hasInt = false;

		for (Xref x : genes) {

			if (interactions.containsKey(x)) {

				
				for (Interaction value : interactions.get(x)) {
					for (Xref y : miRNA) {
						
						if (value.getRegulator().equals(y)) {
							hasInt = true;
							
						}
					}
				}
			}
			
			if (genes != null && interactions.containsKey(x)
					&& x.toString().startsWith(ds) && hasInt == true) {

				positiveList.add(x);

			}
			hasInt = false;
		
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
	public static Set<Xref> criterionEvaluation(Criterion crit, Set<Xref> set)
			throws DataException {

		Set<Xref> cGenePositive = new HashSet<Xref>();

		Collection<? extends IRow> rows = desktop.getGexManager()
				.getCurrentGex().getData(set);

		if (rows != null) {
			for (IRow row : rows) {
				// // Use group (line number) to identify a measurement
				// cGeneTotal.add(row.getGroup() + "");
				try {
					boolean eval = crit.evaluate(row.getByName());
					if (eval)
						cGenePositive.add(row.getXref());

				} catch (CriterionException e) {
					e.printStackTrace();
				}
			}
		}

		return cGenePositive;
	}
}
