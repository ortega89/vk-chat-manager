package com.ortega.vk.chatmanager.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.ortega.vk.chatmanager.Constants;

public class AutoAuthManager extends AuthManager {
	
	private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
	
	private String email;
	private String pass;
	
	public AutoAuthManager(int clientId, String clientSecret, String email, String pass) {
		super(clientId, clientSecret);
		this.email = email;
		this.pass = pass;
	}
	
	@Override
	protected String getRedirectUri() {
		return REDIRECT_URI;
	}
	
	/**
	 * Thanks to <a href="https://vk.com/p0l0z0v">Alexander Polozov</a> for this code!
	 */
	@Override
	protected String getCode() throws Exception {
		try {
			HttpClient httpClient = HttpClients.custom()
					.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
	
			HttpResponse response = httpClient.execute(new HttpGet(
					"https://oauth.vk.com/authorize"
					+ "?client_id=" + clientId
					+ "&display=page"
					+ "&redirect_uri="+REDIRECT_URI
					+ "&scope="+Constants.SCOPE
					+ "&response_type=code"
					+ "&v=5.69"));
	
			String loginPageSource = EntityUtils.toString(response.getEntity());
	
			Matcher mIp_h = Pattern.compile("(?:<input.*name=\"ip_h\" value=\"(.*)\")").matcher(loginPageSource);
			Matcher mLg_h = Pattern.compile("(?:<input.*name=\"lg_h\" value=\"(.*)\")").matcher(loginPageSource);
			Matcher mTo = Pattern.compile("(?:<input.*name=\"to\" value=\"(.*)\")").matcher(loginPageSource);
			String ip_h = mIp_h.find() ? mIp_h.group(1) : "";
			String lg_h = mLg_h.find() ? mLg_h.group(1) : "";
			String to = mTo.find() ? mTo.group(1) : "";
	
			HttpPost post = new HttpPost("https://login.vk.com/?act=login&soft=1");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("ip_h", ip_h));
			nvps.add(new BasicNameValuePair("lg_h", lg_h));
			nvps.add(new BasicNameValuePair("to", to));
			nvps.add(new BasicNameValuePair("_origin", "https://oauth.vk.com"));
			nvps.add(new BasicNameValuePair("email", email));
			nvps.add(new BasicNameValuePair("pass", pass));
			post.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpClient.execute(post);
	
			response = httpClient.execute(new HttpPost(response.getFirstHeader("Location").getValue()));
			response = httpClient.execute(new HttpPost(response.getFirstHeader("Location").getValue()));
	
			return response.getFirstHeader("Location").getValue().replaceAll("(?:.*code=([a-z0-9]*).*)", "$1");
		} catch (Throwable e) {
			throw new RuntimeException("Auto auth failed, please verify your config file", e);
		}
	}

}
