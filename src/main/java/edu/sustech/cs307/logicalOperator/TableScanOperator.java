package edu.sustech.cs307.logicalOperator;

import java.util.Collections;

public class TableScanOperator extends LogicalOperator {
    private final String tableName;

    public TableScanOperator(String tableName) {
        super(Collections.emptyList());  // TableScan 没有子节点
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return "TableScanOperator(table=" + tableName + ")";
    }
}