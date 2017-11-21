package com.ortega.vk.chatmanager.operator;

import static com.ortega.vk.chatmanager.Utils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ortega.vk.chatmanager.Constants;
import com.ortega.vk.chatmanager.key.UserIdCase;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.Sex;
import com.vk.api.sdk.objects.messages.Action;
import com.vk.api.sdk.objects.messages.Chat;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetHistoryResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.users.UsersNameCase;

public class VkChatOperator extends VkOperator {

	protected Map<Integer, UserXtrCounters> usersMap = new HashMap<>();
	protected Map<UserIdCase, String> userNames = new HashMap<>();
	
	public VkChatOperator(VkApiClient vk, UserActor actor) {
		super(vk, actor);
	}
	
	public Chat getChat(int chatId) throws Exception {
		Chat chat = vk.messages().getChat(actor)
				 .chatId(chatId)
				 .execute();
		sleepAfterRequest();
		return chat;
	}
	
	public String getMessageDescription(Message msg) throws Exception {
		Action action = msg.getAction();
		if (action != null)
			return String.format("%s %s %s %s", 
					dateTime(msg.getDate()), 
					getUserName(msg.getActionMid()),
					translateAction(action.getValue(), msg.getActionMid()),
					getUserNameWithCase(msg.getUserId(), UsersNameCase.INSTRUMENTAL));
		else
			return String.format("%s %s: %s", 
					dateTime(msg.getDate()), 
					getUserName(msg.getUserId()), 
					msg.getBody());
	}
	
	public String translateAction(String action, int userId) {
		UserXtrCounters user = getUser(userId);
		boolean isFemale = user != null && user.getSex() == Sex.FEMALE;
		if (action == null)
			return isFemale ? "как-то обработана" : "как-то обработан";
		
		switch (action) {
		case "chat_invite_user": return isFemale ? "приглашена" : "приглашен";
		case "chat_kick_user": return getUser(userId).getSex() == Sex.FEMALE ? "исключена" : "исключен";
		default: return action;
		}
	}
	
	public UserXtrCounters getUser(int userId) {
		UserXtrCounters user = usersMap.get(userId);
		return user;
	}
	
	public String getUserName(int userId) {
		UserXtrCounters user = usersMap.get(userId);
		return user != null ? getUserName(user) : ("id" + userId);
	}
	
	public String getUserName(UserXtrCounters user) {
		String fullName = user.getFirstName()+" "+user.getLastName();
		return fullName;
	}
	
	public String getUserNameWithCase(int userId, UsersNameCase theCase) throws Exception {
		if (theCase == UsersNameCase.NOMINATIVE) {
			return getUserName(userId);
		} else {
			UserIdCase key = new UserIdCase(userId, theCase);
			if (userNames.containsKey(key)) {
				return userNames.get(key);
			} else {
				List<UserXtrCounters> users = vk.users().get(actor)
						.nameCase(theCase)
						.fields(UserField.SCREEN_NAME)
						.userIds(String.valueOf(userId))
						.execute();
				sleepAfterRequest();
				if (users.size() != 1)
					return null;
				String userName = getUserName(users.get(0));
				userNames.put(key, userName);
				return userName;
			}
		}
	}
	
	public String getUserURL(int userId) {
		UserXtrCounters user = usersMap.get(userId);
		return getUserURL(user);
	}
	
	public String getUserURL(UserXtrCounters user) {
		String screenName = user.getScreenName();
		return screenName == null ? "нет страницы" : ("https://vk.com/"+screenName);
	}
	
	public Map<Integer, UserXtrCounters> loadChatUsers(int chatId) throws Exception {
		Chat chat = getChat(chatId);
		List<String> userIds = chat.getUsers().stream()
				.map(id -> String.valueOf(id))
				.collect(Collectors.toList());
		sleepAfterRequest();
		
		List<UserXtrCounters> users = vk.users().get(actor)
				.userIds(userIds)
				.fields(UserField.SCREEN_NAME)
				.execute();
		sleepAfterRequest();
		
		Map<Integer, UserXtrCounters> usersMap = users.stream()
				.collect(Collectors.toMap(u -> ((UserXtrCounters)u).getId(), u -> u));
		this.usersMap.putAll(usersMap);
		
		return usersMap;
	}
	
	public List<Message> getMessagesAfter(Date dateLimit, int chatId) throws Exception {
		long dateLimitMillis = dateLimit.getTime();
		List<Message> messages = new ArrayList<>();
		
		int offset = 0;
		do {
			GetHistoryResponse resp = vk.messages().getHistory(actor)
					.peerId(Constants.CHAT_PEER_OFFSET + chatId)
					.offset(offset)
					.count(Constants.MESSAGES_PER_REQUEST)
					.execute();
			sleepAfterRequest();
			
			offset += Constants.MESSAGES_PER_REQUEST;
			int oldCount = messages.size();
			messages.addAll(resp.getItems().stream()
					.filter(m -> secToMillis(m.getDate()) > dateLimitMillis)
					.collect(Collectors.toList()));
			if (messages.size() - oldCount != Constants.MESSAGES_PER_REQUEST)
				break;
		} while (true);
		
		return messages;
	}
	
	public void sleepAfterRequest() throws InterruptedException {
		Thread.sleep(Constants.REQUEST_DELAY);
	}
}
