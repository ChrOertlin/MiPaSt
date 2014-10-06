package org.pathvisio.mipast.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class FileMerger {
	
	File mergedFile;
	
	public  File fileMerger(List<String> array1, List<String> array2) throws java.io.FileNotFoundException{
		String line;
		// Merge the ArrayLists together into one big array
		ArrayList<String> mergedArrays = new ArrayList<String>(mergeArrays(array1, array2));
		
		// Write the merged arrays to a file and return it
		File mergedFile= new File("mergedFile.txt");
		try{
		BufferedWriter output = new BufferedWriter(new FileWriter(mergedFile));
	
		for (int i=0; i< mergedArrays.size();i++){
			line = mergedArrays.get(i);
			System.out.println(line);
			output.write(line);
			output.newLine();
		}
		output.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return mergedFile;
		}
	
	
	

	// The method that merges the arrays together into on big array
	public static List<String> mergeArrays(List<String> array1, List<String> array2){
		List<String> mergedArray = new ArrayList<String>();
		for (int i=0; i< array1.size();i++){
				if (i==0){
				mergedArray.add(array1.get(i)+"\t"+ "type");
			}
				else{
					mergedArray.add(array1.get(i)+"\t"+ "miRNA");
				}
		}
		
		for (int j=1 ;j<array2.size();j++){
			
			mergedArray.add(array2.get(j)+"\t"+ "gene");
		}
		
		
		return mergedArray;
	}
	
	

}
