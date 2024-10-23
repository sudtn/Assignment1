package com.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.model.EmployeeDetails;

public class EmployeeApp {

	public static void main(String[] args) {
		EmployeeApp e = new EmployeeApp();
		HashMap<Integer, List<EmployeeDetails>> employeeDetailsManagerIdMap = e.getEmployeeMgrDetailsFromCSV();
		Map<Integer, EmployeeDetails> allEmployeesByIDMap = employeeDetailsManagerIdMap.values().stream()
				.flatMap(List::stream).collect(Collectors.toMap(emp -> emp.getId(), emp -> emp));
		e.analyseManagersSalary(employeeDetailsManagerIdMap, allEmployeesByIDMap);
		e.getEmployeesWithLongReportingLine(allEmployeesByIDMap, 4);
	}

	public HashMap<Integer, List<EmployeeDetails>> getEmployeeMgrDetailsFromCSV() {
		HashMap<Integer, List<EmployeeDetails>> employeesbyManagerIdMap = new HashMap<Integer, List<EmployeeDetails>>();
		String line;
		try {
			FileReader in = new FileReader("C:/Employee.csv");
			BufferedReader br = new BufferedReader(in);

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length < 4) {
					throw new RuntimeException("Incorrect number of columns");
				}
				// Exclude the initial row with column names by using number regex
				if (data[0].matches("[0-9]+")) {
					Integer id = Integer.parseInt(data[0]);
					String firstName = data[1];
					String lastName = data[2];
					Double salary = Double.parseDouble(data[3]);
					// for ceo no manager, his mgr id will be 0
					Integer managerId = data.length > 4 ? Integer.parseInt(data[4]) : 0;
					if (employeesbyManagerIdMap.containsKey(managerId)) {
						employeesbyManagerIdMap.get(managerId)
								.add(new EmployeeDetails(id, firstName + " " + lastName, salary, managerId));
					} else {
						List<EmployeeDetails> newEmployeeList = new ArrayList<EmployeeDetails>();
						newEmployeeList.add(new EmployeeDetails(id, firstName + " " + lastName, salary, managerId));
						employeesbyManagerIdMap.put(managerId, newEmployeeList);
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return employeesbyManagerIdMap;
	}

	// if it is less than 20% than avg sal of direct subordinates
	// managers more than 50% than avg sal of direct subordinates
	public HashMap<Integer, String> analyseManagersSalary(
			HashMap<Integer, List<EmployeeDetails>> employeeDetailsManagerIdMap,
			Map<Integer, EmployeeDetails> allEmployeesMap) {
		HashMap<Integer, String> salaryDiffMap = new HashMap<Integer, String>();

		employeeDetailsManagerIdMap.entrySet().stream().filter(entry -> entry.getKey() != 0).forEach(entry -> {
			List<EmployeeDetails> subordinatesList = entry.getValue();
			Double avgSalaryOfSubordinates = subordinatesList.stream().mapToDouble(sub -> sub.getSalary()).average()
					.getAsDouble();
			Double minSalaryForManager = avgSalaryOfSubordinates - (0.2 * avgSalaryOfSubordinates);
			Double maxSalaryForManager = avgSalaryOfSubordinates + (0.5 * avgSalaryOfSubordinates);
			Double salaryDiff = 0D;
			EmployeeDetails mgrEmployeeDetail = null;
			Integer mgrId = entry.getKey();
			mgrEmployeeDetail = allEmployeesMap.get(mgrId);
			if (mgrEmployeeDetail.getSalary() < minSalaryForManager) {
				salaryDiff = minSalaryForManager - mgrEmployeeDetail.getSalary();
				salaryDiffMap.put(mgrEmployeeDetail.getId(), "Lesser by " + salaryDiff);
				System.out.println(mgrEmployeeDetail.getName() + "with ID : " + mgrEmployeeDetail.getId()
						+ " has a salary which is less by " + salaryDiff + "Rs");
			} else if (mgrEmployeeDetail.getSalary() > maxSalaryForManager) {
				salaryDiff = mgrEmployeeDetail.getSalary() - maxSalaryForManager;
				System.out.println(mgrEmployeeDetail.getName() + "with ID : " + mgrEmployeeDetail.getId()
						+ " has a salary which is higher by " + salaryDiff + "Rs");
				salaryDiffMap.put(mgrEmployeeDetail.getId(), "Higher by " + salaryDiff);
			}
		});

		return salaryDiffMap;
	}

	// employees which have more than 4 mgrs bw them and CEO
	public HashMap<Integer, Integer> getEmployeesWithLongReportingLine(
			Map<Integer, EmployeeDetails> allEmployeesByIDMap, Integer level) {
		HashMap<Integer, Integer> empLevelMap = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, EmployeeDetails> entry : allEmployeesByIDMap.entrySet()) {
			Integer employeeId = entry.getKey();
			Integer managerId = entry.getValue().getManagerId();

			Integer managerCount = 0;
			while (managerId != 0) {
				managerCount++;
				EmployeeDetails manager = allEmployeesByIDMap.get(managerId);
				managerId = manager.getManagerId();

				if (managerCount > level) {
					empLevelMap.put(employeeId, managerCount);
					System.out.println("Employee " + allEmployeesByIDMap.get(employeeId).getName() + " has "
							+ managerCount + " managers between them and the CEO.");
					break;
				}
			}
		}
		return empLevelMap;
	}
}
