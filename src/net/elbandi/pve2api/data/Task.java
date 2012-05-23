package net.elbandi.pve2api.data;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
	private Date endtime;
	private String id;
	private String node;
	private boolean saved;
	private Date starttime;
	private String status;
	private String type;
	private String upid;
	private String user;

	public Task(JSONObject data) throws JSONException {
		endtime = new Date(data.getInt("endtime"));
		id = data.getString("id");
		node = data.getString("node");
		saved = data.getString("saved").equals("1");
		starttime = new Date(Integer.parseInt(data.getString("starttime")));
		status = data.getString("status");
		type = data.getString("type");
		upid = data.getString("upid");
		user = data.getString("user");
	}

	public Date getEndtime() {
		return endtime;
	}

	public String getId() {
		return id;
	}

	public String getNode() {
		return node;
	}

	public boolean isSaved() {
		return saved;
	}

	public Date getStarttime() {
		return starttime;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public String getUpid() {
		return upid;
	}

	public String getUser() {
		return user;
	}

}
