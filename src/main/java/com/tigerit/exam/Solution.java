package com.tigerit.exam;


import java.util.*;
import java.util.stream.Collectors;

import static com.tigerit.exam.IO.*;

/**
 * All of your application logic should be placed inside this class.
 * Remember we will load your application from our custom container.
 * You may add private method inside this class but, make sure your
 * application's execution points start from inside run method.
 */
public class Solution implements Runnable {
	private static final int QUERY_MAX_LINE_NUM = 4;
	private static final HashMap<String, Table> tables = new HashMap<>();
	// Maintain the list of queries
	private static final List<String> queries = new ArrayList<>();
	
	@Override
    public void run() {
		// application entry point
	    Integer numberOfTestCases = readLineAsInteger();
	    // iterate over all the test cases
		for (int caseNumber = 1; caseNumber <= numberOfTestCases; caseNumber++) {
			testCase(caseNumber);
		}
    }
	
	private void testCase(Integer caseNumber) {
    	printLine(String.format("Test: %d", caseNumber));
    	Integer numberOfTables = readLineAsInteger();
		for (int tableNumber = 0; tableNumber < numberOfTables; tableNumber++) {
			initiateTable(numberOfTables);
		}
		printLine("Done table initialization");
		
		Integer numberOfQueries = readLineAsInteger();

		for (int queryNumber = 0; queryNumber < numberOfQueries; queryNumber++) {
			String query = collectQuery();
			queries.add(query);
		}
		
		printLine(queries);
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
		// dimensions -> row, cols
		int[] dimensions = Arrays.stream(readLine().split("\\s")).mapToInt(Integer::parseInt).toArray();
		Table table = new Table(readLine(), dimensions[0]);
		
		printLine(String.format("TEST: Dimensions: %d, %d", dimensions[0], dimensions[1]));
		printLine(String.format("TEST: Table name: %s", table.getName()));
		
		String [] columnNames = readLine().split("\\s");
		// todo check for optimization
		List<String> columns = Arrays.stream(columnNames).collect(Collectors.toList());
		table.setColumns(columns);
		
		
		printLine("TEST: Collected column names");
		for (int row = 0; row < dimensions[0]; row++) {
			// todo check for optimization
			String [] values = readLine().split("\\s");
			Integer [] rowData = new Integer[values.length];
			for (int idx = 0; idx < rowData.length; idx++) {
				rowData[idx] = Integer.parseInt(values[idx]);
			}
			table.addRow(row, rowData);
		}
		printLine("Table :" + table);
		
	}
	
	class Table {
    	String name;
    	List<String> columns;
    	Row [] rows;
		
		public Table(String name, Integer totalRows) {
			this.name = name;
			this.rows = new Row [totalRows];
		}
		
		public List<String> getColumns() {
			return columns;
		}
		
		public void setColumns(List<String> columns) {
			this.columns = columns;
		}
	    
	    String getName() {
    		return this.name;
	    }
	    
	    void setName(String name) {
    		this.name = name;
	    }
	    
	    void addRow(Integer index, Integer [] data) {
    		rows[index] = new Row(data);
	    }
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			columns.forEach(col -> builder.append(col).append("\t"));
			builder.append("\n");
			Arrays.asList(rows).forEach(row -> builder.append(row).append("\n"));
			return String.format("Table [%s]\n%s", this.name, builder.toString());
		}
	}
	
	
	class Row {
		List<Integer> data;
		
		public Row() {}
		public Row(Integer [] data) {
			this.data = Arrays.asList(data);
		}
		
		void setData(List<Integer> data) {
			this.data = data;
		}
		
		public List<Integer> getData() {
			return data;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			data.forEach(integer -> builder.append(integer).append("\t"));
			return builder.toString();
		}
	}
}
