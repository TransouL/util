package util.semantic;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.update.UpdateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import virtuoso.jena.driver.*;

import javax.sql.rowset.CachedRowSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides utilities for Virtuoso(version 6.1.7+, support SPARQL 1.1
 * Update) based on Virtuoso Jena Provider.
 *
 * @since 2014-08
 */
public class VirtuosoUtil {

	private static final Log LOG = LogFactory.getLog(VirtuosoUtil.class);
	private static final int IMPORT_LIMIT = 1000;
	private static final String DEFAULT_GRAPH = "virt:DEFAULT";

	/**
	 * get all graphs in the connected virtuoso
	 *
	 * @param conn
	 * @return
	 */
	public static ArrayList<String> getGraphs(Connection conn) {
		ArrayList<String> result = new ArrayList<>();
		CachedRowSet crs = null;
		try {
			crs = VirtuosoISQLExecution.executeQuery(conn,
					"DB.DBA.SPARQL_SELECT_KNOWN_GRAPHS()");
			while (crs.next()) {
				result.add(crs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (crs != null) {
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * get all graphs in virtuoso
	 *
	 * @param conn
	 * @return
	 */
	public static ArrayList<String> getGraphs(VirtGraph virtGraph) {
		Connection conn = VirtuosoISQLExecution.getConnection(virtGraph);
		ArrayList<String> result = getGraphs(conn);
		VirtuosoISQLExecution.closeConnection(conn);
		return result;
	}

	/**
	 * execute the sparql query statement against the VirtGraph
	 *
	 * @param virtGraph
	 * @param sparql
	 * @return
	 */
	public static ResultSet execSelect(VirtGraph virtGraph, String sparql) {
		if (virtGraph == null) {
			LOG.error("VirtGraph is null");
			return null;
		}
		if (sparql == null || sparql.isEmpty()) {
			LOG.error("sparql query statement is null or empty");
			return null;
		}

		if (virtGraph.getGraphName().equals(DEFAULT_GRAPH)) {
			if (!sparql.substring(0, sparql.indexOf("{")).toLowerCase()
					.contains(" from ")) {
				LOG.error("graph unclear in both virtGraph and sparql query statement");
				return null;
			}
		}

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(
				sparql, virtGraph);
		ResultSet queryResultSet = vqe.execSelect();
		ResultSet returnResultSet = ResultSetFactory
				.copyResults(queryResultSet);
		vqe.close();
		return returnResultSet;
	}

	/**
	 * count the number of triples in all graphs in the connected virtuoso
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static long countAll(Connection conn) {
		CachedRowSet crs = null;
		try {
			crs = VirtuosoISQLExecution.executeQuery(conn,
					"select count(*) from DB.DBA.RDF_QUAD");
			if (crs.next()) {
				return crs.getLong(1);
			} else {
				return -1L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1L;
		} finally {
			if (crs != null) {
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * count the number of triples in all graphs in the VirtGraph
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static long countAll(VirtGraph virtGraph) {
		Connection conn = VirtuosoISQLExecution.getConnection(virtGraph);
		long result = countAll(conn);
		VirtuosoISQLExecution.closeConnection(conn);
		return result;
	}

	/**
	 * count the number of triples group by graphs in the VirtGraph
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static HashMap<String, Long> countAllGroupByGraph(Connection conn) {
		HashMap<String, Long> result = new HashMap<>();
		CachedRowSet crs = null;
		try {
			crs = VirtuosoISQLExecution
					.executeQuery(conn,
							"select id_to_iri(G), count(*) from DB.DBA.RDF_QUAD group by G");
			while (crs.next()) {
				result.put(crs.getString(1), crs.getLong(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (crs != null) {
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * count the number of triples group by graphs in the VirtGraph
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static HashMap<String, Long> countAllGroupByGraph(VirtGraph virtGraph) {
		Connection conn = VirtuosoISQLExecution.getConnection(virtGraph);
		HashMap<String, Long> result = countAllGroupByGraph(conn);
		VirtuosoISQLExecution.closeConnection(conn);
		return result;
	}

	/**
	 * refined counting the number of triples in the VirtGraph, using the sql
	 * query against the base layer database of virtuoso
	 * <p>
	 * <b>countGraph1</b>: query the count group by graph first, to get all
	 * graph count, and get the specified graph from them; <br>
	 * <b>countGraph2</b>: only query the count of the specified graph; <br>
	 * <b>hard to say which one will be faster</b>, depends on the specific
	 * situation
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static long countGraph1(Connection conn, String graphName) {
		HashMap<String, Long> result = countAllGroupByGraph(conn);
		if (result.containsKey(graphName)) {
			return result.get(graphName);
		} else {
			return 0;
		}
	}

	/**
	 * refined counting the number of triples in the VirtGraph, using the sql
	 * query against the base layer database of virtuoso
	 * <p>
	 * <b>countGraph1</b>: query the count group by graph first, to get all
	 * graph count, and get the specified graph from them; <br>
	 * <b>countGraph2</b>: only query the count of the specified graph; <br>
	 * <b>hard to say which one will be faster</b>, depends on the specific
	 * situation
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static long countGraph1(VirtGraph virtGraph, String graphName) {
		HashMap<String, Long> result = countAllGroupByGraph(virtGraph);
		if (result.containsKey(graphName)) {
			return result.get(graphName);
		} else {
			return 0;
		}
	}

	/**
	 * refined counting the number of triples in the VirtGraph, using the sql
	 * query against the database of virtuoso
	 * <p>
	 * <b>countGraph1</b>: query the count group by graph first, to get all
	 * graph count, and get the specified graph from them; <br>
	 * <b>countGraph2</b>: only query the count of the specified graph; <br>
	 * <b>hard to say which one will be faster</b>, depends on the specific
	 * situation
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return if return <0 means error
	 */
	public static long countGraph1(VirtGraph virtGraph) {
		if (!virtGraph.getGraphName().equals(DEFAULT_GRAPH)) {
			return countGraph1(virtGraph, virtGraph.getGraphName());
		} else {
			LOG.error("graph unclear in virtGraph, please config the graph,"
					+ " or use countAll() to get the triples in all graph");
			return -1;
		}
	}

	/**
	 * refined counting the number of triples in the VirtGraph, using the sql
	 * query against the base layer database of virtuoso
	 * <p>
	 * <b>countGraph1</b>: query the count group by graph first, to get all
	 * graph count, and get the specified graph from them; <br>
	 * <b>countGraph2</b>: only query the count of the specified graph; <br>
	 * <b>hard to say which one will be faster</b>, depends on the specific
	 * situation
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static long countGraph2(Connection conn, String graphName) {
		CachedRowSet crs = null;
		String sql = "select count(*) from DB.DBA.RDF_QUAD WHERE G = iri_to_id('"
				+ graphName + "')";
		try {
			crs = VirtuosoISQLExecution.executeQuery(conn, sql);
			if (crs.next()) {
				return crs.getLong(1);
			} else {
				LOG.error("unexpected result returned while executing isql query: "
						+ sql);
				return -1L;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1L;
		} finally {
			if (crs != null) {
				try {
					crs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * refined counting the number of triples in the VirtGraph, using the sql
	 * query against the base layer database of virtuoso
	 * <p>
	 * <b>countGraph1</b>: query the count group by graph first, to get all
	 * graph count, and get the specified graph from them; <br>
	 * <b>countGraph2</b>: only query the count of the specified graph; <br>
	 * <b>hard to say which one will be faster</b>, depends on the specific
	 * situation
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static long countGraph2(VirtGraph virtGraph, String graphName) {
		Connection conn = VirtuosoISQLExecution.getConnection(virtGraph);
		long result = countGraph2(conn, graphName);
		VirtuosoISQLExecution.closeConnection(conn);
		return result;
	}

	/**
	 * refined counting the number of triples in the VirtGraph, using the sql
	 * query against the database of virtuoso
	 * <p>
	 * <b>countGraph1</b>: query the count group by graph first, to get all
	 * graph count, and get the specified graph from them; <br>
	 * <b>countGraph2</b>: only query the count of the specified graph; <br>
	 * <b>hard to say which one will be faster</b>, depends on the specific
	 * situation
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return if return <0 means error
	 */
	public static long countGraph2(VirtGraph virtGraph) {
		if (!virtGraph.getGraphName().equals(DEFAULT_GRAPH)) {
			return countGraph2(virtGraph, virtGraph.getGraphName());
		} else {
			LOG.error("graph unclear in virtGraph, please config the graph,"
					+ " or use countAll() to get the triples in all graph");
			return -1;
		}
	}

	/**
	 * count the number of triples in the VirtGraph, using the method provide by
	 * lib
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return
	 */
	public static int countGraphOriginal(VirtGraph virtGraph, String graphName) {
		if (virtGraph.getGraphName().equals(graphName)) {
			return virtGraph.getCount();
		} else {
			LOG.debug("graph name not match(" + virtGraph.getGraphName() + "/"
					+ graphName
					+ "), will create new graph with the specified graph("
					+ graphName + ")");
			VirtGraph newGraph = new VirtGraph(graphName,
					virtGraph.getGraphUrl(), virtGraph.getGraphUser(),
					virtGraph.getGraphPassword());
			int result = newGraph.getCount();
			newGraph.close();
			return result;
		}
	}

	/**
	 * count the number of triples in the VirtGraph, using the method provide by
	 * lib
	 *
	 * @param virtGraph
	 * @param graphName
	 * @return if return <0 means error
	 */
	public static int countGraphOriginal(VirtGraph virtGraph) {
		if (!virtGraph.getGraphName().equals(DEFAULT_GRAPH)) {
			return virtGraph.getCount();
		} else {
			LOG.error("graph unclear in virtGraph, please config the graph,"
					+ " or use countAll() to get the triples in all graph");
			return -1;
		}
	}

	/*
	 * todo
	 *
	 * 1. check why ask result is always false; 2. should we consider if the
	 * graph is configed in the virtGraph
	 */
	// /**
	// * !!!!something wrong, always false
	// *
	// * execute the sparql ask statement against the VirtGraph
	// *
	// * @param virtGraph
	// * @param ask
	// * @return
	// * @throws Exception
	// */
	// public static boolean ask(VirtGraph virtGraph, String ask) throws
	// Exception {
	// if (virtGraph == null) {
	// LOG.error("VirtGraph is null");
	// Exception e = new Exception("VirtGraph is null");
	// throw e;
	// }
	// if (ask == null || ask.isEmpty()) {
	// LOG.error("sparql ask statement is null or empty");
	// Exception e = new Exception("sparql ask statement is null or empty");
	// throw e;
	// }
	//
	// VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(ask,
	// virtGraph);
	// boolean result = vqe.execAsk();
	// vqe.close();
	// return result;
	// }

	/**
	 * import all the triples into destination graph.
	 *
	 * @param filePath  NT file name
	 * @param virtGraph virt graph
	 * @param graphName graph name
	 * @return 0--succeed, other--fail
	 */
	public static int importTriple(VirtGraph virtGraph, String graphName,
								   String triples) {
		if (virtGraph == null) {
			LOG.error("VirtGraph is null");
			return -1;
		}
		if (graphName == null || graphName.isEmpty()) {
			LOG.error("graphName is null or empty");
			return -1;
		}
		if (triples == null || triples.isEmpty()) {
			LOG.error("triples is null or empty");
			return -1;
		}

		String sparul = "INSERT DATA {GRAPH <" + graphName + "> { \n" + triples
				+ " } }";
		try {
			VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(sparul,
					virtGraph);
			vur.exec();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * import all the triples into destination graph.
	 *
	 * @param filePath  NT file name
	 * @param virtGraph virt graph
	 * @param graphName graph name
	 * @return 0--succeed, other--fail
	 */
	public static int importTriple(VirtGraph virtGraph, String graphName,
								   List<String> tripleList) {
		if (virtGraph == null) {
			LOG.error("VirtGraph is null");
			return -1;
		}
		if (graphName == null || graphName.isEmpty()) {
			LOG.error("graphName is null or empty");
			return -1;
		}
		if (tripleList == null || tripleList.isEmpty()) {
			LOG.error("triple list is null or empty");
			return -1;
		}

		StringBuffer sparulSB = new StringBuffer("INSERT DATA {GRAPH <"
				+ graphName + "> { \n");
		for (String triple : tripleList) {
			sparulSB.append(triple);
			sparulSB.append("\n");
		}
		sparulSB.append(" } }");
		try {
			VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(
					sparulSB.toString(), virtGraph);
			vur.exec();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * import all the triples into destination graph.
	 *
	 * @param filePath  NT file name
	 * @param virtGraph virt graph
	 * @param graphName graph name
	 * @return 0--succeed, other--fail
	 */
	public static int importNTFile(VirtGraph virtGraph, String graphName,
								   String filePath) {
		if (virtGraph == null) {
			LOG.error("VirtGraph is null");
			return -1;
		}
		if (graphName == null || graphName.isEmpty()) {
			LOG.error("graphName is null or empty");
			return -1;
		}
		if (filePath == null || filePath.isEmpty()) {
			LOG.error("filePath is null or empty");
			return -1;
		}

		long start_time = System.currentTimeMillis();

		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			long totalCount = 0;

			String line;
			StringBuffer sparulSB;
			int limit_tmp;
			while (true) {
				// insert data into virtuoso one time with limitation rows.
				limit_tmp = IMPORT_LIMIT;
				sparulSB = new StringBuffer("INSERT DATA {GRAPH <" + graphName
						+ "> { \n");
				while (limit_tmp-- > 0 && (line = br.readLine()) != null) {
					sparulSB.append(line);
					sparulSB.append("\n");
					totalCount++;
				}

				// if the lines of NT file is exactly multiple of IMPORT_LIMIT,
				// limit_tmp will be IMPORT_LIMIT - 1 here, should not insert
				if (limit_tmp != IMPORT_LIMIT - 1) {
					sparulSB.append(" } }");
					VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(
							sparulSB.toString(), virtGraph);
					vur.exec();
				}

				// no more triples in the file
				if (limit_tmp > 0) {
					break;
				}
			}
			LOG.debug("total triple size: " + totalCount + "\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (UpdateException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		long end_time = System.currentTimeMillis();
		LOG.debug("Virtuoso Insert execute time is: " + (end_time - start_time)
				+ " msec");
		return 0;
	}

	/**
	 * Clear the specified graph
	 *
	 * @param virtGraph the virt graph.
	 * @param graphName the graph name of the virt graph.
	 */
	public static void clearGraph(VirtGraph virtGraph, String graphName) {
		if (virtGraph == null || graphName == null || graphName.isEmpty()) {
			return;
		}
		/*
		 * something wrong with VirtGraph.clear() in this project
		 * virtuoso.jdbc4.VirtuosoFNSException: createArrayOf(typeName,
		 * elements) not supported
		 */
		// if (virtGraph.getGraphName().equals(graphName)) {
		// virtGraph.clear();
		// } else {
		String str = "CLEAR GRAPH <" + graphName + ">";
		VirtuosoUpdateRequest vur = VirtuosoUpdateFactory
				.create(str, virtGraph);
		vur.exec();
		// }
	}

	/**
	 * Clear the specified graph
	 *
	 * @param virtGraph the virt graph.
	 */
	public static void clearGraph(VirtGraph virtGraph) {
		if (!virtGraph.getGraphName().equals(DEFAULT_GRAPH)) {
			clearGraph(virtGraph, virtGraph.getGraphName());
		} else {
			LOG.error("graph unclear in virtGraph, please config the graph");
			return;
		}
	}
}
