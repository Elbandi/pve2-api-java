package net.elbandi.pve2api.data;

import java.util.HashMap;
import java.util.Map;

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
	private String netif; // TODO: make netif it object!
	private boolean onboot;
	private String ostemplate;
	private int quotatime;
	private int quotaugidlimit;
	private String searchdomain;
	private String storage;
	private String password;
	private int vmid;
	private String node;

	public VmOpenvz()
	{
		setStandardSettings();
	}
	
	public VmOpenvz(JSONObject data) throws JSONException 
	{
		setStandardSettings();
		
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
	
	private void setStandardSettings()
	{
		cpu = 0;
		cpus = 0;
		disk = 0;
		diskread = 0;
		diskwrite = 0;
		maxdisk = 0;
		maxmem = 0;
		maxswap = 0;
		mem = 0;
		name = "";
		netin = 0;
		netout = 0;
		nproc = 0;
		status = "";
		swap = 0;
		uptime = 0;
		cpuunits = 0;
		digest = "";
		diskspace = 0;
		memory = 0;
		hostname = "";
		nameserver = "";
		netif = "";
		onboot = false;
		ostemplate = "";
		quotatime = 0;
		quotaugidlimit = 0;
		searchdomain = "";
		storage = "";
		password = "";
		vmid = 0;
		node = "";
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
	
	public Map<String, String> getCreateParams()
	{
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("ostemplate", this.ostemplate);
		parameters.put("vmid", Integer.toString(this.vmid));
		
		if(this.cpus > 0)
			parameters.put("cpus", Integer.toString(this.cpus));
		if(this.cpuunits > 0)
			parameters.put("cpuunits", Integer.toString(this.cpuunits));
		if(this.disk > 0)
			parameters.put("disk", Float.toString(this.disk));
		if(this.hostname != null && this.hostname.length() > 0)
			parameters.put("hostname", this.hostname);
		if(this.memory > 0)
			parameters.put("memory", Integer.toString(this.memory));
		if(this.nameserver != null && this.nameserver.length() > 0)
			parameters.put("nameserver", this.nameserver);
		if(this.netif != null && this.netif.length() > 0)
			parameters.put("netif", this.netif);
		if(this.password != null && this.password.length() > 0)
			parameters.put("password", this.password);
		if(this.quotatime > 0)
			parameters.put("quotatime", Integer.toString(this.quotatime));
		if(this.quotaugidlimit > 0)
			parameters.put("quotaugidlimit", Integer.toString(this.quotaugidlimit));
		if(this.searchdomain != null && this.searchdomain.length() > 0)
			parameters.put("searchdomain", this.searchdomain);
		if(this.storage != null && this.storage.length() > 0)
			parameters.put("storage", this.storage);
		if(this.swap > 0)
			parameters.put("swap", Long.toString(this.swap));
		
		return parameters;
	}

	/* getter */
	
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
	
	public String getNode()
	{
		return this.node;
	}
	
	/* setter */
	
	public void setOstemplate(String ostemplate)
	{
		this.ostemplate = ostemplate;
	}
	
	public void setVmid(int vmid)
	{
		this.vmid = vmid;
	}
	
	public void setNode(String node)
	{
		this.node = node;
	}
}
