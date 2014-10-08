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

import org.pathvisio.gexplugin.ImportInformation;
import org.pathvisio.gexplugin.GexTxtImporter;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.desktop.PvDesktop;

/**
 * 
 * @author ChrOertlin
 *
 */
public class FileMerger {
	
	public void createCombinedFile(ImportInformation miRNA, ImportInformation gene,ProgressKeeper pk, PvDesktop desktop) throws IOException {
		File combinedFile= new File("combinedFiles.txt");
		FileWriter fw= new FileWriter(combinedFile);
		BufferedWriter write = new BufferedWriter(fw);		
		
		if(gene != null) {
			GexTxtImporter.importFromTxt(miRNA, pk, 
					desktop.getSwingEngine().getGdbManager().getCurrentGdb(), 
					desktop.getGexManager());
			
		}
		
		else{
			String aLine=null;
			if(miRNA.getColNames()==gene.getColNames()){
				
					
				}
			}
			
			
		}
	}
}
