package net.elbandi.pve2api.data.storage;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.data.Storage;

public class Dir extends Storage {
	private String path;
	int maxfiles;

	public Dir(JSONObject data) throws JSONException {
		super(data);
		path = data.getString("path");
		maxfiles = data.getInt("maxfiles");
	}

	public String getPath() {
		return path;
	}

	public int getMaxfiles() {
		return maxfiles;
	}

}
