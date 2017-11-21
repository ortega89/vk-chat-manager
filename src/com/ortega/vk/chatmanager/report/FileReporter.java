package com.ortega.vk.chatmanager.report;

import static com.ortega.vk.chatmanager.Utils.dateTime;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ortega.vk.chatmanager.Utils;
import com.ortega.vk.chatmanager.data.ChatUsersActivity;
import com.ortega.vk.chatmanager.data.ChatUsersPresence;
import com.ortega.vk.chatmanager.data.UserPresence;
import com.ortega.vk.chatmanager.operator.VkChatOperator;
import com.ortega.vk.chatmanager.report.data.TableSettings;

public class FileReporter {
	private VkChatOperator operator;
	
	public FileReporter(VkChatOperator operator) {
		this.operator = operator;
	}
	
	public void reportHead(PrintStream out, Date reportDate, int... chatIds) throws Exception {
		out.println("VK Chat Manager");
		out.println();
		out.printf("Дата отчета: %s%n", dateTime(reportDate));
		if (chatIds.length == 1)
			out.printf("Чат c%d \"%s\"%n", chatIds[0], operator.getChat(chatIds[0]).getTitle());
		else if (chatIds.length > 1) {
			out.println();
			out.println("Чаты:");
			for (int chatId : chatIds)
				out.printf("* c%d \"%s\"%n", chatId, operator.getChat(chatId).getTitle());
		}
		out.println();
	}
	
	public void reportActiveAndPassiveUsers(ChatUsersActivity cua) throws Exception {
		Date reportDate = new Date();
		PrintStream out = createPrintStream(cua.getChatId(), reportDate);
		reportHead(out, reportDate, cua.getChatId());

		if (cua == ChatUsersActivity.DUMMY) {
			out.println("Не удалось сформировать отчет активности пользователей");
			return;
		}
		
		out.println("Активных пользователей за последние "+cua.getDaysChecked()+" дней: "+cua.getActiveUsers().size());
		out.println("Пассивных пользователей за последние "+cua.getDaysChecked()+" дней: "+cua.getPassiveUsers().size());
		
		out.println();
		out.println("Активные пользователи:");
		out.println();
		for (int i = 0; i < cua.getActiveUsers().size(); i++) {
			out.printf("%3d) %s%n", 
					i+1, 
					operator.getMessageDescription(cua.getUserMessages().get(cua.getActiveUsers().get(i))));
		}
		
		out.println();
		out.println("Пассивные пользователи:");
		out.println();
		for (int i = 0; i < cua.getPassiveUsers().size(); i++) {
			out.printf("%3d) %s | %s%n", 
					i+1, 
					operator.getUserName(cua.getPassiveUsers().get(i)), 
					operator.getUserURL(cua.getPassiveUsers().get(i)));
		}
		
		out.close();
	}
	
	public void reportUsersPresence(ChatUsersPresence cup) throws Exception {
		Date reportDate = new Date();
		PrintStream out = createPrintStream("presence", reportDate);
		
		int[] chatIds = cup.getChatIds();
		reportHead(out, reportDate, chatIds);
		
		List<UserPresence> users = getUserPresenceList(cup);
		
		out.println("Присутствие пользователей в чатах:");
		out.println();
		
		TableSettings ts = new TableSettings("c%d  ", "[%1s]", chatIds);
		
		out.println(ts.getHeader());
		out.println();
		
		for (UserPresence up : users) {
			Object[] presence = Arrays.stream(chatIds)
					.mapToObj(chatId -> up.getUsedChats().contains(chatId) ? "x" : "")
					.collect(Collectors.toList()).toArray(new String[0]);
			
			out.println(ts.getRow(presence)+" "+operator.getUserName(up.getUserId()));
		}
		
		out.close();
	}
	
	private List<UserPresence> getUserPresenceList(ChatUsersPresence cup) {
		List<Integer> userIds = new ArrayList<>(cup.getAllUsers());
		List<UserPresence> userPresenceList = userIds.stream()
				.map(id -> new UserPresence(id, cup))
				.collect(Collectors.toList());
		
		userPresenceList.sort((a, b) -> {
			int deltaRating = b.getRating() - a.getRating();
			if (deltaRating != 0)
				return deltaRating;
			return operator.getUserName(a.getUserId()).compareTo(operator.getUserName(b.getUserId()));
		});
		
		return userPresenceList;
	}
	
	private PrintStream createPrintStream(int chatId, Date reportDate) throws Exception {
		return createPrintStream(String.valueOf(chatId), "c"+chatId, reportDate);
	}
	
	private PrintStream createPrintStream(String prefix, Date reportDate) throws Exception {
		return createPrintStream(prefix, prefix, reportDate);
	}
	
	private PrintStream createPrintStream(String dir, String prefix, Date reportDate) throws Exception {
		File outputDir = new File("reports/"+dir);
		if (!outputDir.isDirectory())
			outputDir.mkdirs();
		File outputFile = new File(outputDir, 
				String.format("%s - %s.txt", prefix, Utils.dateTimeForFileName(reportDate)));
		PrintStream filePrintStream = new PrintStream(outputFile, "UTF-8");
		return filePrintStream;
	}
}
