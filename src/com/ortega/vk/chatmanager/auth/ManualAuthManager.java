package com.ortega.vk.chatmanager.auth;
import java.awt.Desktop;
import java.net.URI;
import java.util.Scanner;

import com.ortega.vk.chatmanager.Constants;
import com.ortega.vk.chatmanager.Utils;

public class ManualAuthManager extends AuthManager {

	private static final String 
			AUTH_URL = "https://oauth.vk.com/authorize"
			+ "?client_id=%d"
			+ "&redirect_uri=%s"
			+ "&scope=%s";
	
	private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
	
	public ManualAuthManager(int clientId, String clientSecret) {
		super(clientId, clientSecret);
	}

	@Override
	protected String getCode() throws Exception {
		String url = String.format(AUTH_URL, clientId, REDIRECT_URI, Constants.SCOPE);
		Desktop.getDesktop().browse(new URI(url));
		
		Scanner sc = Utils.getConsoleScanner();
		
		System.out.print("Input the code you got: ");
		String code = sc.nextLine();
		
		return code;
	}

	@Override
	protected String getRedirectUri() {
		return REDIRECT_URI;
	}

}
