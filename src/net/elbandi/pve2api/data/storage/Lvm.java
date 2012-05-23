package net.elbandi.pve2api.data.storage;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.data.Storage;

public class Lvm extends Storage {
	private String vgname;
	private boolean saferemove;

	public Lvm(JSONObject data) throws JSONException {
		super(data);
		vgname = data.getString("vgname");
		saferemove = data.optInt("saferemove") == 1;
	}

	public String getVgname() {
		return vgname;
	}

	public boolean isSaferemove() {
		return saferemove;
	}
}
