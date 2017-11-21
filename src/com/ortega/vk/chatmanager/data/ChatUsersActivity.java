package com.ortega.vk.chatmanager.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.vk.api.sdk.objects.messages.Message;

public class ChatUsersActivity {
	public static final ChatUsersActivity DUMMY = new ChatUsersActivity(
			0, 0, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
	
	private int chatId;
	private int daysChecked;
	private List<Integer> activeUsers;
	private List<Integer> passiveUsers;
	private Map<Integer, Message> userMessages;
	
	public ChatUsersActivity(int chatId, int daysChecked,
			List<Integer> activeUsers, List<Integer> passiveUsers, Map<Integer, Message> userMessages) {
		this.chatId = chatId;
		this.daysChecked = daysChecked;
		this.activeUsers = activeUsers;
		this.passiveUsers = passiveUsers;
		this.userMessages = userMessages;
	}

	public int getChatId() {
		return chatId;
	}
	
	public int getDaysChecked() {
		return daysChecked;
	}
	
	public List<Integer> getActiveUsers() {
		return activeUsers;
	}

	public List<Integer> getPassiveUsers() {
		return passiveUsers;
	}

	public Map<Integer, Message> getUserMessages() {
		return userMessages;
	}
}
