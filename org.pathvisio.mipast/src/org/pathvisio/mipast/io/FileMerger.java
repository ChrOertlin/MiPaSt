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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.pathvisio.core.preferences.GlobalPreference;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.gexplugin.ImportInformation;

import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.mipast.MiPaStFileReader;

/**
 * 
 * The FilerMerger class combines two data expression files into one combined file that can 
 * be further handled by PathVisio.
 * 
 * @author ChrOertlin
 * 
 */
public class FileMerger {

	MiPaStFileReader fr = new MiPaStFileReader();
	boolean sharedHeader = false;
	File combinedFile = new File(PreferenceManager.getCurrent().get(
			GlobalPreference.DIR_LAST_USED_PGEX) +"/combinedTxt.txt");
	
	
	/**
	 * Creates the combined file, if two files are given to the plugin, and
	 * return a combinedFile which can be accessed for importinformation
	 * 
	 * @return
	 */
	public File createCombinedFile(ImportInformation miRNA,
			ImportInformation gene) throws IOException {

		
		File miRNAFile = new File("miRNA");
		File geneFile = new File("gene");
		List<String> miRNALines;
		List<String> geneLines;

		MiPaStFileReader fr = new MiPaStFileReader();

		miRNAFile = miRNA.getTxtFile();
		geneFile = gene.getTxtFile();

		miRNALines = fr.fileReader(miRNAFile);
		geneLines = fr.fileReader(geneFile);
		BufferedWriter fbw = new BufferedWriter(new FileWriter(combinedFile));
		getDataRows(miRNA, miRNALines, gene, geneLines, fbw);
		fbw.close();
		return combinedFile;
	}

	/**
	 * Creates the combined header of the two input files, if two headers are the same only one
	 * of the headers is taken into the combined header.
	 * 
	 */
	public List<String> createCombinedHeader(ImportInformation miRNA,
			ImportInformation gene) {

		List<String> combinedHeader = new ArrayList<String>(
				miRNA.getColNames().length + gene.getColNames().length + 1);
		combinedHeader.add("identifier");
		combinedHeader.add("system code");

		for (int i = 0; i < miRNA.getColNames().length; i++) {
			if (!combinedHeader.contains(miRNA.getColNames()[i])

			&& i != miRNA.getIdColumn()) {
				combinedHeader.add(miRNA.getColNames()[i]);

			}
		}

		checkDuplicateHeaders(gene, combinedHeader);

		for (int i = 0; i < gene.getColNames().length; i++) {
			if (!combinedHeader.contains(gene.getColNames()[i])

			&& i != gene.getIdColumn()) {
				combinedHeader.add(gene.getColNames()[i]);

			}
		}

		combinedHeader.add("type");
		for (int i = 0; i < combinedHeader.size(); i++) {
			if (combinedHeader.get(i).isEmpty()) {
				combinedHeader.remove(i);
			}
		}

		return combinedHeader;
	}

	/**
	 * Retrieves data rows from the expression data files and writes them to the 
	 * combined file. If there are values that fall under a shared header they will be written below the 
	 * shared header. If there is no value for a header then the column will be left blank.
	 */
	public void getDataRows(ImportInformation miRNA, List<String> miRNALines,
			ImportInformation gene, List<String> geneLines, BufferedWriter fbw)
			throws IOException {
		String[] miRNAValues = null;
		String[] geneValues = null;
		List<String> combinedHeader = new ArrayList<String>();
		combinedHeader = createCombinedHeader(miRNA, gene);
		List<String> miRNAData = new ArrayList<String>();
		List<String> geneData = new ArrayList<String>();

		writeToFile(combinedHeader, fbw);

		for (int i = 1; i < miRNALines.size(); i++) {
			miRNAValues = miRNALines.get(i).split(miRNA.getDelimiter());
			miRNAData = fillDataRows(miRNAValues, combinedHeader, miRNA,
					"miRNA");

			writeToFile(miRNAData, fbw);
		}

		for (int j = 1; j < geneLines.size(); j++) {
			geneValues = geneLines.get(j).split(gene.getDelimiter());
			geneData = fillDataRows(geneValues, combinedHeader, gene, "gene");
			writeToFile(geneData, fbw);
		}

	}

	/**
	 * Sorts the data rows to the right place along the combined header, so that
	 * every value is written into the right column.
	 * 
	 */
	public List<String> fillDataRows(String[] dataArray,
			List<String> combinedHeader, ImportInformation info, String type) {
		List<String> data = new ArrayList<String>(combinedHeader.size());
		for (int i = 0; i < combinedHeader.size(); i++) {
			data.add("");
		}
		boolean systemCodeAdded;

		for (int k = 0; k < dataArray.length; k++) {
			systemCodeAdded = false;

			if (k == info.getIdColumn()) {
				data.add(0, dataArray[k]);

			}
			if (info.isSyscodeFixed()
					&& !combinedHeader.contains(info.getColNames()[k])
					&& !systemCodeAdded) {
				data.add(1, info.getDataSource().getSystemCode());
				systemCodeAdded = true;
			}
			if (!info.isSyscodeFixed()
					&& !combinedHeader.contains(info.getColNames()[k])
					&& !systemCodeAdded) {
				data.add(1, dataArray[info.getSyscodeColumn()]);
				systemCodeAdded = true;
			}

			if (combinedHeader.contains(info.getColNames()[k])
					&& k != info.getIdColumn()
					&& combinedHeader.get(k) != "type") {

				data.add(combinedHeader.indexOf(info.getColNames()[k]),
						dataArray[k]);
			}

			if (k == dataArray.length - 1) {
				data.add(combinedHeader.indexOf("type"), type);
			} else if (!combinedHeader.get(k).isEmpty()) {
				data.add("");
			}
		}

		return data;
	}

	public void writeToFile(List<String> array, BufferedWriter fbw)
			throws IOException {

		for (int i = 0; i < array.size(); i++) {

			fbw.write(array.get(i) + "\t");
		}
		fbw.newLine();

	}

	/**
	 * checkDuplicateHeaders look in both files and checks if there are headers
	 * that exist in both files. If so, sharedHeader will be true. This check is needed to report back
	 * to make sure that a later visualization will be possible. If there are no shared columns it will not be 
	 * possible to shows interactions.
	 * 
	 */
	public void checkDuplicateHeaders(ImportInformation info,
			List<String> combinedHeader) {

		for (int i = 0; i < info.getColNames().length; i++) {
			if (combinedHeader.contains(info.getColNames()[i])) {
				sharedHeader = true;

			}

		}
	}

	/**
	 * Setters and getters 
	 *
	 */
	public boolean getSharedHeader() {
		return sharedHeader;
	}
	public File getCombinedFile(){
		return combinedFile;
	}
}