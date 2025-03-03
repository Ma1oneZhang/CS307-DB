package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class NestedLoopJoinOperator implements PhysicalOperator {
    private PhysicalOperator leftInput;
    private PhysicalOperator rightInput;
    private List<Expression> joinExprs;

    public NestedLoopJoinOperator(PhysicalOperator leftInput, PhysicalOperator rightInput, List<Expression> joinExprs) {
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.joinExprs = joinExprs;
    }

    @Override
    public boolean hasNext() {
        //TODO: implement hasNext()
        return false;
    }

    @Override
    public void Begin() {
        //TODO: implement Begin()
    }

    @Override
    public void Next() {
        //TODO: implement Next()
    }

    @Override
    public Tuple Current() {
        //TODO: implement Current()
        return null;
    }

    @Override
    public void Close() {
        //TODO: implement Close()
    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        //TODO: implement outputSchema()
        return null;
    }
}
