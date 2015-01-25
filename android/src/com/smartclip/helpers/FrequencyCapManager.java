package com.smartclip.helpers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class FrequencyCapManager {

	private static final int BD_FREQUENCY_CAP_DEFAULT_VALUE = Integer.MAX_VALUE;
	private static final int BD_SKIP_AFTER_IN_SECS_DEFAULT_VALUE = -1;
	private static final int BD_UPDATE_CAP_MINIMUM_DELAY_IN_SECS = 10;
 	private final String BD_BASE_DOMAIN = "smartclip.bitdrome-hosting.com";
	
	private final String PREFERENCES_NAME = "BDFrequencyCapPreferences";

	private static FrequencyCapManager frequencyCapManager = null;

	private HashMap<String, Object> _preferencesMainNodeKey;

	private HashMap<String, Object> _preferencesFrequencyCapKey;
	private final String PREFERENCES_MAIN_NODE_KEY = "PreferencesMainNodeKey";
	private SharedPreferences _prefs;
	private SharedPreferences.Editor _prefEditor;

	private final String PREFERENCES_FREQUENCY_CAP_KEY = "PreferencesFrequencyCapKey";

	private String appKey;
	private long lastUpdateTimeStamp = 0;

	public static FrequencyCapManager initializeWithAppKey(String _appKey, Context _context){
		if(frequencyCapManager == null)
			frequencyCapManager = new FrequencyCapManager(_appKey, _context);
		frequencyCapManager.updateFrequencyCap();
		return frequencyCapManager;	

	}

	public static FrequencyCapManager getInstance(){
		return frequencyCapManager;
	}

	private FrequencyCapManager(String _appKey, Context _context){
		appKey = _appKey;
		_prefs = _context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		_prefEditor = _prefs.edit();
		
		loadPreferencesMainNodeKey(_prefs.getString(PREFERENCES_MAIN_NODE_KEY, null));

		loadPreferencesFrequencyCapKey(_prefs.getString(PREFERENCES_FREQUENCY_CAP_KEY, null));
	}
	
	private void loadPreferencesMainNodeKey(String jsonString){
		if(jsonString == null)
			return;
		
		try {
			JSONObject jsonResponse = new JSONObject(jsonString);   //parser risposta JSON
			//parse 
			_preferencesMainNodeKey = new HashMap<String, Object>(); 
			_preferencesMainNodeKey.put("today", jsonResponse.optString("today", null));
			JSONArray jsonItemIds = jsonResponse.getJSONArray("itemIds");

			ArrayList<HashMap<String, Object>> nodes = new ArrayList<HashMap<String,Object>>();

			for(int i = 0; i < jsonItemIds.length(); i++){

				JSONObject jsonIt = jsonItemIds.getJSONObject(i);
				HashMap<String, Object> currNode = new HashMap<String, Object>();
				currNode.put("count", jsonIt.optInt("count", 0));
				currNode.put("itemId", jsonIt.optString("itemId", null));
				currNode.put("showSkipAfterSecs", jsonIt.optInt("showSkipAfterSecs"));

				nodes.add(currNode);
			}

			_preferencesMainNodeKey.put("itemIds", nodes);

		} catch (JSONException e) {
		} catch (Exception e) {
		}
	}
	
	private void loadPreferencesFrequencyCapKey(String jsonString){
		if(jsonString == null)
			return;
		
		try {
			JSONObject jsonResponse = new JSONObject(jsonString);   //parser risposta JSON
			//parse 
			_preferencesFrequencyCapKey = new HashMap<String, Object>(); 
			_preferencesFrequencyCapKey.put("checksum", jsonResponse.optString("checksum", null));
			JSONArray jsonItemIds = jsonResponse.getJSONArray("itemIds");

			ArrayList<HashMap<String, Object>> nodes = new ArrayList<HashMap<String,Object>>();

			for(int i = 0; i < jsonItemIds.length(); i++){

				JSONObject jsonIt = jsonItemIds.getJSONObject(i);
				HashMap<String, Object> currNode = new HashMap<String, Object>();
				currNode.put("count", jsonIt.optInt("count", 0));
				currNode.put("itemId", jsonIt.optString("itemId", null));
				currNode.put("showSkipAfterSecs", jsonIt.optInt("showSkipAfterSecs", 0));

				nodes.add(currNode);
			}

			_preferencesFrequencyCapKey.put("settings", nodes);

		} catch (JSONException e) {
		} catch (Exception e) {
		}
	}
	
	private HashMap<String, Object> getPreferencesMainNodeKey(){
		return _preferencesMainNodeKey;
	}
	
	private HashMap<String, Object> getPreferencesFrequencyCapKey(){
		return _preferencesFrequencyCapKey;
	}
	
	private void setPreferencesMainNodeKey(HashMap<String, Object> map){
		try {
			_preferencesMainNodeKey = map;
			if(map == null)
				_prefEditor.putString(PREFERENCES_MAIN_NODE_KEY, null);
			else
				_prefEditor.putString(PREFERENCES_MAIN_NODE_KEY, toJSONMainNode(map));
			_prefEditor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void setPreferencesFrequencyCapKey(HashMap<String, Object> map){
		try {
			_preferencesFrequencyCapKey = map;
			if(map == null)
				_prefEditor.putString(PREFERENCES_FREQUENCY_CAP_KEY, null);
			else
				_prefEditor.putString(PREFERENCES_FREQUENCY_CAP_KEY, toJSONPreferenceKey(map));
			_prefEditor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private String toJSONMainNode(HashMap<String, Object> map) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("today", map.get("today"));	
		JSONArray list = new JSONArray();
		for(HashMap<String, Object> node : (ArrayList<HashMap<String, Object>>)map.get("itemIds")){
			JSONObject item = new JSONObject();
			item.put("showSkipAfterSecs", node.get("showSkipAfterSecs"));
			item.put("count", node.get("count"));
			item.put("itemId", node.get("itemId"));
			list.put(item);
		}

		obj.put("itemIds", list);

		return obj.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String toJSONPreferenceKey(HashMap<String, Object> map) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put("checksum", map.get("checksum"));	
		JSONArray list = new JSONArray();
		for(HashMap<String, Object> node : (ArrayList<HashMap<String, Object>>)map.get("settings")){
			JSONObject item = new JSONObject();
			item.put("showSkipAfterSecs", node.get("showSkipAfterSecs"));
			item.put("count", node.get("count"));
			item.put("itemId", node.get("itemId"));
			list.put(item);
		}

		obj.put("itemIds", list);

		return obj.toString();
	}

	@SuppressWarnings("unchecked")
	private int getSkipAfterSecsValueForItemWithIdPrivate(String itemId){
		int skip = BD_SKIP_AFTER_IN_SECS_DEFAULT_VALUE;

		if(itemId != null){
			HashMap<String, Object> mainNode = getPreferencesFrequencyCapKey();
			if(mainNode != null){
				ArrayList<HashMap<String, Object>> settings = (ArrayList<HashMap<String, Object>>) mainNode.get("settings");
				if(settings != null)
					for(HashMap<String, Object> node : settings){
						if(((String)node.get("itemId")).compareTo(itemId)==0){
							if((Integer) node.get("showSkipAfterSecs") >= 0)
								skip = (Integer) node.get("showSkipAfterSecs");
							break;
						}
					}
			}
		}	
		return skip;
	}

	@SuppressWarnings("unchecked")
	private int getCapValueForItemWithId(String itemId){
		int cap = BD_FREQUENCY_CAP_DEFAULT_VALUE;

		if(itemId != null){
			HashMap<String, Object> mainNode = getPreferencesFrequencyCapKey();
			if(mainNode != null){
				ArrayList<HashMap<String, Object>> settings = (ArrayList<HashMap<String, Object>>) mainNode.get("settings");
				if(settings != null)
					for(HashMap<String, Object> node : settings){
						if(((String)node.get("itemId")).compareTo(itemId)==0){
							if((Integer) node.get("count") >= 0)
								cap = (Integer) node.get("count");
							break;
						}
					}
			}
		}
		return cap;
	}

	@SuppressLint("SimpleDateFormat")
	private String getTodayString(){
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(today);
	}

	private void updateCapValues(HashMap<String, Object> capVAlueObj){
		if(capVAlueObj == null)
			return;

		if(getPreferencesFrequencyCapKey() != null){
			HashMap<String, Object> mainNode = getPreferencesFrequencyCapKey();
			String actualChecksum = (String) mainNode.get("checksum");

			String newChecksum = (String) capVAlueObj.get("checksum");
			if(newChecksum.compareTo(actualChecksum)==0)
				return;
			setPreferencesFrequencyCapKey(null);
		}
		setPreferencesFrequencyCapKey(capVAlueObj);
		setPreferencesMainNodeKey(null);
	}

	private void updateFrequencyCap(){
		PerformUpdateFrequencyCapInBackground task = new PerformUpdateFrequencyCapInBackground();
		task.execute();
	}

	@SuppressWarnings("unchecked")
	public int getNumberOfDisplaysStillAvailableForItemWithId(String itemId){
		int left = 0;
		if(getPreferencesMainNodeKey() == null){
			ArrayList<HashMap<String, Object>> itemIds = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("count", getCapValueForItemWithId(itemId));
			node.put("itemId", itemId);
			node.put("showSkipAfterSecs", getSkipAfterSecsValueForItemWithIdPrivate(itemId));
			itemIds.add(node);
			HashMap<String, Object> mainNode = new HashMap<String, Object>();
			mainNode.put("today", getTodayString());
			mainNode.put("itemIds", itemIds);
			setPreferencesMainNodeKey(mainNode);

			left = (Integer) node.get("count");
		}else{
			HashMap<String, Object> mainNode = getPreferencesMainNodeKey();
			String today = (String) mainNode.get("today");

			if(today.compareTo(getTodayString()) != 0){
				setPreferencesMainNodeKey(null);
				ArrayList<HashMap<String, Object>> itemIds = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> node = new HashMap<String, Object>();
				node.put("itemId", itemId);
				node.put("count", getCapValueForItemWithId(itemId));
				node.put("showSkipAfterSecs", getSkipAfterSecsValueForItemWithIdPrivate(itemId));
				itemIds.add(node);
				HashMap<String, Object> mainNodeNew = new HashMap<String, Object>();
				mainNodeNew.put("today", getTodayString());
				mainNodeNew.put("itemIds", itemIds);
				setPreferencesMainNodeKey(mainNodeNew);
				left = (Integer) node.get("count");
			}else{
				ArrayList<HashMap<String, Object>> itemIds = (ArrayList<HashMap<String, Object>>) mainNode.get("itemIds");
				boolean itemFound = false;
				int count = 0;

				for(HashMap<String, Object> node : itemIds){
					String _itemId = (String) node.get("itemId");

					if(_itemId.compareTo(itemId) == 0){
						count = (Integer) node.get("count");
						itemFound = true;
						break;
					}
				}
				if(itemFound == false){
					HashMap<String, Object> node = new HashMap<String, Object>();
					node.put("itemId", itemId);
					node.put("count", getCapValueForItemWithId(itemId));
					node.put("showSkipAfterSecs", getSkipAfterSecsValueForItemWithIdPrivate(itemId));
					count = (Integer) node.get("count");

					setPreferencesMainNodeKey(null);
					itemIds.add(node);
					HashMap<String, Object> mainNodeNew = getPreferencesMainNodeKey();
					if(mainNodeNew != null){
						mainNodeNew.put("today", getTodayString());
						mainNodeNew.put("itemIds", itemIds);
					}
				}
				left = count;
			}
		}
		updateFrequencyCap();
		if(left < 0)
			return 0;
		return left;
	}

	public boolean canDisplayAdForItemWithId(String itemId){
		if(getNumberOfDisplaysStillAvailableForItemWithId(itemId) > 0){
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void setDisplayConsumedForItemWithId(String itemId){
		if(getPreferencesMainNodeKey() == null){
			ArrayList<HashMap<String, Object>> itemIds = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("itemId", itemId);
			node.put("count", getCapValueForItemWithId(itemId) - 1);
			node.put("showSkipAfterSecs", getSkipAfterSecsValueForItemWithIdPrivate(itemId));
			itemIds.add(node);
			HashMap<String, Object> mainNode = new HashMap<String, Object>();
			mainNode.put("today", getTodayString());
			mainNode.put("itemIds", itemIds);
			setPreferencesMainNodeKey(mainNode);
		}else{
			HashMap<String, Object> mainNode = getPreferencesMainNodeKey();
			String today = (String) mainNode.get("today");
			if(getTodayString().compareTo(today) != 0){
				setPreferencesMainNodeKey(null);

				ArrayList<HashMap<String, Object>> itemIds = new ArrayList<HashMap<String,Object>>();
				HashMap<String, Object> node = new HashMap<String, Object>();
				node.put("itemId", itemId);
				node.put("count", getCapValueForItemWithId(itemId) - 1);
				node.put("showSkipAfterSecs", getSkipAfterSecsValueForItemWithIdPrivate(itemId));
				itemIds.add(node);
				HashMap<String, Object> mainNodeNew = new HashMap<String, Object>();
				mainNodeNew.put("today", getTodayString());
				mainNodeNew.put("itemIds", itemIds);
				setPreferencesMainNodeKey(mainNodeNew);
			}else{
				ArrayList<HashMap<String, Object>> itemIds = (ArrayList<HashMap<String, Object>>) mainNode.get("itemIds");
				HashMap<String, Object> targetItem = null;
				for(HashMap<String, Object> node : itemIds){
					String _itemId = (String) node.get("itemId");

					if(_itemId.compareTo(itemId) == 0){
						targetItem = node;
						break;
					}
				}
				if(targetItem == null){
					HashMap<String, Object> node = new HashMap<String, Object>();
					node.put("itemId", itemId);
					node.put("count", getCapValueForItemWithId(itemId) - 1);
					node.put("showSkipAfterSecs", getSkipAfterSecsValueForItemWithIdPrivate(itemId));

					setPreferencesMainNodeKey(null);
					itemIds.add(node);
					HashMap<String, Object> mainNodeNew = new HashMap<String, Object>();
					mainNodeNew.put("today", getTodayString());
					mainNodeNew.put("itemIds", itemIds);
					setPreferencesMainNodeKey(mainNodeNew);
				}else{
					int countNew = (Integer) targetItem.get("count");
					int skip = Integer.parseInt("" + targetItem.get("showSkipAfterSecs").toString());
					//int skip = 10;
					HashMap<String, Object> updateNode = new HashMap<String, Object>();
					updateNode.put("itemId", itemId);
					updateNode.put("count", countNew - 1);
					updateNode.put("showSkipAfterSecs", skip);

					itemIds.remove(targetItem);
					itemIds.add(updateNode);

					setPreferencesMainNodeKey(null);

					HashMap<String, Object> mainNodeNew = new HashMap<String, Object>();
					mainNodeNew.put("today", getTodayString());
					mainNodeNew.put("itemIds", itemIds);
					setPreferencesMainNodeKey(mainNodeNew);
				}
			}
		}
		updateFrequencyCap();
	}

	public int getSkipAfterSecsValueForItemWithId(String itemId){
		return getSkipAfterSecsValueForItemWithIdPrivate(itemId);
	}

	private class PerformUpdateFrequencyCapInBackground extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			long timestamp = System.currentTimeMillis()/1000;

			if(timestamp < lastUpdateTimeStamp + BD_UPDATE_CAP_MINIMUM_DELAY_IN_SECS)
				return null;

			lastUpdateTimeStamp = timestamp;

			HttpClient httpclient = new DefaultHttpClient();
			String url = "http://" + BD_BASE_DOMAIN + "/get_daily_cap.php?app_key=" + appKey;
			HttpGet httpGet = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 30000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			//cookie???
			try {
				// Execute HTTP Get Request
				HttpResponse response = httpclient.execute(httpGet); 
				String result = null;
				result = EntityUtils.toString(response.getEntity());
				//JSONObject node = new JSONObject(result);
				//devo aggiornare
				return result;
			} catch (ClientProtocolException e) {
				return null;

			} catch (IOException e) {
				return null;
			} 

		}
		@Override
		protected void onPostExecute(String result) {
			updateCapValues(preferencesFrequencyCapKey(result));      //lancio la callback che verr� gestita all'interno del metodo che ha lanciato il Task
		}

	}

	private synchronized HashMap<String, Object> preferencesFrequencyCapKey(String jsonString){
		if(jsonString == null)
			return null;

		try {
			JSONObject jsonResponse = new JSONObject(jsonString);   //parser risposta JSON
			//parse 
			//ArrayList<Rubrica> channels = new ArrayList<Rubrica>();
			HashMap<String, Object> mainNodeNew = new HashMap<String, Object>();
			mainNodeNew.put("checksum", jsonResponse.optString("checksum", null));
			JSONArray jsonItemIds = jsonResponse.getJSONArray("settings");
			ArrayList<HashMap<String, Object>> nodes = new ArrayList<HashMap<String,Object>>();

			for(int i = 0; i < jsonItemIds.length(); i++){

				JSONObject jsonIt = jsonItemIds.getJSONObject(i);
				HashMap<String, Object> currNode = new HashMap<String, Object>();
				currNode.put("count", jsonIt.optInt("cap", BD_FREQUENCY_CAP_DEFAULT_VALUE));
				currNode.put("itemId", jsonIt.optString("itemId", null));
				currNode.put("showSkipAfterSecs", jsonIt.optInt("showSkipAfterSecs", BD_SKIP_AFTER_IN_SECS_DEFAULT_VALUE));
				nodes.add(currNode);
			}

			mainNodeNew.put("settings", nodes);
			return mainNodeNew;
		} catch (JSONException e) {
			return null;
		} catch (Exception e) {
			return null;
		}

	}

}
