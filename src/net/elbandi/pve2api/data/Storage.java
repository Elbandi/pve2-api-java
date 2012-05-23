package net.elbandi.pve2api.data;

import java.util.EnumSet;

import org.json.JSONException;
import org.json.JSONObject;

public class Storage {
	private String storage;
	private String digest;
	private EnumSet<Content> content;
	private String nodes;
	private boolean shared;
	private boolean disable;

	public Storage(JSONObject data) throws JSONException {
		storage = data.getString("storage");
		digest = data.getString("digest");
		content = Content.parse(data.getString("content"));
		shared = data.optInt("shared") == 1;
		disable = data.optInt("disable") == 1;
		nodes = data.optString("nodes");
	}

	public String getStorage() {
		return storage;
	}

	public String getDigest() {
		return digest;
	}

	public EnumSet<Content> getContent() {
		return content;
	}

	public String getNodes() {
		return nodes;
	}

	public boolean isShared() {
		return shared;
	}

	public boolean isDisable() {
		return disable;
	}

	public static Storage createStorage(JSONObject data) throws JSONException {
		switch (convertType(data.getString("type"))) {
			case Dir:
				return new net.elbandi.pve2api.data.storage.Dir(data);
			case Nfs:
				return new net.elbandi.pve2api.data.storage.Nfs(data);
			case Lvm:
				return new net.elbandi.pve2api.data.storage.Lvm(data);
			case Iscsi:
				return new net.elbandi.pve2api.data.storage.Iscsi(data);
			default:
				return new Storage(data);
		}
	}

	public static Type convertType(String name) {
		if (name.equals("dir"))
			return Type.Dir;
		else if (name.equals("nfs"))
			return Type.Nfs;
		else if (name.equals("lvm"))
			return Type.Lvm;
		else if (name.equals("iscsi"))
			return Type.Iscsi;
		else
			return Type.Unknown;
	}

	enum Type {
		Dir, Nfs, Lvm, Iscsi, Unknown
	}

	enum Content {
		images, rootdir, vztmpl, iso, backup;

		final static EnumSet<Content> parse(final String str) {
			final EnumSet<Content> set = EnumSet.noneOf(Content.class);
			if (!str.isEmpty()) {
				for (int i, j = 0; j >= 0;) {
					i = j;
					j = str.indexOf(',', i + 1);
					final String sub = j >= 0 ? str.substring(i, j) : str.substring(i);
					set.add(Enum.valueOf(Content.class, sub.trim().toUpperCase()));
				}
			}
			return set;
		}
	};
}
