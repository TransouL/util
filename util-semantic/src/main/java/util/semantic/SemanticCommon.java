package util.semantic;

import org.apache.jena.riot.RDFLanguages;

/**
 * This class provide common information for all semantic utility classes
 */
public class SemanticCommon {

	/**
	 * the default limit for virtuoso triple store
	 */
	public static final int VIRTUOSO_LIMIT = 10000;

	// string formats with writer in jena(Model.write API)
	/**
	 * @deprecated Use {@link RDFLanguages#strLangRDFXML}
	 */
	@Deprecated
	public static final String FORMAT_RDF_XML = "RDF/XML";
	/**
	 * NT, N-TRIPLE, N-TRIPLEs are same in jena
	 *
	 * @deprecated Use {@link RDFLanguages#strLangNTriples}
	 */
	@Deprecated
	public static final String FORMAT_RDF_N_TRIPLE = "N-TRIPLE";
	/**
	 * @deprecated Use {@link RDFLanguages#strLangJSONLD}
	 */
	@Deprecated
	public static final String FORMAT_RDF_JSON_LD = "JSON-LD";
	/**
	 * @deprecated Use {@link RDFLanguages#strLangRDFJSON}
	 */
	@Deprecated
	public static final String FORMAT_RDF_RDF_JSON = "RDF/JSON";

	public static final String FORMAT_RDF_XML_ABBREV = "RDF/XML-ABBREV";
	/**
	 * N3, TTL, Turtle are same in jena
	 *
	 * @deprecated Use {@link RDFLanguages#strLangTurtle}
	 */
	@Deprecated
	public static final String FORMAT_RDF_TURTLE = "TURTLE";

	// the other semantic formats without writer in jena
	/**
	 * @deprecated Use {@link RDFLanguages#strLangNQuads}
	 */
	@Deprecated
	public static final String FORMAT_RDF_N_QUADS = "N-NQUADS";
	/**
	 * @deprecated Use {@link RDFLanguages#strLangTriG}
	 */
	@Deprecated
	public static final String FORMAT_RDF_TRIG = "TRIG";
}
