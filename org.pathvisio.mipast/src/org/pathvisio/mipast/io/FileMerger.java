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
import org.pathvisio.mipast.mipastFileReader;
import org.pathvisio.gexplugin.ImportInformation;

import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;

/**
 * 
 * @author ChrOertlin
 * 
 */
public class FileMerger {

	mipastFileReader fr = new mipastFileReader();

	public void createCombinedFile(ImportInformation miRNA,
			ImportInformation gene, ProgressKeeper pk, PvDesktop desktop)
			throws IOException {
		File miRNAFile = new File("miRNA");
		File geneFile = new File("gene");
		List<String> miRNALines;
		List<String> geneLines;

		mipastFileReader fr = new mipastFileReader();

		miRNAFile = miRNA.getTxtFile();
		geneFile = gene.getTxtFile();

		miRNALines = fr.fileReader(miRNAFile);
		geneLines = fr.fileReader(geneFile);
		getDataRows(miRNA, miRNALines, gene, geneLines);

	}

	public List<String> createCombinedHeader(ImportInformation miRNA,
			ImportInformation gene) {

		List<String> combinedHeader = new ArrayList<String>();
		combinedHeader.add("identifier");
		combinedHeader.add("system code");

		for (int i = 0; i < miRNA.getColNames().length; i++) {
			if (!combinedHeader.contains(miRNA.getColNames()[i])
					&& i != miRNA.getIdColumn()) {
				combinedHeader.add(miRNA.getColNames()[i]);
			}
		}

		for (int i = 0; i < gene.getColNames().length; i++) {
			if (!combinedHeader.contains(gene.getColNames()[i])
					&& i != gene.getIdColumn()) {
				combinedHeader.add(gene.getColNames()[i]);
			}

		}
		combinedHeader.add("type");
		return combinedHeader;
	}

	public void getDataRows(ImportInformation miRNA, List<String> miRNALines,
			ImportInformation gene, List<String> geneLines) throws IOException {
		String[] miRNAValues = null;
		String[] geneValues = null;

		List<String> combinedHeader = new ArrayList<String>();
		combinedHeader = createCombinedHeader(miRNA, gene);
		List<String> miRNAData = new ArrayList<String>();
		List<String> geneData = new ArrayList<String>();

		for (int i = 1; i < miRNALines.size(); i++) {
			miRNAValues = miRNALines.get(i).split(miRNA.getDelimiter());
			miRNAData = fillDataRows(miRNAValues, combinedHeader, miRNA,
					"miRNA");

		}

		for (int j = 1; j < geneLines.size(); j++) {
			geneValues = geneLines.get(j).split(gene.getDelimiter());
			geneData = fillDataRows(geneValues, combinedHeader, gene, "gene");

		}

	}

	public List<String> fillDataRows(String[] dataArray,
			List<String> combinedHeader, ImportInformation info, String type) {
		List<String> data = new ArrayList<String>(combinedHeader.size());

		int valuePosition;
		boolean systemCodeAdded;

		for (int k = 0; k < dataArray.length; k++) {
			systemCodeAdded = false;

			if (k == info.getIdColumn()) {
				data.add(0, dataArray[k]);

			}
			if (info.isSyscodeFixed()
					&& !combinedHeader.contains(info.getColNames()[k])
					&& !systemCodeAdded) {
				data.add(1, info.getDataSource().toString());
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
				valuePosition = combinedHeader.indexOf(info.getColNames()[k]);
				data.add(combinedHeader.indexOf(info.getColNames()[k]),
						dataArray[k]);
			} else {
				data.add("");
			}

		}

		return data;
	}
}
