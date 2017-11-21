package com.ortega.vk.chatmanager.analyzer;

import static com.ortega.vk.chatmanager.Utils.daysAgo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ortega.vk.chatmanager.data.ChatUsersActivity;
import com.ortega.vk.chatmanager.operator.VkChatOperator;
import com.vk.api.sdk.objects.messages.Action;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.UserXtrCounters;

public class ChatMessagesAnalyzer {
	private VkChatOperator operator;
	
	public ChatMessagesAnalyzer(VkChatOperator operator) {
		this.operator = operator;
	}
	
	public ChatUsersActivity getActiveAndPassiveUsers(int chatId, int daysToCheck) throws Exception {
		Map<Integer, UserXtrCounters> usersMap = operator.loadChatUsers(chatId);
		List<Message> messages = operator.getMessagesAfter(daysAgo(daysToCheck), chatId);
				
		if (messages.isEmpty())
			return ChatUsersActivity.DUMMY;

		Map<Integer, Message> lastUserMessages = new LinkedHashMap<>();
		
		for (Message msg : messages) {
			if (!lastUserMessages.containsKey(msg.getUserId()))
				lastUserMessages.put(msg.getUserId(), msg);

			Action action = msg.getAction();
			if (action != null) {
				if (msg.getUserId() != msg.getActionMid())
					if (!lastUserMessages.containsKey(msg.getActionMid()))
						lastUserMessages.put(msg.getActionMid(), msg);
			}
		}
		
		List<Integer> activeUsers = new ArrayList<>(lastUserMessages.keySet());
		List<Integer> passiveUsers = new ArrayList<>(usersMap.keySet());
		
		activeUsers.retainAll(usersMap.keySet());		
		passiveUsers.removeAll(activeUsers);
		
		sortUsersByName(passiveUsers);
		
		return new ChatUsersActivity(chatId, daysToCheck, activeUsers, passiveUsers, lastUserMessages);
	}
	
	public void sortUsersByName(List<Integer> userIds) {
		userIds.sort((a, b) -> operator.getUserName(a).compareTo(operator.getUserName(b)));
	}
}
