package edu.sustech.cs307.logicalOperator;

import net.sf.jsqlparser.expression.Expression;

import java.util.Collections;

public class FilterOperator extends LogicalOperator {
    private final Expression condition;

    public FilterOperator(LogicalOperator child, Expression condition) {
        super(Collections.singletonList(child));
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "FilterOperator(condition=" + condition + ")\n ├── " + children.get(0);
    }
}
