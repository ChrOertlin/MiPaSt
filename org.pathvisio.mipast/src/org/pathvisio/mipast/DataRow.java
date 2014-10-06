package org.pathvisio.mipast;

import java.util.HashMap;
import java.util.Map;

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
	
	
}
