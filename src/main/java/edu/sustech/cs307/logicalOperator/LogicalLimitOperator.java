package edu.sustech.cs307.logicalOperator;

public class LogicalLimitOperator extends LogicalOperator {
    private int limitValue; // 占位符类型，需要根据实际代码库修改

    public LogicalLimitOperator() {
        super(null); // 或者根据需要传递适当的输入
    }

    public int getLimitValue() {
        return limitValue;
    }

    @Override
    public String toString() {
        return "LogicalLimitOperator(" +
                "limitValue=" + limitValue +
                ')';
    }
}
