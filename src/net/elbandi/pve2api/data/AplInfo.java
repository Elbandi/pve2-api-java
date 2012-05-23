package net.elbandi.pve2api.data;

import org.json.JSONException;
import org.json.JSONObject;

public class AplInfo {
	private String description;
	private String headline;
	private String infopage;
	private String location;
	private String maintainer;
	private String md5sum;
	private String os;
	private String aplpackage;
	private String section;
	private String source;
	private String template;
	private String type;
	private String version;

	public AplInfo(JSONObject data) throws JSONException {
		description = data.getString("description");
		headline = data.getString("headline");
		infopage = data.getString("infopage");
		location = data.getString("location");
		maintainer = data.getString("maintainer");
		md5sum = data.getString("md5sum");
		os = data.getString("os");
		aplpackage = data.getString("package");
		section = data.getString("section");
		source = data.getString("source");
		template = data.getString("template");
		type = data.getString("type");
		version = data.getString("version");
	}

	public String getDescription() {
		return description;
	}

	public String getHeadline() {
		return headline;
	}

	public String getInfopage() {
		return infopage;
	}

	public String getLocation() {
		return location;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public String getMd5sum() {
		return md5sum;
	}

	public String getOs() {
		return os;
	}

	public String getPackage() {
		return aplpackage;
	}

	public String getSection() {
		return section;
	}

	public String getSource() {
		return source;
	}

	public String getTemplate() {
		return template;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}
}
