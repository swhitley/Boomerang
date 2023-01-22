package com.whitleymedia;

public class CustomUtil {

	public static String replaceBraces(String str) {
		if (str == null || str.equalsIgnoreCase("null")) {
			return "";
		}
		if (str.indexOf("<?xml") >= 0) {
			str = str.substring(str.indexOf('\n')+1);
		}
		str = str.replace("{{", "<xsl:value-of select=\"wd:");
		str = str.replace("}}", "\"/>");
		str = str.replace("wd:wd:", "wd:");
		return str;
	}

}
