package net.elbandi.pve2api.data;

import org.json.JSONException;
import org.json.JSONObject;

public class VmOpenvz {
	private float cpu;
	private int cpus;
	private float disk;
	private long diskread;
	private long diskwrite;
	private long maxdisk;
	private long maxmem;
	private long maxswap;
	private long mem;
	private String name;
	private long netin;
	private long netout;
	private int nproc;
	private String status;
	private long swap;
	private int uptime;

	private int cpuunits;
	private String digest;
	private int diskspace; // ! name collision
	private int memory;
	private String hostname;
	private String nameserver;
	// TODO: make it object!
	private String netif;
	private boolean onboot;
	private String ostemplate;
	private int quotatime;
	private int quotaugidlimit;
	private String searchdomain;
	private String storage;

	public VmOpenvz(JSONObject data) throws JSONException {
		cpu = (float) data.getDouble("cpu");
		cpus = data.getInt("cpus");
		disk = (float) data.getDouble("disk");
		diskread = data.getLong("diskread");
		diskwrite = data.getLong("diskwrite");
		maxdisk = data.getLong("maxdisk");
		maxmem = data.getLong("maxmem");
		maxswap = data.getLong("maxswap");
		mem = data.getLong("mem");
		name = data.getString("name");
		netin = data.getLong("netin");
		netout = data.getLong("netout");
		nproc = data.getInt("nproc");
		status = data.getString("status");
		swap = data.getLong("swap");
		uptime = data.getInt("uptime");
	}

	public void SetConfig(JSONObject data) throws JSONException {
		cpuunits = data.optInt("cpuunits", 1000);
		digest = data.getString("digest");
		diskspace = data.getInt("disk");
		memory = data.getInt("memory");
		hostname = data.getString("hostname");
		nameserver = data.getString("nameserver");
		netif = data.getString("netif");
		onboot = data.getInt("onboot") == 1;
		ostemplate = data.getString("ostemplate");
		quotatime = data.getInt("quotatime");
		quotaugidlimit = data.getInt("quotaugidlimit");
		searchdomain = data.getString("searchdomain");
		storage = data.getString("storage");
	}

	public float getCpu() {
		return cpu;
	}

	public int getCpus() {
		return cpus;
	}

	public float getDisk() {
		return disk;
	}

	public long getDiskread() {
		return diskread;
	}

	public long getDiskwrite() {
		return diskwrite;
	}

	public long getMaxdisk() {
		return maxdisk;
	}

	public long getMaxmem() {
		return maxmem;
	}

	public long getMaxswap() {
		return maxswap;
	}

	public long getMem() {
		return mem;
	}

	public String getName() {
		return name;
	}

	public long getNetin() {
		return netin;
	}

	public long getNetout() {
		return netout;
	}

	public int getNproc() {
		return nproc;
	}

	public String getStatus() {
		return status;
	}

	public long getSwap() {
		return swap;
	}

	public int getUptime() {
		return uptime;
	}

	public int getCpuunits() {
		return cpuunits;
	}

	public String getDigest() {
		return digest;
	}

	public int getDiskspace() {
		return diskspace;
	}

	public int getMemory() {
		return memory;
	}

	public String getHostname() {
		return hostname;
	}

	public String getNameserver() {
		return nameserver;
	}

	public String getNetif() {
		return netif;
	}

	public boolean isOnboot() {
		return onboot;
	}

	public String getOstemplate() {
		return ostemplate;
	}

	public int getQuotatime() {
		return quotatime;
	}

	public int getQuotaugidlimit() {
		return quotaugidlimit;
	}

	public String getSearchdomain() {
		return searchdomain;
	}

	public String getStorage() {
		return storage;
	}
}
