package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.tuple.Tuple;

import java.util.List;
import java.util.ArrayList;
import net.sf.jsqlparser.statement.select.GroupByElement;

public class GroupByOperator implements PhysicalOperator {
    private PhysicalOperator inputOperator;
    private List<GroupByElement> groupByElements;
    private List<String> aggFuncs; // 占位符类型，需要根据实际代码库修改

    public GroupByOperator(PhysicalOperator inputOperator, List<GroupByElement> groupByElements, List<String> aggFuncs) {
        this.inputOperator = inputOperator;
        this.groupByElements = groupByElements;
        this.aggFuncs = aggFuncs;
    }

    public GroupByOperator(PhysicalOperator inputOperator, List<GroupByElement> groupByElements) {
        this.inputOperator = inputOperator;
        this.groupByElements = groupByElements;
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
