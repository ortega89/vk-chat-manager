package com.ortega.vk.chatmanager.operator;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;

public abstract class VkOperator {
	protected VkApiClient vk;
	protected UserActor actor;
	
	public VkOperator(VkApiClient vk, UserActor actor) {
		this.vk = vk;
		this.actor = actor;
	}
}
