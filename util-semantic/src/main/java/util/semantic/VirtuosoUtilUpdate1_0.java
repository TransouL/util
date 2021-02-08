package util.semantic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoUpdateFactory;
import virtuoso.jena.driver.VirtuosoUpdateRequest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * This class provides utilities for Virtuoso(less then version 6.1.7, not
 * support SPARQL 1.1 Update) based on Virtuoso Jena Provider.
 *
 * @since 2014-08
 */
public class VirtuosoUtilUpdate1_0 {

	private static final Log LOG = LogFactory
			.getLog(VirtuosoUtilUpdate1_0.class);
	private static final int IMPORT_LIMIT = 1000;

	/**
	 * import all the triples into destination graph.
	 *
	 * @param virtGraph virt graph
	 * @param graphName graph name
	 * @return 0--succeed, other--fail
	 */
	public static int importTriple(VirtGraph virtGraph, String graphName,
								   String triples) {
		if (virtGraph == null || graphName == null || graphName.isEmpty()
				|| triples == null) {
			return -1;
		}
		if (triples.isEmpty()) {
			LOG.debug("triples is empty");
			return 0;
		}

		String sparul = "INSERT DATA INTO GRAPH <" + graphName + "> { \n"
				+ triples + " }";
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
		if (virtGraph == null || graphName == null || graphName.isEmpty()
				|| tripleList == null) {
			return -1;
		}
		if (tripleList.isEmpty()) {
			LOG.debug("triple list is empty");
			return 0;
		}

		StringBuffer sparulSB = new StringBuffer("INSERT DATA INTO GRAPH <"
				+ graphName + "> { \n");
		for (String triple : tripleList) {
			sparulSB.append(triple);
			sparulSB.append("\n");
		}
		sparulSB.append(" }");
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
		if (virtGraph == null || graphName == null || filePath == null
				|| graphName.isEmpty() || filePath.isEmpty()) {
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
				sparulSB = new StringBuffer("INSERT DATA INTO GRAPH <"
						+ graphName + "> { \n");
				while (limit_tmp-- > 0 && (line = br.readLine()) != null) {
					sparulSB.append(line);
					sparulSB.append("\n");
					totalCount++;
				}
				sparulSB.append(" }");
				VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(
						sparulSB.toString(), virtGraph);
				vur.exec();

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
		String str = "CLEAR GRAPH <" + graphName + ">";
		VirtuosoUpdateRequest vur = VirtuosoUpdateFactory
				.create(str, virtGraph);
		vur.exec();
	}
}
