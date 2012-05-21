package net.elbandi.pve2api.data.resource;

import org.json.JSONException;
import org.json.JSONObject;

import net.elbandi.pve2api.data.Resource;

public class Vm extends Resource {
	private String name;
	private int vmid;
	private String node;
	private int maxcpu;
	private long maxmem;
	private long maxdisk;
	private int uptime;
	private long mem;
	private long disk;
	private float cpu;

	public Vm(JSONObject data) throws JSONException {
		super(data);
		name = data.getString("name");
		vmid = data.getInt("vmid");
		node = data.getString("node");
		maxcpu = data.getInt("maxcpu");
		maxmem = data.getLong("maxmem");
		maxdisk = data.getLong("maxdisk");
		uptime = data.getInt("uptime");
		cpu = (float) data.getDouble("cpu");
		mem = data.getLong("mem");
		disk = data.getLong("disk");
	}

	public String getName() {
		return name;
	}

	public int getVmid() {
		return vmid;
	}

	public String getNode() {
		return node;
	}

	public int getMaxcpu() {
		return maxcpu;
	}

	public long getMaxmem() {
		return maxmem;
	}

	public long getMaxdisk() {
		return maxdisk;
	}

	public int getUptime() {
		return uptime;
	}

	public long getMem() {
		return mem;
	}

	public long getDisk() {
		return disk;
	}

	public float getCpu() {
		return cpu;
	}
}
