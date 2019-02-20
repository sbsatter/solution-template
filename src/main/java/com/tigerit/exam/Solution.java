package com.tigerit.exam;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
		
		printLine(join("table1", "table2", null, null, "col_a", "col_c"));
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
		tables.put(table.getName(), table);
		
	}
	
	Table join(String table1, String table2, String alias1, String alias2, String column1, String column2) {
		Table t1 = tables.get(table1);
		Table t2 = tables.get(table2);
		Integer index1 = t1.getColumns().indexOf(column1);
		Integer index2 = t2.getColumns().indexOf(column2);
		Row[] row1 = t1.getRows();
		Row [] row2 = t2.getRows();
		Table result = new Table();
		List<String> columns = new ArrayList<>();
		columns.addAll(t1.getColumns());
		columns.addAll(t2.getColumns());
		result.setColumns(columns);
		List<Row> resultRows = new ArrayList<>();
		for (int i = 0; i < row1.length; i++) {
			List<Integer> data1 = row1[i].getData();
			for (int j = 0; j < row2.length; j++) {
				List<Integer> data2 = row2[j].getData();
				if (data1.get(index1).equals(data2.get(index2))) {
					Row row = new Row();
					row.addData(data1).addAll(data2);
					resultRows.add(row);
				}
			}
		}
		result.addRows(resultRows);
		return result;
	}
	
	private String collectQuery() {
		StringBuilder builder = new StringBuilder();
		for (int lineNumber = 0; lineNumber < QUERY_MAX_LINE_NUM; lineNumber++) {
			builder.append(readLine());
		}
		readLine(); // empty input line
		return builder.toString();
	}
	
	class Table {
    	private String name;
    	private List<String> columns;
    	private Row [] rows;
		
		Table(String name, Integer totalRows) {
			this.name = name;
			this.rows = new Row [totalRows];
		}
		
		Table() {}
		
		Row[] getRows() {
			return rows;
		}
		
		List<String> getColumns() {
			return columns;
		}
		
		void setColumns(List<String> columns) {
			this.columns = columns;
		}
	    
	    String getName() {
    		return this.name;
	    }
	    
	    void setName(String name) {
    		this.name = name;
	    }
	    
	    void addRows(List<Row> rows) {
			// todo check for efficiency
			this.rows = rows.toArray(new Row[0]);
//		    for (int i = 0; i < rows.size(); i++) {
//			    this.rows[i] = rows.get(1);
//		    }
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
		private final List<Integer> data;
		
		Row() {
			this.data = new ArrayList<>();
		}
		
		Row(Integer [] data) {
			this.data = Arrays.asList(data);
		}
		
		List<Integer> getData() {
			return data;
		}
		
		List<Integer> addData(Integer [] data) {
			return addData(Arrays.asList(data));
		}
		
		List<Integer> addData(List<Integer> data) {
			this.data.addAll(data);
			return this.data;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			data.forEach(integer -> builder.append(integer).append("\t"));
			return builder.toString();
		}
	}
}
