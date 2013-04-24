package net.elbandi.pve2api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import net.elbandi.pve2api.data.AplInfo;
import net.elbandi.pve2api.data.ClusterLog;
import net.elbandi.pve2api.data.Network;
import net.elbandi.pve2api.data.Node;
import net.elbandi.pve2api.data.Resource;
import net.elbandi.pve2api.data.Service;
import net.elbandi.pve2api.data.Storage;
import net.elbandi.pve2api.data.Task;
import net.elbandi.pve2api.data.VmOpenvz;
import net.elbandi.pve2api.data.VmQemu;
import net.elbandi.pve2api.data.VncData;

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

	public Pve2Api(String pve_hostname, String pve_username, String pve_realm, String pve_password) {
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
			pve_login_ticket_timestamp = new Date(); // decode from token
			return;
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
			throw new LoginException("Login failed. Please try again");
		} else {
			throw new IOException(client.getErrorMessage());
			// error connecting to server, lets just return an error
		}
	}

	public void pve_check_login_ticket() throws LoginException, JSONException, IOException {
		if (pve_login_ticket_timestamp == null
				|| pve_login_ticket_timestamp.getTime() >= (new Date()).getTime() - 3600) {
			login(); // shoud drop exception
		}
	}

	private JSONObject pve_action(String Path, RestClient.RequestMethod method, Map<String, String> data) throws JSONException, LoginException, IOException 
	{
		pve_check_login_ticket();
		if (!Path.startsWith("/"))
			Path = "/".concat(Path);
		RestClient client = new RestClient("https://" + this.pve_hostname + ":8006/api2/json" + Path);
		if (!method.equals(RestClient.RequestMethod.GET))
			client.addHeader("CSRFPreventionToken", pve_login_token);
		client.addHeader("Cookie", "PVEAuthCookie=" + pve_login_ticket);
		if (data != null)
			for (Map.Entry<String, String> entry : data.entrySet()) {
				client.addParam(entry.getKey(), entry.getValue());
			}
		try {
			client.execute(method);
		} catch (Exception e) {
			throw new IOException(e);
		}
		if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// Successfully connected
			return new JSONObject(client.getResponse());
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			throw new LoginException(client.getErrorMessage());
		} else if (client.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
			// TODO: find a better exception
			throw new IOException(client.getErrorMessage());
		} else {
			throw new IOException(client.getErrorMessage());
			// error connecting to server, lets just return an error
		}
	}

	// TODO cluster adatok
	public List<ClusterLog> getClusterLog() throws JSONException, LoginException, IOException {
		List<ClusterLog> res = new ArrayList<ClusterLog>();
		JSONObject jObj = pve_action("/cluster/log", RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new ClusterLog(data2.getJSONObject(i)));
		}
		return res;
	}

	public List<Resource> getResources() throws JSONException, LoginException, IOException {
		List<Resource> res = new ArrayList<Resource>();
		JSONObject jObj = pve_action("/cluster/resources", RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(Resource.createResource(data2.getJSONObject(i)));
		}
		return res;
	}

	public List<Task> getTasks() throws JSONException, LoginException, IOException {
		List<Task> res = new ArrayList<Task>();
		JSONObject jObj = pve_action("/cluster/tasks", RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new Task(data2.getJSONObject(i)));
		}
		return res;
	}

	// TODO: getOptions
	// TODO: setOptions
	// TODO: getClusterStatus

	public List<String> getNodeList() throws JSONException, LoginException, IOException {
		List<String> res = new ArrayList<String>();
		JSONObject jObj = pve_action("/nodes", RestClient.RequestMethod.GET, null);
		JSONArray data2;
		data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			JSONObject row = data2.getJSONObject(i);
			res.add(row.getString("node"));
		}
		return res;
	}

	public Node getNode(String name) throws JSONException, LoginException, IOException {
		JSONObject jObj = pve_action("/nodes/" + name + "/status", RestClient.RequestMethod.GET,
				null);
		JSONObject data2 = jObj.getJSONObject("data");
		return new Node(data2);
	}

	public List<Service> getNodeServices(String name) throws JSONException, LoginException,
			IOException {
		List<Service> res = new ArrayList<Service>();
		JSONObject jObj = pve_action("/nodes/" + name + "/services", RestClient.RequestMethod.GET,
				null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new Service(data2.getJSONObject(i)));
		}
		return res;
	}

	public List<AplInfo> getNodeAppliances(String node) throws JSONException, LoginException,
			IOException {
		List<AplInfo> res = new ArrayList<AplInfo>();
		JSONObject jObj = pve_action("/nodes/" + node + "/aplinfo", RestClient.RequestMethod.GET,
				null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new AplInfo(data2.getJSONObject(i)));
		}
		return res;
	}

	public String downloadAppliances(String node, String storage, String template)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/aplinfo", RestClient.RequestMethod.POST,
				new PveParams("storage", storage).Add("template", template));
		return jObj.getString("data");
	}

	public String startNodeVMs(String node) throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/startall", RestClient.RequestMethod.POST,
				null);
		return jObj.getString("data");
	}

	public String stopNodeVMs(String node) throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/stopall", RestClient.RequestMethod.POST,
				null);
		return jObj.getString("data");
	}

	public List<String> getNodeSyslog(String name) throws JSONException, LoginException,
			IOException {
		List<String> res = new ArrayList<String>();
		JSONObject jObj = pve_action("/nodes/" + name + "/syslog", RestClient.RequestMethod.GET,
				null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(data2.getJSONObject(i).getString("t"));
		}
		return res;
	}

	public List<Network> getNodeNetwork(String name) throws JSONException, LoginException,
			IOException {
		List<Network> res = new ArrayList<Network>();
		JSONObject jObj = pve_action("/nodes/" + name + "/network", RestClient.RequestMethod.GET,
				null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new Network(data2.getJSONObject(i)));
		}
		return res;
	}

	public VncData shellNode(String node) throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/vncshell", RestClient.RequestMethod.POST,
				null);
		return new VncData(jObj.getJSONObject("data"));
	}

	public List<Storage> getStorages() throws JSONException, LoginException, IOException {
		List<Storage> res = new ArrayList<Storage>();
		JSONObject jObj = pve_action("/storage", RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(Storage.createStorage(data2.getJSONObject(i)));
		}
		return res;
	}

	public void createStorage(Storage storage) throws LoginException, JSONException, IOException {
		Map<String, String> data = storage.getCreateParams();
		pve_action("/storage", RestClient.RequestMethod.POST, data);
	}

	public void updateStorage(Storage storage) throws LoginException, JSONException, IOException {
		Map<String, String> data = storage.getUpdateParams();
		pve_action("/storage/" + storage.getStorage(), RestClient.RequestMethod.PUT, data);
	}

	public void deleteStorage(String storage) throws LoginException, JSONException, IOException {
		pve_action("/storage/" + storage, RestClient.RequestMethod.DELETE, null);
	}

	public List<VmQemu> getQemuVMs(String node) throws JSONException, LoginException, IOException {
		List<VmQemu> res = new ArrayList<VmQemu>();
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu", RestClient.RequestMethod.GET, null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new VmQemu(data2.getJSONObject(i)));
		}
		return res;
	}

	public VmQemu getQemuVM(String node, int vmid) throws JSONException, LoginException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/current",
				RestClient.RequestMethod.GET, null);
		return new VmQemu(jObj.getJSONObject("data"));
	}

	public void getQemuConfig(String node, int vmid, VmQemu vm) throws JSONException,
			LoginException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/config",
				RestClient.RequestMethod.GET, null);
		vm.SetConfig(jObj.getJSONObject("data"));
	}

	public String startQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/start",
				RestClient.RequestMethod.POST, null);
		// FIXME: already running?
		return jObj.getString("data");
	}

	protected String stopQemu(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/stop",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	public String stopQemu(String node, int vmid) throws LoginException, JSONException, IOException {
		return stopQemu(node, vmid, null);
	}

	public String stopQemu(String node, int vmid, boolean keepActive) throws LoginException,
			JSONException, IOException {
		return stopQemu(node, vmid, new PveParams("keepActive", keepActive));
	}

	public String stopQemu(String node, int vmid, int timeout) throws LoginException,
			JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return stopQemu(node, vmid, new PveParams("timeout", timeout));
	}

	public String stopQemu(String node, int vmid, int timeout, boolean keepActive)
			throws LoginException, JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return stopQemu(node, vmid, new PveParams("timeout", timeout).Add("keepActive", keepActive));
	}

	public String resetQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/reset",
				RestClient.RequestMethod.POST, null);
		return jObj.getString("data");
	}

	// TODO: QemuCreate(String node, int vmid, params)
	// TODO: QemuUpdate(String node, int vmid, params) PUT

	protected String deleteQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid,
				RestClient.RequestMethod.DELETE, null);
		return jObj.getString("data");
	}

	protected String unlinkQemu(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/unlink",
				RestClient.RequestMethod.PUT, data);
		return jObj.getString("data");
	}

	public String unlinkQemu(String node, int vmid, String idlist) throws LoginException,
			JSONException, IOException {
		return unlinkQemu(node, vmid, new PveParams("idlist", idlist));
	}

	public String unlinkQemu(String node, int vmid, String idlist, boolean force)
			throws LoginException, JSONException, IOException {
		return unlinkQemu(node, vmid, new PveParams("idlist", idlist).Add("force", force));
	}

	protected String shutdownQemu(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/shutdown",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	public String shutdownQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		return shutdownQemu(node, vmid, null);
	}

	public String shutdownQemu(String node, int vmid, int timeout) throws LoginException,
			JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return shutdownQemu(node, vmid, new PveParams("timeout", timeout));
	}

	public String shutdownQemu(String node, int vmid, int timeout, boolean keepActive)
			throws LoginException, JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return shutdownQemu(node, vmid,
				new PveParams("timeout", timeout).Add("keepActive", keepActive));
	}

	public String shutdownQemu(String node, int vmid, int timeout, boolean keepActive,
			boolean forceStop) throws LoginException, JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return shutdownQemu(
				node,
				vmid,
				new PveParams("timeout", timeout).Add("keepActive", keepActive).Add("forceStop",
						forceStop));
	}

	public VncData consoleQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/vncproxy",
				RestClient.RequestMethod.POST, null);
		return new VncData(jObj.getJSONObject("data"));
	}

	public String suspendQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/suspend",
				RestClient.RequestMethod.POST, null);
		return jObj.getString("data");
	}

	public String resumeQemu(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/status/resume",
				RestClient.RequestMethod.POST, null);
		return jObj.getString("data");
	}

	public String sendkeyQemu(String node, int vmid, String key) throws LoginException,
			JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/sendkey",
				RestClient.RequestMethod.PUT, new PveParams("key", key));
		return jObj.getString("data");
	}

	protected String migrateQemu(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/migrate",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	public String migrateQemu(String node, int vmid, String target) throws LoginException,
			JSONException, IOException {
		return migrateQemu(node, vmid, new PveParams("target", target));
	}

	public String migrateQemu(String node, int vmid, String target, boolean online)
			throws LoginException, JSONException, IOException {
		return migrateQemu(node, vmid, new PveParams("target", target).Add("online", online));
	}

	public String migrateQemu(String node, int vmid, String target, boolean online, boolean force)
			throws LoginException, JSONException, IOException {
		return migrateQemu(node, vmid,
				new PveParams("target", target).Add("online", online).Add("force", force));
	}

	public String monitorQemu(String node, int vmid, String command) throws LoginException,
			JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/qemu/" + vmid + "/monitor",
				RestClient.RequestMethod.POST, new PveParams("command", command));
		return jObj.getString("data");
	}

	public List<VmOpenvz> getOpenvzCTs(String node) throws JSONException, LoginException,
			IOException {
		List<VmOpenvz> res = new ArrayList<VmOpenvz>();
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz", RestClient.RequestMethod.GET,
				null);
		JSONArray data2 = jObj.getJSONArray("data");
		for (int i = 0; i < data2.length(); i++) {
			res.add(new VmOpenvz(data2.getJSONObject(i)));
		}
		return res;
	}

	public VmOpenvz getOpenvzCT(String node, int vmid) throws JSONException, LoginException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/status/current",
				RestClient.RequestMethod.GET, null);
		return new VmOpenvz(jObj.getJSONObject("data"));
	}

	public void getOpenvzConfig(String node, int vmid, VmOpenvz vm) throws JSONException,
			LoginException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/config",
				RestClient.RequestMethod.GET, null);
		vm.SetConfig(jObj.getJSONObject("data"));
	}
	
	
	public String createOpenvz(VmOpenvz vm) throws LoginException, JSONException, IOException 
	{
		return createOpenvz(vm.getNode(), vm);
	}
	
	public String createOpenvz(String node, VmOpenvz vm) throws LoginException, JSONException, IOException 
	{
		Map<String, String> parameterData = vm.getCreateParams();
		System.out.println(parameterData.toString());
		String path = "/nodes/" + node + "/openvz";
		JSONObject jsonObject = pve_action(path, RestClient.RequestMethod.POST, parameterData);
		return jsonObject.getString("data");
	}
	
	public String updateOpenvz(String node, VmOpenvz vm) throws LoginException, JSONException, IOException 
	{
		return createOpenvz(node, vm);
	}

	protected Map<Integer, String> initlogOpenvz(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/initlog",
				RestClient.RequestMethod.GET, data);
		JSONArray data2 = jObj.getJSONArray("data");
		Map<Integer, String> res = new HashMap<Integer, String>();
		for (int i = 0; i < data2.length(); i++) {
			JSONObject o = data2.getJSONObject(i);
			res.put(o.getInt("n"), o.getString("t"));
		}
		return res;
	}

	public Map<Integer, String> initlogOpenvz(String node, int vmid, int start)
			throws LoginException, JSONException, IOException {
		if (start < 0)
			throw new IllegalArgumentException("Start paramter need to be positive");
		return initlogOpenvz(node, vmid, new PveParams("start", start));
	}

	public Map<Integer, String> initlogOpenvz(String node, int vmid, int start, int limit)
			throws LoginException, JSONException, IOException {
		if (start < 0)
			throw new IllegalArgumentException("Start paramter need to be positive");
		if (limit < 0)
			throw new IllegalArgumentException("Limit paramter need to be positive");
		return initlogOpenvz(node, vmid, new PveParams("start", start).Add("limit", limit));
	}

	protected String deleteOpenvz(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid,
				RestClient.RequestMethod.DELETE, null);
		return jObj.getString("data");
	}

	public VncData consoleOpenvz(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/vncproxy",
				RestClient.RequestMethod.POST, null);
		return new VncData(jObj.getJSONObject("data"));
	}

	// TODO: status/ubc, ???

	public String startOpenvz(String node, int vmid) throws LoginException, JSONException,
			IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/status/start",
				RestClient.RequestMethod.POST, null);
		// FIXME: already running?
		return jObj.getString("data");
	}

	protected String stopOpenvz(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/status/stop",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	protected String mountOpenvz(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/status/mount",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	protected String umountOpenvz(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/status/umount",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	protected String shutdownOpenvz(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/status/shutdown",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	public String shutdownOpenvz(String node, int vmid) throws LoginException, JSONException,
			IOException {
		return shutdownOpenvz(node, vmid, null);
	}

	public String shutdownOpenvz(String node, int vmid, int timeout) throws LoginException,
			JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return shutdownOpenvz(node, vmid, new PveParams("timeout", timeout));
	}

	public String shutdownOpenvz(String node, int vmid, int timeout, boolean forceStop)
			throws LoginException, JSONException, IOException {
		if (timeout < 0)
			throw new IllegalArgumentException("Timeout paramter need to be positive");
		return shutdownOpenvz(node, vmid,
				new PveParams("timeout", timeout).Add("forceStop", forceStop));
	}

	protected String migrateOpenvz(String node, int vmid, Map<String, String> data)
			throws LoginException, JSONException, IOException {
		JSONObject jObj = pve_action("/nodes/" + node + "/openvz/" + vmid + "/migrate",
				RestClient.RequestMethod.POST, data);
		return jObj.getString("data");
	}

	public String migrateOpenvz(String node, int vmid, String target) throws LoginException,
			JSONException, IOException {
		return migrateOpenvz(node, vmid, new PveParams("target", target));
	}

	public String migrateOpenvz(String node, int vmid, String target, boolean online)
			throws LoginException, JSONException, IOException {
		return migrateOpenvz(node, vmid, new PveParams("target", target).Add("online", online));
	}

	// TODO: refactor to use BasicNameValuePair
	public static class PveParams extends HashMap<String, String> {
		private static final long serialVersionUID = 1L;

		public PveParams(String key, String value) {
			Add(key, value);
		}

		public PveParams(String key, int value) {
			Add(key, value);
		}

		public PveParams(String key, boolean value) {
			Add(key, value);
		}

		public PveParams Add(String key, String value) {
			if (value != null)
				put(key, value);
			return this;
		}

		public PveParams Add(String key, int value) {
			put(key, "" + value);
			return this;
		}

		public PveParams Add(String key, boolean value) {
			put(key, value ? "1" : "0");
			return this;
		}
	}
}