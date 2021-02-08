package util.semantic;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import java.util.HashMap;

public class OntologyCommon {

	public static final String LANG_EN = "en";

	public static final HashMap<String, Resource> BASE_TYPES;

	static {
		BASE_TYPES = new HashMap<>();
		BASE_TYPES.put(XSD.xstring.getURI(), XSD.xstring);
		BASE_TYPES.put(XSD.decimal.getURI(), XSD.decimal);
		BASE_TYPES.put(XSD.xboolean.getURI(), XSD.xboolean);
		BASE_TYPES.put(XSD.xbyte.getURI(), XSD.xbyte);
		BASE_TYPES.put(XSD.xdouble.getURI(), XSD.xdouble);
		BASE_TYPES.put(XSD.xfloat.getURI(), XSD.xfloat);
		BASE_TYPES.put(XSD.xint.getURI(), XSD.xint);
		BASE_TYPES.put(XSD.xlong.getURI(), XSD.xlong);
		BASE_TYPES.put(XSD.xshort.getURI(), XSD.xshort);
		BASE_TYPES.put(XSD.date.getURI(), XSD.date);
		BASE_TYPES.put(XSD.dateTime.getURI(), XSD.dateTime);
		BASE_TYPES.put(XSD.duration.getURI(), XSD.duration);
		BASE_TYPES.put(XSD.integer.getURI(), XSD.integer);
		BASE_TYPES.put(XSD.language.getURI(), XSD.language);
		BASE_TYPES.put(XSD.time.getURI(), XSD.time);
		BASE_TYPES.put(XSD.dateTime.getURI(), XSD.dateTime);
		BASE_TYPES.put(XSD.anyURI.getURI(), XSD.anyURI);

		// !!
		BASE_TYPES.put(OWL2.Thing.getURI(), OWL2.Thing);
		BASE_TYPES.put(OWL2.Nothing.getURI(), OWL2.Nothing);
		BASE_TYPES.put(OWL2.Class.getURI(), OWL2.Class);
		BASE_TYPES.put(OWL2.OntologyProperty.getURI(), OWL2.OntologyProperty);
		BASE_TYPES.put(OWL2.DatatypeProperty.getURI(), OWL2.DatatypeProperty);
		BASE_TYPES.put(OWL2.ObjectProperty.getURI(), OWL2.ObjectProperty);

		BASE_TYPES.put(RDF.Property.getURI(), RDF.Property);

		BASE_TYPES.put(RDFS.Resource.getURI(), RDFS.Resource);
		BASE_TYPES.put(RDFS.Literal.getURI(), RDFS.Literal);
		BASE_TYPES.put(RDFS.Datatype.getURI(), RDFS.Datatype);
	}
}
