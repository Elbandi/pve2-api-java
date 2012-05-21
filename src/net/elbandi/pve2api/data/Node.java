package net.elbandi.pve2api.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Node {
	private float cpu;
	private CpuInfo cpuinfo;
	private String kversion;
	private String pveversion;
	private int uptime;
	private float[] loadavg;
	private long memory_free;
	private long memory_total;
	private long swap_free;
	private long swap_total;
	private long rootfs_free;
	private long rootfs_total;

	public Node(JSONObject data) throws JSONException {
		JSONObject o;
		cpu = (float) data.getDouble("cpu");
		cpuinfo = new CpuInfo(data.getJSONObject("cpuinfo"));
		kversion = data.getString("kversion");
		pveversion = data.getString("pveversion");
		o = data.getJSONObject("memory");
		memory_free = o.getLong("free");
		memory_total = o.getLong("total");
		o = data.getJSONObject("swap");
		swap_free = o.getLong("free");
		swap_total = o.getLong("total");
		o = data.getJSONObject("rootfs");
		rootfs_free = o.getLong("free");
		rootfs_total = o.getLong("total");
		uptime = data.getInt("uptime");
		JSONArray load = data.getJSONArray("loadavg");
		loadavg = new float[load.length()];
		for (int i = 0; i < load.length(); i++) {
			loadavg[i] = (float) load.getDouble(i);
		}
	}

	public float getCpu() {
		return cpu;
	}

	public CpuInfo getCpuinfo() {
		return cpuinfo;
	}

	public String getKversion() {
		return kversion;
	}

	public String getPveversion() {
		return pveversion;
	}

	public int getUptime() {
		return uptime;
	}

	public float[] getLoadavg() {
		return loadavg;
	}

	public long getMemory_free() {
		return memory_free;
	}

	public long getMemory_total() {
		return memory_total;
	}

	public long getSwap_free() {
		return swap_free;
	}

	public long getSwap_total() {
		return swap_total;
	}

	public long getRootfs_free() {
		return rootfs_free;
	}

	public long getRootfs_total() {
		return rootfs_total;
	}

	class CpuInfo {
		private int cpus;
		private float mhz;
		private String model;
		private int sockets;

		public CpuInfo(JSONObject data) throws JSONException {
			cpus = data.getInt("cpus");
			mhz = (float) data.getDouble("mhz");
			model = data.getString("model");
			sockets = data.getInt("sockets");
		}

		public int getCpus() {
			return cpus;
		}

		public float getMhz() {
			return mhz;
		}

		public String getModel() {
			return model;
		}

		public int getSockets() {
			return sockets;
		}
	}
}
