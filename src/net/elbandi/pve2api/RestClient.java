package net.elbandi.pve2api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

// Based on http://lukencode.com/2010/04/27/calling-web-services-in-android-using-httpclient/
public class RestClient {

	private boolean authentication;
	private ArrayList<NameValuePair> headers;

	private String jsonBody;
	private String message;

	private ArrayList<NameValuePair> params;
	private String response;
	private int responseCode;

	private String url;
	private static HttpClient client = new DefaultHttpClient();

	// HTTP Basic Authentication
	private String username;
	private String password;

	public enum RequestMethod {
		DELETE, GET, POST, PUT
	}

	public static final String SYS_PROP_SOCKS_PROXY_HOST = "socksProxyHost";
	public static final String SYS_PROP_SOCKS_PROXY_PORT = "socksProxyPort";

	public RestClient(String url) {
		this.url = url;
		try {
			SSLSocketFactory sslsf = new SSLSocketFactory(new TrustSelfSignedStrategy(),
					new AllowAllHostnameVerifier());
			Scheme https = new Scheme("https", 8006, sslsf);
			client.getConnectionManager().getSchemeRegistry().register(https);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// HttpHost proxy = new HttpHost("192.168.1.1", 8081);
		// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);
		// System.setProperty(SYS_PROP_SOCKS_PROXY_HOST, "192.168.1.1");
		// System.setProperty(SYS_PROP_SOCKS_PROXY_PORT, ""+1234);
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	// Be warned that this is sent in clear text, don't use basic auth unless
	// you have to.
	public void addBasicAuthentication(String user, String pass) {
		authentication = true;
		username = user;
		password = pass;
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void clearHeader() {
		headers.clear();
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void clearParams() {
		params.clear();
	}

	public void execute(RequestMethod method) throws Exception {
		switch (method) {
			case GET: {
				HttpGet request = new HttpGet(url + addGetParams());
				request = (HttpGet) addHeaderParams(request);
				executeRequest(request, url);
				break;
			}
			case POST: {
				HttpPost request = new HttpPost(url);
				request = (HttpPost) addHeaderParams(request);
				request = (HttpPost) addBodyParams(request);
				executeRequest(request, url);
				break;
			}
			case PUT: {
				HttpPut request = new HttpPut(url);
				request = (HttpPut) addHeaderParams(request);
				request = (HttpPut) addBodyParams(request);
				executeRequest(request, url);
				break;
			}
			case DELETE: {
				HttpDelete request = new HttpDelete(url);
				request = (HttpDelete) addHeaderParams(request);
				executeRequest(request, url);
			}
		}
	}

	private HttpUriRequest addHeaderParams(HttpUriRequest request) throws Exception {
		for (NameValuePair h : headers) {
			request.addHeader(h.getName(), h.getValue());
		}

		if (authentication) {
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
			request.addHeader(new BasicScheme().authenticate(creds, request));
		}

		return request;
	}

	private HttpUriRequest addBodyParams(HttpUriRequest request) throws Exception {
		if (jsonBody != null) {
			request.addHeader("Content-Type", "application/json");
			if (request instanceof HttpPost)
				((HttpPost) request).setEntity(new StringEntity(jsonBody, "UTF-8"));
			else if (request instanceof HttpPut)
				((HttpPut) request).setEntity(new StringEntity(jsonBody, "UTF-8"));

		} else if (!params.isEmpty()) {
			if (request instanceof HttpPost)
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			else if (request instanceof HttpPut)
				((HttpPut) request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		}
		return request;
	}

	private String addGetParams() throws Exception {
		StringBuffer combinedParams = new StringBuffer();
		if (!params.isEmpty()) {
			combinedParams.append("?");
			for (NameValuePair p : params) {
				combinedParams.append((combinedParams.length() > 1 ? "&" : "") + p.getName() + "="
						+ URLEncoder.encode(p.getValue(), "UTF-8"));
			}
		}
		return combinedParams.toString();
	}

	public String getErrorMessage() {
		return message;
	}

	public String getResponse() {
		return response;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setJSONString(String data) {
		jsonBody = data;
	}

	private void executeRequest(HttpUriRequest request, String url) {

		HttpParams params = client.getParams();

		// Setting 30 second timeouts
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
