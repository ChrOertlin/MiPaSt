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

import javax.xml.crypto.Data;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
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
import org.pathvisio.rip.ResultsObj;

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
	private RegIntPlugin regintPlugin;

	public PositiveGeneList(PvDesktop desktop, SwingEngine se,
			RegIntPlugin plugin) {
		this.desktop = desktop;
		this.regintPlugin = plugin;

	}

	private Set<Xref> allXref = new HashSet<Xref>();
	private Set<Xref> geneTotalList = new HashSet<Xref>();
	private Set<Xref> miRNATotalList = new HashSet<Xref>();
	private Set<Xref> miRNAUp;
	private Set<Xref> geneUp;
	private Set<Xref> miRNADown;
	private Set<Xref> geneDown;
	Set<Xref> genesFinal = new HashSet<Xref>();
	Set<Xref> xrefSet = new HashSet<Xref>();

	public void execute() throws DataException, IOException, IDMapperException {

		Set<Xref> allMiRNAInDataset = new HashSet<Xref>();
		Set<Xref> allGenesInDataset = new HashSet<Xref>();

		
		allMiRNAInDataset = getallXrefs(DataHolding.getMiRNAImportInformation().getDataSource(), "miRNA");
		
		
		if (DataHolding.getGeneImportInformation() != null) {
			allGenesInDataset = getallXrefs(DataHolding.getGeneImportInformation().getDataSource(), "gene");
		}
		

		if (DataHolding.isMiRNAUpCritCheck()) {
			miRNAUp = criterionEvaluation(DataHolding.getMiRNAUpCriterion(), allMiRNAInDataset);
		}
		
		if (DataHolding.isMiRNADownCritCheck()) {
			miRNADown = criterionEvaluation(DataHolding.getMiRNADownCriterion(), allMiRNAInDataset);
			
		}
		
		if (DataHolding.isGeneFileLoaded() & DataHolding.isGeneUpCritCheck()) {
			geneUp = criterionEvaluation(DataHolding.geneUpCriterion, allGenesInDataset);
			
		}

		if (DataHolding.isGeneFileLoaded() & DataHolding.isGeneDownCritCheck()) {
			geneDown = criterionEvaluation(DataHolding.geneDownCriterion, allGenesInDataset);
			
		}
	
		
		Set<Xref> targetsDown = getTargets(miRNADown);
		
		Set<Xref> targetsUp = getTargets(miRNAUp);
		
		
		// different regulation
		if(!DataHolding.isGeneFileLoaded()){
		
			if(DataHolding.isBolAllReg()){
				for(Xref x: targetsDown){
					if(!genesFinal.contains(x)){
						genesFinal.add(x);
					}
				}
				for(Xref x: targetsUp){
					if(!genesFinal.contains(x)){
						genesFinal.add(x);
					}
				}
				
				DataHolding.setGeneBackgroundSet(getTargets(allMiRNAInDataset));
			}
		} 
		if(DataHolding.isBolmiRNADownGeneDown()) {
		
			genesFinal.addAll(targetsDown);
		}
		if(DataHolding.isBolmiRNADownGeneUp()) {
			
		
			genesFinal.addAll(targetsDown);
		}
		if(DataHolding.isBolmiRNAUpGeneDown()) {
		
			
			genesFinal.addAll(targetsUp);
		}
		if(DataHolding.isBolmiRNAUpGeneUp()) {
			
		
			genesFinal.addAll(targetsUp);
			
		}
	
		
		if(DataHolding.isGeneFileLoaded()){
		
		if(DataHolding.isBolAllReg()) {
			System.out.println("all regulation");
			Set<Xref> allGenes = new HashSet<Xref>();
			allGenes.addAll(geneUp);
			allGenes.addAll(geneDown);
	
			Set<Xref> validTargetsDown = findRegulatedTargets(targetsDown, allGenes);
			Set<Xref> validTargetsUp = findRegulatedTargets(targetsUp, allGenes);
			genesFinal.addAll(validTargetsUp);
			genesFinal.addAll(validTargetsDown);
	
		} 
		if(DataHolding.isBolmiRNADownGeneDown()) {
			Set<Xref> validTargetsDown = findRegulatedTargets(targetsDown, geneDown);
		
			genesFinal.addAll(validTargetsDown);
		}
		if(DataHolding.isBolmiRNADownGeneUp()) {
			Set<Xref> validTargetsUp = findRegulatedTargets(targetsDown, geneUp);
		
			genesFinal.addAll(validTargetsUp);
		}
		if(DataHolding.isBolmiRNAUpGeneDown()) {
			Set<Xref> validTargetsDown = findRegulatedTargets(targetsUp, geneDown);
			
			genesFinal.addAll(validTargetsDown);
		}
		if(DataHolding.isBolmiRNAUpGeneUp()) {
			Set<Xref> validTargetsUp = findRegulatedTargets(targetsUp, geneUp);
		
			genesFinal.addAll(validTargetsUp);
		}
		}


		DataHolding.setAllGenesList(allGenesInDataset);
		DataHolding.setAllmiRNAList(allMiRNAInDataset);
		DataHolding.setGeneFinal(genesFinal);
	
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
	
	public Set<Xref> findRegulatedTargets(Set<Xref> allTargets, Set<Xref> selectedGenes) throws IDMapperException {
		Set<Xref> positive = new HashSet<Xref>();
		for(Xref x : allTargets) {
			Set<Xref> result = desktop.getSwingEngine().getGdbManager().getCurrentGdb().mapID(x, DataHolding.getGeneImportInformation().getDataSource());
			for(Xref xref : result) {
				if(selectedGenes.contains(xref)) {
					if(!positive.contains(xref)) {
						positive.add(xref);
					}
				}
			}
		}
		return positive;
	}
	
	// get interactions
	public Set<Xref> getTargets(Set<Xref> miRNAs) {
		Set<Xref> result = new HashSet<Xref>();
		for(Xref x : miRNAs) {
			if(regintPlugin.getInteractions().containsKey(x)){
			try {
				
				ResultsObj res = regintPlugin.findInteractions(x);
				
				
				
					
					for(Xref tx : res.getTargetMap().keySet()) {
						if(!result.contains(tx)) {
							result.add(tx);
						}
					}
			
					
				
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		return result;
	}

	public Set<Xref> createMetcriteriaHaveInteractionList(Set<Xref> genes,
			Set<Xref> miRNA, String ds) {
		
		Map<Xref, List<Interaction>> interactions = regintPlugin.getInteractions();
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