package edu.sustech.cs307.tuple;

import edu.sustech.cs307.meta.TabCol;
import edu.sustech.cs307.record.Record;
import edu.sustech.cs307.value.Value;

public abstract class Tuple {
    public abstract Value getValue(Record record, TabCol tabCol);

    public abstract TabCol[] getTupleSchema();
}
