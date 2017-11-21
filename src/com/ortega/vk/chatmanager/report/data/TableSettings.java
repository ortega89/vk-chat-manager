package com.ortega.vk.chatmanager.report.data;

public class TableSettings {
	private String header;
	private String rowFormat;
	
	public TableSettings(String headerFormat, String chatCellFormat, int... chatIds) {
		StringBuilder header = new StringBuilder();
		StringBuilder row = new StringBuilder();
		
		int cellSize = String.format(chatCellFormat, "").length();
		
		for (int i = 0; i < chatIds.length; i++) {
			String chatStr = String.format(headerFormat, chatIds[i]);
			header.append(chatStr);
			int headerSize = chatStr.length();
			row.append(chatCellFormat);
			
			for (int j = cellSize; j < headerSize; j++)
				row.append(" ");
		}
		
		this.header = header.toString();
		this.rowFormat = row.toString();
	}
	
	public String getHeader() {
		return header;
	}
	
	public String getRow(Object[] data) {
		return String.format(rowFormat, data);
	}
}
