package it.alcacoop.gnubackgammon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.OrderedMap;

public class JSONProperties {
	public OrderedMap<String,Object> data;
	
	public static JSONProperties newFromFile(FileHandle fh) {
        InputStream inStream = fh.read();
        JSONProperties properties = new JSONProperties();

        try {
            properties.load(fh);
            inStream.close();
        } catch (IOException e) {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ex) {
                    // TODO handle IOException
				}
			}
		}
		return properties;
	}
	
	@SuppressWarnings("unchecked")
	private void load(FileHandle fh) throws IOException {
		JsonReader reader = new JsonReader();
		data = (OrderedMap<String,Object>) reader.parse(fh.reader());
	}
	
	public JSONProperties() {
		data = new OrderedMap<String,Object>();
	}
	
	public JSONProperties(OrderedMap<String,Object> data) {
		this.data = data;
	}
	
	public JSONProperties(FileHandle fh) {
        try {
            load(fh);
        } catch (IOException e) {
            // ERROR LOADING PROPERTIES
        }
	}
	
	public String[] getKeys() {
		if (data == null) {
			return null;
		}
		Array<String> keys = data.orderedKeys();
		String[] keys_string = new String[keys.size];
		
		for (int i=0; i<keys.size; i++) {
			keys_string[i] = keys.get(i);
		}
		
		return keys_string;
	}
	
	private Object getProperty(String name) {
		if (!data.containsKey(name))
			return null;
		
		return data.get(name);
	}
	
	public int asInt (String name, int fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return ((Float)v).intValue();
	}

	public float asFloat (String name, float fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return (Float)v;
	}

	public String asString (String name, String fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return (String)v;
	}

	public boolean asBool(String name, boolean fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return (Boolean)v;
	}
	
	@SuppressWarnings("unchecked")
	public Array<Object> asArray(String name, Array<Object> fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return (Array<Object>)v;
	}
	
	@SuppressWarnings("unchecked")
	public JSONProperties asJSONProperties(String name, JSONProperties fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return (new JSONProperties((OrderedMap<String,Object>)v));
	}

	public void set(String key, Object value) {
		data.put(key, value);
	}

	public void save(FileHandle fh) throws IOException {
		Writer writer = fh.writer(false);
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		json.toJson(data,writer);
		writer.flush();
		writer.close();
	}

	public Object asObject(String name, Object fallback) {
		Object v = getProperty(name);
		if (v == null) return fallback;
		return v;
	}

	public Object getData() {
		return data;
	}
}
