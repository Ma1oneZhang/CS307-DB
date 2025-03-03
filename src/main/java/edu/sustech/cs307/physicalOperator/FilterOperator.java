package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.logicalOperator.LogicalOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column; // Correct import for Column
import net.sf.jsqlparser.expression.BinaryExpression;
import edu.sustech.cs307.value.ValueComparer;
import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.tuple.Tuple;
import edu.sustech.cs307.meta.TabCol;

import java.util.ArrayList;

import edu.sustech.cs307.exception.DBException;
import net.sf.jsqlparser.expression.StringValue; // Import StringValue
import net.sf.jsqlparser.expression.DoubleValue; // Import DoubleValue
import net.sf.jsqlparser.expression.LongValue; // Import LongValue
import edu.sustech.cs307.value.ValueType;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression; // Import AndExpression
import net.sf.jsqlparser.expression.operators.conditional.OrExpression; // Import OrExpression

public class FilterOperator implements PhysicalOperator {
    private PhysicalOperator input;
    private Expression whereExpr;
    private Tuple currentTuple;
    private boolean isOpen = false;
    private String tableName; // Add tableName field

    public FilterOperator(PhysicalOperator input, Expression whereExpr, String tableName) { // Add tableName parameter
        this.input = input;
        this.whereExpr = whereExpr;
        this.tableName = tableName; // Initialize tableName
    }

    @Override
    public boolean hasNext() {
        if (!isOpen) return false;
        // Keep fetching next tuple until condition is met or no more tuples
        while(input.hasNext()) {
            input.Next();
            Tuple tuple = input.Current();
            if (tuple == null) return false; // No more tuples from input
            if (evaluateCondition(tuple)) {
                currentTuple = tuple;
                return true; // Found tuple that satisfies condition
            }
        }
        return false; // No more tuples satisfy condition
    }

    private boolean evaluateCondition(Tuple tuple) {
        if (whereExpr instanceof AndExpression) {
            AndExpression andExpr = (AndExpression) whereExpr;
            // Recursively evaluate left and right expressions
            return evaluateCondition(tuple, andExpr.getLeftExpression()) && evaluateCondition(tuple, andExpr.getRightExpression());
        } else if (whereExpr instanceof OrExpression) {
            OrExpression orExpr = (OrExpression) whereExpr;
            // Recursively evaluate left and right expressions
            return evaluateCondition(tuple, orExpr.getLeftExpression()) || evaluateCondition(tuple, orExpr.getRightExpression());
        } else if (whereExpr instanceof BinaryExpression) {
            return evaluateBinaryExpression(tuple, (BinaryExpression) whereExpr);
        } else {
            return true; // For non-binary and non-AND expressions, just return true for now
        }
    }

    private boolean evaluateCondition(Tuple tuple, Expression expr) {
        if (expr instanceof BinaryExpression) {
            return evaluateBinaryExpression(tuple, (BinaryExpression) expr);
        } else if (expr instanceof AndExpression) {
            AndExpression andExpr = (AndExpression) expr;
            return evaluateCondition(tuple, andExpr.getLeftExpression()) && evaluateCondition(tuple, andExpr.getRightExpression());
        } else if (expr instanceof OrExpression) {
            OrExpression orExpr = (OrExpression) expr;
            // Recursively evaluate left and right expressions
            return evaluateCondition(tuple, orExpr.getLeftExpression()) || evaluateCondition(tuple, orExpr.getRightExpression());
        }
        return true; // For other expressions, return true for now
    }


    private boolean evaluateBinaryExpression(Tuple tuple, BinaryExpression binaryExpr) {
        Expression leftExpr = binaryExpr.getLeftExpression();
        Expression rightExpr = binaryExpr.getRightExpression();
        String operator = binaryExpr.getStringExpression();
        Value leftValue = null;
        Value rightValue = null;

        try {
            if (leftExpr instanceof Column) {
                leftValue = tuple.getValue(null, new TabCol(tableName, ((Column) leftExpr).getColumnName()));
            } else {
                leftValue = getConstantValue(leftExpr); // Handle constant left value
            }

            if (rightExpr instanceof Column) {
                rightValue = tuple.getValue(null, new TabCol(tableName, ((Column) rightExpr).getColumnName()));
            } else {
                rightValue = getConstantValue(rightExpr); // Handle constant right value
            }

            if (leftValue == null || rightValue == null) return false;

            int comparisonResult = ValueComparer.compare(leftValue, rightValue);
            if (operator.equals("=")) {
                return comparisonResult == 0;
            } else if (operator.equals(">")) {
                return comparisonResult > 0;
            } else if (operator.equals("<")) {
                return comparisonResult < 0;
            } else if (operator.equals(">=")) {
                return comparisonResult >= 0;
            } else if (operator.equals("<=")) {
                return comparisonResult <= 0;
            }
        } catch (DBException e) {
            e.printStackTrace(); // Handle exception properly
        }

        return false;
    }


    private Value getConstantValue(Expression expr) {
        if (expr instanceof StringValue) {
            return new Value(((StringValue) expr).getValue(), ValueType.CHAR);
        } else if (expr instanceof DoubleValue) {
            return new Value(((DoubleValue) expr).getValue(), ValueType.FLOAT);
        } else if (expr instanceof LongValue) {
            return new Value(((LongValue) expr).getValue(), ValueType.INTEGER);
        }
        return null; // Unsupported constant type
    }


    @Override
    public void Begin() {
        input.Begin();
        isOpen = true;
    }

    @Override
    public void Next() {
        if (hasNext()) {
            // hasNext() already sets currentTuple
        } else {
            currentTuple = null;
        }
    }

    @Override
    public Tuple Current() {
        return currentTuple;
    }

    @Override
    public void Close() {
        input.Close();
        isOpen = false;
        currentTuple = null;
    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        return input.outputSchema();
    }
}
