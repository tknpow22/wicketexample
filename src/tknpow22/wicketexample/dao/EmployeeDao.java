package tknpow22.wicketexample.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tknpow22.wicketexample.dto.Employee;

/**
 * 従業員データの操作を定義する
 */
public class EmployeeDao extends DaoBase {

	public List<Employee> findAllEmployees() {

		List<Employee> result = new ArrayList<>();

		final String sql = ""
				+ " SELECT"
				+ "   EmployeeId"
				+ "   , EmployeeName"
				+ "   , Age"
				+ " FROM Employees"
				;

		try {
			List<Map<String, Object>> list = executeQuery(sql);
			for (Map<String, Object> value : list) {
				result.add(mapToEmployee(value));
			}

		} catch (SQLException ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	public Employee findEmployeeById(String employeeId) {

		Employee result = null;

		final String sql = ""
				+ " SELECT"
				+ "   EmployeeId"
				+ "   , EmployeeName"
				+ "   , Age"
				+ " FROM Employees"
				+ " WHERE EmployeeId = ?"
				;

		try {
			List<Map<String, Object>> list = executeQuery(sql, employeeId);
			if (0 < list.size()) {
				result = mapToEmployee(list.get(0));
			}

		} catch (SQLException ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	public List<Employee> findEmployeesByName(String name) {

		List<Employee> result = new ArrayList<>();

		final String sql = ""
				+ " SELECT"
				+ "   EmployeeId"
				+ "   , EmployeeName"
				+ "   , Age"
				+ " FROM Employees"
				+ " WHERE EmployeeName LIKE ?"
				;

		try {
			List<Map<String, Object>> list = executeQuery(sql, String.format("%%%s%%", name));
			for (Map<String, Object> value : list) {
				result.add(mapToEmployee(value));
			}

		} catch (SQLException ex) {
			logger.error("{}", ex);
		}

		return result;
	}

	private Employee mapToEmployee(Map<String, Object> map) {
		return new Employee((String) map.get("EmployeeId"), (String) map.get("EmployeeName"), (int) map.get("Age"));
	}
}
