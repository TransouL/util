package util.semantic;

import com.hp.hpl.jena.query.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides utilities for triple stores.
 *
 * @since 2014-08
 */
public class TripleStoreUtil {

	/**
	 * logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(TripleStoreUtil.class);

	/**
	 * dump the data in the graph, to the file in N-Triples format
	 *
	 * @param endpoint       URL of the sparql endpoint
	 * @param graphName      the URI of the graph
	 * @param outputFileName the name of the output file
	 */
	public static void dumpGraph(String endpoint, String graphName,
								 String outputFileName, int query_limit) {
		String sparql;
		if (graphName != null && !graphName.isEmpty()) {
			sparql = "select * from " + graphName + " where {?s ?p ?o .}";
		} else {
			sparql = "select * where {?s ?p ?o .}";
		}
		int pageIndex = 0;
		while (true) {
			LOG.debug("dump the graph " + graphName + " #" + pageIndex + " ("
					+ query_limit + " each time)");
			ResultSet rSet = SparqlQueryUtil.query(endpoint, sparql, pageIndex,
					query_limit);
			if (rSet != null) {
				ResultSetUtil.writeSpecialToFile(rSet, outputFileName, true);
				if (rSet.getRowNumber() < query_limit) {
					break;
				}
			} else {
				LOG.error("query exception during the dump #" + pageIndex);
			}
			pageIndex++;
		}
	}
}
