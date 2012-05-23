package net.elbandi.pve2api.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class ClusterLog {
	private String msg;
	private String node;
	private int pid;
	private int pri;
	private String tag;
	private Date time;
	private int uid;
	private String user;

	public ClusterLog(JSONObject data) throws JSONException {
		msg = data.getString("msg");
		node = data.getString("node");
		pid = data.getInt("pid");
		pri = data.getInt("pri");
		tag = data.getString("tag");
		time = new Date(data.getInt("time"));
		uid = data.getInt("uid");
		user = data.getString("user");
	}

	public String getMsg() {
		return msg;
	}

	public String getNode() {
		return node;
	}

	public int getPid() {
		return pid;
	}

	public int getPri() {
		return pri;
	}

	public String getTag() {
		return tag;
	}

	public Date getTime() {
		return time;
	}

	public int getUid() {
		return uid;
	}

	public String getUser() {
		return user;
	}
}
