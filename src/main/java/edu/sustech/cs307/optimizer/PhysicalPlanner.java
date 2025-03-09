package edu.sustech.cs307.optimizer;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;
import edu.sustech.cs307.logicalOperator.*;
import edu.sustech.cs307.physicalOperator.*;
import edu.sustech.cs307.system.DBManager;
import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.value.ValueType;
import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.meta.TableMeta;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.statement.select.Values;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhysicalPlanner {
    public static PhysicalOperator generateOperator(DBManager dbManager, LogicalOperator logicalOp) throws DBException {
        if (logicalOp instanceof LogicalTableScanOperator) {
            return handleTableScan(dbManager, (LogicalTableScanOperator) logicalOp);
        } else if (logicalOp instanceof LogicalFilterOperator) {
            return handleFilter(dbManager, (LogicalFilterOperator) logicalOp);
        } else if (logicalOp instanceof LogicalJoinOperator) {
            return handleJoin(dbManager, (LogicalJoinOperator) logicalOp);
        } else if (logicalOp instanceof LogicalProjectOperator) {
            return handleProject(dbManager, (LogicalProjectOperator) logicalOp);
        } else if (logicalOp instanceof LogicalInsertOperator) {
            return handleInsert(dbManager, (LogicalInsertOperator) logicalOp);
        } else if (logicalOp instanceof LogicalDeleteOperator) {
            return handleDelete(dbManager, (LogicalDeleteOperator) logicalOp);
        } else if (logicalOp instanceof LogicalUpdateOperator) {
            return handleUpdate(dbManager, (LogicalUpdateOperator) logicalOp);
        } else {
            throw new DBException(ExceptionTypes.UnsupportedOperator(logicalOp.getClass().getSimpleName()));
        }
    }

    private static PhysicalOperator handleTableScan(DBManager dbManager, LogicalTableScanOperator logicalTableScanOp) {
        String tableName = logicalTableScanOp.getTableName();
        TableMeta tableMeta;
        try {
            tableMeta = dbManager.getMetaManager().getTable(tableName);
        } catch (DBException e) {
            // Fallback to SeqScan if TableMeta cannot be retrieved
            return new SeqScanOperator(tableName, dbManager);
        }

        // Check if index exists for the table (for now, assume RBTreeIndex always
        // exists if index is defined)
        if (tableMeta.getIndexes() != null && !tableMeta.getIndexes().isEmpty()) {
            throw new RuntimeException("unimplement");
            // InMemoryOrderedIndex index = new InMemoryOrderedIndex();
            // return new InMemoryIndexScanOperator(index);
        } else {
            return new SeqScanOperator(tableName, dbManager);
        }
    }

    private static PhysicalOperator handleFilter(DBManager dbManager, LogicalFilterOperator logicalFilterOp)
            throws DBException {
        PhysicalOperator inputOp = generateOperator(dbManager, logicalFilterOp.getInput());
        String tableName = "";
        if (logicalFilterOp.getInput() instanceof LogicalTableScanOperator) {
            tableName = ((LogicalTableScanOperator) logicalFilterOp.getInput()).getTableName();
        }
        // TODO: Implement filter operator
        return new FilterOperator(inputOp, logicalFilterOp.getWhereExpr(), tableName);
    }

    private static PhysicalOperator handleJoin(DBManager dbManager, LogicalJoinOperator logicalJoinOp)
            throws DBException {
        PhysicalOperator leftOp = generateOperator(dbManager, logicalJoinOp.getLeftInput());
        PhysicalOperator rightOp = generateOperator(dbManager, logicalJoinOp.getRightInput());
        PhysicalOperator joinOp = new NestedLoopJoinOperator(leftOp, rightOp, logicalJoinOp.getJoinExprs());

        List<Expression> joinFilters = logicalJoinOp.getJoinExprs();
        if (joinFilters != null && !joinFilters.isEmpty()) {
            Expression filterExpr = null;
            Iterator<Expression> iterator = joinFilters.iterator();
            if (iterator.hasNext()) {
                filterExpr = iterator.next();
                while (iterator.hasNext()) {
                    filterExpr = new AndExpression(filterExpr, iterator.next());
                }
            }
            String tableName = "";
            if (logicalJoinOp.getLeftInput() instanceof LogicalTableScanOperator) {
                tableName = ((LogicalTableScanOperator) logicalJoinOp.getLeftInput()).getTableName();
            }
            joinOp = new FilterOperator(joinOp, filterExpr, tableName);
        }

        return joinOp;
    }

    private static PhysicalOperator handleProject(DBManager dbManager, LogicalProjectOperator logicalProjectOp)
            throws DBException {
        PhysicalOperator inputOp = generateOperator(dbManager, logicalProjectOp.getInput());

        for (var item : logicalProjectOp.getSelectItems()) {
            System.out.println(item);
        }
        // return new ProjectOperator(inputOp, logicalProjectOp.getSelectItems());
        return inputOp;
    }

    /**
     * 处理将逻辑插入操作转换为物理插入运算符的过程
     * 
     * @param dbManager       提供数据库操作访问的数据库管理器实例
     * @param logicalInsertOp 需要被转换的逻辑插入运算符
     * @return 准备好执行的物理插入运算符
     * @throws DBException 如果存在列不匹配、类型不匹配或无效SQL语法时抛出
     */
    @SuppressWarnings("deprecation") // for ExpressionList<?>::getExpressions
    private static PhysicalOperator handleInsert(DBManager dbManager, LogicalInsertOperator logicalInsertOp)
            throws DBException {
        var tableMeta = dbManager.getMetaManager().getTable(logicalInsertOp.tableName);
        // Process columns
        List<String> columns = new ArrayList<>();
        if (logicalInsertOp.columns != null) {
            // the length must equal to the number of columns in the table
            if (tableMeta.columns.size() != logicalInsertOp.columns.size()) {
                throw new DBException(ExceptionTypes.InsertColumnSizeMismatch());
            }
            for (int i = 0; i < logicalInsertOp.columns.size(); i++) {
                String colName = logicalInsertOp.columns.get(i).getColumnName();
                if (tableMeta.getColumnMeta(colName) == null) {
                    throw new DBException(ExceptionTypes.ColumnDoseNotExist(colName));
                }
                if (!tableMeta.columns_list.get(i).name.equals(colName)) {
                    throw new DBException(ExceptionTypes.InsertColumnNameMismatch());
                }
                columns.add(colName);
            }

        } else {
            // If no columns specified, use all table columns in order
            for (ColumnMeta columnMeta : tableMeta.columns_list) {
                columns.add(columnMeta.name);
            }
        }
        if (!(logicalInsertOp.values instanceof Values)) {
            throw new DBException(ExceptionTypes.InvalidSQL("INSERT", "Values must be an expression list"));
        }
        ExpressionList<?> valuesList = ((Values) logicalInsertOp.values).getExpressions();
        if (columns.size() != valuesList.size()) {
            throw new DBException(ExceptionTypes.InsertColumnSizeMismatch());
        }

        List<Value> values = new ArrayList<>();
        parseValue(values, valuesList, tableMeta);
        // will always be same size tuple

        // check the

        return new InsertOperator(logicalInsertOp.tableName, columns,
                values, dbManager);
    }

    private static void parseValue(List<Value> values, ExpressionList<?> valuesList, TableMeta tableMeta) throws DBException {
        for (int i = 0; i < valuesList.size(); i++) {
            var expr = valuesList.getExpressions().get(i);
            if (expr instanceof StringValue string_value) {
                if (tableMeta.columns_list.get(i).type != ValueType.CHAR) {
                    throw new DBException(ExceptionTypes.InsertColumnTypeMismatch());
                }
                String value_str = string_value.getValue();
                if (value_str.length() > 64) {
                    value_str = value_str.substring(0, 64);
                }
                values.add(new Value(value_str));
            } else if (expr instanceof DoubleValue float_value) {
                if (tableMeta.columns_list.get(i).type != ValueType.FLOAT) {
                    throw new DBException(ExceptionTypes.InsertColumnTypeMismatch());
                }
                values.add(new Value(float_value.getValue()));
            } else if (expr instanceof LongValue long_value) {
                if (tableMeta.columns_list.get(i).type != ValueType.INTEGER) {
                    throw new DBException(ExceptionTypes.InsertColumnTypeMismatch());
                }
                values.add(new Value(long_value.getValue()));
            } else if (expr instanceof ParenthesedExpressionList<?> expressionList) {
                parseValue(values, expressionList, tableMeta);
            } else {
                throw new DBException(ExceptionTypes.InvalidSQL("INSERT", "Unsupported value type in VALUES clause"));
            }
        }
    }

    private static PhysicalOperator handleDelete(DBManager dbManager, LogicalDeleteOperator logicalDeleteOp) {
        // TODO: Implement handleDelete
        return null;
    }

    private static PhysicalOperator handleUpdate(DBManager dbManager, LogicalUpdateOperator logicalUpdateOp) {
        // TODO: Implement handleUpdate
        return null;
    }
}
