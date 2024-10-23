package com.app.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.main.EmployeeApp;
import com.model.EmployeeDetails;

public class EmployeeAppTest {

	private HashMap<Integer, List<EmployeeDetails>> employeeDetailsManagerIdMap;
	private EmployeeApp employeeApp;
	Map<Integer, EmployeeDetails> allEmployeesByIDMap;

	@BeforeEach
	public void setUp() {
		employeeApp = new EmployeeApp();
		employeeDetailsManagerIdMap = new HashMap<Integer, List<EmployeeDetails>>();
		createTestDataForEmpManager(employeeDetailsManagerIdMap);
		allEmployeesByIDMap = employeeDetailsManagerIdMap.values().stream().flatMap(List::stream)
				.collect(Collectors.toMap(emp -> emp.getId(), emp -> emp));
	}

	private void createTestDataForEmpManager(HashMap<Integer, List<EmployeeDetails>> employeeDetailsManagerIdMap) {
		List<EmployeeDetails> list1 = new ArrayList<EmployeeDetails>();
		list1.add(new EmployeeDetails(123, "Joe Doe", 90000D, 0));
		employeeDetailsManagerIdMap.put(0, list1);

		List<EmployeeDetails> list2 = new ArrayList<EmployeeDetails>();
		list2.add(new EmployeeDetails(124, "Martin	Chekov", 45000D, 123));
		list2.add(new EmployeeDetails(125, "Bob Ronstad", 47000D, 123));
		employeeDetailsManagerIdMap.put(123, list2);

		List<EmployeeDetails> list3 = new ArrayList<EmployeeDetails>();
		list3.add(new EmployeeDetails(300, "Alice Hasacat", 25000D, 124));
		employeeDetailsManagerIdMap.put(124, list3);

		List<EmployeeDetails> list4 = new ArrayList<EmployeeDetails>();
		list4.add(new EmployeeDetails(305, "Brett HardLeaf", 34000D, 300));
		employeeDetailsManagerIdMap.put(300, list4);
	}

	@Test
	public void testManagerWithLowSalary() {
		HashMap<Integer, String> salaryDiffMap = employeeApp.analyseManagersSalary(employeeDetailsManagerIdMap,
				allEmployeesByIDMap);
		assertNotNull(salaryDiffMap);
		assertTrue(salaryDiffMap.containsKey(300));
		assertEquals(salaryDiffMap.get(300), "Lesser by 2200.0");
	}

	@Test
	public void testManagerWithHighSalary() {
		HashMap<Integer, String> salaryDiffMap = employeeApp.analyseManagersSalary(employeeDetailsManagerIdMap,
				allEmployeesByIDMap);
		assertNotNull(salaryDiffMap);
		assertTrue(salaryDiffMap.containsKey(123));
		assertTrue(salaryDiffMap.containsKey(124));
		assertEquals(salaryDiffMap.get(124), "Higher by 7500.0");
		assertEquals(salaryDiffMap.get(123), "Higher by 21000.0");
	}

	@Test
	public void testManagerHierarchy() {
		HashMap<Integer, Integer> employeeLevelMap = employeeApp.getEmployeesWithLongReportingLine(allEmployeesByIDMap,
				2);
		assertNotNull(employeeLevelMap);
		assertTrue(employeeLevelMap.containsKey(305));
		assertEquals(employeeLevelMap.get(305), 3);
	}

}
