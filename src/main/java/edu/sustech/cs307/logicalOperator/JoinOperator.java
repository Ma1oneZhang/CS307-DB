package edu.sustech.cs307.logicalOperator;

import net.sf.jsqlparser.expression.Expression;

import java.util.Arrays;
import java.util.Collection;

public class JoinOperator extends LogicalOperator {
    private final Collection<Expression> onExpressions;

    public JoinOperator(LogicalOperator left, LogicalOperator right, Collection<Expression> onExpressions) {
        super(Arrays.asList(left, right));
        this.onExpressions = onExpressions;
    }

    public Collection<Expression> getOnExpressions() {
        return onExpressions;
    }

    @Override
    public String toString() {
        return "JoinOperator(condition=" + onExpressions + ")\n ├── " + children.get(0) + "\n ├── " + children.get(1);
    }
}
