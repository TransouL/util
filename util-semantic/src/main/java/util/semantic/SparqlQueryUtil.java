package util.semantic;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class provides utilities for sparql query statement.
 *
 * @since 2014-05
 */

public class SparqlQueryUtil {
	/**
	 * logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(SparqlQueryUtil.class);
	/**
	 * use to avoid the properties from the data graph which is not needed
	 */
	private static final HashSet<String> excludeProperty;
	/**
	 * the common prefix for sparql queries, could be updated by addPrefix()
	 */
	public static String prefix;
	/**
	 * the timeout on the query execution
	 */
	private static long QUERY_TIME_OUT = 30000;

	static {
		prefix = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n";
		/*
		 * initialize the excluded properties
		 */
		excludeProperty = new HashSet<String>();
		excludeProperty.add("http://www.w3.org/2000/01/rdf-schema#label");
		excludeProperty.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	}

	// private String endpoint = null;
	// private String ontologyGraph = null;
	// private String data_graph = null;
	// private static HashMap<String, HashSet<String>> classMapProperty = new
	// HashMap<String, HashSet<String>>();
	//
	// /**
	// * constructor.
	// *
	// * @param endpoint
	// * @param ontologyGraph
	// * @param data_graph
	// */
	// public SparqlUtil(String endpoint, String ontologyGraph, String
	// data_graph) {
	// this.endpoint = endpoint;
	// this.ontologyGraph = ontologyGraph;
	// this.data_graph = data_graph;
	// }

	/**
	 * add a new prefix. Use the statement directly. e.g.
	 * <p>
	 * <b>{@code PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>}</b>
	 *
	 * @param newPrefixStatement
	 */
	public static void addPrefix(String newPrefixStatement) {
		prefix = prefix + newPrefixStatement + "\n";
	}

	/**
	 * add a new prefix, use the prefix and uri separately, without the angle
	 * brackets, e.g.
	 * <p>
	 * newPrefix: <b>{@code rdf}</b>, and uri: <b>
	 * {@code http://www.w3.org/1999/02/22-rdf-syntax-ns#}</b>
	 *
	 * @param newPrefix the string to be used for the prefix.
	 * @param uri       the URI prefix to be named
	 */
	public static void addPrefix(String newPrefix, String uri) {
		prefix = prefix + "PREFIX " + newPrefix + ": <" + uri + ">\n";
	}

	/**
	 * Execute the sparql query statement against the endpiont
	 * <p>
	 * the prefix and graph info should be included in the statement.
	 *
	 * @param endpoint URL of the sparql endpoint
	 * @param sparql   the sparql query statement
	 * @return
	 */
	public static ResultSet query(String endpoint, String sparql) {
		return query(endpoint, sparql, 0, 0);
	}

	/**
	 * Execute the sparql query statement against the endpiont
	 * <p>
	 * the prefix and graph info should be included in the statement.
	 * <p>
	 * the <b>pageSize</b> should be >0, and <b>pages</b> should be >=0, if not
	 * they will be ignored.
	 *
	 * @param endpoint  URL of the sparql endpoint
	 * @param sparql    the sparql query statement
	 * @param pageIndex the index of page, begin from 0
	 * @param pageSize  size limit for each page, since the virtuoso limit to 10000,
	 *                  so the pageSize should be >0 and <=10000
	 * @return
	 * @throws Exception
	 */
	public static ResultSet query(String endpoint, String sparql,
								  int pageIndex, int pageSize) {
		if (endpoint == null || endpoint.isEmpty() || sparql == null
				|| sparql.isEmpty()) {
			return null;
		}

		if (pageSize > SemanticCommon.VIRTUOSO_LIMIT) {
			LOG.error("the pageSize exceeds the limit of virtuoso("
					+ SemanticCommon.VIRTUOSO_LIMIT + ")");
			return null;
		}

		if (pageIndex >= 0 && pageSize > 0) {
			sparql += " OFFSET " + pageIndex * pageSize + " LIMIT " + pageSize;
		}

		try {
			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					endpoint, query);
			qexec.setTimeout(QUERY_TIME_OUT, TimeUnit.MILLISECONDS);
			ResultSet queryResultSet = qexec.execSelect();
			ResultSet returnResultSet = ResultSetFactory
					.copyResults(queryResultSet);
			qexec.close();
			return returnResultSet;
		} catch (Exception e) {
			LOG.error("Exception during execute the sparql query", e);
			LOG.debug("sparql statement: \n" + sparql);
		}
		return null;
	}

	/**
	 * Execute the sparql query statement against the endpiont with limit each
	 * round
	 *
	 * @param endpoint       URL of the sparql endpoint
	 * @param sparql         the sparql query statement
	 * @param outputFileName the name of the output file
	 * @param limit          the limit of each round
	 */
	public static void queryAll(String endpoint, String sparql,
								String outputFileName, int limit) {
		int pageIndex = 0;
		boolean withVarNames = true;
		while (true) {
			LOG.info("execute the sparql statement #" + pageIndex + " ("
					+ limit + " each time)");
			ResultSet rSet = query(endpoint, sparql, pageIndex, limit);
			if (rSet != null) {
				ResultSetUtil.display(rSet, withVarNames, outputFileName, true);
				withVarNames = false;
				if (rSet.getRowNumber() < limit) {
					break;
				}
			} else {
				LOG.error("query exception during the dump #" + pageIndex);
			}
			pageIndex++;
		}
	}

	/**
	 * Execute the sparql query statement over the Model
	 *
	 * @param model  the model to query over
	 * @param sparql the sparql query statement
	 * @return
	 */
	public static ResultSet query(Model model, String sparql) {
		if (model == null || sparql == null || sparql.isEmpty()) {
			return null;
		}
		QueryExecution qexec = null;
		try {
			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
			qexec = QueryExecutionFactory.create(query, model);
			qexec.setTimeout(QUERY_TIME_OUT, TimeUnit.MILLISECONDS);
			ResultSet queryResultSet = qexec.execSelect();
			ResultSet returnResultSet = ResultSetFactory
					.copyResults(queryResultSet);
			return returnResultSet;
		} catch (Exception e) {
			LOG.error("Exception during execute the sparql query", e);
			LOG.debug("sparql statement: \n" + sparql);
			return null;
		} finally {
			if (qexec != null) {
				qexec.close();
			}
		}

	}

	/**
	 * Execute the sparql query statement against the endpiont.
	 * <p>
	 * The prefix and graph info should be included in the statement.
	 * <p>
	 * the <b>pageSize</b> should be >0, and <b>pages</b> should be >=0, if not
	 * they will be ignored.
	 * <p>
	 * The exception during the querying will throws up with no dealing
	 *
	 * @param endpoint  URL of the sparql endpoint
	 * @param sparql    the sparql query statement
	 * @param pageIndex the index of page, begin from 0
	 * @param pageSize  size limit for each page, since the virtuoso limit to 10000,
	 *                  so the pageSize should be >0 and <=10000
	 * @return
	 */
	public static ResultSet queryThrowsException(String endpoint,
												 String sparql, int pageIndex, int pageSize) {
		if (endpoint == null || endpoint.isEmpty() || sparql == null
				|| sparql.isEmpty()) {
			return null;
		}

		if (pageSize > SemanticCommon.VIRTUOSO_LIMIT) {
			LOG.error("the pageSize exceeds the limit of virtuoso("
					+ SemanticCommon.VIRTUOSO_LIMIT + ")");
			return null;
		}

		if (pageIndex >= 0 && pageSize > 0) {
			sparql += " OFFSET " + pageIndex * pageSize + " LIMIT " + pageSize;
		}

		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint,
				query);
		qexec.setTimeout(QUERY_TIME_OUT, TimeUnit.MILLISECONDS);
		ResultSet queryResultSet = qexec.execSelect();
		ResultSet returnResultSet = ResultSetFactory
				.copyResults(queryResultSet);
		qexec.close();
		return returnResultSet;
	}

	/**
	 * Execute the sparql ask statement against the endpiont
	 *
	 * @param endpoint
	 * @param sparql
	 * @return
	 */
	public static boolean ask(String endpoint, String sparql) {
		if (endpoint == null || endpoint.isEmpty() || sparql == null
				|| sparql.isEmpty()) {
			throw new RuntimeException("some parameter is null or empty");
		}

		try {
			Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					endpoint, query);
			qexec.setTimeout(QUERY_TIME_OUT, TimeUnit.MILLISECONDS);
			boolean queryResult = qexec.execAsk();
			qexec.close();
			return queryResult;
		} catch (Exception e) {
			LOG.error("Exception during execute the sparql ask", e);
			LOG.debug("sparql statement: \n" + sparql);
			throw new RuntimeException(
					"Exception during execute the sparql ask", e);
		}
	}

	/**
	 * Execute the sparql insert statement against the endpiont, by sending the
	 * request to a SPARQL endpoint by using an HTML form and POST Use of
	 * application/sparql-update
	 * <p>
	 * <B>!!Jena only support SPARQL 1.1 Update, which is not supported by
	 * virtuoso <= 6.1.7 version (org.apache.jena.atlas.web.HttpException: 400 -
	 * Bad Request)</B>
	 * <p>
	 * the prefix and graph info should be included in the statement.
	 * <p>
	 * for virtuoso, createRemote() doesn't work, have to use
	 * createRemoteForm(), not try on other sparql endpoints
	 * <p>
	 *
	 * @param endpoint URL of the sparql endpoint
	 * @param sparul   the sparql update statement
	 * @return 0--succeed, other--fail
	 */
	public static int executeUpdate(String endpoint, String sparul) {
		if (endpoint == null || endpoint.isEmpty() || sparul == null
				|| sparul.isEmpty()) {
			return -1;
		}

		try {
			UpdateRequest updateRequest = UpdateFactory.create(sparul,
					Syntax.syntaxSPARQL_11);
			UpdateProcessor up = UpdateExecutionFactory.createRemoteForm(
					updateRequest, endpoint);
			up.execute();
			return 0;
		} catch (Exception e) {
			LOG.error("Exception during execute the sparql insert", e);
			LOG.debug("sparql statement: \n" + sparul);
		}
		return -1;
	}

	/**
	 * insert triples to specified endpoint and graph
	 *
	 * @param endpoint
	 * @param graphName
	 * @param triples
	 * @return
	 */
	public static int insertTriples(String endpoint, String graphName,
									String triples) {
		String sparul = "INSERT DATA {GRAPH <" + graphName + "> { \n" + triples
				+ " } }";
		return executeUpdate(endpoint, sparul);
	}

	/**
	 * insert triples to specified endpoint and graph
	 *
	 * @param endpoint
	 * @param graphName
	 * @param tripleList
	 * @return
	 */
	public static int insertTriples(String endpoint, String graphName,
									List<String> tripleList) {
		StringBuffer sparulSB = new StringBuffer("INSERT DATA {GRAPH <"
				+ graphName + "> { \n");
		for (String triple : tripleList) {
			sparulSB.append(triple);
			sparulSB.append("\n");
		}
		sparulSB.append(" } }");

		return executeUpdate(endpoint, sparulSB.toString());
	}

	/**
	 * Count the number of triples in the data graph.
	 *
	 * @return
	 */
	public static long countGraphTriples(String endpoint, String graphName) {
		String sparql = "select (count(*) as ?count) from " + graphName
				+ " where {?s ?p ?o .}";
		return ResultSetUtil.parseLong(query(endpoint, sparql), "count");
	}

	/**
	 * Count instances for every type based on input type.
	 *
	 * @param type class type with prefix.
	 * @return
	 */
	public static long countEntities(String endpoint, String dataGraphURI,
									 String type) {
		long result = Long.MIN_VALUE;
		if (type != null && type.length() != 0) {
			String sparql = prefix
					+ "select (count(distinct ?s) as ?count) from "
					+ dataGraphURI + " where {?s rdf:type " + type + " .}";
			result = ResultSetUtil.parseLong(query(endpoint, sparql), "count");
		}
		return result;
	}

	/**
	 * Get properties for input type
	 *
	 * @param type the entire name of the class (WITH prefix, or the original
	 *             entire URI).
	 */
	public static HashSet<String> getProperty(String endpoint,
											  String ontologyGraphURI, String dataGraphURI, String type,
											  boolean exclude) {
		// get properties from ontology graph.
		HashSet<String> properties = getPropertyOntology(endpoint,
				ontologyGraphURI, type);
		// get properties from data graph.
		HashSet<String> propertiesData = getPropertyData(endpoint,
				dataGraphURI, type);
		// combine the two set
		properties.addAll(propertiesData);
		// exclude the properties if needed
		if (exclude) {
			properties.removeAll(excludeProperty);
		}
		return properties;
	}

	/**
	 * Get property for input class type from ontology graph, which can be
	 * identified by rdfs:domain predict.
	 *
	 * @param classType the class type, with prefix, or the original entire URI
	 * @return
	 */
	public static HashSet<String> getPropertyOntology(String endpoint,
													  String ontologyGraphURI, String classType) {
		HashSet<String> result = new HashSet<String>();
		if (ontologyGraphURI != null) {
			String sparql = prefix + "select distinct ?s from "
					+ ontologyGraphURI + " where {?s rdfs:domain " + classType
					+ ".}";
			ResultSet resultSet = query(endpoint, sparql);
			for (Object object : ResultSetUtil.parseAsSetString(resultSet, "s")) {
				result.add(object.toString());
			}
		}
		return result;
	}

	/**
	 * Get property for input class type from data graph.
	 *
	 * @param classType the class type, with prefix, or the original entire URI
	 * @return
	 */
	public static HashSet<String> getPropertyData(String endpoint,
												  String dataGraphURI, String classType) {
		HashSet<String> result = new HashSet<String>();
		if (dataGraphURI != null) {
			String sparql = prefix + "select distinct ?p from " + dataGraphURI
					+ " where {?s a " + classType + ". ?s ?p ?o. }";
			ResultSet resultSet = query(endpoint, sparql);
			for (Object object : ResultSetUtil.parseAsSetString(resultSet, "p")) {
				result.add(object.toString());
			}
		}
		return result;
	}

	/**
	 * get the count with specified class type and property full name.
	 *
	 * @param classType class type with prefix.
	 * @param property  the property is specified with prefix like 'dining:', or the
	 *                  original entire URI with “<>”
	 * @return
	 */
	public static long getCountForProperty(String endpoint,
										   String dataGraphURI, String classType, String property) {
		long result = Long.MIN_VALUE;
		if (classType != null && !classType.isEmpty() && property != null
				&& !property.isEmpty()) {
			String sparql = prefix
					+ "select (count(distinct ?s) as ?count) from "
					+ dataGraphURI + " where {?s a " + classType + ". ?s "
					+ property + " ?o. }";
			result = ResultSetUtil.parseLong(query(endpoint, sparql), "count");
		}

		return result;
	}
}
