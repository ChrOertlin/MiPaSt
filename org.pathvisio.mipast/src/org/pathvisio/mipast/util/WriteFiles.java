package org.pathvisio.mipast.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

//Copyright 2014 BiGCaT
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.


/**
 * 
 * This class writes data to files to see what the plugin retrieves as lists when filtering
 * the data set based on the given criteria and also after comparing he lists to the interaction list.
 * 
 * @author ChrOertlin
 *
 */

public class WriteFiles {

	
	 public void writeListToFile(Set<String> list, File file) throws IOException {
		 BufferedWriter cbw = new BufferedWriter(new FileWriter(file));
		Iterator it = list.iterator();
		while(it.hasNext()){
			cbw.write(it.next() + "\n");
		}
		cbw.close();
	 }
	 

}

