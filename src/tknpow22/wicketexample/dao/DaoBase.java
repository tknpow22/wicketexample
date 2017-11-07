package tknpow22.wicketexample.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tknpow22.wicketexample.app.AppDatabase;

/**
 * DAO のベースクラスを定義する
 */
public class DaoBase implements AutoCloseable {

	protected static final Logger logger = LoggerFactory.getLogger(DaoBase.class);

	protected Connection connection;

	public DaoBase() {
		connection = AppDatabase.getConnection();
	}

	protected List<Map<String, Object>> executeQuery(final String sql, Object... args) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			int index = 0;
			for (Object arg : args) {
				stmt.setObject(++index, arg);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				return (new MapListHandler()).handle(rs);
			}
		} catch (SQLException ex) {
			throw ex;
		}
	}

	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("{}", ex);
			}
			connection = null;
		}
	}

	// セーフネット。finalize は使用しない方が良いのだろうけど、万一に備えて定義する。
	@SuppressWarnings("unused")
	private final Object finalizerGurdian = new Object() {
		@Override
		protected void finalize() throws Throwable {
			if (connection != null) {
				logger.error("!!! finalize was called !!!");
				try {
					connection.close();
				} catch (Throwable ignore) {
				}
				connection = null;
			}
		}
	};
}
