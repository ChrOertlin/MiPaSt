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


import java.io.File;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import org.pathvisio.mipast.mipastFileReader;
import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.mipast.DataRow;

import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;

/**
 * 
 * @author ChrOertlin
 *
 */
public class FileMerger {
	File combinedFile= new File("combinedFiles.txt");
	mipastFileReader fr= new mipastFileReader();
	DataRow dr= new DataRow("combine");
	
	public void createCombinedFile(ImportInformation miRNA, ImportInformation gene,ProgressKeeper pk, PvDesktop desktop) throws IOException {
		File miRNAFile = new File("miRNA");
		File geneFile = new File("gene");
		List<String> miRNALines;
		List<String> geneLines;
		String[] values;
		
		mipastFileReader fr= new mipastFileReader();
		DataRow dr= new DataRow("combine");

	
		
		miRNAFile =miRNA.getTxtFile();
		
	
		geneFile = gene.getTxtFile();
		miRNALines= fr.fileReader(miRNAFile);
		
		geneLines= fr.fileReader(geneFile);
		
		
		addKeys(miRNA);
		addKeys(gene);
		addValues(miRNA,miRNALines);
		addValues(gene,geneLines);
		dr.getProperties();	
		dr.printMap();
				
			
		
		
		
	}
	

	public void addKeys(ImportInformation info){
		for(int i=0;i<info.getColNames().length;i++){
			dr.addProperty(info.getColNames()[i], null);
		}
	}
	
	public void addValues(ImportInformation info ,List<String> lines){
		String[] values;
		for(int i=0;i<lines.size();i++){
			values= lines.get(i).split(info.getDelimiter());
			for (int j=0;j< values.length;j++){
				
				System.out.println(values[j]);
				
				dr.addProperty(info.getColNames()[i], values[j]);
				}
			}
	}
}
		

	


