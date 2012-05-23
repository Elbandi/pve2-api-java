package net.elbandi.pve2api.data.storage;

import java.util.EnumSet;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.Pve2Api.PveParams;
import net.elbandi.pve2api.data.Storage;

public class Nfs extends Storage {
	private String path;
	private int maxfiles;
	private String server;
	private String export;
	private String options;

	public Nfs(JSONObject data) throws JSONException {
		super(data);
		path = data.getString("path");
		maxfiles = data.getInt("maxfiles");
		server = data.getString("server");
		export = data.getString("export");
		options = data.optString("options");
	}

	// for create
	public Nfs(String storage, EnumSet<Content> content, String nodes, boolean disable,
			String path, int maxfiles, String server, String export) throws JSONException {
		super(storage, content, nodes, true, disable);
		this.path = path;
		this.maxfiles = maxfiles;
		this.server = server;
		this.export = export;
	}

	// for update
	public Nfs(String storage, String digest, EnumSet<Content> content, String nodes,
			boolean disable, int maxfiles) {
		super(storage, digest, content, nodes, true, disable);
		this.maxfiles = maxfiles;
	}

	public String getPath() {
		return path;
	}

	public int getMaxfiles() {
		return maxfiles;
	}

	public String getServer() {
		return server;
	}

	public String getExport() {
		return export;
	}

	public String getOptions() {
		return options;
	}

	public PveParams getCreateParams() {
		// hack: for now we always create nvf v3
		PveParams res = super.getCreateParams().Add("type", "nfs").Add("path", path)
				.Add("maxfiles", maxfiles).Add("export", export).Add("options", "vers=3")
				.Add("server", server);
		res.remove("shared"); // always shared
		return res;
	}

	public PveParams getUpdateParams() {
		PveParams res = super.getUpdateParams().Add("maxfiles", maxfiles);
		res.remove("shared"); // always shared
		return res;
	}
}
