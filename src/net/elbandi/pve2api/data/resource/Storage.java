package net.elbandi.pve2api.data.resource;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.data.Resource;

public class Storage extends Resource {
	private String storage;
	private String node;
	private long maxdisk;
	private long disk;

	public Storage(JSONObject data) throws JSONException {
		super(data);
		storage = data.getString("storage");
		node = data.getString("node");
		disk = data.getLong("disk");
		maxdisk = data.getLong("maxdisk");
	}

	public String getStorage() {
		return storage;
	}

	public String getNode() {
		return node;
	}

	public long getMaxdisk() {
		return maxdisk;
	}

	public long getDisk() {
		return disk;
	}
}
