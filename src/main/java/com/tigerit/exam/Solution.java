package com.tigerit.exam;


import java.util.*;

import static com.tigerit.exam.IO.*;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
public class Solution implements Runnable {
	private static final int QUERY_MAX_LINE_NUM = 4;
	
	@Override
    public void run() {
        // your application entry point

        // sample input process
//        String string = readLine();
//
//        Integer integer = readLineAsInteger();

        // sample output process
//        printLine(string);
//        printLine(integer);
	   
	    Integer numberOfTestCases = readLineAsInteger();
	    
	    testCase(numberOfTestCases);
	   
	   
	   
    }
	
	private void testCase(Integer caseNumber) {
    	printLine(String.format("Test: %d", caseNumber));
    	Integer numberOfTables = readLineAsInteger();
		for (int tableNumber = 0; tableNumber < numberOfTables; tableNumber++) {
			initiateTable(numberOfTables);
		}
		
		Integer numberOfQueries = readLineAsInteger();
		List<String> queries = new ArrayList<>();
		for (int queryNumber = 0; queryNumber < numberOfQueries; queryNumber++) {
			String query = collectQuery();
			queries.add(query);
		}
	}
	
	private String collectQuery() {
		StringBuilder builder = new StringBuilder();
		for (int lineNumber = 0; lineNumber < QUERY_MAX_LINE_NUM; lineNumber++) {
			builder.append(readLine());
		}
		readLine(); // empty input line
		return builder.toString();
	}
	
	private void initiateTable(Integer numberOfTables) {
		String tableName = readLine();
		// dimensions -> row, cols
		int[] dimensions = Arrays.stream(readLine().split("\\s")).mapToInt(Integer::parseInt).toArray();
		printLine(String.format("TEST: Dimensions: %d, %d", dimensions[0], dimensions[1]));
		printLine(String.format("TEST: Table name: %s", tableName));
		String [] columnNames = readLine().split("\\s");
		printLine("TEST: Collected column names");
		for (int row = 0; row < dimensions[0]; row++) {
			int[] rowData = Arrays.stream(readLine().split("\\s")).mapToInt(Integer::parseInt).toArray();
		}
		
		
	}
	
	class Table {
    	String name;
    	Rows rows;
	}
	
	
	class Rows {
		Set<String> columns;
		List<Integer> data;
	}
}
