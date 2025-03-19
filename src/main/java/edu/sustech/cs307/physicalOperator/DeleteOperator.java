package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.record.RID;
import edu.sustech.cs307.record.RecordFileHandle;
import edu.sustech.cs307.system.DBManager;
import edu.sustech.cs307.tuple.TableTuple;
import edu.sustech.cs307.tuple.TempTuple;
import edu.sustech.cs307.tuple.Tuple;

import java.util.List;
import java.util.ArrayList;

import edu.sustech.cs307.value.Value;
import edu.sustech.cs307.value.ValueType;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

public class DeleteOperator implements PhysicalOperator {
    private SeqScanOperator seqScanOperator;
    private String tableName;
    private Expression whereExpr;
    private boolean isDone;
    private int deleteCount;

    public DeleteOperator(PhysicalOperator inputOperator, String tableName, Expression whereExpr) {
        if (!(inputOperator instanceof SeqScanOperator scanOperator)) {
            throw new RuntimeException("The delete operator only accepts SeqScanOperator as input");
        }
        this.seqScanOperator = scanOperator;
        this.tableName = tableName;
        this.whereExpr = whereExpr;
        this.isDone = false;
        this.deleteCount = 0;
    }

    @Override
    public boolean hasNext() {
        return !isDone;
    }

    @Override
    public void Begin() throws DBException {
        seqScanOperator.Begin();
        RecordFileHandle fileHandle = this.seqScanOperator.getFileHandle();

        while (seqScanOperator.hasNext()) {
            seqScanOperator.Next();
            TableTuple tuple = (TableTuple) seqScanOperator.Current();
            if (whereExpr == null || tuple.eval_expr(whereExpr)) {
                fileHandle.DeleteRecord(tuple.getRID());
                deleteCount++;
            }
        }

        isDone = false;
    }

    @Override
    public void Next() {
        isDone = true;
    }

    @Override
    public Tuple Current() {
        ArrayList<Value> values = new ArrayList<>();
        values.add(new Value(deleteCount, ValueType.INTEGER));
        return new TempTuple(values);
    }

    @Override
    public void Close() {
        seqScanOperator.Close();

    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        ArrayList<ColumnMeta> outputSchema = new ArrayList<>();
        outputSchema.add(new ColumnMeta("delete", "numberOfDeletedRows", ValueType.INTEGER, 0, 0));
        return outputSchema;
    }
}
