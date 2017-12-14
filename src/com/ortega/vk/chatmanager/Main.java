package com.ortega.vk.chatmanager;

import com.ortega.vk.chatmanager.data.ChatUsersActivity;
import com.ortega.vk.chatmanager.data.ChatUsersPresence;
import com.ortega.vk.chatmanager.operator.VkChatOperator;
import com.ortega.vk.chatmanager.report.FileReporter;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import java.util.Arrays;

import com.ortega.vk.chatmanager.analyzer.ChatMessagesAnalyzer;
import com.ortega.vk.chatmanager.analyzer.ChatUsersPresenceAnalyzer;
import com.ortega.vk.chatmanager.auth.AuthManager;
import com.ortega.vk.chatmanager.auth.AutoAuthManager;
import com.ortega.vk.chatmanager.auth.ManualAuthManager;
import com.ortega.vk.chatmanager.client.ClientBuilder;
import com.ortega.vk.chatmanager.client.ProxyClientBuilder;
import com.ortega.vk.chatmanager.client.SimpleClientBuilder;
import com.ortega.vk.chatmanager.config.Config;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		if (!Config.init(Constants.CONFIG_FILE)) {
			Config.createDefaultConfig(Constants.CONFIG_FILE);
			System.err.println("Default config file created: "+Constants.CONFIG_FILE);
			System.err.println("Please fill it with your settings before the next run");
			return;
		}
		
		ClientBuilder builder;
		
		if (Config.PROXY_HOST.isEmpty()) {
			System.out.println("Proxy settings missing, building a simple VK client...");
			builder = new SimpleClientBuilder();
		} else {
			System.out.println("Proxy settings provided, building a proxy VK client...");
			builder = new ProxyClientBuilder(Config.PROXY_HOST, Config.PROXY_PORT);
		}
		
		VkApiClient vk = builder.buildClient();
		
		AuthManager auth;
		if (Config.EMAIL.isEmpty() || Config.PASS.isEmpty()) {
			System.out.println("Email/password missing, applying manual authentication...");
			auth = new ManualAuthManager(Config.CLIENT_ID, Config.CLIENT_SECRET);
		} else {
			System.out.println("Email/password provided, applying automatic authentication...");
			auth = new AutoAuthManager(Config.CLIENT_ID, Config.CLIENT_SECRET, Config.EMAIL, Config.PASS);
		}
		
		System.out.println();
		
		UserActor actor = auth.getUserActor(vk, Constants.SCOPE);
		
		VkChatOperator op = new VkChatOperator(vk, actor);
		FileReporter reporter = new FileReporter(op);
		
		if (Config.isReportRequired("activity")) {
			ChatMessagesAnalyzer cma = new ChatMessagesAnalyzer(op);
			for (int chatId : Config.CHATS) {
				System.out.println("Collecting users activity data for chat "+chatId);
				ChatUsersActivity cua = cma.getActiveAndPassiveUsers(chatId, Config.REPORT_DAYS);
				System.out.println("Making a report...");
				reporter.reportActiveAndPassiveUsers(cua);
				System.out.println("Done");
				System.out.println();
			}
		}
		
		if (Config.isReportRequired("presence")) {
			System.out.println("Collecting users presence data for chats "+Arrays.toString(Config.CHATS));
			ChatUsersPresenceAnalyzer cupa = new ChatUsersPresenceAnalyzer(op);
			ChatUsersPresence cup = cupa.getUsersPresence(Config.CHATS);
			System.out.println("Making a report...");
			reporter.reportUsersPresence(cup);
			System.out.println("Done");
			System.out.println();
		}
		
		System.out.println("Check reports directory for results");
	}	
}
