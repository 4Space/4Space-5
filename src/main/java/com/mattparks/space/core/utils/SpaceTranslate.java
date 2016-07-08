package com.mattparks.space.core.utils;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.StatCollector;

/**
 * A class for translating strings using minecrafts lang loader.
 */
public class SpaceTranslate {
	/**
	 * Translates from a key.
	 * 
	 * @param key The key to translate from.
	 * 
	 * @return The translated value.
	 */
	public static String translate(String key) {
		String result = StatCollector.translateToLocal(key);
		int comment = result.indexOf('#');
		return (comment > 0) ? result.substring(0, comment).trim() : result;
	}

	/**
	 * Reads a translated value into a string array.
	 * 
	 * @param key The key to translate from.
	 * 
	 * @return The translated list.
	 */
	public static List<String> translateWithSplit(String key) {
		String translated = translate(key);
		int comment = translated.indexOf('#');
		translated = (comment > 0) ? translated.substring(0, comment).trim() : translated;
		return Arrays.asList(translated.split("\\$"));
	}

	/**
	 * Translates a key using values.
	 * 
	 * @param key The key to translate from.
	 * @param values Values to use when translating.
	 * 
	 * @return The translated value.
	 */
	public static String translateWithFormat(String key, Object... values) {
		String result = StatCollector.translateToLocalFormatted(key, values);
		int comment = result.indexOf('#');
		return (comment > 0) ? result.substring(0, comment).trim() : result;
	}
}
