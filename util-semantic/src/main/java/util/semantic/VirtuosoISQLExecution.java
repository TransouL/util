package util.semantic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import virtuoso.jena.driver.VirtGraph;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.List;

public class VirtuosoISQLExecution {
	private static final String VIRTUOSO_JDBC4_DRIVER = "virtuoso.jdbc4.Driver";
	private static final Log LOG = LogFactory
			.getLog(VirtuosoISQLExecution.class);

	public static Connection getConnection(String url, String user,
										   String password) {
		Connection conn = null;
		try {
			Class.forName(VIRTUOSO_JDBC4_DRIVER);
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			LOG.error("Load jdbc driver [" + VIRTUOSO_JDBC4_DRIVER
					+ "] failed!");
			e.printStackTrace();
		} catch (SQLException e) {
			LOG.error("Failded to establish the connection.");
			e.printStackTrace();
		}
		return conn;
	}

	public static Connection getConnection(VirtGraph graph) {
		return getConnection(graph.getGraphUrl(), graph.getGraphUser(),
				graph.getGraphPassword());
	}

	public static void closeConnection(Connection conn) {
		try {
			if (!conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			LOG.error("Failed to access the database when closing the connection.");
			e.printStackTrace();
		}
	}

	/**
	 * execute a virtuoso isql cmd against the base layer database of virtuoso
	 * <p>
	 * !!<b>there can NOT be ";" at the last of the sql statement!!</b>
	 *
	 * @param conn
	 * @param cmd
	 * @throws SQLException
	 * @throws Exception
	 */
	public static void execute(Connection conn, String cmd) throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(cmd);
		} catch (SQLException e) {
			LOG.error(e.getMessage());
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * execute a sql statement against the base layer database of virtuoso
	 * <p>
	 * !!<b>there can NOT be ";" at the last of the sql statement!!</b>
	 *
	 * @param conn
	 * @param sql
	 * @throws SQLException
	 * @throws Exception
	 */
	public static void execute(Connection conn, List<String> cmds)
			throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			for (String cmd : cmds) {
				stmt.execute(cmd);
			}
		} catch (SQLException e) {
			LOG.error(e.getMessage());
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * execute a sql query statement against the base layer database of virtuoso
	 * <p>
	 * !!<b>there can NOT be ";" at the last of the sql statement!!</b>
	 *
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static CachedRowSet executeQuery(Connection conn, String sql)
			throws SQLException {
		Statement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery(sql);
			RowSetFactory factory = RowSetProvider.newFactory();
			CachedRowSet crs = factory.createCachedRowSet();
			crs.populate(resultSet);
			return crs;
		} catch (SQLException e) {
			LOG.error(e.getMessage());
			throw e;
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
