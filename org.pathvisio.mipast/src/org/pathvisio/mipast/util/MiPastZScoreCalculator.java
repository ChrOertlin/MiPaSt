package org.pathvisio.mipast.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.core.util.Stats;
import org.pathvisio.data.DataException;
import org.pathvisio.data.DataInterface;
import org.pathvisio.data.IRow;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.gex.CachedData;
import org.pathvisio.desktop.gex.GexManager;
import org.pathvisio.desktop.visualization.Criterion;
import org.pathvisio.desktop.visualization.Criterion.CriterionException;
import org.pathvisio.mipast.DataHolding;
import org.pathvisio.rip.RegIntPlugin;
import org.pathvisio.statistics.Column;
import org.pathvisio.statistics.PathwayMap;
import org.pathvisio.statistics.StatisticsPathwayResult;
import org.pathvisio.statistics.StatisticsResult;
import org.pathvisio.statistics.StatisticsTableModel;
import org.pathvisio.statistics.PathwayMap.PathwayInfo;
import org.pathvisio.statistics.ZScoreCalculator.RefInfo;

// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2011 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

/**
 * Calculates statistics on a set of Pathways, either step by step with
 * intermediate results, or all at once.
 */
public class MiPastZScoreCalculator {
	private PathwayMap pwyMap;
	private Map<Xref, RefInfo> dataMap;
	private final StatisticsResult result;
	private final ProgressKeeper pk;
	private Map<PathwayInfo, StatisticsPathwayResult> statsMap = new HashMap<PathwayInfo, StatisticsPathwayResult>();
	private GexManager gm;
	private RegIntPlugin plugin;
	private PvDesktop desktop;
	Set<Xref> evalthis = new HashSet<Xref>();
	/**
	 * @param pwDir
	 * @param pk
	 */
	public MiPastZScoreCalculator(File pwDir, ProgressKeeper pk,
			CachedData gex, GexManager gm, IDMapper gdb, RegIntPlugin plugin,
			PvDesktop desktop) {
		if (pk != null) {
			pk.setProgress(0);
			pk.setTaskName("Analyzing data");
		}

		result = new StatisticsResult();
		// result.crit = crit;
		result.stm = new StatisticsTableModel();
		result.stm.setColumns(new Column[] { Column.PATHWAY_NAME, Column.R,
				Column.N, Column.TOTAL, Column.PCT, Column.ZSCORE,
				Column.PERMPVAL });
		result.pwDir = pwDir;
		result.gex = gex;
		result.gdb = gdb;
		this.pk = pk;
		this.gm = gm;
		this.plugin = plugin;
		this.desktop = desktop;

	}

	/**
	 * We have two slightly different methods for calculating zscores:
	 * MappFinder and Alternative
	 * 
	 * This base class abstracts the difference out so we can easily select one
	 * of the two methods.
	 */
	private abstract class Method {
		/**
		 * calculate result.bigN and result.bigR
		 */
		public abstract void calculateTotals() throws IDMapperException,
				DataException;

		/**
		 * Do a permutation test to calculate permP and adjP
		 * 
		 * @throws DataException
		 * @throws IDMapperException
		 */
		public abstract void permute() throws IDMapperException, DataException;

		/**
		 * calculate n and r for a single pathway.
		 * 
		 * dataMap should already have been initialized
		 * 
		 * @throws IDMapperException
		 * @throws DataException
		 */
		public abstract StatisticsPathwayResult calculatePathway(PathwayInfo pi)
				throws IDMapperException, DataException;

		public abstract String getDescription();
	}

	/**
	 * Information about the result of evaluating a criterion on a xref.
	 * 
	 * A given xref can have 0 or more measured probes associated with it, and
	 * each measured probe can be positive or not.
	 */
	public static class RefInfo {
		final Set<String> probesMeasured;
		final Set<String> probesPositive;

		/**
		 * Initialize.
		 * 
		 * @param aProbesMeasured
		 *            must be >= 0
		 * @param aProbesPostive
		 *            must be >= 0, and <= aProbesMeasured.
		 */
		public RefInfo(Set<String> aProbesMeasured, Set<String> aProbesPositive) {
			probesMeasured = aProbesMeasured;
			probesPositive = aProbesPositive;

			if (probesPositive.size() > probesMeasured.size())
				throw new IllegalArgumentException();
		}

		/**
		 * Get the measured probes
		 */
		public Set<String> getProbesMeasured() {
			return probesMeasured;
		}

		/**
		 * Get the positive probes
		 */
		public Set<String> getProbesPositive() {
			return probesPositive;
		}

		/**
		 * Calculate the positive fraction of probes. E.g if 2 out of 3 probes
		 * are positive, count only 2/3. This is not the method Used by
		 * MAPPFinder.
		 */
		double getPositiveFraction() {
			return (double) probesPositive.size()
					/ (double) probesMeasured.size();
		}

		/**
		 * returns true if probesPositive > 0, meaning that at least one of the
		 * probes is measured and positive.
		 * 
		 * If probesMeasured is false, this will be false too.
		 * 
		 * This is a very optimistic way of calling a ref positive, because
		 * there could be 1000 non-positive probes. But this is the method used
		 * by MAPPFinder.
		 * 
		 * For an alternative way, check getPositiveFraction
		 */
		boolean isPositive() {
			return probesPositive.size() > 0;
		}

		/**
		 * returns true if at least one probe is measured
		 */
		boolean isMeasured() {
			return probesMeasured.size() > 0;
		}

	}

	public RefInfo evaluatedRef(Xref srcRef) throws IDMapperException {

		Set<String> cGeneTotal = new HashSet<String>();
		Set<String> cGenePositive = new HashSet<String>();

		List<? extends IRow> rows = result.gex.getData(srcRef);

		Set<Xref> ref = new HashSet<Xref>();

		// * make this generic...
		if (DataHolding.isGeneFileLoaded()) {
			String sysCode = new String(DataHolding.getGeneImportInformation()
					.getDataSource().getSystemCode());

			if (!srcRef.toString().startsWith(sysCode)) {

				ref = desktop.getSwingEngine().getGdbManager().getCurrentGdb()
						.mapID(srcRef, DataSource.getBySystemCode(sysCode));

			}
			if (srcRef.toString().startsWith(sysCode)) {
				ref.add(srcRef);
			}

			if (rows != null) {
				for (IRow row : rows) {

					if (pk != null && pk.isCancelled())
						return null;
					// Use group (line number) to identify a measurement
					cGeneTotal.add(row.getGroup() + "");
					for (Xref x : ref) {
						boolean eval = DataHolding.getGeneFinal().contains(x);
						System.out.print(eval + "\n");
						if (eval == true) {
							cGenePositive.add(row.getGroup() + "");
						}
					}
				}
			}

		}

		if (!DataHolding.isGeneFileLoaded()) {
			boolean eval=false;
			cGeneTotal.add(srcRef + "");
			
			
			
			Set<Xref> evalRef= new HashSet<Xref>();
			
			evalRef.addAll(desktop.getSwingEngine().getGdbManager().getCurrentGdb().mapID(srcRef, DataSource.getBySystemCode("En")));
			for(Xref x: evalRef){
			eval = evalthis.contains(x);
			}
			if(eval){
				cGenePositive.add(srcRef + "");
			}
			
			
		}

		return new RefInfo(cGeneTotal, cGenePositive);
	}

	private class MappFinderMethod extends Method {
		/**
		 * Shuffle the values of the map, so that each K, V pair is (likely)
		 * broken up, each K will (likely) get a new V.
		 * 
		 * @param map
		 *            : the map to be permuted. This value is modified directly.
		 */
		private <K, V> void permuteMap(Map<K, V> map) {
			List<V> values = new ArrayList<V>();
			values.addAll(map.values());
			Collections.shuffle(values);

			int i = 0;
			for (K key : map.keySet()) {
				map.put(key, values.get(i));
				i++;
			}
		}

		/**
		 * Perform a permutation test and calculate PermuteP values.
		 * 
		 * permutes the data 1000 times while keeping the labels fixed.
		 * Calculate the rank of the actual zscore compared to the permuted
		 * zscores. Two-tailed test, so checks for very low z-scores as well as
		 * very high z-scores.
		 * 
		 * @throws DataException
		 * @throws IDMapperException
		 */
		public void permute() throws IDMapperException, DataException {
			// create a deep copy of dataMap.
			Map<Xref, RefInfo> dataMap2 = new HashMap<Xref, RefInfo>();
			for (Xref key : dataMap.keySet()) {
				dataMap2.put(key, dataMap.get(key));
			}

			// we count the number of times a zscore is extremer,
			// i.e. further away from 0 than the actual zscore.
			Map<PathwayInfo, Integer> extremer = new HashMap<PathwayInfo, Integer>();

			for (PathwayInfo pi : pwyMap.getPathways())
				extremer.put(pi, 0);

			for (int i = 0; i < 999; ++i) {
				permuteMap(dataMap2);

				for (PathwayInfo pi : pwyMap.getPathways()) {
					int cPwyMeasured = 0;
					int cPwyPositive = 0;

					for (Xref ref : pi.getSrcRefs()) {

						RefInfo refInfo = dataMap2.get(ref);
						if (refInfo.isMeasured())

							cPwyMeasured++;
						if (refInfo.isPositive())
							cPwyPositive++;
					}
					double zscore = Stats.zscore(cPwyMeasured, cPwyPositive,
							result.bigN, result.bigR);

					// compare absolutes -> two-tailed test
					if (Math.abs(zscore) > Math.abs((statsMap.get(pi))
							.getZScore())) {
						extremer.put(pi, extremer.get(pi) + 1);
					}
				}
			}

			// report p-vals
			for (PathwayInfo pi : pwyMap.getPathways()) {
				double pval = (double) extremer.get(pi) / 1000.0;
				StatisticsPathwayResult spr = statsMap.get(pi);
				spr.permP = pval;
			}
		}

		public String getDescription() {
			return "Calculation method: pathway-centric. Calculations are performed after mapping data to pathways.";
		}

		/**
		 * calculate bigN and bigR. This only takes the part of the dataset that
		 * maps to pathways, which leads to a calculation very similar to
		 * MAPPFinder.
		 */
		@Override
		public void calculateTotals() {
			// go over all datanodes in all pathways
			for (Xref ref : dataMap.keySet()) {

				RefInfo refInfo = dataMap.get(ref);

				if (refInfo.isMeasured())
					result.bigN++;
				if (refInfo.isPositive())
					result.bigR++;
			}

		}

		/**
		 * Calculates n and r for a pathway the MAPPFinder way:
		 * <UL>
		 * <LI>n: the number of genes on the pathway that map to at least one
		 * row in the dataset.
		 * <LI>r: the subset of n that has at least one significant row in the
		 * dataset.
		 * </UL>
		 * 
		 * @throws IDMapperException
		 * @throws DataException
		 */
		public StatisticsPathwayResult calculatePathway(PathwayInfo pi)
				throws IDMapperException, DataException {
			int cPwyMeasured = 0;
			int cPwyPositive = 0;
			int cPwyTotal = pi.getSrcRefs().size();

			for (Xref ref : pi.getSrcRefs()) {

				RefInfo refInfo = dataMap.get(ref);
				if (refInfo.isMeasured())
					cPwyMeasured++;
				if (refInfo.isPositive())
					cPwyPositive++;
			}

			double zscore = Stats.zscore(cPwyMeasured, cPwyPositive,
					result.bigN, result.bigR);
			StatisticsPathwayResult spr = new StatisticsPathwayResult(
					pi.getFile(), pi.getName(), cPwyMeasured, cPwyPositive,
					cPwyTotal, zscore);
			return spr;
		}
	}

	
		private Set<Xref> filterGenesforPathway(Set<Xref> set) throws IDMapperException{
			Set<Xref> mappedPw = new HashSet<Xref>();
			Set<Xref> mappedGenes = new HashSet<Xref>();
			Set<Xref> filteredGenes = new HashSet<Xref>();
			
			for(Xref x: DataHolding.getPathwayGenes()){
				mappedPw.addAll(desktop.getSwingEngine().getGdbManager().getCurrentGdb().mapID(x, DataSource.getBySystemCode("En")));
			}
		
			for(Xref y: set){
				mappedGenes.addAll(desktop.getSwingEngine().getGdbManager().getCurrentGdb().mapID(y, DataSource.getBySystemCode("En")));
			}
		
			for(Xref z: mappedGenes){
				if(mappedPw.contains(z)){
					filteredGenes.add(z);
				}
			}
			
	
		
		return filteredGenes;
		
		
	}
	
	private void calculateDataMapSingle() throws IDMapperException {
		dataMap = new HashMap<Xref, RefInfo>();
		// go over all datanodes in all pathways

		for (Xref srcRef : pwyMap.getSrcRefs()) {
			if (pk != null && pk.isCancelled())
				return;

			RefInfo refInfo = evaluatedRef(srcRef);

			dataMap.put(srcRef, refInfo);

		}
	}

	private void calculateDataMapBoth() throws IDMapperException {
		dataMap = new HashMap<Xref, RefInfo>();
		// go over all datanodes in all pathways

		Map<Xref, Set<Xref>> res = desktop
				.getSwingEngine()
				.getGdbManager()
				.getCurrentGdb()
				.mapID(DataHolding.pathwayGenes,
						DataSource.getBySystemCode("L"));
		Set<Xref> xrefs = new HashSet<Xref>();
		for (Xref x : res.keySet()) {
			for (Xref x2 : res.get(x)) {
				xrefs.add(x2);
			}
		}

		for (Xref srcRef : pwyMap.getSrcRefs()) {
			if (pk != null && pk.isCancelled())
				return;

			RefInfo refInfo = evaluatedRef(srcRef);

			dataMap.put(srcRef, refInfo);

		}
	}

	private StatisticsResult calculate(Method m) throws IDMapperException,
			DataException {
		result.methodDesc = m.getDescription();

		// read all pathways
		if (pk != null) {
			if (pk.isCancelled())
				return null;
			pk.setTaskName("Creating pathway list");
			pk.setProgress(0);
		}
		pwyMap = new PathwayMap(result.pwDir);
		DataHolding.setPathwayGenes(pwyMap.getSrcRefs());

		// cache data for all pathways at once.
		if (pk != null) {
			if (pk.isCancelled())
				return null;
			pk.setTaskName("Reading dataset");
			pk.setProgress(20);
		}

		BackgroundsetMethods bm = new BackgroundsetMethods(desktop, plugin);

		if (DataHolding.isBolMethodDataset()) {
			bm.datasetMethod();
		}

		if (DataHolding.isBolMethodPathway()) {
			bm.pathwayMethod();
		}
		if (DataHolding.isBolMethodAllGenesMeasured()) {
			bm.allGenesMeasuredMethod();
		}
		if (DataHolding.isBolMethodPathway2()) {
			bm.measuredInPathwaysMethod();
		}

		result.gex.setMapper(result.gdb);
		result.gex.syncSeed(pwyMap.getSrcRefs());

		// calculate dataMap
		if (pk != null) {
			if (pk.isCancelled())
				return null;
			pk.setTaskName("Calculating expression data");
			pk.setProgress(40);
		}
		if (!DataHolding.isGeneFileLoaded()) {
			evalthis = filterGenesforPathway(DataHolding.getGeneFinal());
			calculateDataMapSingle();
		}
		if (DataHolding.isGeneFileLoaded()) {
			calculateDataMapBoth();
		}

		if (pk != null) {
			if (pk.isCancelled())
				return null;
			pk.setTaskName("Calculating on dataset");
			pk.setProgress(60);
		}

		m.calculateTotals();

		Logger.log.info("N: " + result.bigN + ", R: " + result.bigR);

		int i = 0;
		for (PathwayInfo pi : pwyMap.getPathways()) {
			if (pk != null) {
				if (pk.isCancelled())
					return null;
				pk.setTaskName("Analyzing " + pi.getFile().getName());
				pk.setProgress((int) ((0.6 + (0.2 * (double) i / (double) pwyMap
						.getPathways().size())) * 100.0));
			}
			StatisticsPathwayResult spr = m.calculatePathway(pi);
			statsMap.put(pi, spr);
			if (spr != null)
				result.stm.addRow(spr);
		}

		if (pk != null) {
			if (pk.isCancelled())
				return null;
			pk.setTaskName("Calculating permutation P values");
			pk.setProgress(80);
		}
		m.permute();

		result.stm.sort();
		if (pk != null) {
			pk.setProgress(100);
			pk.setTaskName("Done");
		}
		return result;
	}

	/**
	 * calculate StatisticsResult, using the alternative method (not used by
	 * MappFinder)
	 * 
	 * This alternative method includes the whole dataset into the calculation
	 * of the N and R parameters for the zscore, not just the part of the
	 * dataset that maps to Pathways.
	 * 
	 * @throws DataException
	 */

	public StatisticsResult calculateMappFinder() throws IDMapperException,
			DataException {
		return calculate(new MappFinderMethod());
	}
}