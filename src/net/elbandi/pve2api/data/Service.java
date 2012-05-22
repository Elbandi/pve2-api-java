package net.elbandi.pve2api.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Service {
	private String desc;
	private String name;
	private String service;
	private String state;

	public Service(JSONObject data) throws JSONException {
		desc = data.getString("desc");
		name = data.getString("name");
		service = data.getString("service");
		state = data.getString("state");
	}

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return name;
	}

	public String getService() {
		return service;
	}

	public String getState() {
		return state;
	}
}
