package net.elbandi.pve2api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import net.elbandi.pve2api.data.Network;
import net.elbandi.pve2api.data.Node;
import net.elbandi.pve2api.data.Resource;
import net.elbandi.pve2api.data.Service;
import net.elbandi.pve2api.data.VmQemu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pve2Api {
	protected String pve_hostname;
	protected String pve_username;
	protected String pve_realm;
	protected String pve_password;

	private String pve_login_ticket;
	private String pve_login_token;
	private Date pve_login_ticket_timestamp;

	public Pve2Api(String pve_hostname, String pve_username, String pve_realm,
			String pve_password) {
		this.pve_hostname = pve_hostname;
		this.pve_username = pve_username;
		this.pve_password = pve_password;
		this.pve_realm = pve_realm;

		pve_login_ticket_timestamp = null;
	}

	public void login() throws JSONException, LoginException, IOException {
		RestClient client = new RestClient("https://" + pve_hostname
				+ ":8006/api2/json/access/ticket");
		client.addParam("username", pve_username);
		client.addParam("password", pve_password);
		client.addParam("realm", pve_realm);
		try {
			client.execute(RestClient.RequestMethod.POST);
		} catch (Exception e) {
			throw new IOException(e);
		}
		if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// Successfully connected
			JSONObject jObj = new JSONObject(client.getResponse());
			JSONObject data = jObj.getJSONObject("data");
			pve_login_ticket = data.getString("ticket");
			pve_login_token = data.getString("CSRFPreventionToken");
			pve_login_ticket_timestamp = new Date();
			return;
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
			throw new LoginException("Login failed. Please try again");
		} else {
			throw new IOException(client.getErrorMessage());
			// error connecting to server, lets just return an error
		}
	}

	private void pve_check_login_ticket() throws LoginException, JSONException,
			IOException {
		if (pve_login_ticket_timestamp == null
				|| pve_login_ticket_timestamp.getTime() >= (new Date())
						.getTime() - 3600) {
			login(); // shoud drop exception
		}
	}

	private JSONObject pve_action(String Path, RestClient.RequestMethod method,
			Map<String, String> data) throws JSONException, LoginException,
			IOException {
		pve_check_login_ticket();
		if (!Path.startsWith("/"))
			Path = "/".concat(Path);
		RestClient client = new RestClient("https://" + this.pve_hostname
				+ ":8006/api2/json" + Path);
		client.addHeader("Cookie", "CSRFPreventionToken=" + pve_login_token);
		client.addHeader("Cookie", "PVEAuthCookie=" + pve_login_ticket);
		if (data != null)
			for (Map.Entry<String, String> entry : data.entrySet()) {
				client.addParam(entry.getKey(), entry.getValue());
			}
		try {
			client.execute(RestClient.RequestMethod.POST);
		} catch (Exception e) {
			throw new IOException(e);
		}
		if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// Successfully connected
			return new JSONObject(client.getResponse());
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new LoginException(client.getErrorMessage());
		} else {
			throw new IOException(client.getErrorMessage());
			// error connecting to server, lets just return an error
		}
	}

	// TODO cluster adatok

	public List<Resource> getResources() throws JSONException, LoginException,
			IOException {
		List<Resource> res = new ArrayList<Resource>();
		JSONObject jObj = pve_action("/cluster/resources",
				RestClient.RequestMethod.GET, null);
		JSONArray data2;
		data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(Resource.createResource(data2.getJSONObject(i)));
		}
		return res;
	}

	public List<String> getNodeList() throws JSONException, LoginException,
			IOException {
		List<String> res = new ArrayList<String>();
		JSONObject jObj = pve_action("/nodes", RestClient.RequestMethod.GET,
				null);
		JSONArray data2;
		data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			JSONObject row = data2.getJSONObject(i);
			res.add(row.getString("node"));
		}
		return res;
	}

	public Node getNode(String name) throws JSONException, LoginException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + name + "/status",
				RestClient.RequestMethod.GET, null);
		JSONObject data2 = jObj.getJSONObject("data");
		return new Node(data2);
	}

	public List<Service> getNodeServices(String name) throws JSONException,
			LoginException, IOException {
		List<Service> res = new ArrayList<Service>();
		JSONObject jObj = pve_action("/nodes/" + name + "/services",
				RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new Service(data2.getJSONObject(i)));
		}
		return res;
	}

	public List<String> getNodeSyslog(String name) throws JSONException,
			LoginException, IOException {
		List<String> res = new ArrayList<String>();
		JSONObject jObj = pve_action("/nodes/" + name + "/syslog",
				RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(data2.getJSONObject(i).getString("t"));
		}
		return res;
	}

	public List<Network> getNodeNetwork(String name) throws JSONException,
			LoginException, IOException {
		List<Network> res = new ArrayList<Network>();
		JSONObject jObj = pve_action("/nodes/" + name + "/network",
				RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new Network(data2.getJSONObject(i)));
		}
		return res;
	}

	// node commands!!!

	public List<VmQemu> getQemuVMs(String node) throws JSONException,
			LoginException, IOException {
		List<VmQemu> res = new ArrayList<VmQemu>();
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu",
				RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new VmQemu(data2.getJSONObject(i)));
		}
		return res;
	}

	public VmQemu getQemuVM(String node, int vmid) throws JSONException,
			LoginException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid
				+ "/status/current", RestClient.RequestMethod.GET, null);
		return new VmQemu(jObj.getJSONObject("data"));
	}

	public void getQemuConfig(String node, int vmid, VmQemu vm)
			throws JSONException, LoginException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid
				+ "/config", RestClient.RequestMethod.GET, null);
		vm.SetConfig(jObj.getJSONObject("data"));
	}
}