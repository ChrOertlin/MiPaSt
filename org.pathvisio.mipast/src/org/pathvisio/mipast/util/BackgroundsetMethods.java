package org.pathvisio.mipast.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.rip.Interaction;
import org.pathvisio.rip.RegIntPlugin;

public class BackgroundsetMethods {

	private static PvDesktop desktop;
	private RegIntPlugin plugin;

	public BackgroundsetMethods(RegIntPlugin plugin) {
		this.plugin = plugin;

	}
/**
 * datasetMethod
 * 
 * This method creates a backgroundlist for the pathway statistics based on all genes 
 * that are measured and have an interaction.
 * 
 */
	public void datasetMethod() {
		Set<Xref> geneMeasuredAndInteraction = new HashSet<Xref>();
		for (Xref x : DataHolding.getAllGenesList()) {
			if (plugin.getInteractions().containsKey(x)) {
				geneMeasuredAndInteraction.add(x);
			}
//			for (Xref y : DataHolding.allmiRNAList) {
//				if (plugin.getInteractions().containsKey(y)) {
//
//					geneMeasuredAndInteraction.add(x);
//
//				}

	//		}

		}
		
		DataHolding.setGeneTotal(geneMeasuredAndInteraction);
	}

/**
 * PathwayMethod
 * 
 * this method creates a backgroundlist for the pathway statistics based on 
 * the genes that are measured, have an interaction and are found in the pathways.
 * 	
 */
	
	public void pathwayMethod() {
		Set<Xref> geneMeasuredAndInterActionAndInPathway = new HashSet<Xref>();

		for (Xref x : DataHolding.getAllGenesList()) {
			if (plugin.getInteractions().containsKey(x)
					&& DataHolding.pathwayGenes.contains(x)) {
				geneMeasuredAndInterActionAndInPathway.add(x);
			}
		}
		
		DataHolding.setGeneTotal(geneMeasuredAndInterActionAndInPathway);
	}
	
	
	
	public void pathwayMethod2(){
		
		Set<Xref> genesMeasuredAndInPathway = new HashSet<Xref>();
		
		for (Xref x : DataHolding.getAllGenesList()){
			if(DataHolding.pathwayGenes.contains(x)){
				genesMeasuredAndInPathway.add(x);
			}
		}
		DataHolding.setGeneTotal(genesMeasuredAndInPathway);
	}
	
	
	public void allGenesMeasuredMethod(){
		DataHolding.setGeneTotal(DataHolding.getAllGenesList());
	}
}
