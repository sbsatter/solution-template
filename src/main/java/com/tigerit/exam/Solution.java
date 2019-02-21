package com.tigerit.exam;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	private static final Pattern pattern;

	static {
		//language=RegExp
		String regEx = "select ([a-z0-9_,.\\s*]+) from ([a-z0-9_]+)(?:\\s+([a-z0-9_]+))? join ([a-z0-9_]+)(?:\\s+([a-z0-9_]+))? on ([a-z0-9_]+)\\.([a-z0-9_]+)\\s*([=<>])\\s*([a-z0-9_]+)\\.([a-z0-9_]+)\\s*";
		pattern = Pattern.compile(regEx);
	}

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
		clearTables();
    	printLine(String.format("Test: %d", caseNumber));
    	Integer numberOfTables = readLineAsInteger();
		for (int tableNumber = 0; tableNumber < numberOfTables; tableNumber++) {
			initiateTable(numberOfTables);
		}
		printLine("Done table initialization");

		Integer numberOfQueries = readLineAsInteger();

		for (int queryNumber = 0; queryNumber < numberOfQueries; queryNumber++) {
			String query = collectQuery();
			Join join = parseQuery(query.toLowerCase());
			printLine(join(join));
		}



//		printLine(queries);
//
	}


	private Join parseQuery(String query) {
		Matcher matcher = pattern.matcher(query);
		if (matcher.matches()) {
			printLine("Found groups: " + matcher.groupCount());
			for (int i = 0; i <= matcher.groupCount(); i++) {
				printLine(String.format("Group no: %d => %s", i, matcher.group(i)));
			}
			return new Join(matcher);
		}
		return null;
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

	Table join(Join join) {
		Table t1 = tables.get(join.getTable1());
		Table t2 = tables.get(join.getTable2());
		Integer index1 = t1.getColumns().indexOf(join.getLeftTableColumn());
		Integer index2 = t2.getColumns().indexOf(join.getRightTableColumn());
		Row [] row1 = t1.getRows();
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

//Table join(String table1, String table2, String alias1, String alias2, String column1, String column2) {
//		Table t1 = tables.get(table1);
//		Table t2 = tables.get(table2);
//		Integer index1 = t1.getColumns().indexOf(column1);
//		Integer index2 = t2.getColumns().indexOf(column2);
//		Row [] row1 = t1.getRows();
//		Row [] row2 = t2.getRows();
//		Table result = new Table();
//		List<String> columns = new ArrayList<>();
//		columns.addAll(t1.getColumns());
//		columns.addAll(t2.getColumns());
//		result.setColumns(columns);
//		List<Row> resultRows = new ArrayList<>();
//		for (int i = 0; i < row1.length; i++) {
//			List<Integer> data1 = row1[i].getData();
//			for (int j = 0; j < row2.length; j++) {
//				List<Integer> data2 = row2[j].getData();
//				if (data1.get(index1).equals(data2.get(index2))) {
//					Row row = new Row();
//					row.addData(data1).addAll(data2);
//					resultRows.add(row);
//				}
//			}
//		}
//		result.addRows(resultRows);
//		return result;
//	}

	private String collectQuery() {
		StringBuilder builder = new StringBuilder();
		for (int lineNumber = 0; lineNumber < QUERY_MAX_LINE_NUM; lineNumber++) {
			builder.append(readLine().trim()).append(" "); // read trimmed query lines
		}
		readLine(); // empty input line
		return builder.toString();
	}


	private void clearTables() {
		tables.clear();
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

	class Join {
		private String query;
		private String columns;
		private String table1;
		private String table2;
		private String tableAlias1;
		private String tableAlias2;
		private String leftTable;
		private String rightTable;
		private String operator;
		private String leftTableColumn;
		private String rightTableColumn;

		Join(Matcher matcher) {
			this.query = matcher.group(0);
			this.columns = matcher.group(1);
			this.table1 = matcher.group(2);
			this.table2 = matcher.group(4);
			this.tableAlias1 = matcher.group(3);
			this.tableAlias2 = matcher.group(5);
			this.leftTable = matcher.group(6);
			this.rightTable = matcher.group(9);
			this.operator = matcher.group(8);
			this.leftTableColumn = matcher.group(7);
			this.rightTableColumn = matcher.group(10);
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public String getTable1() {
			return table1;
		}

		public void setTable1(String table1) {
			this.table1 = table1;
		}

		public String getTable2() {
			return table2;
		}

		public void setTable2(String table2) {
			this.table2 = table2;
		}

		public String getTableAlias1() {
			return tableAlias1;
		}

		public void setTableAlias1(String tableAlias1) {
			this.tableAlias1 = tableAlias1;
		}

		public String getTableAlias2() {
			return tableAlias2;
		}

		public void setTableAlias2(String tableAlias2) {
			this.tableAlias2 = tableAlias2;
		}

		public String getLeftTable() {
			return leftTable;
		}

		public void setLeftTable(String leftTable) {
			this.leftTable = leftTable;
		}

		public String getRightTable() {
			return rightTable;
		}

		public void setRightTable(String rightTable) {
			this.rightTable = rightTable;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getLeftTableColumn() {
			return leftTableColumn;
		}

		public void setLeftTableColumn(String leftTableColumn) {
			this.leftTableColumn = leftTableColumn;
		}

		public String getRightTableColumn() {
			return rightTableColumn;
		}

		public void setRightTableColumn(String rightTableColumn) {
			this.rightTableColumn = rightTableColumn;
		}
	}
}
