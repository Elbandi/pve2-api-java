package net.elbandi.pve2api.data.storage;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.data.Storage;

public class Nfs extends Storage {
	private String path;
	private int maxfiles;
	private String export;
	private String options;
	private String server;

	public Nfs(JSONObject data) throws JSONException {
		super(data);
		path = data.getString("path");
		maxfiles = data.getInt("maxfiles");
		export = data.getString("export");
		options = data.getString("options");
		server = data.getString("server");
	}

	public String getPath() {
		return path;
	}

	public int getMaxfiles() {
		return maxfiles;
	}

	public String getExport() {
		return export;
	}

	public String getOptions() {
		return options;
	}

	public String getServer() {
		return server;
	}
}
