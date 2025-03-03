package edu.sustech.cs307.optimizer;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.exception.ExceptionTypes;
import edu.sustech.cs307.logicalOperator.*;
import edu.sustech.cs307.physicalOperator.*;
import edu.sustech.cs307.system.DBManager;
import edu.sustech.cs307.meta.TableMeta;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;

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
        } else if (logicalOp instanceof LogicalSortOperator) {
            return handleSort(dbManager, (LogicalSortOperator) logicalOp);
        } else if (logicalOp instanceof LogicalLimitOperator) {
            return handleLimit(dbManager, (LogicalLimitOperator) logicalOp);
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
        // PhysicalOperator inputOp = generateOperator(dbManager,
        // logicalProjectOp.getInput());

        for (var item : logicalProjectOp.getSelectItems()) {
            System.out.println(item);
        }
        // return new ProjectOperator(inputOp, logicalProjectOp.getSelectItems());
        return null;
    }

    private static PhysicalOperator handleSort(DBManager dbManager, LogicalSortOperator logicalSortOp)
            throws DBException {
        PhysicalOperator inputOp = generateOperator(dbManager, logicalSortOp.getInput());
        return new SortOperator(inputOp, logicalSortOp.getOrderByElements());
    }

    private static PhysicalOperator handleLimit(DBManager dbManager, LogicalLimitOperator logicalLimitOp)
            throws DBException {
        PhysicalOperator inputOp = generateOperator(dbManager, logicalLimitOp.getInput());
        return new LimitOperator(inputOp, logicalLimitOp.getLimitValue());
    }

    private static PhysicalOperator handleInsert(DBManager dbManager, LogicalInsertOperator logicalInsertOp) {
        // TODO: Implement handleInsert
        return null;
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
