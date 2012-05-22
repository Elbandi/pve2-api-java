package net.elbandi.pve2api.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class VmQemu {
	private float cpu;
	private int cpus;
	private float disk;
	private long diskread;
	private long diskwrite;
	private boolean ha;
	private long maxdisk;
	private long maxmem;
	private long mem;
	private String name;
	private long netin;
	private long netout;
	private int pid;
	private String status;
	private int uptime;

	private boolean acpi;
	private String[] boot;
	private String bootdisk;
	private int cores;
	private int cpuunits;
	private String desc;
	private String digest;
	private boolean freeze;
	private Map<Integer, String> ide = new HashMap<Integer, String>();
	private boolean kvm;
	private int memory;
	private Map<Integer, String> net = new HashMap<Integer, String>();
	private boolean onboot;
	private String ostype;
	private int sockets;
	private Map<Integer, String> scsi = new HashMap<Integer, String>();
	private Map<Integer, String> virtio = new HashMap<Integer, String>();

	public VmQemu(JSONObject data) throws JSONException {
		cpu = (float) data.getDouble("cpu");
		cpus = data.getInt("cpus");
		disk = (float) data.getDouble("disk");
		diskread = data.getLong("diskread");
		diskwrite = data.getLong("diskwrite");
		ha = data.optInt("ha") == 1;
		maxdisk = data.getLong("maxdisk");
		maxmem = data.getLong("maxmem");
		mem = data.getLong("mem");
		name = data.getString("name");
		netin = data.getLong("netin");
		netout = data.getLong("netout");
		pid = !data.getString("pid").equals("null") ? (Integer) JSONObject.stringToValue(data
				.getString("pid")) : 0;
		status = data.getString("status");
		uptime = data.getInt("uptime");
	}

	public void SetConfig(JSONObject data) throws JSONException {
		acpi = data.optInt("acpi", 1) == 1;
		cores = data.getInt("cores");
		cpuunits = data.optInt("cpuunits", 1000);
		desc = data.getString("description");
		bootdisk = data.optString("bootdisk");
		String bootdata = data.optString("boot", "cdn");
		int bc = bootdata.length();
		boot = new String[bc];
		for (int i = 0; i < bc; i++) {
			switch (bootdata.charAt(i)) {
			case 'c':
				if (!bootdisk.isEmpty())
					boot[i] = "Disk '" + bootdisk + "'";
				else
					boot[i] = "Disk";
				break;
			case 'd':
				boot[i] = "CD-ROM";
				break;
			case 'a':
				boot[i] = "Floppy";
				break;
			case 'n':
				boot[i] = "Network";
				break;
			default:
				boot[i] = Character.toString(bootdata.charAt(i));
				break;
			}
		}
		digest = data.getString("digest");
		freeze = data.optInt("freeze", 0) == 1;
		kvm = data.optInt("kvm", 1) == 1;
		memory = data.getInt("memory");
		onboot = data.getInt("onboot") == 1;
		sockets = data.getInt("sockets");
		ostype = getOsType(data.getString("ostype"));
		for (String k : JSONObject.getNames(data)) {
			if (k.startsWith("ide"))
				ide.put(Integer.parseInt(k.substring(3)), data.getString(k));
			else if (k.startsWith("net"))
				net.put(Integer.parseInt(k.substring(3)), data.getString(k));
			else if (k.startsWith("scsi"))
				scsi.put(Integer.parseInt(k.substring(4)), data.getString(k));
			else if (k.startsWith("virtio"))
				virtio.put(Integer.parseInt(k.substring(6)), data.getString(k));
		}
	}

	/*
	 * "boot" : "dnc", cdn c disk d, cdrom a, floppy n, network "bootdisk" :
	 * "virtio0",
	 */
	public static String getOsType(String name) {
		if (name.equals("wxp"))
			return "Microsoft Windows XP/2003";
		else if (name.equals("w2k"))
			return "Microsoft Windows 2000";
		else if (name.equals("w2k8"))
			return "Microsoft Windows Vista/2008";
		else if (name.equals("win7"))
			return "Microsoft Windows 7/2008r2";
		else if (name.equals("l24"))
			return "Linux 2.4 Kernel";
		else if (name.equals("l26"))
			return "Linux 3.X/2.6 Kernel";
		else
			return "Other OS types";
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

	public boolean isHa() {
		return ha;
	}

	public long getMaxdisk() {
		return maxdisk;
	}

	public long getMaxmem() {
		return maxmem;
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

	public int getPid() {
		return pid;
	}

	public String getStatus() {
		return status;
	}

	public int getUptime() {
		return uptime;
	}

	public boolean isAcpi() {
		return acpi;
	}

	public String[] getBoot() {
		return boot;
	}

	public String getBootdisk() {
		return bootdisk;
	}

	public int getCores() {
		return cores;
	}

	public int getCpuunits() {
		return cpuunits;
	}

	public String getDesc() {
		return desc;
	}

	public String getDigest() {
		return digest;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public Map<Integer, String> getIde() {
		return ide;
	}

	public boolean isKvm() {
		return kvm;
	}

	public int getMemory() {
		return memory;
	}

	public Map<Integer, String> getNet() {
		return net;
	}

	public boolean isOnboot() {
		return onboot;
	}

	public String getOstype() {
		return ostype;
	}

	public int getSockets() {
		return sockets;
	}

	public Map<Integer, String> getScsi() {
		return scsi;
	}

	public Map<Integer, String> getVirtio() {
		return virtio;
	}
}
