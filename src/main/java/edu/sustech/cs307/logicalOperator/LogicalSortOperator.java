package edu.sustech.cs307.logicalOperator;

import edu.sustech.cs307.meta.TabCol;
import java.util.List;

public class LogicalSortOperator extends LogicalOperator {
    private List<TabCol> orderByElements;

    public LogicalSortOperator() {
        super(null); // 或者根据需要传递适当的输入
    }

    public List<TabCol> getOrderByElements() {
        return orderByElements;
    }

    @Override
    public String toString() {
        return "LogicalSortOperator(" +
                "orderByElements=" + orderByElements +
                ')';
    }
}
