package tknpow22.wicketexample.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ユーザーデータの操作を定義する
 */
public class UserDao extends DaoBase {

	public Map<String, Object> findUserById(String userId) {

		Map<String, Object> result = null;

		final String sql = ""
				+ " SELECT"
				+ "   UserId"
				+ "   , Username"
				+ "   , Password"
				+ " FROM Users"
				+ " WHERE UserId = ?"
				;

		try {
			List<Map<String, Object>> list = executeQuery(sql, userId);
			if (0 < list.size()) {
				result = list.get(0);
			}

		} catch (SQLException ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	public List<Map<String, Object>> findRolesById(String userId) {

		List<Map<String, Object>> result = new ArrayList<>();

		final String sql = ""
				+ " SELECT"
				+ "   U.Role"
				+ "   , R.RoleName"
				+ " FROM UserRoles U, RoleNames R"
				+ " WHERE U.Role = R.Role"
				+ "   AND U.UserId = ?"
				;

		try {
			result = executeQuery(sql, userId);
		} catch (SQLException ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	public List<Map<String, Object>> findAllUsers() {

		List<Map<String, Object>> result = new ArrayList<>();

		final String sql = ""
				+ " SELECT"
				+ "   UserId"
				+ "   , Username"
				+ "   , Password"
				+ " FROM Users"
				;

		try {
			result = executeQuery(sql);
		} catch (SQLException ex) {
			logger.error("{}", ex);
		}

		return result;
	}
}
