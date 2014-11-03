package org.pathvisio.mipast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

/** 
 * Basic file reader to read the chosen data files line by line and store them as an array of lines.
 * 
 * @author ChrOertlin
 *
 */

public class MiPaStFileReader {
	ArrayList<String> Lines;
	public ArrayList<String> fileReader(File file) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		Lines = new ArrayList<String>();
		String line;
		while ((line = in.readLine()) != null)
		{
			Lines.add(line);
		}
		in.close();
		return Lines;
	}

}
