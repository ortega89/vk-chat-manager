package com.ortega.vk.chatmanager.data;

import java.util.HashSet;
import java.util.Set;

public class UserPresence {
	private int userId;
	private Set<Integer> usedChats = new HashSet<>();
	private int rating;
	
	public UserPresence(int userId, ChatUsersPresence cup) {
		this.userId = userId;
		this.usedChats = cup.getUserPresence(userId);
		initRating(cup);
	}
	
	private void initRating(ChatUsersPresence cup) {
		rating = 0;
		int[] chatList = cup.getChatIds();
		for (int chatId : chatList) {
			rating *= 2;
			if (usedChats.contains(chatId))
				rating++;
		}
	}

	public int getUserId() {
		return userId;
	}

	public Set<Integer> getUsedChats() {
		return usedChats;
	}

	public int getRating() {
		return rating;
	}
}
