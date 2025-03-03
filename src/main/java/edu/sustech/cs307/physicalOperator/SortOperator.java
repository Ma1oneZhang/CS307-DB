package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;

import java.util.List;
import java.util.ArrayList;
import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.tuple.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;

public class SortOperator implements PhysicalOperator {
    private PhysicalOperator inputOperator;
    private List<TabCol> orderByElements;

    public SortOperator(PhysicalOperator inputOperator, List<TabCol> orderByElements) {
        this.inputOperator = inputOperator;
        this.orderByElements = orderByElements;
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
