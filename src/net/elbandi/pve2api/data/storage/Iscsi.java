package net.elbandi.pve2api.data.storage;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.data.Storage;

public class Iscsi extends Storage {
	private String portal;
	private String target;

	public Iscsi(JSONObject data) throws JSONException {
		super(data);
		portal = data.getString("portal");
		target = data.getString("target");
	}

	public String getPortal() {
		return portal;
	}

	public String getTarget() {
		return target;
	}
}
