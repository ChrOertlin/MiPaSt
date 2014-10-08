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
import java.util.Map;
import java.util.Set;

import org.pathvisio.mipast.DataRow;



public class FileMerger {
	String line;

	String type;
	String systemcode;
	String[] rowElements;
	Set<String> keySet;
	String[] keys;
	DataRow dm = new DataRow("merge");
	
	
	public  Map<String, String> fileMerger(List<String> array, String type, String del) throws java.io.FileNotFoundException{
		
		for (int i=0; i<array.size();i++){
			if (i==0) {
				line= array.get(i);
				rowElements=line.split(del);
				System.out.print(rowElements);
			}
			
			for (int j= 0; j < rowElements.length;j++){
				dm.addProperty(rowElements[j], null);
				dm.addProperty(type, null);
				dm.addProperty(systemcode, null);
			}
			keySet= dm.getProperties().keySet();
			keys = (String[]) keySet.toArray();
			if(i>0){
				
				line = array.get(i);
				rowElements=line.split(del);
				for (int j=0; j<rowElements.length;j++){
					
					dm.addProperty(keys[j], rowElements[j]);
					
				}
			}
			
		}
		
		dm.printMap();
		return dm.getProperties();

		
	}	
		
		
	
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//		String line;
//		// Merge the ArrayLists together into one big array
//		ArrayList<String> mergedArrays = new ArrayList<String>(mergeArrays(array1, array2));
//		
//		// Write the merged arrays to a file and return it
//		File mergedFile= new File("mergedFile.txt");
//		try{
//		BufferedWriter output = new BufferedWriter(new FileWriter(mergedFile));
//	
//		for (int i=0; i< mergedArrays.size();i++){
//			line = mergedArrays.get(i);
//			System.out.println(line);
//			output.write(line);
//			output.newLine();
//		}
//		output.close();
//		}
//		catch(IOException e){
//			e.printStackTrace();
//		}
//		
//		return mergedFile;
//		}
//	
//	
//	
//
//	// The method that merges the arrays together into on big array
//	public static List<String> mergeArrays(List<String> array1, List<String> array2){
//		List<String> mergedArray = new ArrayList<String>();
//		for (int i=0; i< array1.size();i++){
//				if (i==0){
//				mergedArray.add(array1.get(i)+"\t"+ "type");
//			}
//				else{
//					mergedArray.add(array1.get(i)+"\t"+ "miRNA");
//				}
//		}
//		
//		for (int j=1 ;j<array2.size();j++){
//			
//			mergedArray.add(array2.get(j)+"\t"+ "gene");
//		}
//		
//		
//		return mergedArray;
//	}
//	
	


