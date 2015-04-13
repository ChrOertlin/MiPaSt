package org.pathvisio.mipast.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.pathvisio.data.DataException;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.rip.RegIntPlugin;

public class BackgroundsetMethods {

	private static PvDesktop desktop;
	private RegIntPlugin plugin;

	public BackgroundsetMethods(PvDesktop desktop, RegIntPlugin plugin) {
		this.desktop = desktop;
		this.plugin = plugin;

	}

	/**
	 * datasetMethod
	 * 
	 * This method creates a backgroundlist for the pathway statistics based on
	 * all genes that are measured and have an interaction.
	 * 
	 */
	public void datasetMethod() {
		Set<Xref> geneMeasuredAndInteraction = new HashSet<Xref>();
		for (Xref x : DataHolding.getAllGenesList()) {
			if (plugin.getInteractions().containsKey(x)) {
				geneMeasuredAndInteraction.add(x);
			}
			// for (Xref y : DataHolding.allmiRNAList) {
			// if (plugin.getInteractions().containsKey(y)) {
			//
			// geneMeasuredAndInteraction.add(x);
			//
			// }

			// }

		}

		DataHolding.setGeneTotal(geneMeasuredAndInteraction);
	}

	/**
	 * PathwayMethod
	 * 
	 * this method creates a backgroundlist for the pathway statistics based on
	 * the genes that are measured, have an interaction and are found in the
	 * pathways.
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

	public void measuredInPathwaysMethod() throws DataException,
			IDMapperException {
		Map<Xref, Set<Xref>> res = desktop
				.getSwingEngine()
				.getGdbManager()
				.getCurrentGdb()
				.mapID(DataHolding.getPathwayGenes(),
						DataSource.getBySystemCode("L"));
		Set<Xref> xrefs = new HashSet<Xref>();
		for (Xref x : res.keySet()) {
			for (Xref x2 : res.get(x)) {
				xrefs.add(x2);
			}
		}
		Set<Xref> FinalGenesInPathway = new HashSet<Xref>();
		for (Xref ref : DataHolding.getGeneFinal()) {
			if (xrefs.contains(ref)) {
				FinalGenesInPathway.add(ref);
			}
		}

		DataHolding.setGeneTotal(FinalGenesInPathway);

		Set<Xref> genesMeasuredAndInPathway = new HashSet<Xref>();
		for (Xref x : DataHolding.getAllGenesList()) {
			if (xrefs.contains(x)) {
				genesMeasuredAndInPathway.add(x);
			}
		}
		DataHolding.setGeneTotal(genesMeasuredAndInPathway);
	}

	public void allGenesMeasuredMethod() {
		DataHolding.setGeneTotal(DataHolding.getAllGenesList());
	}
}