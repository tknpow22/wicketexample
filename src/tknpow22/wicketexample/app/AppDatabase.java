package tknpow22.wicketexample.app;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * アプリケーションで使用するデータベースの操作を定義する
 */
public class AppDatabase {

	protected static final Logger logger = LoggerFactory.getLogger(AppDatabase.class);

	public static Connection getConnection() {
		try {

			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/wicketexample");

			Connection connection = ds.getConnection();
			connection.setAutoCommit(false);

			return connection;

		} catch (NamingException | SQLException ex) {
			logger.error("{}", ex);
			throw new RuntimeException(ex);
		}
	}

}
