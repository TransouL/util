package util.semantic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This class use string to represent each component of triple.
 * <p>
 * NOTICE the uri should not have Invalid characters, for aligning with the NT
 * dump by D2RQ, we need encode as follow:
 * <p>
 * # 3 Turkey & Cheese Sandwich -> %23_3_Turkey_%26_Cheese_Sandwich
 * <p>
 * S'MORE -> S%27MORE
 * <p>
 * Sake / Fresh Salmon -> Sake_%2F_Fresh_Salmon
 * <p>
 * Sake (salmon) -> Sake_%28salmon%29
 * <p>
 * Salami, Mozzarella -> Salami%2C_Mozzarella
 * <p>
 * Shirazi Salad* -> Shirazi_Salad*
 * <p>
 * Soda - Sprite -> Soda_-_Sprite
 * <p>
 * Sodas (all flavors). -> Sodas_%28all_flavors%29.
 */
public class Triple {

	/**
	 * the subject of the triple
	 */
	private String subject;

	/**
	 * the predicate of the triple
	 */
	private String predicate;

	/**
	 * the object of the triple
	 */
	private String object;

	/**
	 * Construction function of Triple class.
	 *
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public Triple(String subject, String predicate, String object) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public static String encode(String uriPattern) {
		if (uriPattern == null || uriPattern.isEmpty()) {
			return uriPattern;
		}
		try {
			return URLEncoder.encode(uriPattern.trim().replace(" ", "_"),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Use blank to parse string to Triple object. Support some entity is
	 * anonynous resource(not begin with "<" and end with ">")
	 *
	 * @param str
	 * @return
	 */
	public static Triple parseTriple(String str) {
		return parseTriple(str, false);
	}

	/**
	 * Use blank to parse string to Triple object. Support some entity is
	 * anonynous resource(not begin with "<" and end with ">")
	 *
	 * @param str
	 * @param ifCheck
	 * @return
	 */
	public static Triple parseTriple(String str, boolean ifCheck) {
		return parseTriple(str, ' ', ifCheck);
	}

	/**
	 * Use blank to parse string to Triple object. Support some entity is
	 * anonynous resource(not begin with "<" and end with ">")
	 *
	 * @param str
	 * @param ifCheck
	 * @return
	 */
	public static Triple parseTriple(String str, char separator, boolean ifCheck) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		int firstBlank = str.indexOf(separator);
		if (firstBlank != -1) {
			String subject = str.substring(0, firstBlank);
			str = str.substring(firstBlank + 1);
			int secondBlank = str.indexOf(separator);
			if (secondBlank != -1) {
				String predicate = str.substring(0, secondBlank);
				String object = str
						.substring(secondBlank + 1, str.length() - 2);
				if (ifCheck) {
					if (str.charAt(str.length() - 2) != separator
							&& str.charAt(str.length() - 1) != '.') {
						return null;
					}
				}
				return new Triple(subject, predicate, object);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Use blank to parse string to Triple object. Support some entity is
	 * anonynous resource(not begin with "<" and end with ">")
	 *
	 * @param str
	 * @return
	 */
	public static Triple parseTripleTab(String str) {
		return parseTripleTab(str, false);
	}

	/**
	 * Use blank to parse string to Triple object. Support some entity is
	 * anonynous resource(not begin with "<" and end with ">")
	 *
	 * @param str
	 * @param ifCheck
	 * @return
	 */
	public static Triple parseTripleTab(String str, boolean ifCheck) {
		return parseTriple(str, '\t', ifCheck);
	}

	/**
	 * parse string to Triple object. And the subject and predicate are supposed
	 * to begin with "<" and end with ">", do not support anonynous resource
	 *
	 * @param str
	 * @return
	 */
	public static Triple parseTripleStandard(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		if (str.startsWith("<")) {
			if (str.contains(">")) {
				String subject = str.substring(0, str.indexOf(">") + 1);
				if (str.charAt(str.indexOf(">") + 1) != ' ') {
					return null;
				}
				str = str.substring(str.indexOf(">") + 2);

				if (str.startsWith("<")) {
					if (str.contains(">")) {
						String predicate = str.substring(0,
								str.indexOf(">") + 1);
						if (str.charAt(str.indexOf(">") + 1) != ' ') {
							return null;
						}
						String object = str.substring(str.indexOf(">") + 2,
								str.length() - 2);
						if (str.charAt(str.length() - 2) != ' '
								&& str.charAt(str.length() - 1) != '.') {
							return null;
						}
						return new Triple(subject, predicate, object);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the subject of triple.
	 *
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Set the subject of triple.
	 *
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Get the predict of the triple.
	 *
	 * @return the predicate
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * Set the predict of triple.
	 *
	 * @param predicate the predicate to set
	 */
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	/**
	 * Get the object of triple.
	 *
	 * @return the object
	 */
	public String getObject() {
		return object;
	}

	/**
	 * Set the object of triple.
	 *
	 * @param object the object to set
	 */
	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * Translate the triple into NT format.
	 *
	 * @return
	 */
	public String toNT() {
		if (subject == null || predicate == null || object == null
				|| subject.isEmpty() || predicate.isEmpty() || object.isEmpty()) {
			return null;
		} else {
			return subject + " " + predicate + " " + object + " .";
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Triple [subject=" + subject + ", predicate=" + predicate
				+ ", object=" + object + "]";
	}
}
