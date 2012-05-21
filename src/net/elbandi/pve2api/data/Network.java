package net.elbandi.pve2api.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Network {
	boolean autostart;
	String bridge_ports;
	String iface;
	String method;
	String address;
	String netmask;
	String gateway;
	String type;

	public Network(JSONObject data) throws JSONException {
		autostart = data.getInt("autostart") == 1;
		bridge_ports = data.optString("bridge_ports");
		iface = data.getString("iface");
		address = data.optString("address");
		netmask = data.optString("netmask");
		gateway = data.optString("gateway");
		type = data.getString("type");
		method = data.getString("method");
	}

	public boolean isAutostart() {
		return autostart;
	}

	public String getBridge_ports() {
		return bridge_ports;
	}

	public String getIface() {
		return iface;
	}

	public String getMethod() {
		return method;
	}

	public String getAddress() {
		return address;
	}

	public String getNetmask() {
		return netmask;
	}

	public String getGateway() {
		return gateway;
	}

	public String getType() {
		return type;
	}
}
