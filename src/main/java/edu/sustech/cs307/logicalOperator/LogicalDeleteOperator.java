package edu.sustech.cs307.logicalOperator;

import net.sf.jsqlparser.expression.Expression;

import java.util.Collections;
import java.util.List;

public class LogicalDeleteOperator extends LogicalOperator {
    private final String tableName;
    private final Expression expression;

    public LogicalDeleteOperator(LogicalOperator child, Expression expression) {
        super(Collections.singletonList(child));
        this.tableName = ((LogicalTableScanOperator) child).getTableName();
        this.expression = expression;
    }

    public String getTableName() {
        return tableName;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "DeleteOperator(table=" + tableName + ")";
    }
}