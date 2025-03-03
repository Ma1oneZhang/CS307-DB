package edu.sustech.cs307.logicalOperator;

import java.util.List;

public abstract class LogicalOperator {
    protected List<LogicalOperator> children;

    public LogicalOperator(List<LogicalOperator> children) {
        this.children = children;
    }

    public List<LogicalOperator> getChildren() {
        return children;
    }

    public LogicalOperator getInput() {
        if (children != null && !children.isEmpty()) {
            return children.get(0);
        }
        return null;
    }

    public abstract String toString(); // 用于表示算子的字符串形式
}
