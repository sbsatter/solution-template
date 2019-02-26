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
	private static final Pattern tableColumnIdentifierPattern ;

	static {
		//language=RegExp
		String regEx = "select ([a-z0-9_,.\\s*]+) from ([a-z0-9_]+)(?:\\s+([a-z0-9_]+))? join ([a-z0-9_]+)(?:\\s+([a-z0-9_]+))? on ([a-z0-9_]+)\\.([a-z0-9_]+)\\s*([=<>])\\s*([a-z0-9_]+)\\.([a-z0-9_]+)\\s*";
		pattern = Pattern.compile(regEx);
		// finds the columns selected in each round
		tableColumnIdentifierPattern = Pattern.compile("(?:([a-z0-9_]+)\\.([a-z0-9_]+|\\*),?)");
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
//		printLine("Done table initialization");

		Integer numberOfQueries = readLineAsInteger();

		for (int queryNumber = 0; queryNumber < numberOfQueries; queryNumber++) {
			String query = collectQuery();
			JoinOperation joinOperation = parseQuery(query.toLowerCase());
			if (joinOperation != null) {
				printLine(join(joinOperation));
			}
		}
//
	}


	private JoinOperation parseQuery(String query) {
		Matcher matcher = pattern.matcher(query);
		if (matcher.matches()) {
//			printLine("Found groups: " + matcher.groupCount());
			for (int i = 0; i <= matcher.groupCount(); i++) {
//				printLine(String.format("Group no: %d => %s", i, matcher.group(i)));
			}
			return new JoinOperation(matcher);
		}
		return null;
	}

	private void initiateTable(Integer numberOfTables) {
		// dimensions -> row, cols
		String tableName = readLine().trim();
		int[] dimensions = Arrays.stream(readLine().trim().split("\\s")).mapToInt(Integer::parseInt).toArray();
		Table table = new Table(tableName, dimensions[0]);

//		printLine(String.format("TEST: Dimensions: %d, %d", dimensions[0], dimensions[1]));
//		printLine(String.format("TEST: Table name: %s", table.getName()));

		String [] columnNames = readLine().trim().split("\\s");
		// todo check for optimization
		List<String> columns = Arrays.stream(columnNames).collect(Collectors.toList());
		table.setColumns(columns);


//		printLine("TEST: Collected column names");
		for (int row = 0; row < dimensions[0]; row++) {
			// todo check for optimization
			String [] values = readLine().trim().split("\\s");
			Integer [] rowData = new Integer[values.length];
			for (int idx = 0; idx < rowData.length; idx++) {
				rowData[idx] = Integer.parseInt(values[idx]);
			}
			table.addRow(row, rowData);
		}
//		printLine("Table :" + table);
		tables.put(table.getName(), table);

	}

	private Table join(JoinOperation joinOperation) {
		Table primaryTable = tables.get(joinOperation.getTable1()); // Table based on which the join op is performed
		Table secondaryTable = tables.get(joinOperation.getTable2()); // Table which joins with the primary table
		String primaryTableJoinColumn = joinOperation.getLeftTable().equals(joinOperation.getTable1()) ? joinOperation.getLeftTableColumn() : joinOperation.getRightTableColumn();
		String secondaryTableJoinColumn = joinOperation.getRightTable().equals(joinOperation.getTable2()) ? joinOperation.getRightTableColumn() : joinOperation.getLeftTableColumn();
		Row [] row1 = primaryTable.getRows();
		Row [] row2 = secondaryTable.getRows();
		Table result = new Table();
		List<String> columns = new ArrayList<>();
//		columns.addAll(primaryTableColumns);
//		columns.addAll(secondaryTableColumns);
		// populate primaryTableColumns and secondaryTableColumns
		columns = getRequiredColumns(joinOperation);
		result.setColumns(columns);
		Integer index1 = primaryTable.getColumns().indexOf(primaryTableJoinColumn);
		Integer index2 = secondaryTable.getColumns().indexOf(secondaryTableJoinColumn);
		List<Row> resultRows = new ArrayList<>();
		for (int i = 0; i < row1.length; i++) {
			List<Integer> data1 = row1[i].getData();
			for (int j = 0; j < row2.length; j++) {
				List<Integer> data2 = row2[j].getData();
				if (data1.get(index1).equals(data2.get(index2))) {
					Row row = new Row();
					row.addData(getOrderedData(primaryTable.getColumns(), secondaryTable.getColumns(), columns, data1, data2));
					resultRows.add(row);
				}
			}
		}
		result.addRows(resultRows);
		return result;
	}

	private List<String> getRequiredColumns(JoinOperation joinOperation) {
		String selected = joinOperation.getColumns();
		List<String> list = new ArrayList<>();
		Table table1 = tables.get(joinOperation.getTable1()), table2 = tables.get(joinOperation.getTable2());
		if (selected.equals("*")) {
			list.addAll(table1.getColumns());
			list.addAll(table2.getColumns());
			return list;
		}
		Matcher matcher = tableColumnIdentifierPattern.matcher(selected);
		matcher.reset();
		while (matcher.find()) {
			Table temp = null;
			if (joinOperation.resolveTableName(matcher.group(1)).equals(table1.getName())) {
				// table 1 column
				temp = table1;
			} else if (joinOperation.resolveTableName(matcher.group(1)).equals(table2.getName())) {
				// table 2 column
				temp = table2;
			}
			String colName = matcher.group(2);
			if (colName.equals("*")) {
				list.addAll(temp.getColumns());
			} else {
				list.add(colName);
			}
		}
		return list;
	}

	private String collectQuery() {
		StringBuilder builder = new StringBuilder();
		for (int lineNumber = 0; lineNumber < QUERY_MAX_LINE_NUM; lineNumber++) {
			builder.append(readLine().trim().trim()).append(" "); // read trimmed query lines
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
			columns.forEach(col -> builder.append(col).append(" "));
			builder.append("\n");
			Arrays.asList(rows).forEach(row -> builder.append(row).append("\n"));
			return builder.toString();
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

		List<Integer> getData(List<Integer> selectedDataIndices) {
			List<Integer> preparedData = new ArrayList<>();
			selectedDataIndices.iterator().forEachRemaining(idx -> preparedData.add(data.get(idx)));
			return preparedData;
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
			data.forEach(integer -> builder.append(integer).append(" "));
			return builder.toString();
		}
	}

	class JoinOperation {
		private String query;
		private String columns;
		private String table1;
		private String table2;
		private String tableAlias1;
		private String tableAlias2;
		private String leftTable;
		private String leftTableColumn;
		private String operator;
		private String rightTable;
		private String rightTableColumn;

		JoinOperation(Matcher matcher) {
			this.query = matcher.group(0);
			this.columns = matcher.group(1);
			this.table1 = matcher.group(2);
			this.tableAlias1 = matcher.group(3);
			this.table2 = matcher.group(4);
			this.tableAlias2 = matcher.group(5);
			this.leftTable = matcher.group(6);
			this.leftTableColumn = matcher.group(7);
			this.operator = matcher.group(8);
			this.rightTable = matcher.group(9);
			this.rightTableColumn = matcher.group(10);
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public String getColumns() {
			return columns;
		}

		public void setColumns(String columns) {
			this.columns = columns;
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
			return resolveTableName(this.leftTable);
		}

		public void setLeftTable(String leftTable) {
			this.leftTable = leftTable;
		}

		public String getRightTable() {
			return resolveTableName(this.rightTable);
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


		private String resolveTableName(String tableName) {
			int empty = isEmpty(this.tableAlias1, this.tableAlias2);
			if (empty == 0) {
				// aliases are empty, that implies the table name is provided
				return tableName;
			} else if (tableName.equals(tableAlias1) || tableName.equals(this.table1)) {
				return table1;
			} else if (tableName.equals(tableAlias2) || tableName.equals(this.table2)) {
				return table2;
			} else return ""; // Should never come to this point
		}
	}

	private int isEmpty(String ... args) {
		int result = 0;
		for (String str :
				args) {
			int score = (str == null || str.trim().isEmpty())? 0:1;
			result = (result << 1) | score;
		}
		return result;
	}

	private List<Integer> getOrderedData(List<String> allPrimaryTableCols, List<String> allSecondaryTableCols, List<String> selectedColumns, List<Integer> primaryData, List<Integer> secondaryData) {
		List<Integer> selectedData = new ArrayList<>();
		for (String selectedColumn : selectedColumns) {
			List<String> list;
			List<Integer> data;
			if (allPrimaryTableCols.contains(selectedColumn)) {
				list = allPrimaryTableCols;
				data = primaryData;
			} else if (allSecondaryTableCols.contains(selectedColumn)) {
				list = allSecondaryTableCols;
				data = secondaryData;
			} else {
				// should never occur, will throw index out of bounds eventually
				list = new ArrayList<>();
				data = new ArrayList<>();
			}
			int index = list.indexOf(selectedColumn);
			selectedData.add(data.get(index));
		}
		return selectedData;
	}

}
