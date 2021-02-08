package util.semantic;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.InvalidPropertyURIException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.resultset.RDFOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.out.NodeFmtLib;
import util.common.RefinedMethods;

import java.io.*;
import java.util.*;

/**
 * This class provides utilities for sparql query ResultSet.
 *
 * @since 2014-08
 */
public class ResultSetUtil {

	/**
	 * logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(ResultSetUtil.class);

	/**
	 * Parse the returned result set, and get the mapping of input varKey and
	 * varValue. If the result set contains duplicate varKey, the later one will
	 * replace the early one.(in this case, parseAsMapStringSet() may be
	 * suitable)
	 *
	 * @param resultSet sparql query result set.
	 * @param varKey    the key item need to get from the sparql select items.
	 * @param varValue  the value item need to get from the sparql select items.
	 * @return
	 */
	public static HashMap<Object, Object> parseAsMapObjectObject(ResultSet resultSet, String varKey,
																 String varValue) {
		HashMap<Object, Object> result = new HashMap<Object, Object>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();

			Object key = getValueParsed(solution, varKey);
			Object value = getValueParsed(solution, varValue);
			if (key != null && value != null) {
				result.put(key, value);
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the mapping of input varKey and
	 * varValue. If the result set contains duplicate varKey, the later one will
	 * replace the early one.(in this case, parseAsMapStringSet() may be
	 * suitable)
	 *
	 * @param resultSet sparql query result set.
	 * @param varKey    the key item need to get from the sparql select items.
	 * @param varValue  the value item need to get from the sparql select items.
	 * @return
	 */
	public static HashMap<String, String> parseAsMapStringString(ResultSet resultSet, String varKey,
																 String varValue) {
		HashMap<String, String> result = new HashMap<String, String>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object key = getValueParsed(solution, varKey);
			Object value = getValueParsed(solution, varValue);
			if (key != null && value != null) {
				result.put(key.toString(), value.toString());
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the mapping of input varKey and
	 * varValue set. All the varValue for the same varKey will store in a set,
	 * then the duplicate varKey record will never be lost.
	 *
	 * @param resultSet sparql query result set.
	 * @param varKey    the key item need to get from the sparql select items.
	 * @param varValue  the value item need to get from the sparql select items.
	 * @return
	 */
	public static HashMap<Object, HashSet<Object>> parseAsMapObjectSet(ResultSet resultSet, String varKey,
																	   String varValue) {
		HashMap<Object, HashSet<Object>> result = new HashMap<Object, HashSet<Object>>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object key = getValueParsed(solution, varKey);
			Object value = getValueParsed(solution, varValue);
			if (key != null && value != null) {
				RefinedMethods.putCheck(result, key, value);
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the mapping of input varKey and
	 * varValue set. All the varValue for the same varKey will store in a set,
	 * then the duplicate varKey record will never be lost.
	 *
	 * @param resultSet sparql query result set.
	 * @param varKey    the key item need to get from the sparql select items.
	 * @param varValue  the value item need to get from the sparql select items.
	 * @return
	 */
	public static HashMap<String, HashSet<String>> parseAsMapStringSet(ResultSet resultSet, String varKey,
																	   String varValue) {
		HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object key = getValueParsed(solution, varKey);
			Object value = getValueParsed(solution, varValue);
			if (key != null && value != null) {
				RefinedMethods.putCheck(result, key.toString(), value.toString());
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the value list of input var.
	 *
	 * @param resultSet sparql query result set.
	 * @param var       the item need to get from the sparql select items.
	 * @return
	 */
	public static ArrayList<Object> parseAsListObject(ResultSet resultSet, String var) {
		ArrayList<Object> result = new ArrayList<Object>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object value = getValueParsed(solution, var);
			if (value != null) {
				result.add(value);
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the value list of input var.
	 *
	 * @param resultSet sparql query result set.
	 * @param var       the item need to get from the sparql select items.
	 * @return
	 */
	public static ArrayList<String> parseAsListString(ResultSet resultSet, String var) {
		ArrayList<String> result = new ArrayList<String>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object value = getValueParsed(solution, var);
			if (value != null) {
				result.add(value.toString());
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the value set of input var.
	 *
	 * @param resultSet sparql query result set.
	 * @param var       the item need to get from the sparql select items.
	 * @return
	 */
	public static HashSet<Object> parseAsSetObject(ResultSet resultSet, String var) {
		HashSet<Object> result = new HashSet<Object>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object value = getValueParsed(solution, var);
			if (value != null) {
				result.add(value);
			}
		}
		return result;
	}

	/**
	 * Parse the returned result set, and get the value set of input var.
	 *
	 * @param resultSet sparql query result set.
	 * @param var       the item need to get from the sparql select items.
	 * @return
	 */
	public static HashSet<String> parseAsSetString(ResultSet resultSet, String var) {
		HashSet<String> result = new HashSet<String>();
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Object value = getValueParsed(solution, var);
			if (value != null) {
				result.add(value.toString());
			}
		}
		return result;
	}

	/**
	 * get the display form of specified var in the QuerySolution
	 *
	 * @param solution
	 * @param var
	 * @return
	 */
	private static String getValueDisplay(QuerySolution solution, String var) {
		// return FmtUtils.stringForRDFNode(solution.get(var), null);
		// return NodeFmtLib.str(solution.get(var).asNode(), null, null);
		return NodeFmtLib.displayStr(solution.get(var));

		// RDFNode node = solution.get(var);
		// if (node.isURIResource()) {
		// return "<" + node.asResource().getURI() + ">";
		// } else if (node.isAnon()) {
		// return node.toString();
		// } else if (node.isLiteral()) {
		// String dataTypeURI = node.asLiteral().getDatatypeURI();
		// if (dataTypeURI.equals(XSD.integer.getURI())) {
		// } else if (dataTypeURI.equals(XSD.xdouble.getURI())) {
		// } else {
		// }
		// return node.toString();
		// } else {
		// return node.toString();
		// }
	}

	/**
	 * get the parsed value of specified var in the QuerySolution
	 *
	 * @param solution
	 * @param var
	 * @return
	 */
	private static Object getValueParsed(QuerySolution solution, String var) {
		RDFNode node = solution.get(var);
		if (node == null) {
			return null;
		}
		try {
			if (node.isURIResource()) {
				return "<" + node.asResource().getURI() + ">";
			} else if (node.isAnon()) {
				return node.toString();
			} else if (node.isLiteral()) {
				return node.asLiteral().getValue();
			} else {
				return node.toString();
			}
		} catch (DatatypeFormatException e) {
			LOG.error(e);
			return null;
		}
	}

	/**
	 * Parse the returned sparql query result, and get the value of input var.
	 *
	 * @param resultSet sparql query result set, supposed to have 1 row.
	 * @param var       the item need to get from the sparql select items.
	 * @return
	 */
	public static long parseLong(ResultSet resultSet, String var) {
		long result = Long.MIN_VALUE;
		if (resultSet.hasNext()) {
			try {
				QuerySolution querySolution = resultSet.next();
				String tmpStr = querySolution.get(var).toString();
				result = Long.parseLong(tmpStr.substring(0, tmpStr.indexOf(("^^"))));
			} catch (Exception e) {
				e.printStackTrace();
				result = Long.MIN_VALUE;
			}
		}
		return result;
	}

	/**
	 * Parse the returned sparql query result, and get the value of input var.
	 *
	 * @param resultSet sparql query result set, supposed to have 1 row.
	 * @param var       the item need to get from the sparql select items.
	 * @return
	 */
	public static double parseDouble(ResultSet resultSet, String var) {
		double result = Double.NaN;
		if (resultSet.hasNext()) {
			try {
				QuerySolution querySolution = resultSet.next();
				String tmpStr = querySolution.get(var).toString();
				result = Double.parseDouble(tmpStr.substring(0, tmpStr.indexOf(("^^"))));
			} catch (Exception e) {
				e.printStackTrace();
				result = Double.NaN;
			}
		}
		return result;
	}

	/**
	 * display the result set to System.out using ResultSetFormatter with
	 * <b>display format</b>
	 *
	 * @param resultSet the jena query ResultSet, to be output
	 */
	public static void display(ResultSet resultSet) {
		if (resultSet == null) {
			LOG.error("the resultSet is null");
		} else {
			ResultSetFormatter.out(resultSet);
		}

	}

	/**
	 * display the result set to the specified output file with <b>display
	 * format</b>
	 *
	 * @param resultSet the jena query ResultSet, to be output
	 */
	public static void display(ResultSet resultSet, boolean withVarNames, String outputFileName,
							   boolean append) {
		if (resultSet == null || outputFileName == null) {
			LOG.error("the resultSet or output file is null");
		} else {
			FileWriter fw = null;
			BufferedWriter bw = null;
			try {
				fw = new FileWriter(outputFileName, append);
				bw = new BufferedWriter(fw);
				List<String> varNames = resultSet.getResultVars();
				if (withVarNames) {
					for (int i = 0; i < varNames.size(); i++) {
						if (i < varNames.size() - 1) {
							bw.write(varNames.get(i) + "\t");
						} else {
							bw.write(varNames.get(i));
						}
					}
					bw.newLine();
				}

				while (resultSet.hasNext()) {
					QuerySolution qs = resultSet.next();
					for (int i = 0; i < varNames.size(); i++) {
						String var = varNames.get(i);
						if (i < varNames.size() - 1) {
							bw.write(getValueDisplay(qs, var) + "\t");
						} else {
							bw.write(getValueDisplay(qs, var));
						}
					}
					bw.newLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (fw != null) {
						fw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * output the resultSet to the OutputStream in the specified language. by
	 * default, if the parameter out is null, will output to Syetem.out; and if
	 * the parameter lang is null, will output as N-TRIPLES.
	 * <p>
	 * using the original model encoded from the result set. Not equals to 3
	 * variables from the resultSet can also be handled
	 *
	 * @param resultSet
	 * @param out
	 * @param lang
	 * @return
	 */
	public static int writeOriginal(ResultSet resultSet, OutputStream out, String lang) {
		if (resultSet == null) {
			LOG.error("the resultSet is null");
			return -1;
		}
		if (lang == null) {
			lang = "N-TRIPLES";
		}
		if (out == null) {
			out = System.out;
		}

		// deprecated
		// Model model = ResultSetFormatter.toModel(resultSet);
		Model model = RDFOutput.encodeAsModel(resultSet);
		RDFDataMgr.write(out, model, RDFLanguages.nameToLang(lang));
		// model.write(out, lang);
		model.close();

		return 0;
	}

	/**
	 * output the resultSet, by default(Syetem.out, N-TRIPLES).
	 * <p>
	 * using the original model encoded from the result set. Not equals to 3
	 * variables from the resultSet can also be handled
	 *
	 * @param resultSet
	 * @return
	 */
	public static int writeOriginal(ResultSet resultSet) {
		return writeOriginal(resultSet, null, null);
	}

	/**
	 * output the resultSet to file in the specified language.
	 *
	 * @param resultSet
	 * @param outputFileName
	 * @param append
	 * @param lang
	 * @return
	 */
	public static int writeOriginalToFile(ResultSet resultSet, String outputFileName, boolean append,
										  String lang) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFileName, append);
			return writeOriginal(resultSet, fos, lang);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * convert the resultSet to string,
	 * <p>
	 * using the original model encoded from the result set. Not equals to 3
	 * variables from the resultSet can also be handled
	 *
	 * @param resultSet
	 * @return
	 */
	public static String asTextOriginal(ResultSet resultSet, String lang) {
		// deprecated
		// Model model = ResultSetFormatter.toModel(resultSet);
		Model model = RDFOutput.encodeAsModel(resultSet);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RDFDataMgr.write(baos, model, RDFLanguages.nameToLang(lang));
		model.close();
		// model.write(baos, lang);
		try {
			return baos.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.debug("The Character Encoding is not supported: " + e.getMessage());
			return null;
		}
	}

	/**
	 * output data in the resultSet to the OutputStream as the parameters config
	 * <p>
	 * subjectVar can't be null
	 * <p>
	 * predicateStr_objectVar and predicateVar_objectVar can't be both null
	 * <p>
	 * prefixes can be used, define in the map prefixes, the key is the prefix,
	 * and the value is the original entire uri. It could be null or empty
	 * <p>
	 * The language in which to write the model is specified by the lang
	 * argument. Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE",
	 * "TURTLE", (and "TTL") and "N3". The default value, represented by null,
	 * is "RDF/XML".
	 *
	 * @param resultSet              the jena query ResultSet, to be output
	 * @param subjectVar             the variable to be the subject of each QuerySolution in the
	 *                               resultSet
	 *                               <p>
	 * @param predicateStr_objectVar the 1st kind of predicate & object mapping
	 *                               <p>
	 *                               key(predicate) -> user defined string (please don't add the
	 *                               <>, e.g. http://www.w3.org/1999/02/22-rdf-syntax-ns#type)
	 *                               <p>
	 *                               value(object) -> variable in the resultSet
	 *                               <p>
	 * @param predicateVar_objectVar the 2rd kind of predicate & object mapping
	 *                               <p>
	 *                               key(predicate) -> variable in the resultSet
	 *                               <p>
	 *                               value(object) -> variable in the resultSet
	 * @param prefixes               the prefixes define the prefix that can be used to add to the
	 *                               triples
	 * @param out                    The output stream to which the result is written.
	 *                               <p>
	 *                               for example: System.out;(by default) fos = new
	 *                               FileOutputStream("out.nt");
	 * @param lang                   The output language
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeTriple(ResultSet resultSet, String subjectVar,
								  Map<String, String> predicateStr_objectVar, Map<String, String> predicateVar_objectVar,
								  Map<String, String> prefixes, OutputStream out, String lang) {
		if (resultSet == null) {
			LOG.error("the resultSet is null");
			return -1;
		}

		if (subjectVar == null || (predicateStr_objectVar == null && predicateVar_objectVar == null)) {
			LOG.error("subjectVar or both of predicateStr_objectVar and predicateVar_objectVar can't be null");
			return -1;
		}

		List<String> vars = resultSet.getResultVars();
		if (!vars.contains(subjectVar)) {
			LOG.error("expected variable not found: " + subjectVar);
			return -1;
		}

		if ((predicateStr_objectVar != null && !vars.containsAll(predicateStr_objectVar.values()))
				|| (predicateVar_objectVar != null && (!vars.containsAll(predicateVar_objectVar.keySet()) || !vars
				.containsAll(predicateVar_objectVar.values())))) {
			LOG.error("expected var(s) not found");
			return -1;
		}

		if (lang == null) {
			lang = "N-TRIPLES";
		}

		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(PrefixMapping.Standard);

		if (prefixes != null && !prefixes.isEmpty()) {
			model.setNsPrefixes(prefixes);
		}

		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			Resource subject = solution.getResource(subjectVar);
			if (predicateStr_objectVar != null) {
				for (String predicateStr : predicateStr_objectVar.keySet()) {
					Property predicate = null;
					try {
						predicate = ResourceFactory.createProperty(predicateStr);
					} catch (InvalidPropertyURIException e) {
						LOG.error("create property failed", e);
					}
					if (predicate != null) {
						RDFNode object = solution.get(predicateStr_objectVar.get(predicateStr));
						model.add(subject, predicate, object);
					} else {
						return -1;
					}
				}
			}

			if (predicateVar_objectVar != null) {
				for (String predicateVar : predicateVar_objectVar.keySet()) {
					Property predicate = null;
					try {
						predicate = ResourceFactory.createProperty(solution.get(predicateVar).toString());
					} catch (InvalidPropertyURIException e) {
						LOG.error("exception during creating property:", e);
					}
					if (predicate != null) {
						RDFNode object = solution.get(predicateVar_objectVar.get(predicateVar));
						model.add(subject, predicate, object);
					} else {
						return -1;
					}
				}
			}
		}

		if (out == null) {
			out = System.out;
		}

		RDFDataMgr.write(out, model, RDFLanguages.nameToLang(lang));
		// model.write(out, lang);
		model.close();

		return 0;
	}

	/**
	 * output data in the resultSet to the outputFile as the parameters config
	 * <p>
	 * subjectVar can't be null
	 * <p>
	 * predicateStr_objectVar and predicateVar_objectVar can't be both null
	 * <p>
	 * prefixes can be used, define in the map prefixes, the key is the prefix,
	 * and the value is the original entire uri. It could be null or empty
	 * <p>
	 * The language in which to write the model is specified by the lang
	 * argument. Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE",
	 * "TURTLE", (and "TTL") and "N3". The default value, represented by null,
	 * is "RDF/XML".
	 *
	 * @param resultSet              the jena query ResultSet, to be output
	 * @param subjectVar             the variable to be the subject of each QuerySolution in the
	 *                               resultSet
	 *                               <p>
	 * @param predicateStr_objectVar the 1st kind of predicate & object mapping
	 *                               <p>
	 *                               key(predicate) -> user defined string (please don't add the
	 *                               <>, e.g. http://www.w3.org/1999/02/22-rdf-syntax-ns#type)
	 *                               <p>
	 *                               value(object) -> variable in the resultSet
	 *                               <p>
	 * @param predicateVar_objectVar the 2rd kind of predicate & object mapping
	 *                               <p>
	 *                               key(predicate) -> variable in the resultSet
	 *                               <p>
	 *                               value(object) -> variable in the resultSet
	 * @param prefixes               the prefixes define the prefix that can be used to add to the
	 *                               triples
	 * @param outputFileName         the output file name to which the result is written.
	 * @param append                 if true, then bytes will be written to the end of the file
	 *                               rather than the beginning
	 * @param lang                   The output language
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeTripleToFile(ResultSet resultSet, String subjectVar,
										Map<String, String> predicateStr_objectVar, Map<String, String> predicateVar_objectVar,
										Map<String, String> prefixes, String outputFileName, boolean append, String lang) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFileName, append);
			return writeTriple(resultSet, subjectVar, predicateStr_objectVar, predicateVar_objectVar,
					prefixes, fos, lang);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	/**
	 * convert the triple data in the resultSet to string as the parameters
	 * config
	 *
	 * @param resultSet              the jena query ResultSet, to be output
	 * @param subjectVar             the variable to be the subject of each QuerySolution in the
	 *                               resultSet
	 *                               <p>
	 * @param predicateStr_objectVar the 1st kind of predicate & object mapping
	 *                               <p>
	 *                               key(predicate) -> user defined string (please don't add the
	 *                               <>, e.g. http://www.w3.org/1999/02/22-rdf-syntax-ns#type)
	 *                               <p>
	 *                               value(object) -> variable in the resultSet
	 *                               <p>
	 * @param predicateVar_objectVar the 2rd kind of predicate & object mapping
	 *                               <p>
	 *                               key(predicate) -> variable in the resultSet
	 *                               <p>
	 *                               value(object) -> variable in the resultSet
	 * @param prefixes               the prefixes define the prefix that can be used to add to the
	 *                               triples
	 * @param lang                   The output language
	 * @return
	 */
	public static String asTextTriple(ResultSet resultSet, String subjectVar,
									  Map<String, String> predicateStr_objectVar, Map<String, String> predicateVar_objectVar,
									  Map<String, String> prefixes, String lang) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int writeFlag = writeTriple(resultSet, subjectVar, predicateStr_objectVar, predicateVar_objectVar,
				prefixes, baos, lang);
		if (writeFlag == 0) {
			try {
				return baos.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.debug("The Character Encoding is not supported: " + e.getMessage());
			}
		}
		return null;

	}

	/**
	 * output triples in the <b>special</b> resultSet to the OutputStream in the
	 * specified language.
	 * <p>
	 * the <b>special</b> means, 3 variables(should be subject, predicate,
	 * object) are mandatory in the ResultSet to output, or we can't create
	 * triples..
	 * <p>
	 * the ONLY way to output the jena query ResultSet to N-Triples format so
	 * far(create new model to load the result set as expected format).. if want
	 * the string directly..use the asText() methods.
	 * <p>
	 * prefixes can be used, define in the map prefixes, the key is the prefix,
	 * and the value is the original entire uri. It could be null or empty
	 * <p>
	 * The language in which to write the model is specified by the lang
	 * argument. Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE",
	 * "TURTLE", (and "TTL") and "N3". The default value, represented by null,
	 * is "RDF/XML".
	 *
	 * @param resultSet         the jena query ResultSet, to be output
	 * @param neededPredicates  the needed predicates, at least one of neededPredicates and
	 *                          discardPredicates have to be null . IF they are both null
	 *                          means no need to filter, output all the predicates
	 * @param discardPredicates the discard predicates
	 * @param prefixes          the prefixes define the prefix that can be used to add to the
	 *                          triples
	 * @param out               The output stream to which the result is written.
	 *                          <p>
	 *                          for example: System.out;(by default) fos = new
	 *                          FileOutputStream("out.nt");
	 * @param lang              The output language
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeSpecial(ResultSet resultSet, List<String> neededPredicates,
								   List<String> discardPredicates, Map<String, String> prefixes, OutputStream out, String lang) {
		if (resultSet == null) {
			LOG.error("the resultSet is null");
			return -1;
		}

		if (neededPredicates != null && discardPredicates != null) {
			LOG.error("at least one of neededPredicates and discardPredicates have to be null");
			return -1;
		}

		List<String> vars = resultSet.getResultVars();
		if (vars.size() != 3) {
			LOG.error("the ResultSet needs 3 variables to output, we got only " + vars.size() + ": " + vars);
			return -1;
		}

		if (lang == null) {
			lang = "N-TRIPLES";
		}

		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(PrefixMapping.Standard);

		if (prefixes != null && !prefixes.isEmpty()) {
			model.setNsPrefixes(prefixes);
		}

		while (resultSet.hasNext()) {
			try {
				QuerySolution solution = resultSet.next();
				String predicate = solution.get(vars.get(1)).toString();
				if ((neededPredicates == null && discardPredicates == null)
						|| (neededPredicates != null && neededPredicates.contains(predicate))
						|| (discardPredicates != null && !discardPredicates.contains(predicate))) {
					// transform the solution to statement, and add to the model
					model.add(solution.getResource(vars.get(0)),
							ResourceFactory.createProperty(solution.get(vars.get(1)).toString()),
							solution.get(vars.get(2)));
				}
			} catch (Exception e) {
				LOG.error(e.getClass() + ": " + e.getMessage());
				continue;
			}
		}

		if (out == null) {
			out = System.out;
		}

		RDFDataMgr.write(out, model, RDFLanguages.nameToLang(lang));
		// model.write(out, lang);
		model.close();

		return 0;
	}

	/**
	 * by default to output all the result set to the OutputStream in the
	 * specified language.
	 *
	 * @param resultSet the jena query ResultSet, to be output
	 * @param out       The output stream to which the result is written.
	 *                  <p>
	 *                  for example: System.out;(by default) fos = new
	 *                  FileOutputStream("out.nt");
	 * @param lang      the output language(format)
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeSpecial(ResultSet resultSet, OutputStream out, String lang) {
		return writeSpecial(resultSet, null, null, null, out, lang);
	}

	/**
	 * by default to output all the result set to System.out as N-TRIPLES
	 *
	 * @param resultSet the jena query ResultSet, to be output
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeSpecial(ResultSet resultSet) {
		return writeSpecial(resultSet, null, null, null, null, null);
	}

	/**
	 * output triples in the <b>special</b> resultSet to the outputFile in the
	 * specified language.
	 * <p>
	 * the <b>special</b> means, 3 variables(should be subject, predicate,
	 * object) are mandatory in the ResultSet to output, or we can't create
	 * triples..
	 * <p>
	 * the ONLY way to output the jena query ResultSet to N-Triples format so
	 * far(create new model to load the result set as expected format).. if want
	 * the string directly..use the asText() methods.
	 * <p>
	 * prefixes can be used, define in the map prefixes, the key is the prefix,
	 * and the value is the original entire uri. It could be null or empty
	 * <p>
	 * The language in which to write the model is specified by the lang
	 * argument. Predefined values are "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE",
	 * "TURTLE", (and "TTL") and "N3". The default value, represented by null,
	 * is "RDF/XML".
	 *
	 * @param resultSet         the jena query ResultSet, to be output
	 * @param neededPredicates  the needed predicates, at least one of neededPredicates and
	 *                          discardPredicates have to be null . IF they are both null
	 *                          means no need to filter, output all the predicates
	 * @param discardPredicates the discard predicates
	 * @param prefixes          the prefixes define the prefix that can be used to add to the
	 *                          triples
	 * @param outputFileName    the output file name to which the result is written.
	 * @param append            if true, then bytes will be written to the end of the file
	 *                          rather than the beginning
	 * @param lang              the output language(format)
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeSpecialToFile(ResultSet resultSet, List<String> neededPredicates,
										 List<String> discardPredicates, Map<String, String> prefixes, String outputFileName,
										 boolean append, String lang) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outputFileName, append);
			return writeSpecial(resultSet, neededPredicates, discardPredicates, prefixes, fos, lang);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return -1;
	}

	/**
	 * output all the triples in resultSet to the file
	 *
	 * @param resultSet      the jena query ResultSet, to be output
	 * @param outputFileName the output file name to which the result is written.
	 * @param append         if true, then bytes will be written to the end of the file
	 *                       rather than the beginning
	 * @param lang           the output language(format)
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeSpecialToFile(ResultSet resultSet, String outputFileName, boolean append,
										 String lang) {
		return writeSpecialToFile(resultSet, null, null, null, outputFileName, append, lang);
	}

	/**
	 * output all the triples in the resultSet to the file with default
	 * parameters(language as N-TRIPLES)
	 *
	 * @param resultSet      the jena query ResultSet, to be output
	 * @param outputFileName the output file name to which the result is written.
	 * @param append         if true, then bytes will be written to the end of the file
	 *                       rather than the beginning
	 * @return the status code, 0 -> successful, -1 -> failed
	 */
	public static int writeSpecialToFile(ResultSet resultSet, String outputFileName, boolean append) {
		return writeSpecialToFile(resultSet, null, null, null, outputFileName, append, null);
	}

	/**
	 * convert the triples in the <b>special</b> resultSet to string
	 * <p>
	 * 3 variables(should be subject, predicate, object) are needed from the
	 * ResultSet to converting, or we can't create triples..
	 * <p>
	 *
	 * @param resultSet
	 * @param neededPredicates
	 * @param discardPredicates
	 * @param prefixes
	 * @param lang
	 * @return
	 */
	public static String asTextSpecial(ResultSet resultSet, List<String> neededPredicates,
									   List<String> discardPredicates, Map<String, String> prefixes, String lang) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int writeFlag = writeSpecial(resultSet, neededPredicates, discardPredicates, prefixes, baos, lang);
		if (writeFlag == 0) {
			try {
				return baos.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.debug("The Character Encoding is not supported: " + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * convert the triples in the <b>special</b> resultSet to string, with the
	 * default output language(N-TRIPLES)
	 * <p>
	 * 3 variables(should be subject, predicate, object) are needed from the
	 * ResultSet to converting, or we can't create triples..
	 * <p>
	 *
	 * @param resultSet
	 * @return
	 */
	public static String asTextSpecial(ResultSet resultSet) {
		return asTextSpecial(resultSet, null, null, null, null);
	}
}
