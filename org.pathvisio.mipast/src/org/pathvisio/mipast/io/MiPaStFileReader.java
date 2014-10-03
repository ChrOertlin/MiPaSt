package org.pathvisio.mipast.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MiPaStFileReader {

	
	public static List<String> readFile(File file) throws IOException  {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		List<String> lineArray = new ArrayList<String>();
		while((line = reader.readLine()) != null) {
			lineArray.add(line);
		}
		return lineArray;
	}
}
