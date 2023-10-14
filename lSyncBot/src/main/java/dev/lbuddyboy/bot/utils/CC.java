package dev.lbuddyboy.bot.utils;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class CC {

	public static final String UNICODE_VERTICAL_BAR = StringEscapeUtils.unescapeJava("\u2503");
	public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
	public static final String UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
	public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("▶");
	public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
	public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");
	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");

	public static String format(String string, Object... format) {
		for (int i = 0; i < format.length; i += 2) {
			string = string.replace((String) format[i], String.valueOf(format[i + 1]));
		}
		return string;
	}

	public static List<String> format(List<String> strings, Object... format) {
		ArrayList<String> toAdd = new ArrayList<>();

		for (String string : strings) {
			for (int i = 0; i < format.length; i += 2) {
				toAdd.add(string.replace((String) format[i], String.valueOf(format[i + 1])));
			}
		}

		return toAdd;
	}

}
