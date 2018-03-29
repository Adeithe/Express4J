package com.express4j.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class Json {
	public JSONObject json;
	
	public Json() { this(new JSONObject()); }
	public Json(JSONObject json) {
		this.json = json;
	}
	
	public Json addJson(String key, Json json) {
		this.json.put(key, json.json);
		return this;
	}
	public Json addJson(String key, Array array) {
		this.json.put(key, array.array);
		return this;
	}
	
	public JSONObject getJsonObject(String key) {
		return ((JSONObject)this.json.get(key));
	}
	
	public JSONArray getJsonArray(String key) {
		return ((JSONArray)this.json.get(key));
	}
	
	public HashMap<Object, Object> getObject(String key) { return (HashMap<Object, Object>)this.json.get(key); }
	public Json addObject(String key, HashMap<Object, Object> value) {
		this.json.put(key, value);
		return this;
	}
	
	public List<Object> getArray(String key) { return (List<Object>)this.json.get(key); }
	public Json addArray(Object key, List<Object> value) {
		this.json.put(key, value);
		return this;
	}
	
	public String getString(String key) { return String.valueOf(this.json.get(key)); }
	public Json addString(String key, String value) {
		this.json.put(key, value);
		return this;
	}
	
	public int getInteger(String key) { return Integer.parseInt(String.valueOf(this.json.get(key))); }
	public Json addInteger(String key, int value) {
		this.json.put(key, value);
		return this;
	}
	
	public double getDouble(String key) { return Double.parseDouble(String.valueOf(this.json.get(key))); }
	public Json addDouble(String key, double value) {
		this.json.put(key, value);
		return this;
	}
	
	public long getLong(String key) { return Long.parseLong(String.valueOf(this.json.get(key))); }
	public Json addLong(String key, long value) {
		this.json.put(key, value);
		return this;
	}
	
	public boolean getBoolean(String key) { return Boolean.parseBoolean(String.valueOf(this.json.get(key))); }
	public Json addBoolean(String key, boolean value) {
		this.json.put(key, value);
		return this;
	}
	
	public String toString() { return this.json.toJSONString(); }
	public static Json parse(String json) throws ParseException { return new Json((JSONObject) new JSONParser().parse(json)); }
	
	public static class Array {
		public JSONArray array;
		
		public Array() { this(new JSONArray()); }
		public Array(JSONArray array) {
			this.array = array;
		}
		
		public int size() {
			return this.array.size();
		}
		
		public Object get(int index) {
			return this.array.get(index);
		}
		
		public Array add(Object value) {
			this.array.add(value);
			return this;
		}
		
		public boolean contains(Object value) {
			return this.array.contains(value);
		}
		
		public Array removeKey(int index) {
			this.array.remove(index);
			return this;
		}
		
		public Array remove(Object value) {
			this.array.remove(value);
			return this;
		}
		
		public Array clear() {
			this.array.clear();
			return this;
		}
		
		public String toString() { return this.array.toJSONString(); }
		public static Array parse(String json) throws ParseException { return new Array((JSONArray) new JSONParser().parse(json)); }
		
		public List<Object> toList() {
			List<Object> list = new ArrayList<Object>();
			for(Object obj : this.array)
				list.add(obj);
			return list;
		}
	}
}
