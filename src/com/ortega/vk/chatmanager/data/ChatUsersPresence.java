package com.ortega.vk.chatmanager.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatUsersPresence {
	
	private int[] chatIds;
	private Map<Integer, Set<Integer>> usersByChat = new HashMap<>();
	
	public ChatUsersPresence(int... chatIds) {
		this.chatIds = chatIds;
		Arrays.stream(chatIds)
				.forEach(chatId -> usersByChat.put(chatId, new HashSet<>()));
	}
	
	public void addUser(int chatId, int userId) {
		usersByChat.get(chatId).add(userId);
	}
	
	public void addAll(int chatId, Collection<Integer> userIds) {
		usersByChat.get(chatId).addAll(userIds);
	}
	
	public int[] getChatIds() {
		return chatIds;
	}
	
	public Set<Integer> getUserPresence(int userId) {
		Set<Integer> up = new HashSet<>();
		//Collect IDs of chats where userId is present 
		usersByChat.entrySet().forEach(chat -> {
			if (chat.getValue().contains(userId))
				up.add(chat.getKey());
		});
		return up;
	}
	
	public Set<Integer> getAllUsers() {
		Set<Integer> allUsers = new HashSet<>();
		usersByChat.values().forEach(chatUsers -> allUsers.addAll(chatUsers));
		return allUsers;
	}
}
