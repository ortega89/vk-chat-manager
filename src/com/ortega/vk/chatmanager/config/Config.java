package com.ortega.vk.chatmanager.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringJoiner;

public class Config {
	
	@Prop("proxy.host")
	public static String PROXY_HOST = "";
	
	@Prop("proxy.port")
	public static int PROXY_PORT = 0;
	
	@Prop("client.id")
	public static int CLIENT_ID = 0;
	
	@Prop("client.secret")
	public static String CLIENT_SECRET = "";
	
	@Prop("auth.email")
	public static String EMAIL = "";
	
	@Prop("auth.pass")
	public static String PASS = "";
	
	@Prop("report.chats")
	public static int[] CHATS = new int[] {12, 34};
	
	@Prop("report.kinds")
	public static String[] REPORT_TYPES = new String[] {"activity", "presence"};
	
	@Prop("report.days")
	public static int REPORT_DAYS = 30;
	
	public static boolean init(String fileName) throws Exception {
		Properties defaults = createDefaultProperties();
		Properties props = new Properties(defaults);
		
		try {
			props.load(new FileInputStream(fileName));
			initWithProperties(props);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void createDefaultConfig(String fileName) throws FileNotFoundException, IOException, Exception {
		createDefaultProperties().store(new FileOutputStream(fileName), "Configuration template");
	}
	
	public static boolean isReportRequired(String reportType) {
		return Arrays.asList(REPORT_TYPES).contains(reportType);
	}
	
	private static Properties createDefaultProperties() throws Exception {
		Properties defaults = new MyProperties();
		Field[] fields = Config.class.getDeclaredFields();
		for (Field field : fields) {
			Prop an = field.getAnnotation(Prop.class);
			if (an != null)
				defaults.setProperty(an.value(), fieldToString(field.get(null)));
		}
		return defaults;
	}
	
	private static void initWithProperties(Properties props) throws Exception {
		Field[] fields = Config.class.getDeclaredFields();
		for (Field field : fields) {
			Prop an = field.getAnnotation(Prop.class);
			if (an != null)
				initStaticField(field, props.getProperty(an.value()));
		}
	}
	
	private static void initStaticField(Field field, String rawValue) throws Exception {
		Class<?> type = field.getType();
		try {
			if (type == int.class) {
				field.set(null, Integer.parseInt(rawValue));
			} else if (type.isArray()) {
				String[] split = rawValue.split("\\s*,\\s*");
				Class<?> componentType = type.getComponentType();
				if (componentType == int.class) {
					int[] nums = new int[split.length];
					for (int i = 0; i < nums.length; i++)
						nums[i] = Integer.parseInt(split[i]);
					field.set(null, nums);
				} else if (componentType == String.class) {
					field.set(null, split);
				} else {
					throw new Exception("Unsupported array component type: "+
							type.getComponentType().getSimpleName());
				}
			} else if (type == String.class) {
				field.set(null, rawValue);
			} else {
				throw new Exception("Unsupported config field type: "+type.getSimpleName());
			}
		} catch (Throwable e) {
			throw new RuntimeException("Failed to init config field "+field.getName(), e);
		}
	}
	
	private static String fieldToString(Object obj) {
		if (obj.getClass().isArray()) {
			int length = Array.getLength(obj);
			StringJoiner sj = new StringJoiner(",");
		    for (int i = 0; i < length; i++) {
		        Object item = Array.get(obj, i);
		        sj.add(fieldToString(item));
		    }
		    return sj.toString();
		} else {
			return String.valueOf(obj);
		}
	}
}
