package com.ortega.vk.chatmanager.client;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

public class SimpleClientBuilder implements ClientBuilder {

	@Override
	public VkApiClient buildClient() {
		TransportClient tc = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(tc);
		return vk;
	}

}
