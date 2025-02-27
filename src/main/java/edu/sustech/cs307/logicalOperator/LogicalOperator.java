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

    public abstract String toString();  // 用于打印逻辑计划树

}
