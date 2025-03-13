package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.system.DBManager;
import edu.sustech.cs307.tuple.TableTuple;
import edu.sustech.cs307.tuple.Tuple;

import java.util.List;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

public class DeleteOperator implements PhysicalOperator {
    private SeqScanOperator seqScanOperator;
    private String tableName;
    private Expression whereExpr;

    public DeleteOperator(PhysicalOperator inputOperator, String tableName, Expression whereExpr, DBManager dbManager) {
        if (!(inputOperator instanceof SeqScanOperator seqScanOperator)) {
            throw new RuntimeException("The delete operator only accepts SeqScanOperator as input");
        }
        this.seqScanOperator = seqScanOperator;
        this.tableName = tableName;
        this.whereExpr = whereExpr;
    }

    @Override
    public boolean hasNext() {
        // always return false
        return false;
    }

    @Override
    public void Begin() throws DBException {
        seqScanOperator.Begin();
        while (seqScanOperator.hasNext()) {
            // the seqscan always return a TableTuple
            TableTuple tuple = (TableTuple) seqScanOperator.Current();
            if (tuple.eval_expr(whereExpr)) {
                
            }
        }
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
        // TODO: Implement reset
    }

    public Tuple getNextTuple() {
        // TODO: Implement getNextTuple
        return null;
    }

    public void close() {
        // TODO: Implement close
    }
}
