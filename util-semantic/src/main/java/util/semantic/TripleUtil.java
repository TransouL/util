package util.semantic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * This class provide utilities for semantic triples, like format conversion
 */
public class TripleUtil {
	private static final Log LOG = LogFactory.getLog(TripleUtil.class);

	/**
	 * build a model from content
	 *
	 * @param originalContent
	 * @param originalLang
	 * @param prefixes
	 * @return
	 */
	private static Model modeling(String originalContent, Lang originalLang,
								  Map<String, String> prefixes) {
		// should not include "|| originalContent.isEmpty()"
		if (originalContent == null) {
			return null;
		}
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(originalContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOG.error("The Character Encoding is not supported: "
					+ e.getMessage());
			return null;
		}

		Model model = ModelFactory.createDefaultModel();
		// model.setNsPrefixes(PrefixMapping.Standard);

		if (prefixes != null && !prefixes.isEmpty()) {
			model.setNsPrefixes(prefixes);
		}

		RDFDataMgr.read(model, bais, originalLang);
		// model.read(is, null, originalLangStr);
		try {
			bais.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * convert the original content as the expected language.
	 *
	 * @param originalContent
	 * @param originalLang
	 * @param expectLang
	 * @param prefixes
	 * @return
	 */
	public static String convert(String originalContent, Lang originalLang,
								 Lang expectLang, Map<String, String> prefixes) {
		Model model = modeling(originalContent, originalLang, prefixes);
		if (model == null) {
			LOG.error("build model failed");
		}
		String result = asText(model, expectLang);
		model.close();
		return result;
	}

	/**
	 * convert the original content as the expected language.
	 *
	 * @param originalContent
	 * @param originalLang
	 * @param expectLang
	 * @return
	 */
	public static String convert(String originalContent, Lang originalLang,
								 Lang expectLang) {
		return convert(originalContent, originalLang, expectLang, null);
	}

	/**
	 * convert the original content as the expected language.
	 *
	 * @param originalContent
	 * @param originalLangStr
	 * @param expectLangStr
	 * @param prefixes
	 * @return
	 */
	public static String convert(String originalContent,
								 String originalLangStr, String expectLangStr,
								 Map<String, String> prefixes) {
		Lang originalLang = RDFLanguages.nameToLang(originalLangStr);
		Lang expectLang = RDFLanguages.nameToLang(expectLangStr);
		return convert(originalContent, originalLang, expectLang, prefixes);
	}

	/**
	 * convert the original content as the expected language.
	 *
	 * @param originalContent
	 * @param originalLangStr
	 * @param expectLangStr
	 * @return
	 */
	public static String convert(String originalContent,
								 String originalLangStr, String expectLangStr) {
		return convert(originalContent, originalLangStr, expectLangStr, null);
	}

	/**
	 * Return a string that has the model represented as the specified language
	 *
	 * @param model
	 * @param lang  the specified language
	 * @return
	 */
	public static String asText(Model model, Lang lang) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RDFDataMgr.write(baos, model, lang);
		// model.write(baos, langStr);
		try {
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.debug("The Character Encoding is not supported: "
					+ e.getMessage());
			return null;
		}
	}

	/**
	 * Return a string that has the model represented as the specified language
	 * name(string)
	 *
	 * @param model
	 * @param langStr
	 * @return
	 */
	public static String asText(Model model, String langStr) {
		Lang lang = RDFLanguages.nameToLang(langStr);
		return asText(model, lang);
	}
}
