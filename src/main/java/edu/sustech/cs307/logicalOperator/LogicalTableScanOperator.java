package edu.sustech.cs307.logicalOperator;

import edu.sustech.cs307.system.DBManager;

import java.util.Collections;

public class LogicalTableScanOperator extends LogicalOperator {
    private final String tableName;
    private final DBManager dbManager;

    public LogicalTableScanOperator(String tableName, DBManager dbManager) {
        super(Collections.emptyList());  // TableScan 没有子节点
        this.tableName = tableName;
        this.dbManager = dbManager;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return "TableScanOperator(table=" + tableName + ")";
    }
}