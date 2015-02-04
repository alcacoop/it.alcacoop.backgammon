/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Luca Greco                                           #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.utils.legacy.Json;
import it.alcacoop.backgammon.utils.legacy.JsonReader;
import it.alcacoop.backgammon.utils.legacy.JsonWriter.OutputType;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

public class JSONProperties {
  public OrderedMap<String, Object> data;
	
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

    for (int i = 0; i < keys.size; i++) {
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
    if (v == null)
      return fallback;
    return (new JSONProperties((OrderedMap<String, Object>)v));
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
    if (v == null)
      return fallback;
    return v;
  }

  public Object getData() {
    return data;
  }
}
