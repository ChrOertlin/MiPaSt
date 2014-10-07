package org.pathvisio.mipast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataRow {

	private String id;
	private Map<String, String> properties;
	private Set<String> keys;
	private Collection values;
	
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

	public Set<String> keySet() {
		keys=properties.keySet();
		return keys;
	}

	public void printMap() {
		for(Map.Entry<String, String> entry: properties.entrySet()){
			String keyVal= entry.getKey();
			String value = entry.getValue();
			System.out.println(keyVal+ "" + value);
		}
		
		
	}


	
	
}
