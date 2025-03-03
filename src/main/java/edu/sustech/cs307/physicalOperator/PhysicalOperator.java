package edu.sustech.cs307.physicalOperator;

import edu.sustech.cs307.meta.ColumnMeta;
import edu.sustech.cs307.tuple.Tuple;

import java.util.ArrayList;

public interface PhysicalOperator {
    boolean hasNext();

    void Begin();

    void Next();

    Tuple Current();

    void Close();

    ArrayList<ColumnMeta> outputSchema();
}
