package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.exception.DBException;
import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.tuple.ProjectTuple;
import edu.sustech.cs307.tuple.Tuple;
import edu.sustech.cs307.meta.TabCol;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator implements PhysicalOperator {
    private PhysicalOperator input;
    private List<TabCol> outputSchema; // Use bounded wildcard
    private Tuple currentTuple;

    public ProjectOperator(PhysicalOperator input, List<TabCol> outputSchema) { // Use bounded wildcard
        this.input = input;
        this.outputSchema = outputSchema;
        if (this.outputSchema.size() == 1 && this.outputSchema.get(0).getTableName().equals("*")) {
            List<TabCol> newOutputSchema = new ArrayList<>();
            for (ColumnMeta tabCol : input.outputSchema()) {
                newOutputSchema.add(new TabCol(tabCol.tableName, tabCol.name));
            }
            this.outputSchema = newOutputSchema;
        }
    }

    @Override
    public boolean hasNext() throws DBException{
        return input.hasNext();
    }

    @Override
    public void Begin() throws DBException {
        input.Begin();
    }

    @Override
    public void Next() throws DBException {
        if (hasNext()) {
            input.Next();
            Tuple inputTuple = input.Current();
            if (inputTuple != null) {

                currentTuple = new ProjectTuple(inputTuple, outputSchema); // Create ProjectTuple
            } else {
                currentTuple = null;
            }
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
        currentTuple = null;
    }

    @Override
    public ArrayList<ColumnMeta> outputSchema() {
        return input.outputSchema();
    }
}
