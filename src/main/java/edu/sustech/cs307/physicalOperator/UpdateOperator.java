package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.tuple.Tuple;

import java.util.List;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.update.UpdateSet;

public class UpdateOperator implements PhysicalOperator {
    private PhysicalOperator inputOperator;
    private String tableName;
    private List<UpdateSet> updateSets;
    private Expression whereExpr;

    public UpdateOperator(PhysicalOperator inputOperator, String tableName, List<UpdateSet> updateSets, Expression whereExpr) {
        this.inputOperator = inputOperator;
        this.tableName = tableName;
        this.updateSets = updateSets;
        this.whereExpr = whereExpr;
    }

    @Override
    public boolean hasNext() {
        // TODO: Implement hasNext
        return false;
    }

    @Override
    public void Begin() {
        // TODO: Implement Begin
    }

    @Override
    public void Next() {
        // TODO: Implement Next
    }

    @Override
    public Tuple Current() {
        // TODO: Implement Current
        return null;
    }

    @Override
    public void Close() {
        // TODO: Implement Close
    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        // TODO: Implement outputSchema
        return null;
    }

    public void reset() {
        //TODO: Implement reset
    }

    public Tuple getNextTuple() {
        //TODO: Implement getNextTuple
        return null;
    }

    public void close() {
        //TODO: Implement close
    }
}
