package util.common;

import java.util.HashSet;
import java.util.Map;

/**
 * provide several methods which is refined for safety or convenience
 */
public class RefinedMethods {

	/**
	 * put into the map with a check, if the map previously contains a mapping
	 * value for the key, then add to the original value set instead of
	 * replacing
	 *
	 * @param map      the target map
	 * @param key      the key
	 * @param valueSet the value set
	 */
	public static void putCheck(Map<String, HashSet<String>> map, String key,
								HashSet<String> valueSet) {
		if (!map.containsKey(key)) {
			map.put(key, valueSet);
		} else {
			map.get(key).addAll(valueSet);
		}
	}

	/**
	 * put into the map with a check, if the map previously contains a mapping
	 * value for the key, then add to the original value set instead of
	 * replacing
	 *
	 * @param map      the target map
	 * @param key      the key
	 * @param valueSet the value set
	 */
	public static void putCheck(Map<Object, HashSet<Object>> map, Object key,
								HashSet<Object> valueSet) {
		if (!map.containsKey(key)) {
			map.put(key, valueSet);
		} else {
			map.get(key).addAll(valueSet);
		}
	}

	/**
	 * put into the map with a check, if the map previously contains a mapping
	 * value for the key, then add to the original value set instead of
	 * replacing
	 *
	 * @param map   the target map
	 * @param key   the key
	 * @param value the value
	 */
	public static void putCheck(Map<String, HashSet<String>> map, String key,
								String value) {
		if (!map.containsKey(key)) {
			HashSet<String> valueSet = new HashSet<String>();
			valueSet.add(value);
			map.put(key, valueSet);
		} else {
			map.get(key).add(value);
		}
	}

	/**
	 * put into the map with a check, if the map previously contains a mapping
	 * value for the key, then add to the original value set instead of
	 * replacing
	 *
	 * @param map   the target map
	 * @param key   the key
	 * @param value the value
	 */
	public static void putCheck(Map<Object, HashSet<Object>> map, Object key,
								Object value) {
		if (!map.containsKey(key)) {
			HashSet<Object> valueSet = new HashSet<Object>();
			valueSet.add(value);
			map.put(key, valueSet);
		} else {
			map.get(key).add(value);
		}
	}
}
