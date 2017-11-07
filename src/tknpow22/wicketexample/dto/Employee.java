package tknpow22.wicketexample.dto;

import java.io.Serializable;

/**
 * 従業員データ
 */
public class Employee implements Serializable {

	private String employeeId;
	private String employeeName;
	private int age;

	public Employee(String employeeId, String employeeName, int age) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.age = age;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public int getAge() {
		return age;
	}
}
