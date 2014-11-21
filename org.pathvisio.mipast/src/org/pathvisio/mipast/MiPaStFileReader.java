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
