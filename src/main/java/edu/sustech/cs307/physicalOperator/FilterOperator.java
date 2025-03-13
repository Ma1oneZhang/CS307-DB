package edu.sustech.cs307.physicalOperator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;

import edu.sustech.cs307.value.ValueComparer;
import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.tuple.Tuple;
import edu.sustech.cs307.meta.TabCol;

import java.util.ArrayList;
import java.util.Collection;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.value.ValueType;

public class FilterOperator implements PhysicalOperator {
    private PhysicalOperator input;
    private Collection<Expression> whereExpr;
    private Tuple currentTuple;
    private boolean isOpen = false;
    private String tableName; // Add tableName field

    public FilterOperator(PhysicalOperator input, Expression whereExpr) { // Add tableName parameter
        this.input = input;
        this.whereExpr = new ArrayList<Expression>();
        this.whereExpr.add(whereExpr);
    }

    public FilterOperator(PhysicalOperator input, Collection<Expression> whereExpr) { // Add tableName parameter
        this.input = input;
        this.whereExpr = whereExpr;
    }

    @Override
    public boolean hasNext() throws DBException {
        if (!isOpen)
            return false;
        // Keep fetching next tuple until condition is met or no more tuples
        while (input.hasNext()) {
            input.Next();
            Tuple tuple = input.Current();
            if (tuple == null)
                return false; // No more tuples from input
            assert (this.whereExpr.size() == 1);
            if (tuple.eval_expr((Expression) this.whereExpr.toArray()[0])) {
                this.currentTuple = tuple;
                return true; // Found tuple that satisfies condition
            }
        }
        return false; // No more tuples satisfy condition
    }

    @Override
    public void Begin() throws DBException {
        input.Begin();
        isOpen = true;
    }

    @Override
    public void Next() throws DBException {
        // do nothing
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
