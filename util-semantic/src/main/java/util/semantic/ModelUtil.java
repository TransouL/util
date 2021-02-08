package util.semantic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ModelUtil {

	private static final Log LOG = LogFactory.getLog(ModelUtil.class);

	/**
	 * build a model from content
	 *
	 * @param originalContent
	 * @param originalLang
	 * @param prefixes
	 * @return
	 */
	public static Model modeling(String originalContent, Lang originalLang,
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

}
