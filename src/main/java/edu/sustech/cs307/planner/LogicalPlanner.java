package edu.sustech.cs307.planner;

import java.io.StringReader;
import java.util.Scanner;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.drop.Drop;

import edu.sustech.cs307.exception.ExceptionTypes;
import edu.sustech.cs307.logicalOperator.*;
import edu.sustech.cs307.exception.DBException;


public class LogicalPlanner {
    public static LogicalOperator plan(String sql) throws DBException {
        JSqlParser parser = new CCJSqlParserManager();
        Statement stmt = null;
        try {
            stmt = parser.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            throw new DBException(ExceptionTypes.InvalidSQL(sql, e.getMessage()));
        }
        System.out.println(stmt);
        LogicalOperator operator = null;
        if (stmt instanceof Select) {
            operator = handleSelect((Select) stmt);
        } else if (stmt instanceof Insert) {
            operator = handleInsert((Insert) stmt);
        } else if (stmt instanceof Update) {
            operator = handleUpdate((Update) stmt);
        } else if (stmt instanceof Delete) {
            operator = handleDelete((Delete) stmt);
        } else if (stmt instanceof CreateTable) {
            //TODO
        } else if (stmt instanceof Drop) {
            //TODO
        } else {
            throw new DBException(ExceptionTypes.UnsupportedCommand((stmt.toString())));
        }
        System.out.println(operator);
        return null;
    }

    private static LogicalOperator handleSelect(Select selectStmt) {
        PlainSelect plainSelect = selectStmt.getPlainSelect();

        LogicalOperator root = new TableScanOperator(plainSelect.getFromItem().toString());

        if (plainSelect.getWhere() != null) {
            root = new FilterOperator(root, plainSelect.getWhere());
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                root = new JoinOperator(root, new TableScanOperator(join.getRightItem().toString()), join.getOnExpressions());
            }
        }

        root = new ProjectOperator(root, plainSelect.getSelectItems());

        return root;
    }

    private static LogicalOperator handleInsert(Insert insertStmt) {
        return new InsertOperator(insertStmt.getTable().getName(), insertStmt.getColumns(), insertStmt.getValues());
    }

    private static LogicalOperator handleUpdate(Update updateStmt) {
        LogicalOperator root = new TableScanOperator(updateStmt.getTable().getName());
        if (updateStmt.getWhere() != null) {
            root = new FilterOperator(root, updateStmt.getWhere());
        }
        return new UpdateOperator(root, updateStmt.getUpdateSets(), updateStmt.getExpressions());
    }

    private static LogicalOperator handleDelete(Delete deleteStmt) {
        LogicalOperator root = new TableScanOperator(deleteStmt.getTable().getName());
        if (deleteStmt.getWhere() != null) {
            root = new FilterOperator(root, deleteStmt.getWhere());
        }
        return new DeleteOperator(root);
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String sql = scanner.nextLine();
            try {
                plan(sql);
            } catch (DBException e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
