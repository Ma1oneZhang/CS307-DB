package edu.sustech.cs307.logicalOperator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.Collections;
import java.util.List;

public class InsertOperator extends LogicalOperator {
    private final String tableName;
    private final ExpressionList<Column> columns;
    private final Expression values;

    public InsertOperator(String tableName, ExpressionList<Column> columns, Expression values) {
        super(Collections.emptyList());
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
    }

    @Override
    public String toString() {
        return "InsertOperator(table=" + tableName + ", columns=" + columns + ", values=" + values + ")";
    }
}
