//Copyright 2014 PathVisio
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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author ChrOertlin
 * @author mkutmon
 * 
 * This class stores the data for each row
 * in the experimental data files
 *
 */
public class DataRow {

	private String id;
	private Map<String, String> properties;
	
	public DataRow(String id) {
		super();
		this.id = id;
		properties = new HashMap<String, String>();
	}

	public String getId() {
		return id;
	}
	
	public void addProperty(String key, String value){
		if (!properties.containsKey(key)){
			properties.put(key, value);
		}
	}

	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void printMap() {
		for(String key : properties.keySet()) {
			System.out.println(key + "\t" + properties.get(key));
		}
	}
}
