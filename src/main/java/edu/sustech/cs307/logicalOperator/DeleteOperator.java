package edu.sustech.cs307.logicalOperator;

import java.util.Collections;

public class DeleteOperator extends LogicalOperator {
    private final String tableName;

    public DeleteOperator(LogicalOperator child) {
        super(Collections.singletonList(child));
        this.tableName = ((TableScanOperator) child).getTableName();
    }

    @Override
    public String toString() {
        return "DeleteOperator(table=" + tableName + ")\n ├── " + children.get(0);
    }
}