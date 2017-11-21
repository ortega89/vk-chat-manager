package com.ortega.vk.chatmanager.auth;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.UserAuthResponse;

public abstract class AuthManager {
	
	
	
	protected int clientId;
	private String clientSecret;
	
	public AuthManager(int clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	public UserActor getUserActor(VkApiClient vk, String scope) throws Exception {
		String code = getCode();
		
		UserAuthResponse uar = vk.oauth().userAuthorizationCodeFlow(
				clientId, clientSecret, getRedirectUri(), code)
				.execute();
		
		UserActor actor = new UserActor(uar.getUserId(), uar.getAccessToken());
		return actor;
	}
	
	protected abstract String getRedirectUri();

	protected abstract String getCode() throws Exception;
}
