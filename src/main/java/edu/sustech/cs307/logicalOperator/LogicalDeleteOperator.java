package edu.sustech.cs307.logicalOperator;

import java.util.Collections;

public class LogicalDeleteOperator extends LogicalOperator {
    private final String tableName;

    public LogicalDeleteOperator(LogicalOperator child) {
        super(Collections.singletonList(child));
        this.tableName = ((LogicalTableScanOperator) child).getTableName();
    }

    @Override
    public String toString() {
        return "DeleteOperator(table=" + tableName + ")";
    }
}